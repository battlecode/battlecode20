package battlecode.common;

/**
 * Contains details on various attributes of the different robots. All of this information is in the specs in a more organized form.
 */
public enum RobotType {

    // spawnSource, soupCost, dirtLimit, soupLimit, actionCooldown, sensorRadius, pollutionRadius, pollutionAmount, maxSoupProduced, bytecodeLimit
    /**
     * Miners extract crude soup and bring it to the refineries.
     *
     * @battlecode.doc.robottype
     */
    ARCHON          (null,    0,    400,   -1,   .5f,  -1,  -1,   10,  15, 1f,  30000),
    //                              HP      BC   BR   BS   AP   SR  BSR  STR   BCL
    /**
     * The main producer unit to make other units; can't build Archons or other Gardeners.
     *
     * @battlecode.doc.robottype
     */
    GARDENER        (ARCHON,  10,   40,  100,   .5f,  -1,  -1,   7,  10,   1f, 15000),
    //                              HP    BC   BR   BS   AP   SR  BSR  STR   BCL
    /**
     * A melee based unit that specializes at cutting down ...
     *
     * @battlecode.doc.robottype
     */
    LUMBERJACK      (GARDENER,  10, 50,  100,   .5f,  -1,   2,   7,  10,  1f, 15000),
    //                              HP    BC   BR   BS   AP    SR  BSR  STR   BCL
    /**
     * Cows produce pollution (and they moo).
     * @battlecode.doc.robottype
     */
    SOLDIER         (GARDENER,  10, 50,  100,   .5f,   2f,   2,   7,  10,   1f, 15000),
    //                              HP    BC   BR     BS    AP   SR   BSR  STR   BCL
    /**
     * Net guns shoot down drones.
     * @battlecode.doc.robottype
     */
    TANK            (GARDENER, 10,  200,  300,   .5f,   4,   5,   7,  10,  1f, 15000),
    //                              HP    BC     BR   BS    AP   SR  BSR    STR   BCL
    /**
     * Refineries turn crude soup into refined soup, and produce pollution.
     * @battlecode.doc.robottype
     */
    SCOUT           (GARDENER,  10, 10,   80,   1, 1.5f,   0.5f,   14,  20,  1f, 15000),
    //                              HP    BC   BR    BS   AP   SR  BSR         STR   BCL
    ;
    
    /**
     * Vaporators reduce pollution.
     * @battlecode.doc.robottype
     */
    VAPORATOR               (MINER,  20,  0,  0,  0,  0,  4,  -1,  5,  15000),
    //                       SS      SC   DL  SL  AC  SR  PR  PA   MS  BL
    /**
     * The base produces miners, is also a net gun and a refinery.
     * @battlecode.doc.robottype
     */
    HQ                      (null,  0,  0,  0,  0,  7,  4,  1,  10,  15000),
    //                       SS     SC  DL  SL  AC  SR  PR  PA  MS   BL
    /**
     * Design schools create landscapers.
     * @battlecode.doc.robottype
     */
    DESIGN_SCHOOL           (MINER,  20,  0,  0,  0,  0,  0,  0,  0,  15000),
    //                       SS      SC   DL  SL  AC  SR  PR  PA  MS  BL
    /**
     * Fulfillment centers create drones.
     * @battlecode.doc.robottype
     */
    FULFILLMENT_CENTER      (MINER,  20,  0,  0,  0,  0,  0,  0,  0,  15000),
    //                       SS      SC   DL  SL  AC  SR  PR  PA  MS  BL
    ;
    
    /**
     * For units, this is the structure that spawns it. For non-spawnable robots, this is null.
     */
    public final RobotType spawnSource;

    /**
     * Cost for creating the robot.
     */
    public final int soupCost;

    /**
     * Limit for amount of dirt robot can hold.
     */
    public final int dirtLimit;

    /**
     * Limit for amount of crude soup robot can hold.
     */
    public final int soupLimit;

    /**
     * Cooldown turns for how long before a robot can take 
     * action (build, move, dig, drop, mine, shoot) again.
     */
    public final int actionCooldown;

    /**
     * Range for sensing robots.
     */
    public final int sensorRadius;

    /**
     * How many units a cow pollutes.
     */
    public final int pollutionRadius;

    /**
     * Amount of pollution created when refining soup.
     */
    public final int pollutionAmount;

    /**
     * Maximum amount of soup to be refined per turn.
     */
    public final int maxSoupProduced;

    /**
     * Base bytecode limit of this robot.
     */
    public final int bytecodeLimit;


    /**
     * Returns whether the robot can build buildings.
     *
     * @return whether the robot can build.
     */
    public boolean canBuild(RobotType type) {
        return this == type.spawnSource;
    }

    /**
     * Returns whether the robot can move.
     *
     * @return whether the robot can move.
     */
    public boolean canMove() {
        return this == MINER || this == LANDSCAPER || this == DRONE || this == COW;
    }

    /**
     * Returns whether the robot can build all units except Gardeners and Archons.
     *
     * @return whether the robot can dig.
     */
    public boolean canDig() {
        return this == LANDSCAPER;
    }

    /**
     * Returns whether the robot can mine.
     *
     * @return whether the robot can mine.
     */
    public boolean canMine() {
        return this == MINER;
    }

    /**
     * Returns whether the robot can shoot drones.
     *
     * @return whether the robot can shoot.
     */
    public boolean canShoot() {
        return this == NET_GUN;
    }

    /**
     * Returns whether the robot can pick up units.
     *
     * @return whether the robot can pick up units.
     */
    public boolean canPickUpUnits() {
        return this == DRONE;
    }

    RobotType(RobotType spawnSource, int buildCooldown, int soupCost, int dirtLimit, int soupLimit, 
              int movementCooldown, int digCooldown, int dropCooldown, int mineCooldown, int shootCooldown, 
              int sensorRadius, int pollutionRadius, int pollutionAmount, int maxSoupProduced, int bytecodeLimit) {
        this.spawnSource        = spawnSource;
        this.buildCooldown      = buildCooldown;
        this.soupCost           = soupCost;
        this.dirtLimit          = dirtLimit;
        this.soupLimit          = soupLimit;
        this.movementCooldown   = movementCooldown;
        this.digCooldown        = digCooldown;
        this.dropCooldown       = dropCooldown;
        this.mineCooldown       = mineCooldown;
        this.shootCooldown      = shootCooldown;
        this.sensorRadius       = sensorRadius;
        this.pollutionRadius    = pollutionRadius;
        this.pollutionAmount    = pollutionAmount;
        this.maxSoupProduced    = maxSoupProduced;
        this.bytecodeLimit      = bytecodeLimit;
    }
}
