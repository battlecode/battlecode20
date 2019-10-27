Object.defineProperty(exports, "__esModule", { value: true });
const fs_1 = require("fs");
const battlecode_schema_1 = require("battlecode-schema");
const game_1 = require("../src/game");
const wrapper = battlecode_schema_1.schema.GameWrapper.getRootAsGameWrapper(new battlecode_schema_1.flatbuffers.ByteBuffer(new Uint8Array(fs_1.readFileSync('test.bc20'))));
const game = new game_1.default();
game.loadFullGame(wrapper);
for (let i = 0; i < game.matchCount; i++) {
    console.log(`running game ${i}`);
    game.getMatch(i).compute(0);
}
