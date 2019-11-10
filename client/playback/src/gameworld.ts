import StructOfArrays from './soa';
import Metadata from './metadata';
import { schema } from 'battlecode-schema';

// necessary because victor doesn't use exports.default
import Victor = require('victor');
import deepcopy = require('deepcopy');

// let Position to be vector type?
export type DiedBodiesSchema = {
  id: Int32Array,
  x: Int32Array,
  y: Int32Array,
}

export type BodiesSchema = {
  id: Int32Array,
  team: Int8Array,
  type: Int8Array,
  x: Int32Array,
  y: Int32Array,
  dirt: Int32Array,
  bytecodesUsed: Int32Array, // Only relevant for non-neutral bodies
};

export type MapStats = {
  name: string,
  minCorner: Victor,
  maxCorner: Victor,
  bodies: schema.SpawnedBodyTable,
  randomSeed: number,

  length: number, // total number of tiles
  dirt: Int32Array,
  water: Int32Array,
  pollution: Int32Array,
  soup: Int32Array
};

export type TeamStats = {
  // TODO: get size of array and auto-scale this array's size?

  // An array of numbers corresponding to team stats, which map to RobotTypes
  // Corresponds to robot type (including NONE; empty drones and carrying drones are counted the same. length 11)
  soup: number,
  robots: [number, number, number, number, number, number, number, number, number, number, number]
};

export type IndicatorDotsSchema = {
  id: Int32Array,
  x: Int32Array,
  y: Int32Array,
  red: Int32Array,
  green: Int32Array,
  blue: Int32Array
}

