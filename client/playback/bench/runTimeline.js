"use strict";
exports.__esModule = true;
var fs_1 = require("fs");
var battlecode_schema_1 = require("battlecode-schema");
var game_1 = require("../src/game");
var wrapper = battlecode_schema_1.schema.GameWrapper.getRootAsGameWrapper(new battlecode_schema_1.flatbuffers.ByteBuffer(new Uint8Array(fs_1.readFileSync('test.bc20'))));
var game = new game_1["default"]();
game.loadFullGame(wrapper);
for (var i = 0; i < game.matchCount; i++) {
    console.log("running game " + i);
    game.getMatch(i).compute(0);
}
