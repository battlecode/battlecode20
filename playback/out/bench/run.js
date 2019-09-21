"use strict";
var fs_1 = require("fs");
var simulator_1 = require("../src/simulator");
var battlecode_schema_1 = require("battlecode-schema");
var wrapper = battlecode_schema_1.schema.GameWrapper.getRootAsGameWrapper(new battlecode_schema_1.flatbuffers.ByteBuffer(new Uint8Array(fs_1.readFileSync('test.bc17'))));
simulator_1.crunch(wrapper);
