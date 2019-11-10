"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const soa_1 = require("./soa");
const battlecode_schema_1 = require("battlecode-schema");
// necessary because victor doesn't use exports.default
const Victor = require("victor");
const deepcopy = require("deepcopy");
/**
 * A frozen image of the game world.
 *
 * TODO(jhgilles): better access control on contents.
 */
class GameWorld {
    constructor(meta) {
        this.meta = meta;
        this.diedBodies = new soa_1.default({
            id: new Int32Array(0),
            x: new Int32Array(0),
            y: new Int32Array(0),
        }, 'id');
        this.bodies = new soa_1.default({
            id: new Int32Array(0),
            team: new Int8Array(0),
            type: new Int8Array(0),
            x: new Int32Array(0),
            y: new Int32Array(0),
            onDirt: new Int32Array(0),
            carryDirt: new Int32Array(0),
            cargo: new Int32Array(0),
            isCarried: new Uint8Array(0),
            bytecodesUsed: new Int32Array(0),
        }, 'id');
        // Instantiate teamStats
        this.teamStats = new Map();
        for (let team in this.meta.teams) {
            var teamID = this.meta.teams[team].teamID;
            this.teamStats.set(teamID, {
                soup: 0,
                robots: [
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                ]
            });
        }
        // Instantiate mapStats
        this.mapStats = {
            name: '????',
            minCorner: new Victor(0, 0),
            maxCorner: new Victor(0, 0),
            bodies: new battlecode_schema_1.schema.SpawnedBodyTable(),
            randomSeed: 0,
            length: 0,
            water: new Int32Array(0),
            dirt: new Int32Array(0),
            pollution: new Int32Array(0),
            soup: new Int32Array(0)
        };
        this.indicatorDots = new soa_1.default({
            id: new Int32Array(0),
            x: new Int32Array(0),
            y: new Int32Array(0),
            red: new Int32Array(0),
            green: new Int32Array(0),
            blue: new Int32Array(0)
        }, 'id');
        this.indicatorLines = new soa_1.default({
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
        this._bodiesSlot = new battlecode_schema_1.schema.SpawnedBodyTable();
        this._vecTableSlot1 = new battlecode_schema_1.schema.VecTable();
        this._vecTableSlot2 = new battlecode_schema_1.schema.VecTable();
        this._rgbTableSlot = new battlecode_schema_1.schema.RGBTable();
    }
    loadFromMatchHeader(header) {
        const map = header.map();
        const name = map.name();
        if (name) {
            this.mapName = map.name();
            this.mapStats.name = map.name();
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
    copy() {
        const result = new GameWorld(this.meta);
        result.copyFrom(this);
        return result;
    }
    copyFrom(source) {
        this.turn = source.turn;
        this.minCorner = source.minCorner;
        this.maxCorner = source.maxCorner;
        this.mapName = source.mapName;
        this.diedBodies.copyFrom(source.diedBodies);
        this.bodies.copyFrom(source.bodies);
        this.indicatorDots.copyFrom(source.indicatorDots);
        this.indicatorLines.copyFrom(source.indicatorLines);
        this.teamStats = new Map();
        source.teamStats.forEach((value, key) => {
            this.teamStats.set(key, deepcopy(value));
        });
    }
    /**
     * Process a set of changes.
     */
    processDelta(delta) {
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
            for (let i = 0; i < delta.diedIDsLength(); i++) {
                let index = indices[i];
                let team = this.bodies.arrays.team[index];
                let type = this.bodies.arrays.type[index];
                var statObj = this.teamStats.get(team);
                if (!statObj) {
                    continue;
                } // In case this is a neutral bot
                statObj.robots[type] -= 1;
                this.teamStats.set(team, statObj);
            }
            // Update bodies soa
            this.insertDiedBodies(delta);
            this.bodies.deleteBulk(delta.diedIDsArray());
        }
        // Action
        if (delta.actionsLength() > 0) {
            const arrays = this.bodies.arrays;
            for (let i = 0; i < delta.actionsLength(); i++) {
                const action = delta.actions(i);
                const robotID = delta.actionIDs(i);
                const target = delta.actionTargets(i);
                switch (action) {
                    // TODO: validate actions?
                    // TODO: vaporator things?
                    // Actions list from battlecode.fbs enum Action
                    case battlecode_schema_1.schema.Action.MINE_SOUP:
                        arrays.cargo[robotID] += 1;
                        this.mapStats.soup[target] -= 1;
                        break;
                    case battlecode_schema_1.schema.Action.REFINE_SOUP:
                        const teamID = arrays.team[robotID];
                        this.teamStats[teamID].soup += 1;
                        arrays.cargo[robotID] -= 1;
                        break;
                    case battlecode_schema_1.schema.Action.DIG_DIRT:
                        this.mapStats.dirt[target] -= 1;
                        arrays.carryDirt[robotID] += 1;
                        break;
                    case battlecode_schema_1.schema.Action.DEPOSIT_DIRT:
                        this.mapStats.dirt[target] += 1;
                        arrays.carryDirt[robotID] -= 1;
                        break;
                    case battlecode_schema_1.schema.Action.PICK_UNIT:
                        this.bodies.alter({ id: robotID, cargo: target });
                        this.bodies.alter({ id: target, isCarried: 1 });
                        break;
                    case battlecode_schema_1.schema.Action.DROP_UNIT:
                        this.bodies.alter({ id: robotID, cargo: 0 });
                        this.bodies.alter({ id: target, isCarried: 0 });
                        break;
                    // spawnings are handled by spawnedBodies
                    case battlecode_schema_1.schema.Action.SPAWN_UNIT:
                        break;
                    // deaths are handled by diedIDs
                    case battlecode_schema_1.schema.Action.SHOOT:
                        break;
                    case battlecode_schema_1.schema.Action.DIE_DROWN:
                        break;
                    case battlecode_schema_1.schema.Action.DIE_SHOT:
                        break;
                    case battlecode_schema_1.schema.Action.DIE_TOO_MUCH_DIRT:
                        break;
                    case battlecode_schema_1.schema.Action.DIE_SUICIDE:
                        break;
                    case battlecode_schema_1.schema.Action.DIE_EXCEPTION:
                        console.log(`Exception occured: robotID(${robotID}), target(${target}`);
                        break;
                    default:
                        console.log(`Undefined action: action(${action}), robotID(${robotID}, target(${target}))`);
                        break;
                }
            }
        }
        // Dirt changes on bodies
        if (delta.dirtChangedBodyIDsLength() > 0) {
            this.bodies.alterBulk({
                id: delta.dirtChangedBodyIDsArray(),
                onDirt: delta.dirtChangesBodyArray()
            });
        }
        // Dirt changes on map
        for (let i = 0; i < delta.dirtChangedBodyIDsLength(); i++) {
            const idx = delta.dirtChangedIdxs(i);
            this.mapStats.dirt[idx] = delta.dirtChanges(i);
        }
        // Water changes on map
        for (let i = 0; i < delta.waterChangesLength(); i++) {
            const idx = delta.waterChangedIdxs(i);
            this.mapStats.water[idx] = delta.waterChanges(i);
        }
        // Pollution changes on map
        for (let i = 0; i < delta.pollutionChangesLength(); i++) {
            const idx = delta.pollutionChangedIdxs(i);
            this.mapStats.pollution[idx] = delta.pollutionChanges(i);
        }
        // Soup changes on map
        for (let i = 0; i < delta.soupChangesLength(); i++) {
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
    insertDiedBodies(delta) {
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
    insertIndicatorDots(delta) {
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
            });
        }
    }
    insertIndicatorLines(delta) {
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
            });
        }
    }
    insertBodies(bodies) {
        // Update spawn stats
        var teams = bodies.teamIDsArray();
        var types = bodies.typesArray();
        for (let i = 0; i < bodies.robotIDsLength(); i++) {
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
        const arrays = this.bodies.arrays;
        const initList = [
            arrays.onDirt,
            arrays.carryDirt,
            arrays.cargo,
            arrays.isCarried,
            arrays.bytecodesUsed,
        ];
        initList.forEach((arr) => {
            soa_1.default.fill(arr, 0, startIndex, this.bodies.length);
        });
    }
}
exports.default = GameWorld;
// TODO(jhgilles): encode in flatbuffers
const NEUTRAL_TEAM = 0;
