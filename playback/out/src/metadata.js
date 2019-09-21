"use strict";
exports.UNKNOWN_SPEC_VERSION = "UNKNOWN SPEC";
exports.UNKNOWN_TEAM = "UNKNOWN TEAM";
exports.UNKNOWN_PACKAGE = "UNKNOWN PACKAGE";
/**
 * Metadata about a game.
 */
var Metadata = (function () {
    function Metadata() {
        this.specVersion = exports.UNKNOWN_SPEC_VERSION;
        this.types = Object.create(null);
        this.teams = Object.create(null);
    }
    Metadata.prototype.parse = function (header) {
        this.specVersion = header.specVersion() || exports.UNKNOWN_SPEC_VERSION;
        var teamCount = header.teamsLength();
        for (var i = 0; i < teamCount; i++) {
            var team = header.teams(i);
            this.teams[team.teamID()] = new Team(team.teamID(), team.packageName() || exports.UNKNOWN_PACKAGE, team.name() || exports.UNKNOWN_TEAM);
        }
        var bodyCount = header.bodyTypeMetadataLength();
        for (var i = 0; i < bodyCount; i++) {
            var body = header.bodyTypeMetadata(i);
            this.types[body.type()] = new BodyType(body.type(), body.radius(), body.cost(), body.maxHealth(), body.startHealth(), body.strideRadius(), body.bulletSpeed(), body.bulletAttack(), body.sightRadius(), body.bulletSightRadius());
        }
        // SAFE
        Object.freeze(this.types);
        Object.freeze(this.teams);
        Object.freeze(this);
        return this;
    };
    return Metadata;
}());
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = Metadata;
var Team = (function () {
    function Team(teamID, packageName, name) {
        this.teamID = teamID;
        this.packageName = packageName;
        this.name = name;
        Object.freeze(this);
    }
    return Team;
}());
exports.Team = Team;
/**
 * Information about a specific body type.
 */
var BodyType = (function () {
    function BodyType(type, radius, cost, maxHealth, startHealth, strideRadius, bulletSpeed, bulletAttack, sightRadius, bulletSightRadius) {
        this.type = type;
        this.radius = radius;
        this.cost = cost;
        this.maxHealth = maxHealth;
        this.startHealth = startHealth;
        this.strideRadius = strideRadius;
        this.bulletSpeed = bulletSpeed;
        this.bulletAttack = bulletAttack;
        this.sightRadius = sightRadius;
        this.bulletSightRadius = bulletSightRadius;
        Object.freeze(this);
    }
    return BodyType;
}());
exports.BodyType = BodyType;
