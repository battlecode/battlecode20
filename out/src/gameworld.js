"use strict";
var soa_1 = require('./soa');
var battlecode_schema_1 = require('battlecode-schema');
var Victor = require('victor');
/**
 * A frozen image of the game world.
 */
var GameWorld = (function () {
    function GameWorld(meta, cosmetic) {
        this.meta = meta;
        this.cosmetic = cosmetic;
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
        var result = new GameWorld(this.meta, this.cosmetic);
        result.turn = this.turn;
        result.minCorner = this.minCorner;
        result.maxCorner = this.maxCorner;
        result.mapName = this.mapName;
        result.bodies = this.bodies.copy();
        result.bullets = this.bullets.copy();
        return result;
    };
    /**
     * Process a round.
     * If there is a round after this round, and you're simulating cosmetics,
     * you need to pass it.
     */
    GameWorld.prototype.processRound = function (current, next) {
        if (current.roundID() != this.turn + 1) {
            throw new Error("Bad Round: this.turn = " + this.turn + ", round.roundID() = " + current.roundID());
        }
        if (next && next.roundID() != current.roundID() + 1) {
            throw new Error("Bad Round pair: current.roundID() = " + current.roundID() + ", next.roundID() = " + next.roundID());
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
        var movedLocs = current.movedLocs(this._vecTableSlot);
        if (movedLocs) {
            this.bodies.alterBulk({
                id: current.movedIDsArray(),
                x: movedLocs.xsArray(),
                y: movedLocs.ysArray(),
            });
        }
        // Simulate spawning
        var bodies = current.spawnedBodies(this._bodiesSlot);
        if (bodies) {
            this.insertBodies(bodies);
        }
        // Simulate spawning
        var bullets = current.spawnedBullets(this._bulletsSlot);
        if (bullets) {
            this.insertBullets(bullets);
        }
        if (this.cosmetic && next) {
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
        var xs = locs.xsArray(), ys = locs.ysArray();
        var vels = bullets.vels(this._vecTableSlot);
        this.bullets.insertBulk({
            id: bullets.robotIDsArray(),
            x: xs,
            y: ys,
            velX: vels.xsArray(),
            velY: vels.ysArray(),
            damage: bullets.damagesArray(),
        });
    };
    GameWorld.prototype.insertTrees = function (trees) {
        var locs = trees.locs(this._vecTableSlot);
        this.bodies.insertBulk({
            id: trees.robotIDsArray(),
            radius: trees.radiiArray(),
            x: locs.xsArray(),
            y: locs.ysArray(),
        });
    };
    return GameWorld;
}());
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = GameWorld;
