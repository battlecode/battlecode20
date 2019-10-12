"use strict";
exports.__esModule = true;
var battlecode_schema_1 = require("battlecode-schema");
var create_1 = require("../bench/create");
function createMatch(matches, turnsPerMatch) {
    var builder = new battlecode_schema_1.flatbuffers.Builder();
    var header = create_1.createHeader(builder);
    var events = [];
    events.push(create_1.createEventWrapper(builder, create_1.createHeader(builder), battlecode_schema_1.schema.Event.GameHeader));
    for (var i = 0; i < matches; i++) {
        battlecode_schema_1.schema.GameMap.startGameMap(builder);
        battlecode_schema_1.schema.GameMap.addMinCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, -1000, -1000));
        battlecode_schema_1.schema.GameMap.addMaxCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, 100000, 100000));
        var map = battlecode_schema_1.schema.GameMap.endGameMap(builder);
        battlecode_schema_1.schema.MatchHeader.startMatchHeader(builder);
        battlecode_schema_1.schema.MatchHeader.addMaxRounds(builder, turnsPerMatch);
        battlecode_schema_1.schema.MatchHeader.addMap(builder, map);
        events.push(create_1.createEventWrapper(builder, battlecode_schema_1.schema.MatchHeader.endMatchHeader(builder), battlecode_schema_1.schema.Event.MatchHeader));
    }
    return -1;
}
