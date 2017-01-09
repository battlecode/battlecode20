import StructOfArrays from './soa';
import Metadata from './metadata';
import {schema, flatbuffers} from 'battlecode-schema';

// necessary because victor doesn't use exports.default
import Victor = require('victor');
import deepcopy = require('deepcopy');

export type DiedBodiesSchema = {
  id: Int32Array,
  x: Float32Array,
  y: Float32Array,
  radius: Float32Array
}

export type BodiesSchema = {
  id: Int32Array,
  team: Int8Array,
  type: Int8Array,
  x: Float32Array,
  y: Float32Array,
  health: Float32Array,
  radius: Float32Array,
  maxHealth: Float32Array,
  containedBullets: Float32Array, // Only relevant for neutral trees
  containedBody: Int8Array // Only relevant for neutral trees
};

export type BulletsSchema = {
  id: Int32Array,
  x: Float32Array,
  y: Float32Array,
  velX: Float32Array,
  velY: Float32Array,
  damage: Float32Array,
  spawnedTime: Uint16Array
};

// An array of numbers corresponding to team stats, which map to RobotTypes

export type TeamStats = {
  bullets: number,
  vps: number,
  robots: [number] // Corresponds to robot type and bullet tree (length 7)
};

export type IndicatorDotsSchema = {
  id: Int32Array,
  x: Float32Array,
  y: Float32Array,
  red: Int32Array,
  green: Int32Array,
  blue: Int32Array
}

export type IndicatorLinesSchema = {
  id: Int32Array,
  startX: Float32Array,
  startY: Float32Array,
  endX: Float32Array,
  endY: Float32Array,
  red: Int32Array,
  green: Int32Array,
  blue: Int32Array
}

/**
 * A frozen image of the game world.
 *
 * TODO(jhgilles): better access control on contents.
 */
export default class GameWorld {
  /**
   * Bodies that died this round.
   * {
   *   id: Int32Array,
   *   x: Float32Array,
   *   y: Float32Array,
   *   radius: Float32Array
   * }
   */
  diedBodies: StructOfArrays<DiedBodiesSchema>;

  /**
   * Everything that isn't a bullet or indicator string.
   * {
   *   id: Int32Array,
   *   team: Int8Array,
   *   type: Int8Array,
   *   x: Float32Array,
   *   y: Float32Array,
   *   health: Float32Array,
   *   radius: Float32Array,
   *   maxHealth: Float32Array,
   *   containedBullets: Float32Array,
   *   containedBody: Int8Array
   * }
   */
  bodies: StructOfArrays<BodiesSchema>;

  /*
   * Bullets.
   * {
   *   id: Int32Array,
   *   x: Float32Array,
   *   y: Float32Array,
   *   velX: Float32Array,
   *   velY: Float32Array,
   *   damage: Float32Array,
   *   spawnedTime: Uint16Array
   * }, 'id', capacity)
   */
  bullets: StructOfArrays<BulletsSchema>;

  /*
   * Stats for each team
   */
  stats: Map<number, TeamStats>; // Team ID to their stats

  /**
   * Indicator dots.
   * {
   *   id: Int32Array,
   *   x: Float32Array,
   *   y: Float32Array,
   *   red: Int32Array,
   *   green: Int32Array,
   *   blue: Int32Array
   * }
   */
  indicatorDots: StructOfArrays<IndicatorDotsSchema>;

  /**
   * Indicator lines.
   * {
   *   id: Int32Array,
   *   startX: Float32Array,
   *   startY: Float32Array,
   *   endX: Float32Array,
   *   endY: Float32Array,
   *   red: Int32Array,
   *   green: Int32Array,
   *   blue: Int32Array
   * }
   */
  indicatorLines: StructOfArrays<IndicatorLinesSchema>;

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

  // Cache fields
  // We pass these into flatbuffers functions to avoid allocations, but that's
  // it, they don't hold any state
  private _bodiesSlot: schema.SpawnedBodyTable;
  private _bulletsSlot: schema.SpawnedBulletTable;
  private _vecTableSlot1: schema.VecTable;
  private _vecTableSlot2: schema.VecTable;
  private _rgbTableSlot: schema.RGBTable;

