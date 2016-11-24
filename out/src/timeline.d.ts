/// <reference types="core-js" />
import Metadata from './metadata';
import GameWorld from './gameworld';
import { schema } from 'battlecode-schema';
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
export default class Timeline {
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
     * The current game world.
     */
    readonly current: GameWorld;
    /**
     * The farthest snapshot of the game world we've evaluated.
     */
    private _farthest;
    /**
     * The round we're attempting to seek to.
     */
    private seekTo;
    constructor(header: schema.MatchHeader, meta: Metadata);
    /**
     * Move this.current to the targeted round.
     * Return whether or not it is possible to seek to that round;
     * if we don't have deltas to it, we can't.
     */
    seek(round: number): boolean;
    /**
     * Perform computations for at least timeLimit ms.
     */
    compute(timeLimit?: number): void;
}
