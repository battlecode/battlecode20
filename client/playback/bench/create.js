"use strict";
exports.__esModule = true;
var battlecode_schema_1 = require("battlecode-schema");
var fs_1 = require("fs");
var SIZE = 50;
var SIZE2 = SIZE / 2;
function createHeader(builder) {
    var bodies = [];
    for (var _i = 0, _a = [battlecode_schema_1.schema.BodyType.ARCHON, battlecode_schema_1.schema.BodyType.GARDENER, battlecode_schema_1.schema.BodyType.LUMBERJACK, battlecode_schema_1.schema.BodyType.SOLDIER, battlecode_schema_1.schema.BodyType.TANK, battlecode_schema_1.schema.BodyType.SCOUT, battlecode_schema_1.schema.BodyType.BULLET, battlecode_schema_1.schema.BodyType.TREE_BULLET, battlecode_schema_1.schema.BodyType.TREE_NEUTRAL]; _i < _a.length; _i++) {
        var body = _a[_i];
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
    var teams = [];
    for (var _b = 0, _c = [1, 2]; _b < _c.length; _b++) {
        var team = _c[_b];
        var name_1 = builder.createString('team' + team);
        var packageName = builder.createString('big' + team + '.memes.big.dreams');
        battlecode_schema_1.schema.TeamData.startTeamData(builder);
        battlecode_schema_1.schema.TeamData.addName(builder, name_1);
        battlecode_schema_1.schema.TeamData.addPackageName(builder, packageName);
        battlecode_schema_1.schema.TeamData.addTeamID(builder, team);
        teams.push(battlecode_schema_1.schema.TeamData.endTeamData(builder));
    }
    var version = builder.createString('IMAGINARY VERSION!!!');
    var bodiesPacked = battlecode_schema_1.schema.GameHeader.createBodyTypeMetadataVector(builder, bodies);
    var teamsPacked = battlecode_schema_1.schema.GameHeader.createTeamsVector(builder, teams);
    battlecode_schema_1.schema.GameHeader.startGameHeader(builder);
    battlecode_schema_1.schema.GameHeader.addSpecVersion(builder, version);
    battlecode_schema_1.schema.GameHeader.addBodyTypeMetadata(builder, bodiesPacked);
    battlecode_schema_1.schema.GameHeader.addTeams(builder, teamsPacked);
    return battlecode_schema_1.schema.GameHeader.endGameHeader(builder);
}
exports.createHeader = createHeader;
function createVecTable(builder, xs, ys) {
    var xsP = battlecode_schema_1.schema.VecTable.createXsVector(builder, xs);
    var ysP = battlecode_schema_1.schema.VecTable.createYsVector(builder, ys);
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
    var builder = new battlecode_schema_1.flatbuffers.Builder();
    var events = [];
    events.push(createEventWrapper(builder, createHeader(builder), battlecode_schema_1.schema.Event.GameHeader));
    var alive = new Array(aliveCount);
    var xs = new Array(aliveCount);
    var ys = new Array(aliveCount);
    for (var i = 0; i < alive.length; i++) {
        alive[i] = i;
        xs[i] = i;
        ys[i] = i;
    }
    var locs = createVecTable(builder, xs, ys);
    var initialIDs = battlecode_schema_1.schema.SpawnedBodyTable.createRobotIDsVector(builder, alive);
    battlecode_schema_1.schema.SpawnedBodyTable.startSpawnedBodyTable(builder);
    battlecode_schema_1.schema.SpawnedBodyTable.addLocs(builder, locs);
    battlecode_schema_1.schema.SpawnedBodyTable.addRobotIDs(builder, initialIDs);
    var bodies = battlecode_schema_1.schema.SpawnedBodyTable.endSpawnedBodyTable(builder);
    battlecode_schema_1.schema.GameMap.startGameMap(builder);
    battlecode_schema_1.schema.GameMap.addBodies(builder, bodies);
    battlecode_schema_1.schema.GameMap.addMinCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, 0, 0));
    battlecode_schema_1.schema.GameMap.addMaxCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, SIZE, SIZE));
    var map = battlecode_schema_1.schema.GameMap.endGameMap(builder);
    battlecode_schema_1.schema.MatchHeader.startMatchHeader(builder);
    battlecode_schema_1.schema.MatchHeader.addMaxRounds(builder, turns);
    battlecode_schema_1.schema.MatchHeader.addMap(builder, map);
    events.push(createEventWrapper(builder, battlecode_schema_1.schema.MatchHeader.endMatchHeader(builder), battlecode_schema_1.schema.Event.MatchHeader));
    var diedIDs = new Array(churnCount);
    var bornIDs = new Array(churnCount);
    var bornXs = new Array(churnCount);
    var bornYs = new Array(churnCount);
    var movedIDs = new Array(moveCount);
    var movedXs = new Array(moveCount);
    var movedYs = new Array(moveCount);
    var nextID = aliveCount;
    for (var i = 0; i < churnCount; i++) {
        bornXs[i] = i;
        bornYs[i] = i;
    }
    var bornLocs = createVecTable(builder, bornXs, bornYs);
    for (var i = 1; i < turns + 1; i++) {
        for (var j = 0; j < churnCount; j++) {
            diedIDs[j] = alive[j];
            bornIDs[j] = nextID++;
            alive.push(bornIDs[j]);
        }
        alive.splice(0, churnCount);
        for (var i_1 = 0; i_1 < moveCount; i_1++) {
            var t = Math.random() * Math.PI * 2;
            movedXs[i_1] = SIZE2 + Math.cos(t) * SIZE2;
            movedYs[i_1] = SIZE2 + Math.sin(t) * SIZE2;
        }
        var movedLocs = createVecTable(builder, movedXs, movedYs);
        for (var j = 0; j < moveCount; j++) {
            movedIDs[j] = alive[j];
        }
        var diedP = battlecode_schema_1.schema.Round.createDiedIDsVector(builder, diedIDs);
        var bornP = battlecode_schema_1.schema.SpawnedBodyTable.createRobotIDsVector(builder, bornIDs);
        battlecode_schema_1.schema.SpawnedBodyTable.startSpawnedBodyTable(builder);
        battlecode_schema_1.schema.SpawnedBodyTable.addLocs(builder, bornLocs);
        battlecode_schema_1.schema.SpawnedBodyTable.addRobotIDs(builder, bornP);
        var spawnedP = battlecode_schema_1.schema.SpawnedBodyTable.endSpawnedBodyTable(builder);
        var movedP = battlecode_schema_1.schema.Round.createMovedIDsVector(builder, movedIDs);
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
    var eventsPacked = battlecode_schema_1.schema.GameWrapper.createEventsVector(builder, events);
    var matchHeaders = battlecode_schema_1.schema.GameWrapper.createMatchHeadersVector(builder, [1]);
    var matchFooters = battlecode_schema_1.schema.GameWrapper.createMatchHeadersVector(builder, [turns + 2]);
    battlecode_schema_1.schema.GameWrapper.startGameWrapper(builder);
    battlecode_schema_1.schema.GameWrapper.addEvents(builder, eventsPacked);
    battlecode_schema_1.schema.GameWrapper.addMatchHeaders(builder, matchHeaders);
    battlecode_schema_1.schema.GameWrapper.addMatchFooters(builder, matchFooters);
    var wrapper = battlecode_schema_1.schema.GameWrapper.endGameWrapper(builder);
    builder.finish(wrapper);
    return builder.asUint8Array();
}
exports.createBenchGame = createBenchGame;
function createWanderGame(unitCount, turns) {
    var builder = new battlecode_schema_1.flatbuffers.Builder();
    var events = [];
    events.push(createEventWrapper(builder, createHeader(builder), battlecode_schema_1.schema.Event.GameHeader));
    var ids = new Array(unitCount);
    var xs = new Array(unitCount);
    var ys = new Array(unitCount);
    var velXs = new Array(unitCount);
    var velYs = new Array(unitCount);
    for (var i = 0; i < ids.length; i++) {
        ids[i] = i;
        xs[i] = i;
        ys[i] = i;
        velXs[i] = 0;
        velYs[i] = 0;
    }
    var locs = createVecTable(builder, xs, ys);
    var initialIDs = battlecode_schema_1.schema.SpawnedBodyTable.createRobotIDsVector(builder, ids);
    battlecode_schema_1.schema.SpawnedBodyTable.startSpawnedBodyTable(builder);
    battlecode_schema_1.schema.SpawnedBodyTable.addLocs(builder, locs);
    battlecode_schema_1.schema.SpawnedBodyTable.addRobotIDs(builder, initialIDs);
    var bodies = battlecode_schema_1.schema.SpawnedBodyTable.endSpawnedBodyTable(builder);
    battlecode_schema_1.schema.GameMap.startGameMap(builder);
    battlecode_schema_1.schema.GameMap.addBodies(builder, bodies);
    battlecode_schema_1.schema.GameMap.addMinCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, 0, 0));
    battlecode_schema_1.schema.GameMap.addMaxCorner(builder, battlecode_schema_1.schema.Vec.createVec(builder, SIZE, SIZE));
    var map = battlecode_schema_1.schema.GameMap.endGameMap(builder);
    battlecode_schema_1.schema.MatchHeader.startMatchHeader(builder);
    battlecode_schema_1.schema.MatchHeader.addMaxRounds(builder, turns);
    battlecode_schema_1.schema.MatchHeader.addMap(builder, map);
    events.push(createEventWrapper(builder, battlecode_schema_1.schema.MatchHeader.endMatchHeader(builder), battlecode_schema_1.schema.Event.MatchHeader));
    for (var i = 1; i < turns + 1; i++) {
        for (var i_2 = 0; i_2 < unitCount; i_2++) {
            var t = Math.random() * Math.PI * 2;
            velXs[i_2] += Math.cos(t) * .01;
            velYs[i_2] += Math.sin(t) * .01;
            var totalVel = Math.sqrt(velXs[i_2] * velXs[i_2] + velYs[i_2] * velYs[i_2]);
            if (totalVel > 1) {
                var velMult = 1 / totalVel;
                velXs[i_2] *= velMult;
                velYs[i_2] *= velMult;
            }
            xs[i_2] += velXs[i_2];
            ys[i_2] += velYs[i_2];
            if (xs[i_2] < 0) {
                velXs[i_2] *= -1;
                xs[i_2] = 0;
            }
            else if (xs[i_2] > SIZE) {
                velXs[i_2] *= -1;
                xs[i_2] = SIZE;
            }
            if (ys[i_2] < 0) {
                velYs[i_2] *= -1;
                ys[i_2] = 0;
            }
            else if (ys[i_2] > SIZE) {
                velYs[i_2] *= -1;
                ys[i_2] = SIZE;
            }
        }
        var movedLocs = createVecTable(builder, xs, ys);
        var movedP = battlecode_schema_1.schema.Round.createMovedIDsVector(builder, ids);
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
    var eventsPacked = battlecode_schema_1.schema.GameWrapper.createEventsVector(builder, events);
    var matchHeaders = battlecode_schema_1.schema.GameWrapper.createMatchHeadersVector(builder, [1]);
    var matchFooters = battlecode_schema_1.schema.GameWrapper.createMatchHeadersVector(builder, [turns + 2]);
    battlecode_schema_1.schema.GameWrapper.startGameWrapper(builder);
    battlecode_schema_1.schema.GameWrapper.addEvents(builder, eventsPacked);
    battlecode_schema_1.schema.GameWrapper.addMatchHeaders(builder, matchHeaders);
    battlecode_schema_1.schema.GameWrapper.addMatchFooters(builder, matchFooters);
    var wrapper = battlecode_schema_1.schema.GameWrapper.endGameWrapper(builder);
    builder.finish(wrapper);
    return builder.asUint8Array();
}
exports.createWanderGame = createWanderGame;
var stream = fs_1.createWriteStream('test.bc20');
stream.write(new Buffer(createBenchGame(128, 64, 128, 4096)));
stream.end();
var stream2 = fs_1.createWriteStream('wander.bc20');
stream2.write(new Buffer(createWanderGame(128, 4096)));
stream2.end();
