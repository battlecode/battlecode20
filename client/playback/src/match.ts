import Metadata from './metadata';
import GameWorld from './gameworld';
// import { schema } from 'battlecode-schema';
// import { flatbuffers } from 'flatbuffers';
import { flatbuffers, schema } from 'battlecode-schema'

export type Log = {
  team: string, // 'A' | 'B'
  robotType: string, // 'ARCHON' | 'GARDENER' | 'LUMBERJACK'
                     // | 'SOLDIER' | 'TANK' | 'SCOUT'
  id: number,
  round: number,
  text: string
};

// Return a timestamp representing the _current time in ms, not necessarily from
// any particular epoch.
const timeMS: () => number = typeof window !== 'undefined' && window.performance && window.performance.now?
  window.performance.now.bind(window.performance) : Date.now.bind(Date);

/**
 * A timeline of a match. Allows you to see what the state of the match was,
 * at any particular time.
 *
 * Call timeline.seek() to request the state of the world at a particular
 * time; then call timeline.compute() to allow the timeline to perform computations.
 *
 * Note that this API is a state machine: you call methods on it,
 * then call compute() to let it do its thing, and then inspect it
 * to see what its current state is.
 *
 * This is awkward, but less so than callbacks.
 */
export default class Match {
  /**
   * How frequently to store a snapshots of the gameworld.
   */
  readonly snapshotEvery: number;

  /**
   * Snapshots of the game world.
   * [0] is round 0 (the one stored in the GameMap), [1] is round
   * snapshotEvery * 1, [2] is round snapshotEvery * 2, etc.
   */
  readonly snapshots: Array<GameWorld>;

  /**
   * Sets of changes.
   * [1] is the change between round 0 and 1, [2] is the change between
   * round 1 and 2, etc.
   * [0] is not stored.
   */
  readonly deltas: Array<schema.Round>;

  /**
   * The logs of this match, bucketed by round.
   */
  readonly logs: Array<Array<Log>>;

  /**
   * The current game world.
   * DO NOT CACHE this reference between calls to seek() and compute(), it may
   * change.
   */
  get current(): GameWorld {
    return this._current;
  }
  private _current: GameWorld;

  /**
   * The farthest snapshot of the game world we've evaluated.
   * It is possible that _farthest === _current.
   */
  private _farthest: GameWorld;

  /**
   * The round we're attempting to seek to.
   */
  get seekTo() { return this._seekTo; }
  private _seekTo: number;

  /**
   * Whether we've arrived at the seek point.
   */
  get arrived() { return this._seekTo === this._current.turn; }

  /**
   * The last turn in the match.
   */
  get lastTurn() { return this._lastTurn; }
  private _lastTurn: number | null;

  /**
   * The ID of the winner of this match.
   */
  get winner() { return this._winner; }
  private _winner: number | null;

  /**
   * Whether this match has fully loaded.
   */
  get finished() { return this._winner !== null; }

  /**
   * The maximum turn that can happen in this match.
   */
  readonly maxTurn: number;

  /**
   * Create a Timeline.
   */
  constructor(header: schema.MatchHeader, meta: Metadata) {
    this._current = new GameWorld(meta);
    this._current.loadFromMatchHeader(header);
    this._farthest = this._current;
    this.snapshots = new Array();
    this.snapshotEvery = 50;
    this.snapshots.push(this._current.copy());
    // leave [0] undefined
    this.deltas = new Array(1);
    // leave [0] undefined
    this.logs = new Array(1);
    this.maxTurn = header.maxRounds();
    this._lastTurn = null;
    this._seekTo = 0;
    this._winner = null;
  }

  /**
   * Store a schema.Round and the logs contained in it.
   */
  applyDelta(delta: schema.Round) {
    if (delta.roundID() !== this.deltas.length) {
      throw new Error(`Can't store delta ${delta.roundID()}, only have rounds up to ${this.deltas.length-1}`);
    }
    this.deltas.push(delta);

    this.parseLogs(delta.roundID(), <string> delta.logs(flatbuffers.Encoding.UTF16_STRING));
  }

