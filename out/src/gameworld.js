"use strict";
var soa_1 = require('./soa');
var battlecode_schema_1 = require('battlecode-schema');
// necessary because victor doesn't use exports.default
var Victor = require('victor');
/**
 * A frozen image of the game world.
 *
 * TODO(jhgilles): better access control on contents.
 */
var GameWorld = (function () {
    function GameWorld(meta) {
        this.meta = meta;
        this.bodies = new soa_1.default({
            id: Int32Array,
            team: Int8Array,
            type: Int8Array,
            x: Float32Array,
            y: Float32Array,
            health: Float32Array,
            radius: Float32Array
        }, 'id');
        this.bullets = new soa_1.default({
            id: Int32Array,
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
        this._bodiesSlot = new battlecode_schema_1.schema.SpawnedBodyTable();
        this._bulletsSlot = new battlecode_schema_1.schema.SpawnedBulletTable();
        this._vecTableSlot = new battlecode_schema_1.schema.VecTable();
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
        this.turn = source.turn;
        this.minCorner = source.minCorner;
        this.maxCorner = source.maxCorner;
        this.mapName = source.mapName;
        this.bodies.copyFrom(source.bodies);
        this.bullets.copyFrom(source.bullets);
    };
    /**
     * Process a set of changes.
     */
    GameWorld.prototype.processDelta = function (delta) {
        if (delta.roundID() != this.turn + 1) {
            throw new Error("Bad Round: this.turn = " + this.turn + ", round.roundID() = " + delta.roundID());
        }
        // Increase the turn count
        this.turn += 1;
        // Simulate deaths
        if (delta.diedIDsLength() > 0) {
            this.bodies.deleteBulk(delta.diedIDsArray());
        }
        if (delta.diedBulletIDsLength() > 0) {
            this.bullets.deleteBulk(delta.diedBulletIDsArray());
        }
        // Simulate changed health levels
        if (delta.healthChangedIDsLength() > 0) {
            this.bodies.alterBulk({
                id: delta.healthChangedIDsArray(),
                health: delta.healthChangeLevelsArray()
            });
        }
        // Simulate movement
        var movedLocs = delta.movedLocs(this._vecTableSlot);
        if (movedLocs) {
            this.bodies.alterBulk({
                id: delta.movedIDsArray(),
                x: movedLocs.xsArray(),
                y: movedLocs.ysArray(),
            });
        }
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
    };
    GameWorld.prototype.insertBodies = function (bodies) {
        var locs = bodies.locs(this._vecTableSlot);
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
        var typeArray = this.bodies.arrays['type'];
        var radiusArray = this.bodies.arrays['radius'];
        var healthArray = this.bodies.arrays['radius'];
        for (var i = startIndex; i < endIndex; i++) {
            var type = typeArray[i];
            var typeInfo = this.meta.types[type];
            radiusArray[i] = typeInfo.radius;
            healthArray[i] = typeInfo.startHealth;
        }
    };
    GameWorld.prototype.insertBullets = function (bullets) {
        var locs = bullets.locs(this._vecTableSlot);
        var vels = bullets.vels(this._vecTableSlot);
        var startI = this.bullets.insertBulk({
            id: bullets.robotIDsArray(),
            x: locs.xsArray(),
            y: locs.ysArray(),
            velX: vels.xsArray(),
            velY: vels.ysArray(),
            damage: bullets.damagesArray(),
        });
        // There may be an off-by-one error here but I think this is right
        soa_1.default.fill(this.bullets.arrays['spawnedTime'], this.turn, startI, this.bullets.length);
    };
    GameWorld.prototype.insertTrees = function (trees) {
        var locs = trees.locs(this._vecTableSlot);
        var startI = this.bodies.insertBulk({
            id: trees.robotIDsArray(),
            radius: trees.radiiArray(),
            x: locs.xsArray(),
            y: locs.ysArray(),
        });
        soa_1.default.fill(this.bodies.arrays['team'], NEUTRAL_TEAM, startI, this.bodies.length);
        soa_1.default.fill(this.bodies.arrays['type'], battlecode_schema_1.schema.BodyType.TREE_NEUTRAL, startI, this.bodies.length);
        soa_1.default.fill(this.bodies.arrays['health'], this.meta.types[battlecode_schema_1.schema.BodyType.TREE_NEUTRAL].startHealth, startI, this.bodies.length);
    };
    return GameWorld;
}());
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = GameWorld;
// TODO(jhgilles): encode in flatbuffers
var NEUTRAL_TEAM = 2;
