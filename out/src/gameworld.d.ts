/// <reference types="victor" />
import StructOfArrays from './soa';
import Metadata from './metadata';
import { schema } from 'battlecode-schema';
import Victor = require('victor');
/**
 * A frozen image of the game world.
 */
export default class GameWorld {
    /**
     * Everything that isn't a bullet.
     * {
     *   id: Int32Array,
     *   team: Int8Array,
     *   type: Int8Array,
     *   x: Float32Array,
     *   y: Float32Array,
     *   health: Float32Array,
     *   radius: Float32Array
     * }
     */
    bodies: StructOfArrays;
    bullets: StructOfArrays;
    /**
     * The current turn.
     */
    turn: number;
    /**
     * The minimum corner of the game world.
     */
    minCorner: Victor;
    /**
     * The maximum corner of the game world.
     */
    maxCorner: Victor;
    /**
     * The name of the map.
     */
    mapName: string;
    /**
     * Metadata about the current game.
     */
    meta: Metadata;
    /**
     * Whether to simulate cosmetic effects (next locations, orientations...)
     * We can avoid doing this if we're just running a simulation, and not animating
     * things.
     */
    cosmetic: boolean;
    private _bodiesSlot;
    private _bulletsSlot;
    private _vecTableSlot;
    constructor(meta: Metadata, cosmetic: boolean);
    loadFromMatchHeader(header: schema.MatchHeader): void;
    /**
     * Create a copy of the world in its current state.
     */
    copy(): GameWorld;
    /**
     * Process a round.
     * If there is a round after this round, and you're simulating cosmetics,
     * you need to pass it.
     */
    processRound(current: schema.Round, next?: schema.Round): void;
    private insertBodies(bodies);
    private insertBullets(bullets);
    private insertTrees(trees);
}
