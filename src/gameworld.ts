import StructOfArrays from './soa';
import Metadata from './metadata';
import {schema, flatbuffers} from 'battlecode-schema';
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
  public bodies: StructOfArrays;

  /*
   * Bullets.
   * {
   *   id: Int32Array,
   *   radius: Float32Array,
   *   x: Float32Array,
   *   y: Float32Array,
   *   velX: Float32Array,
   *   velY: Float32Array,
   *   spawnedTime: Uint16Array
   * }, 'id', capacity)
   */
  public bullets: StructOfArrays;

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

  // Cache fields
  // We pass these into flatbuffers functions to avoid allocations, but that's
  // it, they don't hold any state
  private _bodiesSlot: schema.SpawnedBodyTable;
  private _bulletsSlot: schema.SpawnedBulletTable;
  private _vecTableSlot: schema.VecTable;

  constructor(meta: Metadata, cosmetic: boolean) {
    this.meta = meta;
    this.cosmetic = cosmetic;

    this.bodies = new StructOfArrays({
      id: Int32Array,
      team: Int8Array,
      type: Int8Array,
      x: Float32Array,
      y: Float32Array,
      health: Float32Array,
      radius: Float32Array
    }, 'id');

    this.bullets = new StructOfArrays({
      id: Int32Array,
      radius: Float32Array,
      x: Float32Array,
      y: Float32Array,
      velX: Float32Array,
      velY: Float32Array,
      spawnedTime: Uint16Array,
      damage: Float32Array
    }, 'id');

    this.turn = 0;
    this.minCorner = new Victor(0, 0);
    this.maxCorner = new Victor(0, 0);
    this.mapName = '????';

    this._bodiesSlot = new schema.SpawnedBodyTable()
    this._bulletsSlot = new schema.SpawnedBulletTable()
    this._vecTableSlot = new schema.VecTable();
  }

  loadFromMatchHeader(header: schema.MatchHeader) {
    const map = header.map();
    const bodies = map.bodies(this._bodiesSlot);
    if (bodies) {
      this.insertBodies(bodies);
    }
    const trees = map.trees();
    if (trees) {
      this.insertTrees(map.trees());
    }
    const minCorner = map.minCorner();
    this.minCorner.x = minCorner.x();
    this.minCorner.y = minCorner.y();
    const maxCorner = map.maxCorner();
    this.maxCorner.x = maxCorner.x();
    this.maxCorner.y = maxCorner.y();
    const name = map.name() as string;
    if (name) {
      this.mapName = map.name() as string;
    }
  }

  /**
   * Create a copy of the world in its current state.
   */
  copy(): GameWorld {
    const result = new GameWorld(this.meta, this.cosmetic);
    result.turn = this.turn;
    result.minCorner = this.minCorner;
    result.maxCorner = this.maxCorner;
    result.mapName = this.mapName;
    result.bodies = this.bodies.copy();
    result.bullets = this.bullets.copy();
    return result;
  }

  /**
   * Process a round.
   * If there is a round after this round, and you're simulating cosmetics,
   * you need to pass it.
   */
  processRound(current: schema.Round, next?: schema.Round) {
    if (current.roundID() != this.turn + 1) {
      throw new Error(`Bad Round: this.turn = ${this.turn}, round.roundID() = ${current.roundID()}`);
    }
    if (next && next.roundID() != current.roundID() + 1) {
      throw new Error(`Bad Round pair: current.roundID() = ${current.roundID()}, next.roundID() = ${next.roundID()}`);
    }

    // Increase the turn count
    this.turn += 1;

    // Simulate deaths
    if (current.diedIDsLength() > 0) {
      this.bodies.deleteBulk(current.diedIDsArray());
    }
    if (current.diedBulletIDsLength() > 0) {
      this.bullets.deleteBulk(current.diedBulletIDsArray());
    }

    // Simulate changed health levels
    if (current.healthChangedIDsLength() > 0) {
      this.bodies.alterBulk({
        id: current.healthChangedIDsArray(),
        health: current.healthChangeLevelsArray()
      });
    }

    // Simulate movement
    const movedLocs = current.movedLocs(this._vecTableSlot);
    if (movedLocs) {
      this.bodies.alterBulk({
        id: current.movedIDsArray(),
        x: movedLocs.xsArray(),
        y: movedLocs.ysArray(),
      });
    }

    // Simulate spawning
    const bodies = current.spawnedBodies(this._bodiesSlot);
    if (bodies) {
      this.insertBodies(bodies);
    }

    // Simulate spawning
    const bullets = current.spawnedBullets(this._bulletsSlot);
    if (bullets) {
      this.insertBullets(bullets);
    }

    if (this.cosmetic && next) {
      // Eventually we'll simulate some stuff here...
      // Track where robots will be in the next simulation step,
      // which way they're facing, if they're doing an animation, etc.
      // We can use that to make things pretty :)
    }
  }

  private insertBodies(bodies: schema.SpawnedBodyTable) {
    const locs = bodies.locs(this._vecTableSlot);
    // Note: this allocates 6 objects with each call.
    // (One for the container, one for each TypedArray.)
    // All of the objects are small; the TypedArrays are basically
    // (pointer, length) pairs.
    // You can't reuse TypedArrays easily, so I'm inclined to
    // let this slide for now.
    const startIndex = this.bodies.insertBulk({
      id: bodies.robotIDsArray(),
      team: bodies.teamIDsArray(),
      type: bodies.typesArray(),
      x: locs.xsArray(),
      y: locs.ysArray(),
    });

    // Extra initialization
    const endIndex = startIndex + bodies.robotIDsLength();
    const typeArray = this.bodies.arrays['type'];
    const radiusArray = this.bodies.arrays['radius'];
    const healthArray = this.bodies.arrays['radius'];
    for (let i = startIndex; i < endIndex; i++) {
      const type = typeArray[i];
      const typeInfo = this.meta.types[type];
      radiusArray[i] = typeInfo.radius;
      healthArray[i] = typeInfo.startHealth;
    }
  }

  private insertBullets(bullets: schema.SpawnedBulletTable) {
    const locs = bullets.locs(this._vecTableSlot);
    const xs = locs.xsArray(), ys = locs.ysArray();
    const vels = bullets.vels(this._vecTableSlot);

    this.bullets.insertBulk({
      id: bullets.robotIDsArray(),
      x: xs,
      y: ys,
      velX: vels.xsArray(),
      velY: vels.ysArray(),
      damage: bullets.damagesArray(),
    });
  }

  private insertTrees(trees: schema.NeutralTreeTable) {
    const locs = trees.locs(this._vecTableSlot);

    this.bodies.insertBulk({
      id: trees.robotIDsArray(),
      radius: trees.radiiArray(),
      x: locs.xsArray(),
      y: locs.ysArray(),
    });
  }
}
