import {schema, flatbuffers} from 'battlecode-schema';

export const UNKNOWN_SPEC_VERSION = "UNKNOWN SPEC";
export const UNKNOWN_TEAM = "UNKNOWN TEAM";
export const UNKNOWN_PACKAGE = "UNKNOWN PACKAGE";

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
   * Access like: meta.types[schema.BodyType.ARCHON].radius
   */
  types: BodyType[];

  /**
   * All the teams in a game.
   */
  teams: Team[];

  constructor() {
    this.specVersion = UNKNOWN_SPEC_VERSION;
    this.types = [];
    this.teams = [];
  }

  parse(header: schema.GameHeader): Metadata {
    this.specVersion = header.specVersion() as string || UNKNOWN_SPEC_VERSION;
    const teamCount = header.teamsLength();
    for (let i = 0; i < teamCount; i++) {
      const team = header.teams(i);
      this.teams.push(new Team(
        team.teamID(),
        team.packageName() as string || UNKNOWN_PACKAGE,
        team.name() as string || UNKNOWN_TEAM
      ));
    }
    const bodyCount = header.bodyTypeMetadataLength();
    for (let i = 0; i < bodyCount; i++) {
      const body = header.bodyTypeMetadata(i);
      this.types.push(new BodyType(body.type(),
        body.radius(),
        body.cost(),
        body.maxHealth(),
        body.startHealth(),
        body.moveDelay(),
        body.attackDelay(),
        body.cooldownDelay(),
        body.bulletSpeed(),
        body.bulletAttack()
      ));
    }
    // SAFE
    Object.freeze(this.types);
    Object.freeze(this.teams);
    Object.freeze(this);
    return this
  }
}

export class Team {
  /**
   * The ID of the team.
   */
  teamID: number;

  /**
   * The package name of the team.
   */
  packageName: string;

  /**
   * The name of the team.
   */
  name: string;

  constructor(teamID: number, packageName: string, name: string) {
    this.teamID = teamID;
    this.packageName = packageName;
    this.name = name;
    Object.freeze(this);
  }
}

/**
 * Information about a specific body type.
 */
export class BodyType {
  /**
   * The relevant type.
   */
  type: schema.BodyType;

  /**
   * The radius of the type, in distance units.
   */
  radius: number;

  /**
   * The cost of the type, in bullets.
   */
  cost: number;

  /**
   * The maxiumum health of the type, in health units.
   */
  maxHealth: number;

  /**
   * If unset, the same as maxHealth.
   * Otherwise, the health a body of this type starts with.
   */
  startHealth: number;

  /**
   * The delay penalty added to the core counter after movement.
   */
  moveDelay: number;

  /**
   * The delay penalty added to the attack counter after movement.
   */
  attackDelay: number;

  /**
   * The delay penalty added to the attack counter after movement, and vice versa.
   */
  cooldownDelay: number;

  /**
   * The speed that bullets from this unit move.
   * Note: you don't need to keep track of this, SpawnedBody.vel will always be set.
   */
  bulletSpeed: number;

  /**
   * The damage that bullets from this unit inflict.
   * Note: you don't need to keep track of this.
   */
  bulletAttack: number;

  constructor(type: schema.BodyType, radius: number, cost: number, maxHealth: number, startHealth: number, moveDelay: number, attackDelay: number, cooldownDelay: number, bulletSpeed: number, bulletAttack: number) {
    this.type = type;
    this.radius = radius;
    this.cost = cost;
    this.maxHealth = maxHealth;
    this.startHealth = startHealth;
    this.moveDelay = moveDelay;
    this.attackDelay = attackDelay;
    this.cooldownDelay = cooldownDelay;
    this.bulletSpeed = bulletSpeed;
    this.bulletAttack = bulletAttack;
    Object.freeze(this);
  }
}
