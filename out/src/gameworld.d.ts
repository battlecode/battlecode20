/// <reference types="victor" />
import StructOfArrays from './soa';
import Metadata from './metadata';
import { schema } from 'battlecode-schema';
import Victor = require('victor');
export declare type BodiesSchema = {
    id: Int32Array;
    team: Int8Array;
    type: Int8Array;
    x: Float32Array;
    y: Float32Array;
    health: Float32Array;
    radius: Float32Array;
};
export declare type BulletsSchema = {
    id: Int32Array;
    x: Float32Array;
    y: Float32Array;
    velX: Float32Array;
    velY: Float32Array;
    damage: Float32Array;
    spawnedTime: Uint16Array;
};
/**
 * A frozen image of the game world.
 *
 * TODO(jhgilles): better access control on contents.
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
    bodies: StructOfArrays<BodiesSchema>;
    bullets: StructOfArrays<BulletsSchema>;
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
    private _bodiesSlot;
    private _bulletsSlot;
    private _vecTableSlot;
    constructor(meta: Metadata);
    loadFromMatchHeader(header: schema.MatchHeader): void;
    /**
     * Create a copy of the world in its current state.
     */
    copy(): GameWorld;
    copyFrom(source: GameWorld): void;
    /**
     * Process a set of changes.
     */
    processDelta(delta: schema.Round): void;
    private insertBodies(bodies);
    private insertBullets(bullets);
    private insertTrees(trees);
}
