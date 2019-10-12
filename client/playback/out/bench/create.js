Object.defineProperty(exports, "__esModule", { value: true });
const battlecode_schema_1 = require("battlecode-schema");
const fs_1 = require("fs");
const SIZE = 50;
const SIZE2 = SIZE / 2;
function createHeader(builder) {
    const bodies = [];
    for (const body of [battlecode_schema_1.schema.BodyType.ARCHON, battlecode_schema_1.schema.BodyType.GARDENER, battlecode_schema_1.schema.BodyType.LUMBERJACK, battlecode_schema_1.schema.BodyType.SOLDIER, battlecode_schema_1.schema.BodyType.TANK, battlecode_schema_1.schema.BodyType.SCOUT, battlecode_schema_1.schema.BodyType.BULLET, battlecode_schema_1.schema.BodyType.TREE_BULLET, battlecode_schema_1.schema.BodyType.TREE_NEUTRAL]) {
        battlecode_schema_1.schema.BodyTypeMetadata.startBodyTypeMetadata(builder);
        battlecode_schema_1.schema.BodyTypeMetadata.addBulletAttack(builder, 1);
        battlecode_schema_1.schema.BodyTypeMetadata.addBulletSpeed(builder, 1);
        battlecode_schema_1.schema.BodyTypeMetadata.addCost(builder, 100);
        battlecode_schema_1.schema.BodyTypeMetadata.addMaxHealth(builder, 100);
        battlecode_schema_1.schema.BodyTypeMetadata.addRadius(builder, 1);
        battlecode_schema_1.schema.BodyTypeMetadata.addStartHealth(builder, 100);
        battlecode_schema_1.schema.BodyTypeMetadata.addStrideRadius(builder, 5);
        battlecode_schema_1.schema.BodyTypeMetadata.addType(builder, body);
        bodies.push(battlecode_schema_1.schema.BodyTypeMetadata.endBodyTypeMetadata(builder));
    }
    const teams = [];
    for (let team of [1, 2]) {
        const name = builder.createString('team' + team);
        const packageName = builder.createString('big' + team + '.memes.big.dreams');
        battlecode_schema_1.schema.TeamData.startTeamData(builder);
        battlecode_schema_1.schema.TeamData.addName(builder, name);
        battlecode_schema_1.schema.TeamData.addPackageName(builder, packageName);
        battlecode_schema_1.schema.TeamData.addTeamID(builder, team);
        teams.push(battlecode_schema_1.schema.TeamData.endTeamData(builder));
    }
    const version = builder.createString('IMAGINARY VERSION!!!');
    const bodiesPacked = battlecode_schema_1.schema.GameHeader.createBodyTypeMetadataVector(builder, bodies);
    const teamsPacked = battlecode_schema_1.schema.GameHeader.createTeamsVector(builder, teams);
    battlecode_schema_1.schema.GameHeader.startGameHeader(builder);
    battlecode_schema_1.schema.GameHeader.addSpecVersion(builder, version);
    battlecode_schema_1.schema.GameHeader.addBodyTypeMetadata(builder, bodiesPacked);
    battlecode_schema_1.schema.GameHeader.addTeams(builder, teamsPacked);
    return battlecode_schema_1.schema.GameHeader.endGameHeader(builder);
}
exports.createHeader = createHeader;
function createVecTable(builder, xs, ys) {
    const xsP = battlecode_schema_1.schema.VecTable.createXsVector(builder, xs);
    const ysP = battlecode_schema_1.schema.VecTable.createYsVector(builder, ys);
    battlecode_schema_1.schema.VecTable.startVecTable(builder);
    battlecode_schema_1.schema.VecTable.addXs(builder, xsP);
    battlecode_schema_1.schema.VecTable.addYs(builder, ysP);
    return battlecode_schema_1.schema.VecTable.endVecTable(builder);
}
exports.createVecTable = createVecTable;
function createEventWrapper(builder, event, type) {
    battlecode_schema_1.schema.EventWrapper.startEventWrapper(builder);
    battlecode_schema_1.schema.EventWrapper.addEType(builder, type);
    battlecode_schema_1.schema.EventWrapper.addE(builder, event);
    return battlecode_schema_1.schema.EventWrapper.endEventWrapper(builder);
}
exports.createEventWrapper = createEventWrapper;
function createBenchGame(aliveCount, churnCount, moveCount, turns) {
    let builder = new battlecode_schema_1.flatbuffers.Builder();
    let events = [];
    events.push(createEventWrapper(builder, createHeader(builder), battlecode_schema_1.schema.Event.GameHeader));
    let alive = new Array(aliveCount);
    let xs = new Array(aliveCount);
    let ys = new Array(aliveCount);
    for (let i = 0; i < alive.length; i++) {
        alive[i] = i;
        xs[i] = i;
        ys[i] = i;
    }
    const locs = createVecTable(builder, xs, ys);
    const initialIDs = battlecode_schema_1.schema.SpawnedBodyTable.createRobotIDsVector(builder, alive);
    battlecode_schema_1.schema.SpawnedBodyTable.startSpawnedBodyTable(builder);
    battlecode_schema_1.schema.SpawnedBodyTable.addLocs(builder, locs);
    battlecode_schema_1.schema.SpawnedBodyTable.addRobotIDs(builder, initialIDs);
    const bodies = battlecode_schema_1.schema.SpawnedBodyTable.endSpawnedBodyTable(builder);
    battlecode_schema_1.schema.GameMap.startGameMap(builder);
    battlecode_schema_1.schema.GameMap.addBodies(builder, bodies);
    battlecode_schema_1.schema.GameMap.addMinCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, 0, 0));
    battlecode_schema_1.schema.GameMap.addMaxCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, SIZE, SIZE));
    const map = battlecode_schema_1.schema.GameMap.endGameMap(builder);
    battlecode_schema_1.schema.MatchHeader.startMatchHeader(builder);
    battlecode_schema_1.schema.MatchHeader.addMaxRounds(builder, turns);
    battlecode_schema_1.schema.MatchHeader.addMap(builder, map);
    events.push(createEventWrapper(builder, battlecode_schema_1.schema.MatchHeader.endMatchHeader(builder), battlecode_schema_1.schema.Event.MatchHeader));
    const diedIDs = new Array(churnCount);
    const bornIDs = new Array(churnCount);
    const bornXs = new Array(churnCount);
    const bornYs = new Array(churnCount);
    const movedIDs = new Array(moveCount);
    const movedXs = new Array(moveCount);
    const movedYs = new Array(moveCount);
    let nextID = aliveCount;
    for (let i = 0; i < churnCount; i++) {
        bornXs[i] = i;
        bornYs[i] = i;
    }
    const bornLocs = createVecTable(builder, bornXs, bornYs);
    for (let i = 1; i < turns + 1; i++) {
        for (let j = 0; j < churnCount; j++) {
            diedIDs[j] = alive[j];
            bornIDs[j] = nextID++;
            alive.push(bornIDs[j]);
        }
        alive.splice(0, churnCount);
        for (let i = 0; i < moveCount; i++) {
            const t = Math.random() * Math.PI * 2;
            movedXs[i] = SIZE2 + Math.cos(t) * SIZE2;
            movedYs[i] = SIZE2 + Math.sin(t) * SIZE2;
        }
        const movedLocs = createVecTable(builder, movedXs, movedYs);
        for (let j = 0; j < moveCount; j++) {
            movedIDs[j] = alive[j];
        }
        const diedP = battlecode_schema_1.schema.Round.createDiedIDsVector(builder, diedIDs);
        const bornP = battlecode_schema_1.schema.SpawnedBodyTable.createRobotIDsVector(builder, bornIDs);
        battlecode_schema_1.schema.SpawnedBodyTable.startSpawnedBodyTable(builder);
        battlecode_schema_1.schema.SpawnedBodyTable.addLocs(builder, bornLocs);
        battlecode_schema_1.schema.SpawnedBodyTable.addRobotIDs(builder, bornP);
        const spawnedP = battlecode_schema_1.schema.SpawnedBodyTable.endSpawnedBodyTable(builder);
        const movedP = battlecode_schema_1.schema.Round.createMovedIDsVector(builder, movedIDs);
        battlecode_schema_1.schema.Round.startRound(builder);
        battlecode_schema_1.schema.Round.addRoundID(builder, i);
        battlecode_schema_1.schema.Round.addMovedLocs(builder, movedLocs);
        battlecode_schema_1.schema.Round.addMovedIDs(builder, movedP);
        battlecode_schema_1.schema.Round.addSpawnedBodies(builder, spawnedP);
        battlecode_schema_1.schema.Round.addDiedIDs(builder, diedP);
        events.push(createEventWrapper(builder, battlecode_schema_1.schema.Round.endRound(builder), battlecode_schema_1.schema.Event.Round));
    }
    battlecode_schema_1.schema.MatchFooter.startMatchFooter(builder);
    battlecode_schema_1.schema.MatchFooter.addWinner(builder, 1);
    battlecode_schema_1.schema.MatchFooter.addTotalRounds(builder, turns);
    events.push(createEventWrapper(builder, battlecode_schema_1.schema.MatchFooter.endMatchFooter(builder), battlecode_schema_1.schema.Event.MatchFooter));
    battlecode_schema_1.schema.GameFooter.startGameFooter(builder);
    battlecode_schema_1.schema.GameFooter.addWinner(builder, 1);
    events.push(createEventWrapper(builder, battlecode_schema_1.schema.GameFooter.endGameFooter(builder), battlecode_schema_1.schema.Event.GameFooter));
    const eventsPacked = battlecode_schema_1.schema.GameWrapper.createEventsVector(builder, events);
    const matchHeaders = battlecode_schema_1.schema.GameWrapper.createMatchHeadersVector(builder, [1]);
    const matchFooters = battlecode_schema_1.schema.GameWrapper.createMatchHeadersVector(builder, [turns + 2]);
    battlecode_schema_1.schema.GameWrapper.startGameWrapper(builder);
    battlecode_schema_1.schema.GameWrapper.addEvents(builder, eventsPacked);
    battlecode_schema_1.schema.GameWrapper.addMatchHeaders(builder, matchHeaders);
    battlecode_schema_1.schema.GameWrapper.addMatchFooters(builder, matchFooters);
    const wrapper = battlecode_schema_1.schema.GameWrapper.endGameWrapper(builder);
    builder.finish(wrapper);
    return builder.asUint8Array();
}
exports.createBenchGame = createBenchGame;
function createWanderGame(unitCount, turns) {
    let builder = new battlecode_schema_1.flatbuffers.Builder();
    let events = [];
    events.push(createEventWrapper(builder, createHeader(builder), battlecode_schema_1.schema.Event.GameHeader));
    let ids = new Array(unitCount);
    let xs = new Array(unitCount);
    let ys = new Array(unitCount);
    let velXs = new Array(unitCount);
    let velYs = new Array(unitCount);
    for (let i = 0; i < ids.length; i++) {
        ids[i] = i;
        xs[i] = i;
        ys[i] = i;
        velXs[i] = 0;
        velYs[i] = 0;
    }
    const locs = createVecTable(builder, xs, ys);
    const initialIDs = battlecode_schema_1.schema.SpawnedBodyTable.createRobotIDsVector(builder, ids);
    battlecode_schema_1.schema.SpawnedBodyTable.startSpawnedBodyTable(builder);
    battlecode_schema_1.schema.SpawnedBodyTable.addLocs(builder, locs);
    battlecode_schema_1.schema.SpawnedBodyTable.addRobotIDs(builder, initialIDs);
    const bodies = battlecode_schema_1.schema.SpawnedBodyTable.endSpawnedBodyTable(builder);
    battlecode_schema_1.schema.GameMap.startGameMap(builder);
    battlecode_schema_1.schema.GameMap.addBodies(builder, bodies);
    battlecode_schema_1.schema.GameMap.addMinCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, 0, 0));
    battlecode_schema_1.schema.GameMap.addMaxCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, SIZE, SIZE));
    const map = battlecode_schema_1.schema.GameMap.endGameMap(builder);
    battlecode_schema_1.schema.MatchHeader.startMatchHeader(builder);
    battlecode_schema_1.schema.MatchHeader.addMaxRounds(builder, turns);
    battlecode_schema_1.schema.MatchHeader.addMap(builder, map);
    events.push(createEventWrapper(builder, battlecode_schema_1.schema.MatchHeader.endMatchHeader(builder), battlecode_schema_1.schema.Event.MatchHeader));
    for (let i = 1; i < turns + 1; i++) {
        for (let i = 0; i < unitCount; i++) {
            const t = Math.random() * Math.PI * 2;
            velXs[i] += Math.cos(t) * .01;
            velYs[i] += Math.sin(t) * .01;
            const totalVel = Math.sqrt(velXs[i] * velXs[i] + velYs[i] * velYs[i]);
            if (totalVel > 1) {
                const velMult = 1 / totalVel;
                velXs[i] *= velMult;
                velYs[i] *= velMult;
            }
            xs[i] += velXs[i];
            ys[i] += velYs[i];
            if (xs[i] < 0) {
                velXs[i] *= -1;
                xs[i] = 0;
            }
            else if (xs[i] > SIZE) {
                velXs[i] *= -1;
                xs[i] = SIZE;
            }
            if (ys[i] < 0) {
                velYs[i] *= -1;
                ys[i] = 0;
            }
            else if (ys[i] > SIZE) {
                velYs[i] *= -1;
                ys[i] = SIZE;
            }
        }
        const movedLocs = createVecTable(builder, xs, ys);
        const movedP = battlecode_schema_1.schema.Round.createMovedIDsVector(builder, ids);
        battlecode_schema_1.schema.Round.startRound(builder);
        battlecode_schema_1.schema.Round.addRoundID(builder, i);
        battlecode_schema_1.schema.Round.addMovedLocs(builder, movedLocs);
        battlecode_schema_1.schema.Round.addMovedIDs(builder, movedP);
        events.push(createEventWrapper(builder, battlecode_schema_1.schema.Round.endRound(builder), battlecode_schema_1.schema.Event.Round));
    }
    battlecode_schema_1.schema.MatchFooter.startMatchFooter(builder);
    battlecode_schema_1.schema.MatchFooter.addWinner(builder, 1);
    battlecode_schema_1.schema.MatchFooter.addTotalRounds(builder, turns);
    events.push(createEventWrapper(builder, battlecode_schema_1.schema.MatchFooter.endMatchFooter(builder), battlecode_schema_1.schema.Event.MatchFooter));
    battlecode_schema_1.schema.GameFooter.startGameFooter(builder);
    battlecode_schema_1.schema.GameFooter.addWinner(builder, 1);
    events.push(createEventWrapper(builder, battlecode_schema_1.schema.GameFooter.endGameFooter(builder), battlecode_schema_1.schema.Event.GameFooter));
    const eventsPacked = battlecode_schema_1.schema.GameWrapper.createEventsVector(builder, events);
    const matchHeaders = battlecode_schema_1.schema.GameWrapper.createMatchHeadersVector(builder, [1]);
    const matchFooters = battlecode_schema_1.schema.GameWrapper.createMatchHeadersVector(builder, [turns + 2]);
    battlecode_schema_1.schema.GameWrapper.startGameWrapper(builder);
    battlecode_schema_1.schema.GameWrapper.addEvents(builder, eventsPacked);
    battlecode_schema_1.schema.GameWrapper.addMatchHeaders(builder, matchHeaders);
    battlecode_schema_1.schema.GameWrapper.addMatchFooters(builder, matchFooters);
    const wrapper = battlecode_schema_1.schema.GameWrapper.endGameWrapper(builder);
    builder.finish(wrapper);
    return builder.asUint8Array();
}
exports.createWanderGame = createWanderGame;
let stream = fs_1.createWriteStream('test.bc20');
stream.write(new Buffer(createBenchGame(128, 64, 128, 4096)));
stream.end();
let stream2 = fs_1.createWriteStream('wander.bc20');
stream2.write(new Buffer(createWanderGame(128, 4096)));
stream2.end();
