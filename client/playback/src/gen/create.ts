import {schema, flatbuffers} from 'battlecode-schema';
import * as Map from 'core-js/library/es6/map';
import {createWriteStream} from 'fs';
import {gzip} from 'pako';
import { isNull } from 'util';

const SIZE = 50;
const SIZE2 = SIZE / 2;

const bodyTypeList = [
  schema.BodyType.MINER,
  schema.BodyType.LANDSCAPER,
  schema.BodyType.DRONE,
  schema.BodyType.NET_GUN,
  schema.BodyType.COW,
  schema.BodyType.REFINERY,
  schema.BodyType.VAPORATOR,
  schema.BodyType.HQ,
  schema.BodyType.DESIGN_SCHOOL,
  schema.BodyType.FULFILLMENT_CENTER,
  schema.BodyType.NONE
];
const bodyVariety = bodyTypeList.length;

function random(l: number, r: number): number{
  if(l>r){ console.log("Wrong call of random"); return -1; }
  return Math.floor(Math.random() * (r-l+1)) + l;
}
function trimEdge(x: number, l: number, r: number): number{
  return Math.min(Math.max(l, x), r);
}


export function createEventWrapper(builder: flatbuffers.Builder, event: flatbuffers.Offset, type: schema.Event): flatbuffers.Offset {
  schema.EventWrapper.startEventWrapper(builder);
  schema.EventWrapper.addEType(builder, type);
  schema.EventWrapper.addE(builder, event);
  return schema.EventWrapper.endEventWrapper(builder);
}

export function createVecTable(builder: flatbuffers.Builder, xs: number[], ys: number[]) {
  const xsP = schema.VecTable.createXsVector(builder, xs);
  const ysP = schema.VecTable.createYsVector(builder, ys);
  schema.VecTable.startVecTable(builder);
  schema.VecTable.addXs(builder, xsP);
  schema.VecTable.addYs(builder, ysP);
  return schema.VecTable.endVecTable(builder);
}

export function createMap(builder: flatbuffers.Builder, bodies: number, name: string): flatbuffers.Offset {
  const bb_name = builder.createString(name);

  // all values default to zero
  // TODO: test with nonzero values?
  const bb_dirt = schema.GameMap.createDirtVector(builder, new Array(SIZE*SIZE));
  const bb_water = schema.GameMap.createWaterVector(builder, new Array(SIZE*SIZE));
  const bb_poll = schema.GameMap.createPollutionVector(builder, new Array(SIZE*SIZE));
  const bb_soup = schema.GameMap.createSoupVector(builder, new Array(SIZE*SIZE));

  schema.GameMap.startGameMap(builder);
  schema.GameMap.addName(builder, bb_name);

  schema.GameMap.addMinCorner(builder, schema.Vec.createVec(builder, 0, 0));
  schema.GameMap.addMaxCorner(builder, schema.Vec.createVec(builder, SIZE, SIZE));

  if(!isNull(bodies)) schema.GameMap.addBodies(builder, bodies);
  schema.GameMap.addRandomSeed(builder, 42);

  schema.GameMap.addDirt(builder, bb_dirt);
  schema.GameMap.addWater(builder, bb_water);
  schema.GameMap.addPollution(builder, bb_poll);
  schema.GameMap.addSoup(builder, bb_soup);

  return schema.GameMap.endGameMap(builder);
}


