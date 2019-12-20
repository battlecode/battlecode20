# Battlecode Schema
This repository contains the [Flatbuffers](https://google.github.io/flatbuffers/) schema files that describe the wire format of Battlecode matches.

### Spec

##### Match Files
A match file has the extension `.bc20`. It consists of a single flatbuffer with a GameWrapper at its root, containing a valid stream of Events (as described in `battlecode.fbs`). The buffer will be compressed with GZIP.

##### Network Protocol
The battlecode server hosts an unsecured websocket server on port 6175. When you connect to that port, you will receive each Event that has occurred in the current match as a separate websocket message, in order. There are no messages that can be sent from the client to the server. The server may disconnect at any time, and might not resend its messages when it does; any client has to be able to deal with a game being only half-finished over the network. Messages over the network are unsecured.

### How to update things:

1. Update `battlecode.fbs`. Only add fields to the ends of tables; don't remove or rearrange any fields. Do not edit structs.
2. Run `flatc --ts -o ts battlecode.fbs` and `flatc --java -o java battlecode.fbs` to update the TypeScript and Java files.
3. Change line 3 of `ts/battlecode_generated.ts` from `import { flatbuffers } from "./flatbuffers"` to `import { flatbuffers } from "flatbuffers"`.
4. Copy the Java files over to `../engine` and run `npm install` in both `../client/playback` and then `../client/visualizer`.


OLD, 2017, WAY BELOW:

In the new way, we pull flatbuffers from npm as TypeScript directly, instead of having a local copy in JavaScript that we then need to convert into TypeScript manually. This is much nicer, and allows us to skip 1 step. It does require, however, that anyone using the battlecode schema also imports `@typings/flatbuffers` from npm.

1. Update `battlecode.fbs`. Only add fields to the ends of tables; don't remove or rearrange any fields. Do not edit structs.
2. Run `flatc --ts -o ts battlecode.fbs` and `flatc --java -o java battlecode.fbs` to update the JavaScript and Java files.
3. Copy the Java files over to `../engine` and run `npm install` in both `../client/playback` and then `../client/visualizer`.
