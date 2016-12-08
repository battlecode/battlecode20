import {schema, flatbuffers} from 'battlecode-schema';
import * as Map from 'core-js/library/es6/map';
import {createWriteStream} from 'fs';

const SIZE = 50;
const SIZE2 = SIZE / 2;

export function createHeader(builder: flatbuffers.Builder): flatbuffers.Offset {
  const bodies: flatbuffers.Offset[] = [];
  for (const body of [schema.BodyType.ARCHON, schema.BodyType.GARDENER, schema.BodyType.LUMBERJACK, schema.BodyType.RECRUIT, schema.BodyType.SOLDIER, schema.BodyType.TANK, schema.BodyType.SCOUT, schema.BodyType.BULLET, schema.BodyType.TREE_BULLET, schema.BodyType.TREE_NEUTRAL]) {
    schema.BodyTypeMetadata.startBodyTypeMetadata(builder);
    schema.BodyTypeMetadata.addBulletAttack(builder, 1);
    schema.BodyTypeMetadata.addBulletSpeed(builder, 1);
    schema.BodyTypeMetadata.addCost(builder, 100);
    schema.BodyTypeMetadata.addMaxHealth(builder, 100);
    schema.BodyTypeMetadata.addRadius(builder, 1);
    schema.BodyTypeMetadata.addStartHealth(builder, 100);
    schema.BodyTypeMetadata.addStrideRadius(builder, 5);
    schema.BodyTypeMetadata.addType(builder, body);
    bodies.push(schema.BodyTypeMetadata.endBodyTypeMetadata(builder));
  }

  const teams: flatbuffers.Offset[] = [];
  for (let team of [1, 2]) {
    const name = builder.createString('team'+team);
    const packageName = builder.createString('big'+team+'.memes.big.dreams');
    schema.TeamData.startTeamData(builder);
    schema.TeamData.addName(builder, name);
    schema.TeamData.addPackageName(builder, packageName);
    schema.TeamData.addTeamID(builder, team);
    teams.push(schema.TeamData.endTeamData(builder));
  }

  const version = builder.createString('IMAGINARY VERSION!!!');
  const bodiesPacked = schema.GameHeader.createBodyTypeMetadataVector(builder, bodies);
  const teamsPacked = schema.GameHeader.createTeamsVector(builder, teams);

  schema.GameHeader.startGameHeader(builder);
  schema.GameHeader.addSpecVersion(builder, version);
  schema.GameHeader.addBodyTypeMetadata(builder, bodiesPacked);
  schema.GameHeader.addTeams(builder, teamsPacked);
  return schema.GameHeader.endGameHeader(builder);
}

export function createVecTable(builder: flatbuffers.Builder, xs: number[], ys: number[]) {
  const xsP = schema.VecTable.createXsVector(builder, xs);
  const ysP = schema.VecTable.createYsVector(builder, ys);
  schema.VecTable.startVecTable(builder);
  schema.VecTable.addXs(builder, xsP);
  schema.VecTable.addYs(builder, ysP);
  return schema.VecTable.endVecTable(builder);
}

export function createEventWrapper(builder: flatbuffers.Builder, event: flatbuffers.Offset, type: schema.Event): flatbuffers.Offset {
  schema.EventWrapper.startEventWrapper(builder);
  schema.EventWrapper.addEType(builder, type);
  schema.EventWrapper.addE(builder, event);
  return schema.EventWrapper.endEventWrapper(builder);
}