  /**
   * Parse logs for a round.
   */
  parseLogs(round: number, logs: string) {

    // Regex
    let lines = logs.split(/\r?\n/);
    let header = /^\[(A|B):(ARCHON|GARDENER|LUMBERJACK|SOLDIER|TANK|SCOUT)#(\d+)@(\d+)\] (.*)/;

    let roundLogs = new Array<Log>();

    // Parse each line
    let index: number = 0;
    while (index < lines.length) {
      let line = lines[index];
      let matches = line.match(header);

      // Ignore empty string
      if (line === "") {
        index += 1;
        continue;
      }

      // The entire string and its 5 parenthesized substrings must be matched!
      if (matches === null || (matches && matches.length != 6)) {
        throw new Error(`Wrong log format: ${line}`);
      }

      // Get the matches
      let team = matches[1];
      let robotType = matches[2];
      let id = parseInt(matches[3]);
      let logRound = parseInt(matches[4]);
      let text = new Array<string>();
      text.push(line);
      index += 1;

      // If there is additional non-header text in the following lines, add it
      while (index < lines.length && !lines[index].match(header)) {
        text.push(lines[index]);
        index +=1;
      }

      if (logRound != round) {
        console.warn(`Log round mismatch: should be ${round}, is ${logRound}`);
      }

      // Push the parsed log
      roundLogs.push({
        team: team,
        robotType: robotType,
        id: id,
        round: logRound,
        text: text.join('\n')
      });
    }
    this.logs.push(roundLogs);
  }

  /**
   * Finish the timeline.
   */
  applyFooter(footer: schema.MatchFooter) {
    if (footer.totalRounds() !== this.deltas.length - 1) {
      throw new Error(`Wrong total round count: is ${footer.totalRounds()}, should be ${this.deltas.length - 1}`);
    }
    this._lastTurn = footer.totalRounds();
    this._winner = footer.winner();
  }

  /**
   * Attempt to set seekTo to a particular point.
   * Return whether or not it is possible to seek to that round;
   * if we don't have deltas to it, we can't.
   * If we can, each call to compute() will update state until current.turn === seekTo
   */
  seek(round: number): boolean {
    // the last delta we have is this.deltas.length-1, which takes us to turn
    // this.deltas.length-1; if we're higher than that, we can't seek
    if (round <= this.deltas.length-1) {
      this._seekTo = round;
      if (this._seekTo >= this._farthest.turn) {
        // Ahead of where we've processed; we'll need to compute all the way there.
        // OR, exactly at the furthest point.
        this._current = this._farthest;
      } else {
        // We've already computed past seekTo; find the closest round before it.
        // It's possible that a snapshot is closest; this is the turn of that snapshot.
        const snapBefore = this._seekTo - (this._seekTo % this.snapshotEvery);
        if (this._current.turn < snapBefore || this._seekTo < this._current.turn) {
          // If current < snapBefore <= seekTo, set current = snapBefore.
          // If snapBefore <= seekTo < current, set current = snapBefore.
          this._current.copyFrom(this.snapshots[Math.floor(snapBefore / this.snapshotEvery)]);
        }
        // Otherwise, snapBefore < current <= seekTo, so we're fine.
      }

      return true;
    }
    return false;
  }

  /**
   * Perform computations for some amount of time.
   * We try to overshoot timeGoal as little as possible; however, if turn applications start taking a long time, we may overshoot it arbitrarily far.
   * If timeGoal is 0, we'll compute until we're done.
   */
  compute(timeGoal: number = 5) {
    let start = timeMS();

    // Once we hit our soft limit, stop computing.
    while (timeGoal === 0 || timeMS() < start + timeGoal) {
      // This is coded as a state machine, which is somewhat confusing. Sorry.

      // We do one expensive operation (a turn application) every cycle round
      // the while loop.

      if (this._current.turn !== this._seekTo) {
        // Current is not at the seek-goal.
        // Walk it forward.
        this._processDelta(this._current);
        if (this._current.turn > this._farthest.turn) {
          this._farthest = this._current;
        }
      } else {
        if (this._farthest.turn < this.deltas.length-1) {
          // Then, step our highest frame forward while we still have time, and rounds left to process
          if (this._current === this._farthest) {
            // make sure we don't update current when we don't want to
            this._farthest = this._current.copy();
          }
          this._processDelta(this._farthest);
        } else {
          break;
        }
      }
    }
  }

  /**
   * Apply a delta to a GameWorld, based on world.turn.
   */
  private _processDelta(world: GameWorld) {
    if (world.turn + 1 >= this.deltas.length) {
      throw new Error(`Can't process turn ${world.turn+1}, only have up to ${this.deltas.length - 1}`);
    }
    world.processDelta(this.deltas[world.turn + 1]);

    // world.turn is now updated
    if (world.turn % this.snapshotEvery === 0
        && this.snapshots[world.turn / this.snapshotEvery] === undefined) {
      this.snapshots[world.turn / this.snapshotEvery] = world.copy();
    }
  }
}
