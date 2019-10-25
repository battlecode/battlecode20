import {readFileSync} from 'fs';
import {crunch} from '../src/simulator';
import {schema} from 'battlecode-schema';
import { flatbuffers } from 'flatbuffers';

const wrapper = schema.GameWrapper.getRootAsGameWrapper(
  new flatbuffers.ByteBuffer(new Uint8Array(readFileSync('test.bc20')))
);

crunch(wrapper);
