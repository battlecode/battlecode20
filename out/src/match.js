"use strict";
var gameworld_1 = require("./gameworld");
var battlecode_schema_1 = require("battlecode-schema");
// Return a timestamp representing the _current time in ms, not necessarily from
// any particular epoch.
var timeMS = typeof window !== 'undefined' && window.performance && window.performance.now ?
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
var Match = (function () {
    /**
     * Create a Timeline.
     */
    function Match(header, meta) {
        this._current = new gameworld_1.default(meta);
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
    Object.defineProperty(Match.prototype, "current", {
        /**
         * The current game world.
         * DO NOT CACHE this reference between calls to seek() and compute(), it may
         * change.
         */
        get: function () {
            return this._current;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Match.prototype, "seekTo", {
        /**
         * The round we're attempting to seek to.
         */
        get: function () { return this._seekTo; },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Match.prototype, "arrived", {
        /**
         * Whether we've arrived at the seek point.
         */
        get: function () { return this._seekTo === this._current.turn; },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Match.prototype, "lastTurn", {
        /**
         * The last turn in the match.
         */
        get: function () { return this._lastTurn; },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Match.prototype, "winner", {
        /**
         * The ID of the winner of this match.
         */
        get: function () { return this._winner; },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Match.prototype, "finished", {
        /**
         * Whether this match has fully loaded.
         */
        get: function () { return this._winner !== null; },
        enumerable: true,
        configurable: true
    });
    /**
     * Store a schema.Round and the logs contained in it.
     */
    Match.prototype.applyDelta = function (delta) {
        if (delta.roundID() !== this.deltas.length) {
            throw new Error("Can't store delta " + delta.roundID() + ", only have rounds up to " + (this.deltas.length - 1));
        }
        this.deltas.push(delta);
        // Regex
        var lines = delta.logs(battlecode_schema_1.flatbuffers.Encoding.UTF16_STRING).split(/\r?\n/);
        var header = /^\[(A|B):(ARCHON|GARDENER|LUMBERJACK|SOLDIER|TANK|SCOUT)#(\d+)@(\d+)\] (.*)/;
        // Parse each line
        var index = 0;
        while (index < lines.length) {
            var line = lines[index];
            var matches = line.match(header);
            // Ignore empty string
            if (line === "") {
                index += 1;
                continue;
            }
            // The entire string and its 5 parenthesized substrings must be matched!
            if (matches === null || (matches && matches.length != 6)) {
                throw new Error("Wrong log format: " + line);
            }
            // Get the matches
            var team = matches[1];
            var robotType = matches[2];
            var id = parseInt(matches[3]);
            var round = parseInt(matches[4]);
            var text = new Array();
            text.push(line);
            index += 1;
            // If there is additional non-header text in the following lines, add it
            while (index < lines.length && !lines[index].match(header)) {
                text.push(lines[index]);
                index += 1;
            }
            // Push the parsed log
            this.logs.push({
                team: team,
                robotType: robotType,
                id: id,
                round: round,
                text: text.join('\n')
            });
        }
    };
    /**
     * Finish the timeline.
     */
    Match.prototype.applyFooter = function (footer) {
        if (footer.totalRounds() !== this.deltas.length - 1) {
            throw new Error("Wrong total round count: is " + footer.totalRounds() + ", should be " + (this.deltas.length - 1));
        }
        this._lastTurn = footer.totalRounds();
        this._winner = footer.winner();
    };
    /**
     * Attempt to set seekTo to a particular point.
     * Return whether or not it is possible to seek to that round;
     * if we don't have deltas to it, we can't.
     * If we can, each call to compute() will update state until current.turn === seekTo
     */
    Match.prototype.seek = function (round) {
        // the last delta we have is this.deltas.length-1, which takes us to turn
        // this.deltas.length-1; if we're higher than that, we can't seek
        if (round <= this.deltas.length - 1) {
            this._seekTo = round;
            if (this._seekTo >= this._farthest.turn) {
                // Ahead of where we've processed; we'll need to compute all the way there.
                // OR, exactly at the furthest point.
                this._current = this._farthest;
            }
            else {
                // We've already computed past seekTo; find the closest round before it.
                // It's possible that a snapshot is closest; this is the turn of that snapshot.
                var snapBefore = this._seekTo - (this._seekTo % this.snapshotEvery);
                if (this._current.turn < snapBefore || this._seekTo < this._current.turn) {
                    // If current < snapBefore <= seekTo, set current = snapBefore.
                    // If snapBefore <= seekTo < current, set current = snapBefore.
                    this._current.copyFrom(this.snapshots[Math.floor(snapBefore / this.snapshotEvery)]);
                }
            }
            return true;
        }
        return false;
    };
    /**
     * Perform computations for some amount of time.
     * We try to overshoot timeGoal as little as possible; however, if turn applications start taking a long time, we may overshoot it arbitrarily far.
     * If timeGoal is 0, we'll compute until we're done.
     */
    Match.prototype.compute = function (timeGoal) {
        if (timeGoal === void 0) { timeGoal = 5; }
        var start = timeMS();
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
            }
            else {
                if (this._farthest.turn < this.deltas.length - 1) {
                    // Then, step our highest frame forward while we still have time, and rounds left to process
                    if (this._current === this._farthest) {
                        // make sure we don't update current when we don't want to
                        this._farthest = this._current.copy();
                    }
                    this._processDelta(this._farthest);
                }
                else {
                    break;
                }
            }
        }
    };
    /**
     * Apply a delta to a GameWorld, based on world.turn.
     */
    Match.prototype._processDelta = function (world) {
        if (world.turn + 1 >= this.deltas.length) {
            throw new Error("Can't process turn " + (world.turn + 1) + ", only have up to " + (this.deltas.length - 1));
        }
        world.processDelta(this.deltas[world.turn + 1]);
        // world.turn is now updated
        if (world.turn % this.snapshotEvery === 0
            && this.snapshots[world.turn / this.snapshotEvery] === undefined) {
            this.snapshots[world.turn / this.snapshotEvery] = world.copy();
        }
    };
    return Match;
}());
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = Match;