export function createBenchGame(aliveCount: number, churnCount: number, moveCount: number, turns: number) {
  let builder = new flatbuffers.Builder();
  let events: flatbuffers.Offset[] = [];

  events.push(createEventWrapper(builder, createHeader(builder), schema.Event.GameHeader));

  let alive = new Array(aliveCount);
  let xs = new Array(aliveCount);
  let ys = new Array(aliveCount);
  for (let i = 0; i < alive.length; i++) {
    alive[i] = i;
    xs[i] = i;
    ys[i] = i;
  }

  const locs = createVecTable(builder, xs, ys);
  const initialIDs = schema.SpawnedBodyTable.createRobotIDsVector(builder, alive);
  schema.SpawnedBodyTable.startSpawnedBodyTable(builder)
  schema.SpawnedBodyTable.addLocs(builder, locs);
  schema.SpawnedBodyTable.addRobotIDs(builder, initialIDs);
  const bodies = schema.SpawnedBodyTable.endSpawnedBodyTable(builder);

  schema.GameMap.startGameMap(builder);
  schema.GameMap.addBodies(builder, bodies);
  schema.GameMap.addMinCorner(builder, schema.Vec.createVec(builder, 0, 0));
  schema.GameMap.addMaxCorner(builder, schema.Vec.createVec(builder, SIZE, SIZE));
  const map = schema.GameMap.endGameMap(builder);

  schema.MatchHeader.startMatchHeader(builder);
  schema.MatchHeader.addMaxRounds(builder, turns);
  schema.MatchHeader.addMap(builder, map);
  events.push(createEventWrapper(builder, schema.MatchHeader.endMatchHeader(builder), schema.Event.MatchHeader));

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

  for (let i = 1; i < turns+1; i++) {
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
    const diedP = schema.Round.createDiedIDsVector(builder, diedIDs);

    const bornP = schema.SpawnedBodyTable.createRobotIDsVector(builder, bornIDs);
    schema.SpawnedBodyTable.startSpawnedBodyTable(builder);
    schema.SpawnedBodyTable.addLocs(builder, bornLocs);
    schema.SpawnedBodyTable.addRobotIDs(builder, bornP);
    const spawnedP = schema.SpawnedBodyTable.endSpawnedBodyTable(builder);

    const movedP = schema.Round.createMovedIDsVector(builder, movedIDs);

    schema.Round.startRound(builder);
    schema.Round.addRoundID(builder, i);

    schema.Round.addMovedLocs(builder, movedLocs);
    schema.Round.addMovedIDs(builder, movedP);

    schema.Round.addSpawnedBodies(builder, spawnedP);

    schema.Round.addDiedIDs(builder, diedP);

    events.push(createEventWrapper(builder, schema.Round.endRound(builder), schema.Event.Round));
  }

  schema.MatchFooter.startMatchFooter(builder);
  schema.MatchFooter.addWinner(builder, 1);
  schema.MatchFooter.addTotalRounds(builder, turns);
  events.push(createEventWrapper(builder, schema.MatchFooter.endMatchFooter(builder), schema.Event.MatchFooter));

  schema.GameFooter.startGameFooter(builder);
  schema.GameFooter.addWinner(builder, 1);
  events.push(createEventWrapper(builder, schema.GameFooter.endGameFooter(builder), schema.Event.GameFooter));

  const eventsPacked = schema.GameWrapper.createEventsVector(builder, events);
  const matchHeaders = schema.GameWrapper.createMatchHeadersVector(builder, [1]);
  const matchFooters = schema.GameWrapper.createMatchHeadersVector(builder, [turns+2]);
  schema.GameWrapper.startGameWrapper(builder)
  schema.GameWrapper.addEvents(builder, eventsPacked);
  schema.GameWrapper.addMatchHeaders(builder, matchHeaders);
  schema.GameWrapper.addMatchFooters(builder, matchFooters);
  const wrapper = schema.GameWrapper.endGameWrapper(builder);

  builder.finish(wrapper);
  return builder.asUint8Array();
}