  constructor(meta: Metadata) {
    this.meta = meta;

    this.diedBodies = new StructOfArrays({
      id: new Int32Array(0),
      x: new Float32Array(0),
      y: new Float32Array(0),
      radius: new Float32Array(0)
    }, 'id');

    this.bodies = new StructOfArrays({
      id: new Int32Array(0),
      team: new Int8Array(0),
      type: new Int8Array(0),
      x: new Float32Array(0),
      y: new Float32Array(0),
      health: new Float32Array(0),
      radius: new Float32Array(0),
      maxHealth: new Float32Array(0),
      containedBullets: new Float32Array(0),
      containedBody: new Int8Array(0)
    }, 'id');

    this.bullets = new StructOfArrays({
      id: new Int32Array(0),
      x: new Float32Array(0),
      y: new Float32Array(0),
      velX: new Float32Array(0),
      velY: new Float32Array(0),
      spawnedTime: new Uint16Array(0),
      damage: new Float32Array(0)
    }, 'id');

    // Instantiate stats
    this.stats = new Map<number, TeamStats>();
    for (let team in this.meta.teams) {
        var teamID = this.meta.teams[team].teamID;
        this.stats.set(teamID, {
          bullets: 0,
          vps: 0,
          robots: [
            0, // ARCHONS
            0, // GARDENERS
            0, // LUMBERJACKS
            0, // SOLDIERS
            0, // TANKS
            0, // SCOUTS
            0, // TREE_BULLETS
        ]});
    }

    this.indicatorDots = new StructOfArrays({
      id: new Int32Array(0),
      x: new Float32Array(0),
      y: new Float32Array(0),
      red: new Int32Array(0),
      green: new Int32Array(0),
      blue: new Int32Array(0)
    }, 'id');

    this.indicatorLines = new StructOfArrays({
      id: new Int32Array(0),
      startX: new Float32Array(0),
      startY: new Float32Array(0),
      endX: new Float32Array(0),
      endY: new Float32Array(0),
      red: new Int32Array(0),
      green: new Int32Array(0),
      blue: new Int32Array(0)
    }, 'id');

    this.turn = 0;
    this.minCorner = new Victor(0, 0);
    this.maxCorner = new Victor(0, 0);
    this.mapName = '????';

    this._bodiesSlot = new schema.SpawnedBodyTable()
    this._bulletsSlot = new schema.SpawnedBulletTable()
    this._vecTableSlot1 = new schema.VecTable();
    this._vecTableSlot2 = new schema.VecTable();
    this._rgbTableSlot = new schema.RGBTable();
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
    const result = new GameWorld(this.meta);
    result.copyFrom(this);
    return result;
  }

  copyFrom(source: GameWorld) {
    this.turn = source.turn;
    this.minCorner = source.minCorner;
    this.maxCorner = source.maxCorner;
    this.mapName = source.mapName;
    this.diedBodies.copyFrom(source.diedBodies);
    this.bodies.copyFrom(source.bodies);
    this.bullets.copyFrom(source.bullets);
    this.indicatorDots.copyFrom(source.indicatorDots);
    this.indicatorLines.copyFrom(source.indicatorLines);
    this.stats = new Map<number, TeamStats>();
    source.stats.forEach((value: TeamStats, key: number) => {
      this.stats.set(key, deepcopy(value));
    });
  }

  /**
   * Process a set of changes.
   */
  processDelta(delta: schema.Round) {
    if (delta.roundID() != this.turn + 1) {
      throw new Error(`Bad Round: this.turn = ${this.turn}, round.roundID() = ${delta.roundID()}`);
    }

    // Update bullet and vp stats
    for (var i = 0; i < delta.teamIDsArray().length; i++) {
        var teamID = delta.teamIDsArray()[i];
        var statObj = this.stats.get(teamID);

        statObj.bullets = delta.teamBullets(i);
        statObj.vps = delta.teamVictoryPoints(i);

        this.stats.set(teamID, statObj);
    }

    // Increase the turn count
    this.turn += 1;

    // Simulate spawning
    const bodies = delta.spawnedBodies(this._bodiesSlot);
    if (bodies) {
      this.insertBodies(bodies);
    }

    // Simulate spawning
    const bullets = delta.spawnedBullets(this._bulletsSlot);
    if (bullets) {
      this.insertBullets(bullets);
    }

    // Simulate changed health levels
    if (delta.healthChangedIDsLength() > 0) {
      this.bodies.alterBulk({
        id: delta.healthChangedIDsArray(),
        health: delta.healthChangeLevelsArray()
      });
    }

    // Simulate movement
    const movedLocs = delta.movedLocs(this._vecTableSlot1);
    if (movedLocs) {
      this.bodies.alterBulk({
        id: delta.movedIDsArray(),
        x: movedLocs.xsArray(),
        y: movedLocs.ysArray(),
      });
    }

    // Simulate deaths
    if (delta.diedIDsLength() > 0) {

      // Update died stats
      var indices = this.bodies.lookupIndices(delta.diedIDsArray());
      for(let i = 0; i < delta.diedIDsLength(); i++) {
          let index = indices[i];
          let team = this.bodies.arrays.team[index];
          let type = this.bodies.arrays.type[index];
          var statObj = this.stats.get(team);
          statObj.robots[type] -= 1;
          this.stats.set(team, statObj);
      }

      // Update died bodies
      this.insertDiedBodies(delta);

      this.bodies.deleteBulk(delta.diedIDsArray());

    }
    if (delta.diedBulletIDsLength() > 0) {
      this.bullets.deleteBulk(delta.diedBulletIDsArray());
    }

    // Insert indicator dots and lines
    this.insertIndicatorDots(delta);
    this.insertIndicatorLines(delta);
  }

