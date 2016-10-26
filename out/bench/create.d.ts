import { schema, flatbuffers } from 'battlecode-schema';
export declare function createHeader(builder: flatbuffers.Builder): flatbuffers.Offset;
export declare function createVecTable(builder: flatbuffers.Builder, xs: number[], ys: number[]): number;
export declare function createEventWrapper(builder: flatbuffers.Builder, event: flatbuffers.Offset, type: schema.Event): flatbuffers.Offset;
export declare function createBenchGame(aliveCount: number, churnCount: number, moveCount: number, turns: number): Uint8Array;
