"use strict";
var soa_1 = require("./soa");
var battlecode_schema_1 = require("battlecode-schema");
// necessary because victor doesn't use exports.default
var Victor = require("victor");
var deepcopy = require("deepcopy");
/**
 * A frozen image of the game world.
 *
 * TODO(jhgilles): better access control on contents.
 */
var GameWorld = (function () {
    function GameWorld(meta) {
        this.meta = meta;
        this.bodies = new soa_1.default({
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
        this.bullets = new soa_1.default({
            id: new Int32Array(0),
            x: new Float32Array(0),
            y: new Float32Array(0),
            velX: new Float32Array(0),
            velY: new Float32Array(0),
            spawnedTime: new Uint16Array(0),
            damage: new Float32Array(0)
        }, 'id');
        // Instantiate stats
        this.stats = new Map();
        for (var team in this.meta.teams) {
            var teamID = this.meta.teams[team].teamID;
            this.stats.set(teamID, {
                bullets: 0,
                vps: 0,
                robots: [
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
            x: new Float32Array(0),
            y: new Float32Array(0),
            red: new Int32Array(0),
            green: new Int32Array(0),
            blue: new Int32Array(0)
        }, 'id');
        this.indicatorLines = new soa_1.default({
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
        this._bodiesSlot = new battlecode_schema_1.schema.SpawnedBodyTable();
        this._bulletsSlot = new battlecode_schema_1.schema.SpawnedBulletTable();
        this._vecTableSlot1 = new battlecode_schema_1.schema.VecTable();
        this._vecTableSlot2 = new battlecode_schema_1.schema.VecTable();
        this._rgbTableSlot = new battlecode_schema_1.schema.RGBTable();
    }
    GameWorld.prototype.loadFromMatchHeader = function (header) {
        var map = header.map();
        var bodies = map.bodies(this._bodiesSlot);
        if (bodies) {
            this.insertBodies(bodies);
        }
        var trees = map.trees();
        if (trees) {
            this.insertTrees(map.trees());
        }
        var minCorner = map.minCorner();
        this.minCorner.x = minCorner.x();
        this.minCorner.y = minCorner.y();
        var maxCorner = map.maxCorner();
        this.maxCorner.x = maxCorner.x();
        this.maxCorner.y = maxCorner.y();
        var name = map.name();
        if (name) {
            this.mapName = map.name();
        }
    };
    /**
     * Create a copy of the world in its current state.
     */
    GameWorld.prototype.copy = function () {
        var result = new GameWorld(this.meta);
        result.copyFrom(this);
        return result;
    };
    GameWorld.prototype.copyFrom = function (source) {
        var _this = this;
        this.turn = source.turn;
        this.minCorner = source.minCorner;
        this.maxCorner = source.maxCorner;
        this.mapName = source.mapName;
        this.bodies.copyFrom(source.bodies);
        this.bullets.copyFrom(source.bullets);
        this.indicatorDots.copyFrom(source.indicatorDots);
        this.indicatorLines.copyFrom(source.indicatorLines);
        this.stats = new Map();
        source.stats.forEach(function (value, key) {
            _this.stats.set(key, deepcopy(value));
        });
    };
    /**
     * Process a set of changes.
     */
    GameWorld.prototype.processDelta = function (delta) {
        if (delta.roundID() != this.turn + 1) {
            throw new Error("Bad Round: this.turn = " + this.turn + ", round.roundID() = " + delta.roundID());
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
        var bodies = delta.spawnedBodies(this._bodiesSlot);
        if (bodies) {
            this.insertBodies(bodies);
        }
        // Simulate spawning
        var bullets = delta.spawnedBullets(this._bulletsSlot);
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
        var movedLocs = delta.movedLocs(this._vecTableSlot1);
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
            for (var i_1 = 0; i_1 < delta.diedIDsLength(); i_1++) {
                var index = indices[i_1];
                var team = this.bodies.arrays.team[index];
                var type = this.bodies.arrays.type[index];
                var statObj = this.stats.get(team);
                statObj.robots[type] -= 1;
                this.stats.set(team, statObj);
            }
            this.bodies.deleteBulk(delta.diedIDsArray());
        }
        if (delta.diedBulletIDsLength() > 0) {
            this.bullets.deleteBulk(delta.diedBulletIDsArray());
        }
        // Insert indicator dots and lines
        this.insertIndicatorDots(delta);
        this.insertIndicatorLines(delta);
    };
    GameWorld.prototype.insertIndicatorDots = function (delta) {
        // Delete the dots from the previous round
        this.indicatorDots.clear();
        // Insert the dots from the current round
        if (delta.indicatorDotIDsLength() > 0) {
            var locs = delta.indicatorDotLocs(this._vecTableSlot1);
            var rgbs = delta.indicatorDotRGBs(this._rgbTableSlot);
            this.indicatorDots.insertBulk({
                id: delta.indicatorDotIDsArray(),
                x: locs.xsArray(),
                y: locs.ysArray(),
                red: rgbs.redArray(),
                green: rgbs.greenArray(),
                blue: rgbs.blueArray()
            });
        }
    };
    GameWorld.prototype.insertIndicatorLines = function (delta) {
        // Delete the lines from the previous round
        this.indicatorLines.clear();
        // Insert the lines from the current round
        if (delta.indicatorLineIDsLength() > 0) {
            var startLocs = delta.indicatorLineStartLocs(this._vecTableSlot1);
            var endLocs = delta.indicatorLineEndLocs(this._vecTableSlot2);
            var rgbs = delta.indicatorLineRGBs(this._rgbTableSlot);
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
    };
    GameWorld.prototype.insertBodies = function (bodies) {
        // Update spawn stats
        var teams = bodies.teamIDsArray();
        var types = bodies.typesArray();
        for (var i = 0; i < bodies.robotIDsArray().length; i++) {
            var statObj = this.stats.get(teams[i]);
            statObj.robots[types[i]] += 1;
            this.stats.set(teams[i], statObj);
        }
        var locs = bodies.locs(this._vecTableSlot1);
        // Note: this allocates 6 objects with each call.
        // (One for the container, one for each TypedArray.)
        // All of the objects are small; the TypedArrays are basically
        // (pointer, length) pairs.
        // You can't reuse TypedArrays easily, so I'm inclined to
        // let this slide for now.
        var startIndex = this.bodies.insertBulk({
            id: bodies.robotIDsArray(),
            team: bodies.teamIDsArray(),
            type: bodies.typesArray(),
            x: locs.xsArray(),
            y: locs.ysArray(),
        });
        // Extra initialization
        var endIndex = startIndex + bodies.robotIDsLength();
        var typeArray = this.bodies.arrays.type;
        var radiusArray = this.bodies.arrays.radius;
        var healthArray = this.bodies.arrays.health;
        var maxHealthArray = this.bodies.arrays.maxHealth;
        for (var i = startIndex; i < endIndex; i++) {
            var type = typeArray[i];
            var typeInfo = this.meta.types[type];
            radiusArray[i] = typeInfo.radius;
            healthArray[i] = typeInfo.startHealth;
            maxHealthArray[i] = typeInfo.maxHealth;
        }
        soa_1.default.fill(this.bodies.arrays.containedBullets, 0, startIndex, this.bodies.length);
        soa_1.default.fill(this.bodies.arrays.containedBody, battlecode_schema_1.schema.BodyType.NONE, startIndex, this.bodies.length);
    };
    GameWorld.prototype.insertBullets = function (bullets) {
        var locs = bullets.locs(this._vecTableSlot1);
        var vels = bullets.vels(this._vecTableSlot2);
        var startI = this.bullets.insertBulk({
            id: bullets.robotIDsArray(),
            x: locs.xsArray(),
            y: locs.ysArray(),
            velX: vels.xsArray(),
            velY: vels.ysArray(),
            damage: bullets.damagesArray(),
        });
        // There may be an off-by-one error here but I think this is right
        soa_1.default.fill(this.bullets.arrays.spawnedTime, this.turn, startI, this.bullets.length);
    };
    GameWorld.prototype.insertTrees = function (trees) {
        var locs = trees.locs(this._vecTableSlot1);
        var startI = this.bodies.insertBulk({
            id: trees.robotIDsArray(),
            radius: trees.radiiArray(),
            health: trees.healthsArray(),
            x: locs.xsArray(),
            y: locs.ysArray(),
            maxHealth: trees.maxHealthsArray(),
            containedBullets: trees.containedBulletsArray(),
            containedBodies: trees.containedBodiesArray()
        });
        soa_1.default.fill(this.bodies.arrays.team, NEUTRAL_TEAM, startI, this.bodies.length);
        soa_1.default.fill(this.bodies.arrays.type, battlecode_schema_1.schema.BodyType.TREE_NEUTRAL, startI, this.bodies.length);
    };
    return GameWorld;
}());
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = GameWorld;
// TODO(jhgilles): encode in flatbuffers
var NEUTRAL_TEAM = 0;
