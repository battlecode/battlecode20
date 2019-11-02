"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const fs_1 = require("fs");
const simulator_1 = require("../../simulator");
const battlecode_schema_1 = require("battlecode-schema");
const wrapper = battlecode_schema_1.schema.GameWrapper.getRootAsGameWrapper(new battlecode_schema_1.flatbuffers.ByteBuffer(new Uint8Array(fs_1.readFileSync('test.bc20'))));
simulator_1.crunch(wrapper);
