var b = require('./battlecode_generated').battlecode.schema;
var flatbuffers = require('./flatbuffers').flatbuffers;
var fs = require('fs');

/* Create circle map with radius, name, and a single team-1 robot of radius 1 at the center. */
function mapLargeCircle(builder, name, radius) {
  // Create the boundary of the map.
  b.CircleBounds.startCircleBounds(builder);
  b.CircleBounds.addRadius(builder, radius);
  // This value is the offset of the CircleBounds table in the builder's buffer.
  var bounds = b.CircleBounds.endCircleBounds(builder);

  // Create the robot at the center of the map.
  // Loc and Vel will be 0 by default 
  b.SpawnedBody.startSpawnedBody(builder);
  b.SpawnedBody.addType(builder, b.BodyType.ROBOT);
  b.SpawnedBody.addRadius(builder, 1);
  b.SpawnedBody.addTeamID(builder, 1);
  b.SpawnedBody.addRobotID(builder, 1);
  var centerRobot = b.SpawnedBody.endSpawnedBody(builder);

  // Encode the name of the map.
  var nameOffset = builder.createString(name);

  // Create the list of bodies that start on the map.
  // Note that this is a Vector in the sense of a list of elements, not a 2-d Vec.
  var bodiesVector = b.Map.createBodiesVector(builder, [centerRobot]);

  // Create the actual map structure, pointing to its children.
  b.Map.startMap(builder);
  b.Map.addName(builder, nameOffset);
  b.Map.addBounds(builder, bounds);
  b.Map.addBoundsType(builder, b.Bounds.CircleBounds);
  b.Map.addBodies(builder, bodiesVector);
  return b.Map.endMap(builder);
}

/*
 * Wrap an Event union element in an EventWrapper struct.
 * Slightly awkward, but necessary due to the way FlatBuffers handles unions
 * (with a tag in the containing table)
 */
function wrapEvent(builder, event, eventType) {
  b.EventWrapper.startEventWrapper(builder);
  b.EventWrapper.addE(builder, event);
  b.EventWrapper.addEType(builder, eventType);
  return b.EventWrapper.endEventWrapper(builder);
}

/* Create a match with lots of shooting bullets. Returns a list of event offsets. */
function matchBulletsRandom(builder) {
  // We need to keep track of the event indices in a temporary buffer.
  var events = [];

  // Create our map using the mapLargeCircle helper function.
  var map = mapLargeCircle(builder, 'random bullets map!', 2000);

  // Create a match header.
  b.MatchHeader.startMatchHeader(builder);
  b.MatchHeader.addMap(builder, map);
  b.MatchHeader.addMaxRounds(builder, 1000);

  // Wrap it with an EventWrapper, and push the EventWrapper's address to the list of events.
  events.push(
    wrapEvent(builder, b.MatchHeader.endMatchHeader(builder), b.Event.MatchHeader)
  );

  for (var i = 0; i < 1000; i++) {
    // The angle our bullet will be shooting in.
    var theta = Math.random() * Math.PI;

    // The SpawnedBody representing our bullet.
    b.SpawnedBody.startSpawnedBody(builder);
    b.SpawnedBody.addType(builder, b.BodyType.BULLET);
    b.SpawnedBody.addRadius(builder, 1);
    b.SpawnedBody.addRobotID(builder, i+1);
    // Vecs are structs, not tables. They are encoded in a single step.
    // The bullet should start at distance (radius_robot + radius_bullet) = 1
    b.SpawnedBody.addLoc(builder, b.Vec.createVec(builder, Math.cos(theta), Math.sin(theta)));
    b.SpawnedBody.addVel(builder, b.Vec.createVec(builder, Math.cos(theta), Math.sin(theta)));
    b.SpawnedBody.addTeamID(builder, 1);
    var body = b.SpawnedBody.endSpawnedBody(builder);

    // There was only one element spawned this turn.
    var spawnedVector = b.Round.createSpawnedVector(builder, [body]);

    // Create the Round object.
    b.Round.startRound(builder);
    b.Round.addNumber(builder, i);
    b.Round.addSpawned(builder, spawnedVector);
    events.push(wrapEvent(builder, b.Round.endRound(builder), b.Event.Round));
  }

  // The match ends with a MatchFooter
  b.MatchFooter.startMatchFooter(builder);
  b.MatchFooter.addTotalRounds(builder, 1000);
  b.MatchFooter.addWinner(builder, 1);
  events.push(wrapEvent(builder, b.MatchFooter.endMatchFooter(builder), b.Event.MatchFooter));

  return events;
}

/**
 * Create a game with a simple game wrapper. The game will consist of one
 * match, created by the callback matchCreateFunction, which should return a
 * list of EventWrapper offsets.
 */
function createGameWrapper(builder, matchCreateFunction) {
  // Build our GameHeader.
  var teamName = builder.createString('teh devs, probably');
  var teamPackage = builder.createString('this.is.not.a.real.package');

  b.TeamData.startTeamData(builder);
  b.TeamData.addTeamID(builder, 1);
  b.TeamData.addName(builder, teamName);
  b.TeamData.addPackage(builder, teamPackage);
  var singleTeam = b.TeamData.endTeamData(builder);

  var teamsVector = b.GameHeader.createTeamsVector(builder, [singleTeam]);

  b.GameHeader.startGameHeader(builder);
  b.GameHeader.addTeams(builder, teamsVector);
  var header = wrapEvent(builder, b.GameHeader.endGameHeader(builder), b.Event.GameHeader);

  b.GameFooter.startGameFooter(builder);
  b.GameFooter.addWinner(builder, 1);
  var footer = wrapEvent(builder, b.GameFooter.endGameFooter(builder), b.Event.GameFooter);

  // Create the events of the match using the passed callback.
  var matchEventOffsets = matchCreateFunction(builder);

  matchEventOffsets.unshift(header);
  matchEventOffsets.push(footer);

  // Create the vector of Event[Wrapper]s that make up the meat of the file.
  var eventsVector = b.GameWrapper.createEventsVector(builder, matchEventOffsets);

  // There is one MatchHeader, at index 1.
  var headersVector = b.GameWrapper.createMatchHeadersVector(builder, [1]);

  // There is one MatchFooter, at the second-to-last index in the list.
  // Which is at len(list) - 2, or len(matchEventOffsets) + 2 - 2.
  var footersVector = b.GameWrapper.createMatchFootersVector(builder, [matchEventOffsets.length]);

  // Create the wrapper around the game.
  b.GameWrapper.startGameWrapper(builder);
  b.GameWrapper.addEvents(builder, eventsVector);
  b.GameWrapper.addMatchHeaders(builder, headersVector);
  b.GameWrapper.addMatchFooters(builder, footersVector);
  return b.GameWrapper.endGameWrapper(builder);
}

/* Run the script. */
function makeSamples() {
  var builder = new flatbuffers.Builder(0);
  var wrapper = createGameWrapper(builder, matchBulletsRandom);
  // The top-level object should be the GameWrapper.
  builder.finish(wrapper);

  // A flatbuffers.Buffer, which is not a normal Node buffer for... reasons
  var buffer = builder.dataBuffer();
  
  // How to create an actual node buffer
  // Slightly arbitrary, but whatever
  var actualBuffer = new Buffer(buffer.bytes().slice(buffer.position(), buffer.bytes().length));

  fs.writeFileSync('output.bin', actualBuffer);
}

if (require.main == module) {
  makeSamples();
}