export function createWanderGame(unitCount: number, turns: number) {
  let builder = new flatbuffers.Builder();
  let events: flatbuffers.Offset[] = [];

  events.push(createEventWrapper(builder, createHeader(builder), schema.Event.GameHeader));

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
  const initialIDs = schema.SpawnedBodyTable.createRobotIDsVector(builder, ids);
  schema.SpawnedBodyTable.startSpawnedBodyTable(builder)
  schema.SpawnedBodyTable.addLocs(builder, locs);
  schema.SpawnedBodyTable.addRobotIDs(builder, initialIDs);
  const bodies = schema.SpawnedBodyTable.endSpawnedBodyTable(builder);

  schema.GameMap.startGameMap(builder);
  schema.GameMap.addBodies(builder, bodies);
  schema.GameMap.addMinCorner(builder, schema.Vec.createVec(builder, 0, 0));
  schema.GameMap.addMaxCorner(builder, schema.Vec.createVec(builder, SIZE, SIZE));
  const map = schema.GameMap.endGameMap(builder);

  schema.MatchHeader.startMatchHeader(builder);
  schema.MatchHeader.addMaxRounds(builder, turns);
  schema.MatchHeader.addMap(builder, map);
  events.push(createEventWrapper(builder, schema.MatchHeader.endMatchHeader(builder), schema.Event.MatchHeader));

  for (let i = 1; i < turns+1; i++) {
    for (let i = 0; i < unitCount; i++) {
      const t = Math.random() * Math.PI * 2;
      velXs[i] += Math.cos(t) * .01;
      velYs[i] += Math.sin(t) * .01;
      const totalVel = Math.sqrt(velXs[i]*velXs[i] + velYs[i]*velYs[i]);
      if (totalVel > 1) {
        const velMult = 1/totalVel;
        velXs[i] *= velMult;
        velYs[i] *= velMult;
      }
      xs[i] += velXs[i];
      ys[i] += velYs[i];
      if (xs[i] < 0) {
        velXs[i] *= -1;
        xs[i] = 0;
      } else if (xs[i] > SIZE) {
        velXs[i] *= -1;
        xs[i] = SIZE;
      }
      if (ys[i] < 0) {
        velYs[i] *= -1;
        ys[i] = 0;
      } else if (ys[i] > SIZE) {
        velYs[i] *= -1;
        ys[i] = SIZE;
      }
    }
    const movedLocs = createVecTable(builder, xs, ys);

    const movedP = schema.Round.createMovedIDsVector(builder, ids);

    schema.Round.startRound(builder);
    schema.Round.addRoundID(builder, i);

    schema.Round.addMovedLocs(builder, movedLocs);
    schema.Round.addMovedIDs(builder, movedP);

    events.push(createEventWrapper(builder, schema.Round.endRound(builder), schema.Event.Round));
  }

  schema.MatchFooter.startMatchFooter(builder);
  schema.MatchFooter.addWinner(builder, 1);
  schema.MatchFooter.addTotalRounds(builder, turns);
  events.push(createEventWrapper(builder, schema.MatchFooter.endMatchFooter(builder), schema.Event.MatchFooter));

  schema.GameFooter.startGameFooter(builder);
  schema.GameFooter.addWinner(builder, 1);
  events.push(createEventWrapper(builder, schema.GameFooter.endGameFooter(builder), schema.Event.GameFooter));

  const eventsPacked = schema.GameWrapper.createEventsVector(builder, events);
  const matchHeaders = schema.GameWrapper.createMatchHeadersVector(builder, [1]);
  const matchFooters = schema.GameWrapper.createMatchHeadersVector(builder, [turns+2]);
  schema.GameWrapper.startGameWrapper(builder)
  schema.GameWrapper.addEvents(builder, eventsPacked);
  schema.GameWrapper.addMatchHeaders(builder, matchHeaders);
  schema.GameWrapper.addMatchFooters(builder, matchFooters);
  const wrapper = schema.GameWrapper.endGameWrapper(builder);

  builder.finish(wrapper);
  return builder.asUint8Array();
}
let stream = createWriteStream('test.bc17');
stream.write(new Buffer(createBenchGame(128, 64, 128, 4096)));
stream.end();
let stream2 = createWriteStream('wander.bc17');
stream2.write(new Buffer(createWanderGame(128, 4096)));
stream2.end();
