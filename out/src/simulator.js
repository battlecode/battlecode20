"use strict";
var metadata_1 = require('./metadata');
var gameworld_1 = require('./gameworld');
var battlecode_schema_1 = require('battlecode-schema');
/**
 * A function that runs through a GameWrapper containing a single match, and
 * returns the state of the world at the end of the game.
 *
 * Intended for testing.
 */
function crunch(game) {
    var gameHeader = game.events(0).e(new battlecode_schema_1.schema.GameHeader());
    var metadata = new metadata_1.default().parse(gameHeader);
    var world = new gameworld_1.default(metadata);
    var matchHeader = game.events(1).e(new battlecode_schema_1.schema.MatchHeader());
    world.loadFromMatchHeader(matchHeader);
    for (var i = 2;; i++) {
        var event_1 = game.events(i);
        if (event_1.eType() === battlecode_schema_1.schema.Event.MatchFooter) {
            return world;
        }
        // must be a Round
        world.processDelta(event_1.e(new battlecode_schema_1.schema.Round()));
    }
}
exports.crunch = crunch;
