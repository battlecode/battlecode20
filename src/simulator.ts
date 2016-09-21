import Metadata from './metadata';
import GameWorld from './gameworld';
import {schema, flatbuffers} from 'battlecode-schema';

/**
 * A function that runs through a GameWrapper containing a single match, and
 * returns the state of the world at the end of the game.
 *
 * Intended for testing.
 */
function crunch(game: schema.GameWrapper): GameWorld {
  const gameHeader = game.events(0).e() as schema.GameHeader;
  const metadata = new Metadata().parse(gameHeader);
  const world = new GameWorld(metadata, false);
  const matchHeader = game.events(1).e() as schema.MatchHeader;
  world.loadFromMatchHeader(matchHeader);

  for (let i = 2;; i++) {
    const event = game.events(i);
    if (event.eType() === schema.Event.MatchFooter) {
      return world;
    }
    // must be a Round
    world.processRound(event.e() as schema.Round);
  }
}
