import {schema} from 'battlecode-playback';
import {Symmetry} from './mapeditor/index';
import { net } from 'electron';

// Body types
export const MINER = schema.BodyType.MINER;
export const LANDSCAPER = schema.BodyType.LANDSCAPER;
export const DRONE = schema.BodyType.DRONE;
export const NET_GUN = schema.BodyType.NET_GUN;
export const COW = schema.BodyType.COW;
export const REFINERY = schema.BodyType.REFINERY;
export const VAPORATOR = schema.BodyType.VAPORATOR;
export const HQ = schema.BodyType.HQ;
export const DESIGN_SCHOOL = schema.BodyType.DESIGN_SCHOOL;
export const FULFILLMENT_CENTER = schema.BodyType.FULFILLMENT_CENTER;
export const NONE = schema.BodyType.NONE;

// Game canvas rendering sizes
export const INDICATOR_DOT_SIZE = .5;
export const INDICATOR_LINE_WIDTH = .4;
export const SIGHT_RADIUS_LINE_WIDTH = .15

// Game canvas rendering parameters
export const HIGH_SPEED_THRESH = (4*4) - .00001;
export const MED_SPEED_THRESH = (2*2) - .00001;

// Map editor canvas parameters
export const DELTA = .0001;
export const MIN_DIMENSION = 30;
export const MAX_DIMENSION = 100;

// Server settings
export const NUMBER_OF_TEAMS = 2;
export const MAX_ROUND_NUM = 3000;
// export const VICTORY_POINT_THRESH = 1000;

// Other constants
// export const BULLET_THRESH = 10000;

// Maps available in the server.
export const SERVER_MAPS = [
  "Barrier",
  "DenseForest",
  "Enclosure",
  "Hurdle",
  "LineOfFire",
  "MagicWood",
  "shrine",
  "SparseForest",
  "Arena",
  "Barbell",
  "Boxed",
  "Bullseye",
  "Chess",
  "Clusters",
  "Cramped",
  "CrossFire",
  "DigMeOut",
  "GiantForest",
  "LilForts",
  "Maniple",
  "MyFirstMap",
  "OMGTree",
  "PasscalsTriangles",
  "Shrubbery",
  "Sprinkles",
  "Standoff",
  "Waves",
  "1337Tree",
  "Aligned",
  "Alone",
  "Blitzkrieg",
  "BugTrap",
  "Captive",
  "Caterpillar",
  "Chevron",
  "Conga",
  "CropCircles",
  "Croquembouche",
  "DarkSide",
  "DeathStar",
  "Defenseless",
  "Fancy",
  "FlappyTree",
  "Grass",
  "GreatDekuTree",
  "GreenHouse",
  "HedgeMaze",
  "HiddenTunnel",
  "HouseDivided",
  "Interference",
  "Lanes",
  "Levels",
  "LilMaze",
  "Misaligned",
  "ModernArt",
  "Ocean",
  "Oxygen",
  "PacMan",
  "PeacefulEncounter",
  "Planets",
  "Present",
  "PureImagination",
  "Shortcut",
  "Slant",
  "Snowflake",
  "TheOtherSide",
  "TicTacToe",
  "TreeFarm",
  "Turtle",
  "Whirligig"
];

export function bodyTypeToString(bodyType: schema.BodyType) {
  switch(bodyType) {
    case MINER:             return "miner";
    case LANDSCAPER:        return "landscaper";
    case DRONE:             return "drone";
    case NET_GUN:           return "netGun";
    case COW:               return "cow";
    case REFINERY:          return "refinery";
    case VAPORATOR:         return "vaporator";
    case HQ:                return "HQ";
    case DESIGN_SCHOOL:     return "designSchool";
    case FULFILLMENT_CENTER:return "fulfillmentCenter";
    case NONE:              return "";
    default:                throw new Error("invalid body type");
  }
}

export function symmetryToString(symmetry: Symmetry) {
  switch(symmetry) {
    case Symmetry.ROTATIONAL: return "Rotational";
    case Symmetry.HORIZONTAL: return "Horizontal";
    case Symmetry.VERTICAL:   return "Vertical";
    default:         throw new Error("invalid symmetry");
  }
}

export function radiusFromBodyType(bodyType: schema.BodyType) {
  switch(bodyType) {
    case MINER:
    case LANDSCAPER:
    case DRONE:
    case NET_GUN:
    case COW:
    case REFINERY:
    case VAPORATOR:
    case HQ:
    case DESIGN_SCHOOL:
    case FULFILLMENT_CENTER: return 1;
    case NONE: return 0;
    default: throw new Error("invalid body type");
  }
}