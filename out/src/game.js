"use strict";
var metadata_1 = require('./metadata');
var battlecode_schema_1 = require('battlecode-schema');
var match_1 = require('./match');
/**
 * Represents an entire game.
 * Contains a Match for every match in a game.
 */
var Game = (function () {
    /**
     * Create a Game with nothing inside.
     */
    function Game() {
        this._winner = null;
        this._matches = new Array();
        this._meta = null;
    }
    Object.defineProperty(Game.prototype, "finished", {
        /**
         * Whether the game has finished loading.
         */
        get: function () { return this._winner !== null; },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Game.prototype, "winner", {
        /**
         * The ID the of winner of the overall game.
         */
        get: function () { return this._winner; },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Game.prototype, "matchCount", {
        /**
         * Match count.
         */
        get: function () { return this._matches.length; },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Game.prototype, "meta", {
        /**
         * The metadata of the game.
         */
        get: function () { return this._meta; },
        enumerable: true,
        configurable: true
    });
    /**
     * Get a particular match.
     */
    Game.prototype.getMatch = function (index) {
        return this._matches[index];
    };
    /**
     * Apply an event to the game.
     */
    Game.prototype.applyEvent = function (event) {
        var gameStarted = this._meta !== null;
        var matchCount = this._matches.length;
        var lastMatchFinished = matchCount > 0 ? this._matches[this._matches.length - 1].finished : true;
        switch (event.eType()) {
            case battlecode_schema_1.schema.Event.GameHeader:
                var gameHeader = event.e(new battlecode_schema_1.schema.GameHeader());
                if (!gameStarted) {
                    this._meta = new metadata_1.default().parse(gameHeader);
                }
                else {
                    throw new Error("Can't start already-started game");
                }
                break;
            case battlecode_schema_1.schema.Event.MatchHeader:
                var matchHeader = event.e(new battlecode_schema_1.schema.MatchHeader());
                if (gameStarted && (matchCount === 0 || lastMatchFinished)) {
                    this._matches.push(new match_1.default(matchHeader, this._meta));
                }
                else {
                    throw new Error("Can't create new game when last hasn't finished");
                }
                break;
            case battlecode_schema_1.schema.Event.Round:
                var delta = event.e(new battlecode_schema_1.schema.Round());
                if (gameStarted && matchCount > 0 && !lastMatchFinished) {
                    this._matches[this._matches.length - 1].applyDelta(delta);
                }
                else {
                    throw new Error("Can't apply delta without unfinished match");
                }
                break;
            case battlecode_schema_1.schema.Event.MatchFooter:
                var matchFooter = event.e(new battlecode_schema_1.schema.MatchFooter());
                if (gameStarted && matchCount > 0 && !lastMatchFinished) {
                    this._matches[this._matches.length - 1].applyFooter(matchFooter);
                }
                else {
                    throw new Error("Can't apply footer without unfinished match");
                }
                break;
            case battlecode_schema_1.schema.Event.GameFooter:
                var gameFooter = event.e(new battlecode_schema_1.schema.GameFooter());
                if (gameStarted && matchCount > 0 && lastMatchFinished) {
                    this._winner = gameFooter.winner();
                }
                else {
                    throw new Error("Can't finish game without finished match");
                }
                break;
            case battlecode_schema_1.schema.Event.NONE:
            default:
                throw new Error('No event to apply?');
        }
    };
    /**
     * Load a game all at once.
     */
    Game.prototype.loadFullGame = function (wrapper) {
        var eventSlot = new battlecode_schema_1.schema.EventWrapper();
        var eventCount = wrapper.eventsLength();
        for (var i = 0; i < eventCount; i++) {
            this.applyEvent(wrapper.events(i, eventSlot));
        }
        if (!this.finished) {
            throw new Error("Gamewrapper did not finish game!");
        }
    };
    return Game;
}());
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = Game;
