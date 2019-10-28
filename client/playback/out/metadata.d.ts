import { schema } from 'battlecode-schema';
export declare const UNKNOWN_SPEC_VERSION = "UNKNOWN SPEC";
export declare const UNKNOWN_TEAM = "UNKNOWN TEAM";
export declare const UNKNOWN_PACKAGE = "UNKNOWN PACKAGE";
/**
 * Metadata about a game.
 */
export default class Metadata {
    /**
     * The version of the spec this game complies with.
     */
    specVersion: string;
    /**
     * All the body types in a game.
     * Access like: meta.types[schema.BodyType.MINOR].strideRadius
     */
    types: {
        [key: number]: BodyType;
    };
    /**
     * All the teams in a game.
     */
    teams: {
        [key: number]: Team;
    };
    constructor();
    parse(header: schema.GameHeader): Metadata;
}
export declare class Team {
    name: string;
    packageName: string;
    teamID: number;
    constructor(teamID: number, packageName: string, name: string);
}
/**
 * Information about a specific body type.
 */
export declare class BodyType {
    type: schema.BodyType;
    cost: number;
    strideRadius: number;
    sightRadius: number;
    soupLimit: number;
    dirtLimit: number;
    constructor(type: schema.BodyType, cost: number, strideRadius: number, sightRadius: number, soupLimit: number, dirtLimit: number);
}