  private insertDiedBodies(delta: schema.Round) {
    // Delete the died bodies from the previous round
    this.diedBodies.clear();

    // Insert the died bodies from the current round
    const startIndex = this.diedBodies.insertBulk({
      id: delta.diedIDsArray()
    });

    // Extra initialization
    const endIndex = startIndex + delta.diedIDsLength();
    const idArray = this.diedBodies.arrays.id;
    const xArray = this.diedBodies.arrays.x;
    const yArray = this.diedBodies.arrays.y;
    const radiusArray = this.diedBodies.arrays.radius;
    for (let i = startIndex; i < endIndex; i++) {
      const body = this.bodies.lookup(idArray[i]);
      xArray[i] = body.x;
      yArray[i] = body.y;
      radiusArray[i] = body.radius;
    }
  }

  private insertIndicatorDots(delta: schema.Round) {
    // Delete the dots from the previous round
    this.indicatorDots.clear();

    // Insert the dots from the current round
    if (delta.indicatorDotIDsLength() > 0) {
      const locs = delta.indicatorDotLocs(this._vecTableSlot1);
      const rgbs = delta.indicatorDotRGBs(this._rgbTableSlot);
      this.indicatorDots.insertBulk({
        id: delta.indicatorDotIDsArray(),
        x: locs.xsArray(),
        y: locs.ysArray(),
        red: rgbs.redArray(),
        green: rgbs.greenArray(),
        blue: rgbs.blueArray()
      })
    }
  }

  private insertIndicatorLines(delta: schema.Round) {
    // Delete the lines from the previous round
    this.indicatorLines.clear();

    // Insert the lines from the current round
    if (delta.indicatorLineIDsLength() > 0) {
      const startLocs = delta.indicatorLineStartLocs(this._vecTableSlot1);
      const endLocs = delta.indicatorLineEndLocs(this._vecTableSlot2);
      const rgbs = delta.indicatorLineRGBs(this._rgbTableSlot);
      this.indicatorLines.insertBulk({
        id: delta.indicatorLineIDsArray(),
        startX: startLocs.xsArray(),
        startY: startLocs.ysArray(),
        endX: endLocs.xsArray(),
        endY: endLocs.ysArray(),
        red: rgbs.redArray(),
        green: rgbs.greenArray(),
        blue: rgbs.blueArray()
      })
    }
  }

  private insertBodies(bodies: schema.SpawnedBodyTable) {

    // Update spawn stats
    var teams = bodies.teamIDsArray();
    var types = bodies.typesArray();
    for(let i = 0; i < bodies.robotIDsArray().length; i++) {
        var statObj = this.stats.get(teams[i]);
        statObj.robots[types[i]] += 1;
        this.stats.set(teams[i], statObj);
    }

    const locs = bodies.locs(this._vecTableSlot1);
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
    const typeArray = this.bodies.arrays.type;
    const radiusArray = this.bodies.arrays.radius;
    const healthArray = this.bodies.arrays.health;
    const maxHealthArray = this.bodies.arrays.maxHealth;
    for (let i = startIndex; i < endIndex; i++) {
      const type = typeArray[i];
      const typeInfo = this.meta.types[type];
      radiusArray[i] = typeInfo.radius;
      healthArray[i] = typeInfo.startHealth;
      maxHealthArray[i] = typeInfo.maxHealth;
    }
    StructOfArrays.fill(
      this.bodies.arrays.containedBullets,
      0,
      startIndex,
      this.bodies.length
    );
    StructOfArrays.fill(
      this.bodies.arrays.containedBody,
      schema.BodyType.NONE,
      startIndex,
      this.bodies.length
    );
  }

  private insertBullets(bullets: schema.SpawnedBulletTable) {
    const locs = bullets.locs(this._vecTableSlot1);
    const vels = bullets.vels(this._vecTableSlot2);

    const startI = this.bullets.insertBulk({
      id: bullets.robotIDsArray(),
      x: locs.xsArray(),
      y: locs.ysArray(),
      velX: vels.xsArray(),
      velY: vels.ysArray(),
      damage: bullets.damagesArray(),
    });

    // There may be an off-by-one error here but I think this is right
    StructOfArrays.fill(this.bullets.arrays.spawnedTime, this.turn, startI, this.bullets.length);
  }

  private insertTrees(trees: schema.NeutralTreeTable) {
    const locs = trees.locs(this._vecTableSlot1);

    const startI = this.bodies.insertBulk({
      id: trees.robotIDsArray(),
      radius: trees.radiiArray(),
      health: trees.healthsArray(),
      x: locs.xsArray(),
      y: locs.ysArray(),
      maxHealth: trees.maxHealthsArray(),
      containedBullets: trees.containedBulletsArray(),
      containedBodies: trees.containedBodiesArray()
    });

    StructOfArrays.fill(
      this.bodies.arrays.team,
      NEUTRAL_TEAM,
      startI,
      this.bodies.length
    );

    StructOfArrays.fill(
      this.bodies.arrays.type,
      schema.BodyType.TREE_NEUTRAL,
      startI,
      this.bodies.length
    );
  }

}

// TODO(jhgilles): encode in flatbuffers
const NEUTRAL_TEAM = 0;
