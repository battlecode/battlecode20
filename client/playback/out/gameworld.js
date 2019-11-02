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
            // health: new Float32Array(0),
            // radius: new Float32Array(0),
            // maxHealth: new Float32Array(0),
            bytecodesUsed: new Int32Array(0),
        }, 'id');
        // this.bullets = new StructOfArrays({
        //   id: new Int32Array(0),
        //   x: new Float32Array(0),
        //   y: new Float32Array(0),
        //   velX: new Float32Array(0),
        //   velY: new Float32Array(0),
        //   spawnedTime: new Uint16Array(0),
        //   damage: new Float32Array(0)
        // }, 'id');
        // Instantiate stats
        this.stats = new Map();
        for (let team in this.meta.teams) {
            var teamID = this.meta.teams[team].teamID;
            this.stats.set(teamID, {
                // bullets: 0,
                // vps: 0,
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
        // this._bulletsSlot = new schema.SpawnedBulletTable()
        this._vecTableSlot1 = new battlecode_schema_1.schema.VecTable();
        this._vecTableSlot2 = new battlecode_schema_1.schema.VecTable();
        this._rgbTableSlot = new battlecode_schema_1.schema.RGBTable();
    }
    loadFromMatchHeader(header) {
        const map = header.map();
        console.log(this);
        const bodies = map.bodies(this._bodiesSlot);
        console.log(this._bodiesSlot);
        if (bodies.robotIDsLength) {
            this.insertBodies(bodies);
        }
        // const trees = map.trees();
        // if (trees) {
        //   this.insertTrees(map.trees());
        // }
        const minCorner = map.minCorner();
        this.minCorner.x = minCorner.x();
        this.minCorner.y = minCorner.y();
        const maxCorner = map.maxCorner();
        this.maxCorner.x = maxCorner.x();
        this.maxCorner.y = maxCorner.y();
        const name = map.name();
        if (name) {
            this.mapName = map.name();
        }
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
        // this.bullets.copyFrom(source.bullets);
        this.indicatorDots.copyFrom(source.indicatorDots);
        this.indicatorLines.copyFrom(source.indicatorLines);
        this.stats = new Map();
        source.stats.forEach((value, key) => {
            this.stats.set(key, deepcopy(value));
        });
    }
    /**
     * Process a set of changes.
     */
    processDelta(delta) {
        if (delta.roundID() != this.turn + 1) {
            throw new Error(`Bad Round: this.turn = ${this.turn}, round.roundID() = ${delta.roundID()}`);
        }
        // Update bullet and vp stats
        for (var i = 0; i < delta.teamIDsLength(); i++) {
            var teamID = delta.teamIDsArray()[i];
            var statObj = this.stats.get(teamID);
            // statObj.bullets = delta.teamBullets(i);
            // statObj.vps = delta.teamVictoryPoints(i);
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
        // const bullets = delta.spawnedBullets(this._bulletsSlot);
        // if (bullets) {
        // this.insertBullets(bullets);
        // }
        // Simulate changed health levels
        // if (delta.healthChangedIDsLength() > 0) {
        //   this.bodies.alterBulk({
        //     id: delta.healthChangedIDsArray(),
        //     health: delta.healthChangeLevelsArray()
        //   });
        // }
        // Simulate movement
        const movedLocs = delta.movedLocs(this._vecTableSlot1);
        if (movedLocs) {
            this.bodies.alterBulk({
                id: delta.movedIDsArray(),
                x: movedLocs.xsArray(),
                y: movedLocs.ysArray(),
            });
        }
        // Simulate actions
        // const containedBullets = this.bodies.arrays.containedBullets;
        // delta.actionsArray().forEach((action: schema.Action, index: number) => {
        //   if (action === schema.Action.SHAKE_TREE) {
        //     this.bodies.alter({
        //       id: delta.actionTargetsArray()[index],
        //       containedBullets: 0
        //     })Int
        //   }
        // });
        // Update bytecode costs
        if (delta.bytecodeIDsLength() > 0) {
            this.bodies.alterBulk({
                id: delta.bytecodeIDsArray(),
                bytecodesUsed: delta.bytecodesUsedArray()
            });
        }
        // Simulate deaths
        if (delta.diedIDsLength() > 0) {
            // Update died stats
            var indices = this.bodies.lookupIndices(delta.diedIDsArray());
            for (let i = 0; i < delta.diedIDsLength(); i++) {
                let index = indices[i];
                let team = this.bodies.arrays.team[index];
                let type = this.bodies.arrays.type[index];
                var statObj = this.stats.get(team);
                if (!statObj) {
                    continue;
                } // In case this is a neutral bot
                statObj.robots[type] -= 1;
                this.stats.set(team, statObj);
            }
            // Update died bodies
            this.insertDiedBodies(delta);
            this.bodies.deleteBulk(delta.diedIDsArray());
        }
        // if (delta.diedBulletIDsLength() > 0) {
        //   this.bullets.deleteBulk(delta.diedBulletIDsArray());
        // }
        // Insert indicator dots and lines
        this.insertIndicatorDots(delta);
        this.insertIndicatorLines(delta);
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
        // const radiusArray = this.diedBodies.arrays.radius;
        for (let i = startIndex; i < endIndex; i++) {
            const body = this.bodies.lookup(idArray[i]);
            xArray[i] = body.x;
            yArray[i] = body.y;
            // radiusArray[i] = body.radius;
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
        // const radiusArray = this.bodies.arrays.radius;
        // const healthArray = this.bodies.arrays.health;
        // const maxHealthArray = this.bodies.arrays.maxHealth;
        // for (let i = startIndex; i < endIndex; i++) {
        //   const type = typeArray[i];
        //   const typeInfo = this.meta.types[type];
        //   radiusArray[i] = typeInfo.radius;
        //   healthArray[i] = typeInfo.startHealth;
        //   maxHealthArray[i] = typeInfo.maxHealth;
        // }
        soa_1.default.fill(this.bodies.arrays.bytecodesUsed, 0, startIndex, this.bodies.length);
        // StructOfArrays.fill(
        //   this.bodies.arrays.containedBullets,
        //   0,
        //   startIndex,
        //   this.bodies.length
        // );
        // StructOfArrays.fill(
        //   this.bodies.arrays.containedBody,
        //   schema.BodyType.NONE,
        //   startIndex,
        //   this.bodies.length
        // );
    }
}
exports.default = GameWorld;
// TODO(jhgilles): encode in flatbuffers
const NEUTRAL_TEAM = 0;