export function createGameHeader(builder: flatbuffers.Builder): flatbuffers.Offset {
  const bodies: flatbuffers.Offset[] = [];
  // what's the default value?
  // Is there any way to automate this?
  for (const body of bodyTypeList) {
    const btmd = schema.BodyTypeMetadata;
    btmd.startBodyTypeMetadata(builder);
    btmd.addType(builder, body);
    btmd.addSpawnSource(builder, body); // Real spawn source?
    btmd.addCost(builder, 100);
    btmd.addDirtLimit(builder, 10);
    btmd.addSoupLimit(builder, 100);
    btmd.addActionCooldown(builder, 10);
    btmd.addSensorRadius(builder, 3);
    btmd.addPollutionRadius(builder, 3);
    btmd.addPollutionAmount(builder, 1);
    btmd.addMaxSoupProduced(builder, 0);
    btmd.addBytecodeLimit(builder, 1000);
    bodies.push(schema.BodyTypeMetadata.endBodyTypeMetadata(builder));
  }

  const teams: flatbuffers.Offset[] = [];
  for (let team of [1, 2]) {
    const name = builder.createString('team '+team);
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

export function createGameFooter(builder: flatbuffers.Builder, winner: number): flatbuffers.Offset {
  schema.GameFooter.startGameFooter(builder);
  schema.GameFooter.addWinner(builder, winner);
  return schema.GameFooter.endGameFooter(builder);
}

export function createMatchHeader(builder: flatbuffers.Builder, turns: number, map: number): flatbuffers.Offset {
  schema.MatchHeader.startMatchHeader(builder);
  schema.MatchHeader.addMaxRounds(builder, turns);
  schema.MatchHeader.addMap(builder, map);

  return schema.MatchHeader.endMatchHeader(builder);
}

export function createMatchFooter(builder: flatbuffers.Builder, turns: number, winner: number): flatbuffers.Offset {
  schema.MatchFooter.startMatchFooter(builder);
  schema.MatchFooter.addWinner(builder, winner);
  schema.MatchFooter.addTotalRounds(builder, turns);

  return schema.MatchFooter.endMatchFooter(builder);
}

export function createGameWrapper(builder: flatbuffers.Builder, events: flatbuffers.Offset[], turns: number): flatbuffers.Offset {
  const eventsPacked = schema.GameWrapper.createEventsVector(builder, events);
  const matchHeaders = schema.GameWrapper.createMatchHeadersVector(builder, [1]);
  const matchFooters = schema.GameWrapper.createMatchFootersVector(builder, [turns+2]);
  schema.GameWrapper.startGameWrapper(builder)
  schema.GameWrapper.addEvents(builder, eventsPacked);
  schema.GameWrapper.addMatchHeaders(builder, matchHeaders);
  schema.GameWrapper.addMatchFooters(builder, matchFooters);
  return schema.GameWrapper.endGameWrapper(builder);
}

// Game without any unit & changes
export function createBlankGame(turns: number){
  let builder = new flatbuffers.Builder();
  let events: flatbuffers.Offset[] = [];

  events.push(createEventWrapper(builder, createGameHeader(builder), schema.Event.GameHeader));

  const map = createMap(builder, null, 'Blank Demo');
  events.push(createEventWrapper(builder, createMatchHeader(builder, turns, map), schema.Event.MatchHeader));

  for (let i = 1; i < turns+1; i++) {
    schema.Round.startRound(builder);
    schema.Round.addRoundID(builder, i);

    events.push(createEventWrapper(builder, schema.Round.endRound(builder), schema.Event.Round));
  }

  events.push(createEventWrapper(builder, createMatchFooter(builder, turns, 1), schema.Event.MatchFooter));
  events.push(createEventWrapper(builder, createGameFooter(builder, 1), schema.Event.GameFooter));

  const wrapper = createGameWrapper(builder, events, turns);
  builder.finish(wrapper);
  return builder.asUint8Array();
}

// Game with every units, without any changes
export function createStandGame(turns: number) {
  let builder = new flatbuffers.Builder();
  let events: flatbuffers.Offset[] = [];

  events.push(createEventWrapper(builder, createGameHeader(builder), schema.Event.GameHeader));

  const unitCount = bodyVariety * 2 + 2;
  let robotIDs = new Array(unitCount);
  let teamIDs = new Array(unitCount);
  let types = new Array(unitCount);
  let xs = new Array(unitCount);
  let ys = new Array(unitCount);
  for (let i = 0; i < unitCount; i++) {
    robotIDs[i] = i;
    teamIDs[i] = i%2+1; // 1 2 1 2 1 2 ...

    let type = Math.floor(i/2);
    if(type>=bodyVariety) type = schema.BodyType.DRONE;
    else type = bodyTypeList[type];
    types[i] = type;

    // assume map is large enough
    xs[i] = Math.floor(i/2) * 2 + 5;
    ys[i] = 5*(i%2)+5;
  }

  const bb_locs = createVecTable(builder, xs, ys);
  const bb_robotIDs = schema.SpawnedBodyTable.createRobotIDsVector(builder, robotIDs);
  const bb_teamIDs = schema.SpawnedBodyTable.createTeamIDsVector(builder, teamIDs);
  const bb_types = schema.SpawnedBodyTable.createTypesVector(builder, types);
  schema.SpawnedBodyTable.startSpawnedBodyTable(builder)
  schema.SpawnedBodyTable.addLocs(builder, bb_locs);
  schema.SpawnedBodyTable.addRobotIDs(builder, bb_robotIDs);
  schema.SpawnedBodyTable.addTeamIDs(builder, bb_teamIDs);
  schema.SpawnedBodyTable.addTypes(builder, bb_types);
  const bodies = schema.SpawnedBodyTable.endSpawnedBodyTable(builder);

  const map = createMap(builder, bodies, "Stand Demo");
  events.push(createEventWrapper(builder, createMatchHeader(builder, turns, map), schema.Event.MatchHeader));

  for (let i = 1; i < turns+1; i++) {
    schema.Round.startRound(builder);
    schema.Round.addRoundID(builder, i);

    events.push(createEventWrapper(builder, schema.Round.endRound(builder), schema.Event.Round));
  }

  events.push(createEventWrapper(builder, createMatchFooter(builder, turns, 1), schema.Event.MatchFooter));
  events.push(createEventWrapper(builder, createGameFooter(builder, 1), schema.Event.GameFooter));

  const wrapper = createGameWrapper(builder, events, turns);
  builder.finish(wrapper);
  return builder.asUint8Array();
}

// Game with every units, moving in random constant speed & direction
export function createWanderGame(unitCount: number, turns: number) {
  let builder = new flatbuffers.Builder();
  let events: flatbuffers.Offset[] = [];

  events.push(createEventWrapper(builder, createGameHeader(builder), schema.Event.GameHeader));

  let robotIDs = new Array(unitCount);
  let teamIDs = new Array(unitCount);
  let types = new Array(unitCount);
  let xs = new Array(unitCount);
  let ys = new Array(unitCount);
  let velxs = new Array(unitCount);
  let velys = new Array(unitCount);
  for (let i = 0; i < robotIDs.length; i++) {
    robotIDs[i] = i;
    teamIDs[i] = i%2+1;
    types[i] = bodyTypeList[random(0, bodyVariety)];
    xs[i] = random(0, SIZE);
    ys[i] = random(0, SIZE);
    velxs[i] = random(-1, 1);
    velys[i] = random(-1, 1);
  }

  const bb_locs = createVecTable(builder, xs, ys);
  const bb_robotIDs = schema.SpawnedBodyTable.createRobotIDsVector(builder, robotIDs);
  const bb_teamIDs = schema.SpawnedBodyTable.createTeamIDsVector(builder, teamIDs);
  const bb_types = schema.SpawnedBodyTable.createTypesVector(builder, types);
  schema.SpawnedBodyTable.startSpawnedBodyTable(builder)
  schema.SpawnedBodyTable.addLocs(builder, bb_locs);
  schema.SpawnedBodyTable.addRobotIDs(builder, bb_robotIDs);
  schema.SpawnedBodyTable.addTeamIDs(builder, bb_teamIDs);
  schema.SpawnedBodyTable.addTypes(builder, bb_types);
  const bodies = schema.SpawnedBodyTable.endSpawnedBodyTable(builder);

  const map = createMap(builder, bodies, 'Wander Demo');
  events.push(createEventWrapper(builder, createMatchHeader(builder, turns, map), schema.Event.MatchHeader));

  for (let i = 1; i < turns+1; i++) {
    for (let i = 0; i < unitCount; i++) {
      if(random(0, 32) === 0){
        velxs[i] = random(-1, 1);
        velys[i] = random(-1, 1);
      }
      xs[i] = trimEdge(xs[i] + velxs[i], 0, SIZE);
      ys[i] = trimEdge(ys[i] + velys[i], 0, SIZE);
      if(xs[i] === 0 || xs[i] == SIZE) velxs[i] = -velxs[i];
      if(ys[i] === 0 || ys[i] == SIZE) velys[i] = -velys[i];
    }
    const movedLocs = createVecTable(builder, xs, ys);

    const movedP = schema.Round.createMovedIDsVector(builder, robotIDs);

    schema.Round.startRound(builder);
    schema.Round.addRoundID(builder, i);

    schema.Round.addMovedLocs(builder, movedLocs);
    schema.Round.addMovedIDs(builder, movedP);

    events.push(createEventWrapper(builder, schema.Round.endRound(builder), schema.Event.Round));
  }

  events.push(createEventWrapper(builder, createMatchFooter(builder, turns, 1), schema.Event.MatchFooter));
  events.push(createEventWrapper(builder, createGameFooter(builder, 1), schema.Event.GameFooter));

  const wrapper = createGameWrapper(builder, events, turns);
  builder.finish(wrapper);
  return builder.asUint8Array();
}

// Game with every units, dies and borns randomly, and moving in random direction
export function createActiveGame(aliveCount: number, churnCount: number, moveCount: number, turns: number) {
  let builder = new flatbuffers.Builder();
  let events: flatbuffers.Offset[] = [];

  events.push(createEventWrapper(builder, createGameHeader(builder), schema.Event.GameHeader));

  let robotIDs = new Array(aliveCount);
  let teamIDs = new Array(aliveCount);
  let types = new Array(aliveCount);
  let xs = new Array(aliveCount);
  let ys = new Array(aliveCount);
  for (let i = 0; i < robotIDs.length; i++) {
    robotIDs[i] = i;
    teamIDs[i] = i%2+1;
    types[i] = 1;
    xs[i] = i;
    ys[i] = i;
  }

  const bb_locs = createVecTable(builder, xs, ys);
  const bb_robotIDs = schema.SpawnedBodyTable.createRobotIDsVector(builder, robotIDs);
  const bb_teamIDs = schema.SpawnedBodyTable.createTeamIDsVector(builder, teamIDs);
  const bb_types = schema.SpawnedBodyTable.createTypesVector(builder, types);
  schema.SpawnedBodyTable.startSpawnedBodyTable(builder)
  schema.SpawnedBodyTable.addLocs(builder, bb_locs);
  schema.SpawnedBodyTable.addRobotIDs(builder, bb_robotIDs);
  schema.SpawnedBodyTable.addTeamIDs(builder, bb_teamIDs);
  schema.SpawnedBodyTable.addTypes(builder, bb_types);
  const bodies = schema.SpawnedBodyTable.endSpawnedBodyTable(builder);

  const map = createMap(builder, bodies, 'Active Demo');
  events.push(createEventWrapper(builder, createMatchHeader(builder, turns, map), schema.Event.MatchHeader));

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
      diedIDs[j] = robotIDs[j];
      bornIDs[j] = nextID++;
      robotIDs.push(bornIDs[j]);
    }
    robotIDs.splice(0, churnCount);

    for (let i = 0; i < moveCount; i++) {
      // TODO: change to discrete?
      // const t = Math.random() * Math.PI * 2;
      // movedXs[i] = SIZE2 + Math.cos(t) * SIZE2;
      // movedYs[i] = SIZE2 + Math.sin(t) * SIZE2;
      movedXs[i] = Math.round(Math.random()*3)-1;
      movedYs[i] = Math.round(Math.random()*3)-1;
    }
    const movedLocs = createVecTable(builder, movedXs, movedYs);

    for (let j = 0; j < moveCount; j++) {
      movedIDs[j] = robotIDs[j];
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

  events.push(createEventWrapper(builder, createMatchFooter(builder, turns, 1), schema.Event.MatchFooter));
  events.push(createEventWrapper(builder, createGameFooter(builder, 1), schema.Event.GameFooter));

  const wrapper = createGameWrapper(builder, events, turns);
  builder.finish(wrapper);
  return builder.asUint8Array();
}

function main(){
  const games = [
    { name: "blank", game: createBlankGame(512)},
    { name: "stand", game: createStandGame(1024) },
    { name: "wander", game: createWanderGame(64, 2048) },
    // { name: "active", game: createActiveGame(128, 128, 128, 4096) },
  ];
  const prefix = "out/files/";

  games.forEach(pair => {
    const filename = `${prefix}${pair.name}.bc20`
    const stream = createWriteStream(filename);
    const game = pair.game;

    console.log(`Writing file to ${filename} ...`);

    if(!game){
      console.log(`Error making ${pair.name}!!`);
    }
    else{
      stream.write(Buffer.from(gzip(game)));
      console.log(`Generated ${pair.name} successfully!\n`)
    }
  });
  console.log(`Finished generating files!`);
}

main();
