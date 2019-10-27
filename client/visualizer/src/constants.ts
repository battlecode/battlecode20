import {schema} from 'battlecode-playback';
import {Symmetry} from './mapeditor/index';

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


// TODO: Old constants
// Game canvas rendering sizes
export const BULLET_SIZE = .5;
export const BULLET_SIZE_HALF = BULLET_SIZE / 2;
export const INDICATOR_DOT_SIZE = .5;
export const INDICATOR_LINE_WIDTH = .4;
export const HEALTH_BAR_HEIGHT = .3;
export const HEALTH_BAR_WIDTH = 2;
export const HEALTH_BAR_WIDTH_HALF = HEALTH_BAR_WIDTH / 2;
export const SIGHT_RADIUS_LINE_WIDTH = .15

// Game canvas rendering parameters
export const HIGH_SPEED_THRESH = (4*4) - .00001;
export const MED_SPEED_THRESH = (2*2) - .00001;

// Map editor canvas parameters
export const DELTA = .0001;
export const ARCHON_RADIUS = 2;
export const MIN_TREE_RADIUS = 0.5;
export const MAX_TREE_RADIUS = 10;
export const MIN_DIMENSION = 30;
export const MAX_DIMENSION = 100;

// Server settings
export const NUMBER_OF_TEAMS = 2;
export const MIN_NUMBER_OF_ARCHONS = 1;
export const MAX_NUMBER_OF_ARCHONS = 3;
export const MAX_ROUND_NUM = 3000;
export const VICTORY_POINT_THRESH = 1000;

// Other constants
export const BULLET_THRESH = 10000;

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
    case MINER:      return "miner";
    case LANDSCAPER:    return "landscaper";
    case DRONE:  return "drone";
    case NET_GUN:     return "net_gun";
    case COW:        return "cow";
    case REFINERY:       return "refinery";
    case VAPORATOR: return "vaporator";
    case HQ:        return "hq";
    case DESIGN_SCHOOL:        return "design_school";
    case FULFILLMENT_CENTER:        return "fulfillment_center";
    default:          throw new Error("invalid body type");
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

// TODO: fix radius (is this vision that can be toggled in sidebar?)
export function radiusFromBodyType(bodyType: schema.BodyType) {
  switch(bodyType) {
    case MINER:               return 2;
    case LANDSCAPER:          return 1;
    case DRONE:               return 1;
    case NET_GUN:             return 1;
    case COW:                 return 2;
    case REFINERY:            return 1;
    case VAPORATOR:           return 1;
    case HQ:                  return 2;
    case DESIGN_SCHOOL:       return 1;
    case FULFILLMENT_CENTER:  return 1;
    default:           throw new Error("invalid body type");
  }
}