export type IndicatorLinesSchema = {
  id: Int32Array,
  startX: Int32Array,
  startY: Int32Array,
  endX: Int32Array,
  endY: Int32Array,
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
   *   x: Int32Array,
   *   y: Int32Array,
   * }
   */
  diedBodies: StructOfArrays<DiedBodiesSchema>;

  /**
   * Everything that isn't a bullet or indicator string.
   * {
   *   id: Int32Array,
   *   team: Int8Array,
   *   type: Int8Array,
   *   x: Int32Array,
   *   y: Int32Array,
   *   bytecodesUsed: Int32Array,
   * }
   */
  bodies: StructOfArrays<BodiesSchema>;

  /*
   * Stats for each team
   */
  teamStats: Map<number, TeamStats>; // Team ID to their stats

  /*
   * Stats for each team
   */
  mapStats: MapStats; // Team ID to their stats

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

  // duplicate with mapStats, but left for compatibility.
  // TODO: change dependencies and remove these map variables
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
  private _vecTableSlot1: schema.VecTable;
  private _vecTableSlot2: schema.VecTable;
  private _rgbTableSlot: schema.RGBTable;

  constructor(meta: Metadata) {
    this.meta = meta;

    this.diedBodies = new StructOfArrays({
      id: new Int32Array(0),
      x: new Int32Array(0),
      y: new Int32Array(0),
    }, 'id');

    this.bodies = new StructOfArrays({
      id: new Int32Array(0),
      team: new Int8Array(0),
      type: new Int8Array(0),
      x: new Int32Array(0),
      y: new Int32Array(0),
      dirt: new Int32Array(0),
      bytecodesUsed: new Int32Array(0),
    }, 'id');


    // Instantiate teamStats
    this.teamStats = new Map<number, TeamStats>();
    for (let team in this.meta.teams) {
        var teamID = this.meta.teams[team].teamID;
        this.teamStats.set(teamID, {
          soup: 0,
          robots: [
            0, // MINER
            0, // LANDSCAPER
            0, // DRONE
            0, // NET_GUN
            0, // COW
            0, // REFINERY
            0, // VAPORATOR
            0, // HQ
            0, // DESIGN_SCHOOL
            0, // FULFILLMENT_CENTER
            0, // NONE
        ]});
    }

    // Instantiate mapStats
    this.mapStats = {
      name: '????',
      minCorner: new Victor(0,0),
      maxCorner: new Victor(0,0),
      bodies: new schema.SpawnedBodyTable(),
      randomSeed: 0,
      length: 0,
      water: new Int32Array(0),
      dirt: new Int32Array(0),
      pollution: new Int32Array(0),
      soup: new Int32Array(0)
    };


    this.indicatorDots = new StructOfArrays({
      id: new Int32Array(0),
      x: new Int32Array(0),
      y: new Int32Array(0),
      red: new Int32Array(0),
      green: new Int32Array(0),
      blue: new Int32Array(0)
    }, 'id');

    this.indicatorLines = new StructOfArrays({
      id: new Int32Array(0),
      startX: new Int32Array(0),
      startY: new Int32Array(0),
      endX: new Int32Array(0),
      endY: new Int32Array(0),
      red: new Int32Array(0),
      green: new Int32Array(0),
      blue: new Int32Array(0)
    }, 'id');

    this.turn = 0;
    this.minCorner = new Victor(0, 0);
    this.maxCorner = new Victor(0, 0);
    this.mapName = '????';

    this._bodiesSlot = new schema.SpawnedBodyTable()
    this._vecTableSlot1 = new schema.VecTable();
    this._vecTableSlot2 = new schema.VecTable();
    this._rgbTableSlot = new schema.RGBTable();
  }

  loadFromMatchHeader(header: schema.MatchHeader) {
    const map = header.map();

    const name = map.name() as string;
    if (name) {
      this.mapName = map.name() as string;
      this.mapStats.name = map.name() as string;
    }

    const minCorner = map.minCorner();
    this.minCorner.x = minCorner.x();
    this.minCorner.y = minCorner.y();
    this.mapStats.minCorner.x = minCorner.x();
    this.mapStats.minCorner.y = minCorner.y();

    const maxCorner = map.maxCorner();
    this.maxCorner.x = maxCorner.x();
    this.maxCorner.y = maxCorner.y();
    this.mapStats.maxCorner.x = maxCorner.x();
    this.mapStats.maxCorner.y = maxCorner.y();

    const bodies = map.bodies(this._bodiesSlot);
    if (bodies && bodies.robotIDsLength) {
      this.insertBodies(bodies);
    }

    this.mapStats.randomSeed = map.randomSeed();

    this.mapStats.water = Int32Array.from(map.waterArray());
    this.mapStats.dirt = Int32Array.from(map.dirtArray());
    this.mapStats.pollution = Int32Array.from(map.pollutionArray());
    this.mapStats.soup = Int32Array.from(map.soupArray());
    
    // Check with header.totalRounds() ?
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
    this.indicatorDots.copyFrom(source.indicatorDots);
    this.indicatorLines.copyFrom(source.indicatorLines);
    this.teamStats = new Map<number, TeamStats>();
    source.teamStats.forEach((value: TeamStats, key: number) => {
      this.teamStats.set(key, deepcopy(value));
    });
  }

  /**
   * Process a set of changes.
   */
  processDelta(delta: schema.Round) {
    if (delta.roundID() != this.turn + 1) {
      throw new Error(`Bad Round: this.turn = ${this.turn}, round.roundID() = ${delta.roundID()}`);
    }

    // Soup changes on team
    for (var i = 0; i < delta.teamIDsLength(); i++) {
        var teamID = delta.teamIDsArray()[i];
        var statObj = this.teamStats.get(teamID);

        statObj.soup = delta.teamSoups(i);

        this.teamStats.set(teamID, statObj);
    }

    // Location changes on bodies
    const movedLocs = delta.movedLocs(this._vecTableSlot1);
    if (movedLocs) {
      this.bodies.alterBulk({
        id: delta.movedIDsArray(),
        x: movedLocs.xsArray(),
        y: movedLocs.ysArray(),
      });
    }

    // Spawned bodies
    const bodies = delta.spawnedBodies(this._bodiesSlot);
    if (bodies) {
      this.insertBodies(bodies);
    }

    // Died bodies
    if (delta.diedIDsLength() > 0) {

      // Update team stats
      var indices = this.bodies.lookupIndices(delta.diedIDsArray());
      for(let i = 0; i < delta.diedIDsLength(); i++) {
          let index = indices[i];
          let team = this.bodies.arrays.team[index];
          let type = this.bodies.arrays.type[index];
          var statObj = this.teamStats.get(team);
          if(!statObj) {continue;} // In case this is a neutral bot
          statObj.robots[type] -= 1;
          this.teamStats.set(team, statObj);
      }

      // Update bodies soa
      this.insertDiedBodies(delta);

      this.bodies.deleteBulk(delta.diedIDsArray());
    }

    // Action
    if(delta.actionsLength() > 0){
      for(let i=0; i<delta.actionsLength(); i++){
        const action = delta.actions(i);
        const robotID = delta.actionIDs(i);
        const targetID = delta.actionTargets(i);
        switch (action) {
          // TODO: implement each actions
          case schema.Action.SHOOT:
            break;
        
          default:
            console.log(`Undefined action: action(${action}), robotID(${robotID}, targetID(${targetID}))`);
            break;
        }
      }
    }

    // Simulate actions
    // const containedBullets = this.bodies.arrays.containedBullets;
    // delta.actionsArray().forEach((action: schema.Action, index: number) => {
    //   if (action === schema.Action.SHAKE_TREE) {
    //     this.bodies.alter({
    //       id: delta.actionTargetsArray()[index],
    //       containedBullets: 0
    //     })
    //   }
    // });

    // Dirt changes on bodies
    if (delta.dirtChangedBodyIDsLength() > 0) {
      this.bodies.alterBulk({
        id: delta.dirtChangedBodyIDsArray(),
        dirt: delta.dirtChangesBodyArray()
      });
    }
    
    // Dirt changes on map
    for(let i = 0; i<delta.dirtChangedBodyIDsLength(); i++){
      const idx = delta.dirtChangedIdxs(i);
      this.mapStats.dirt[idx] = delta.dirtChanges(i);
    }

    // Water changes on map
    for(let i = 0; i<delta.waterChangesLength(); i++){
      const idx = delta.waterChangedIdxs(i);
      this.mapStats.water[idx] = delta.waterChanges(i);
    }

    // Pollution changes on map
    for(let i = 0; i<delta.pollutionChangesLength(); i++){
      const idx = delta.pollutionChangedIdxs(i);
      this.mapStats.pollution[idx] = delta.pollutionChanges(i);
    }

    // Soup changes on map
    for(let i = 0; i<delta.soupChangesLength(); i++){
      const idx = delta.soupChangedIdxs(i);
      this.mapStats.soup[idx] = delta.soupChanges(i);
    }

    // Insert indicator dots and lines
    this.insertIndicatorDots(delta);
    this.insertIndicatorLines(delta);

    // Logs
    // TODO

    // Message pool
    // TODO

    // Increase the turn count
    this.turn = delta.roundID();

    // Update bytecode costs
    if (delta.bytecodeIDsLength() > 0) {
      this.bodies.alterBulk({
        id: delta.bytecodeIDsArray(),
        bytecodesUsed: delta.bytecodesUsedArray()
      });
    }
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
    for (let i = startIndex; i < endIndex; i++) {
      const body = this.bodies.lookup(idArray[i]);
      xArray[i] = body.x;
      yArray[i] = body.y;
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
    for(let i = 0; i < bodies.robotIDsLength(); i++) {
        var statObj = this.teamStats.get(teams[i]);
        statObj.robots[types[i]] += 1;
        this.teamStats.set(teams[i], statObj);
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
    StructOfArrays.fill(
      this.bodies.arrays.bytecodesUsed,
      0,
      startIndex,
      this.bodies.length
    );
    StructOfArrays.fill(
      this.bodies.arrays.dirt,
      0,
      startIndex,
      this.bodies.length
    );
  }

}

// TODO(jhgilles): encode in flatbuffers
const NEUTRAL_TEAM = 0;
