import GameWorld from './gameworld';
import * as gameworld from './gameworld';
import Metadata from './metadata';
import * as metadata from './metadata';
import StructOfArrays from './soa';
import * as soa from './soa';
import * as simulator from './simulator';
import Match from './match';
import {Log} from './match';
import Game from './game';
import { schema } from 'battlecode-schema';
import { flatbuffers } from 'flatbuffers';

export {Game, Log, Match, GameWorld, gameworld, Metadata, metadata, StructOfArrays, soa, simulator, flatbuffers, schema};

// TODO provide ergonomic main export
