package battlecode.common;

/**
 * Contains details on various attributes of the different robots. All of this information is in the specs in a more organized form.
 */
public enum RobotType {

    // spawnSource, soupCost, dirtLimit, soupLimit, actionCooldown, sensorRadius, pollutionRadius, pollutionAmount, maxSoupProduced, bytecodeLimit
    /**
     * The base produces miners, is also a net gun and a refinery.
     * @battlecode.doc.robottype
     */
    HQ                      (null,  0,  0,  0,  1,  7,  4,  1,  1,  10,  15000),
    //                       SS     C   DL  SL  AC  SR  PR  PA  GP  MS   BL
    /**
     * Miners extract crude soup and bring it to the refineries.
     *
     * @battlecode.doc.robottype
     */
    MINER                   (HQ,  10,  0,  40,  1,  8,  0,  0,  0,  0,  15000), // chef?
    //                       SS   C    DL  SL   AC  SR  PR  PA  GP  MS  BL
    /**
     * Refineries turn crude soup into refined soup, and produce pollution.
     * @battlecode.doc.robottype
     */
    REFINERY                (MINER,  20,  0,  0,  1,  5,  4,  1,  1,  10,  15000),
    //                       SS      C    DL  SL  AC  SR  PR  PA  GP  MS   BL
    /**
     * Vaporators reduce pollution.
     * @battlecode.doc.robottype
     */
    VAPORATOR               (MINER,  20,  0,  0,  1,  5,  4,  -1,  -1,  5,  15000),
    //                       SS      C    DL  SL  AC  SR  PR  PA   GP   MS  BL
    /**
     * Design schools create landscapers.
     * @battlecode.doc.robottype
     */
    DESIGN_SCHOOL           (MINER,  20,  0,  0,  1,  5,  0,  0,  0,  0,  15000),
    //                       SS      C    DL  SL  AC  SR  PR  PA  GP  MS  BL
    /**
     * Fulfillment centers create drones.
     * @battlecode.doc.robottype
     */
    FULFILLMENT_CENTER      (MINER,  20,  0,  0,  1,  5,  0,  0,  0,  0,  15000),
    //                       SS      C    DL  SL  AC  SR  PR  PA  GP  MS  BL
    /**
     * Landscapers take dirt from adjacent (decreasing the elevation)
     * squares or deposit dirt onto adjacent squares, including
     * into water (increasing the elevation).
     * @battlecode.doc.robottype
     */
    LANDSCAPER              (DESIGN_SCHOOL,  10,  40,  0,  1,  4,  0,  0,  0,  0,  15000),
    //                       SS              C    DL   SL  AC  SR  PR  PA  GP  MS  BL
    /**
     * Drones pick up any unit and drop them somewhere else.
     * @battlecode.doc.robottype
     */
    DELIVERY_DRONE          (FULFILLMENT_CENTER,  10,  0,  0,  1,  4,  0,  0,  0,  0,  15000),
    //                       SS                   C    DL  SL  AC  SR  PR  PA  GP  MS  BL
    /**
     * Net guns shoot down drones.
     * @battlecode.doc.robottype
     */
    NET_GUN                 (MINER,  7,  0,  0,  1,  6,  0,  0,  0,  0,  15000),
    //                       SS      C   DL  SL  AC  SR  PR  PA  GP  MS  BL
    /**
     * Cows produce pollution (and they moo).
     * @battlecode.doc.robottype
     */
    COW                     (null,  0,  0,  0,  1,  8,  0,  0,  0,  0,  0),
    //                       SS     C   DL  SL  AC  SR  PR  PA  GP  MS  BL
    ;
    
    /**
     * For units, this is the structure that spawns it. For non-spawnable robots, this is null.
     */
    public final RobotType spawnSource;

    /**
     * Cost for creating the robot.
     */
    public final int cost;

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
    public final float actionCooldown;

    /**
     * Range for sensing robots and trees.
     */
    public final int sensorRadiusSquared;

    /**
     * How many units a cow pollutes.
     */
    public final int pollutionRadiusSquared;

    /**
     * Amount of pollution created when refining soup.
     */
    public final int pollutionAmount;

    /**
     * Amount of global pollution created when refining soup.
     */
    public final int globalPollutionAmount;

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
     * @param type the RobotType of the robot to get information on
     * @return whether the robot can build
     */
    public boolean canBuild(RobotType type) {
        return this == type.spawnSource;
    }

    /**
     * Returns whether the robot can produce soup.
     *
     * @return whether the robot can produce soup
     */
    public boolean canProduceSoupAndPollution() {
        return this == REFINERY || this == VAPORATOR || this == HQ;
    }

    /**
     * Returns whether the robot can move.
     *
     * @return whether the robot can move
     */
    public boolean canMove() {
        return this == MINER || this == LANDSCAPER || this == DELIVERY_DRONE || this == COW;
    }

    /**
     * Returns whether the robot can fly.
     *
     * @return whether the robot can fly
     */
    public boolean canFly() {
        return this == DELIVERY_DRONE;
    }

    /**
     * Returns whether the robot can dig.
     *
     * @return whether the robot can dig
     */
    public boolean canDig() {
        return this == LANDSCAPER;
    }

    /**
     * Returns whether the robot can deposit dirt.
     *
     * @return whether the robot can deposit dirt
     */
    public boolean canDeposit() {
        return this == LANDSCAPER;
    }

    /**
     * Returns whether the robot can mine.
     *
     * @return whether the robot can mine
     */
    public boolean canMine() {
        return this == MINER;
    }

    /**
     * Returns whether the robot can refine.
     *
     * @return whether the robot can refine
     */
    public boolean canRefine() {
        return this == MINER;
    }

    /**
     * Returns whether the robot can shoot drones.
     *
     * @return whether the robot can shoot
     */
    public boolean canShoot() {
        return this == NET_GUN || this == HQ;
    }

    /**
     * Returns whether the robot can be shot.
     *
     * @return whether the robot can be shot
     */
    public boolean canBeShot() {
        return this == DELIVERY_DRONE;
    }

    /**
     * Returns whether the robot can pick up units.
     *
     * @return whether the robot can pick up units
     */
    public boolean canPickUpUnits() {
        return this == DELIVERY_DRONE;
    }

    /**
     * Returns whether the robot can drop off units.
     *
     * @return whether the robot can drop off units
     */
    public boolean canDropOffUnits() {
        return this == DELIVERY_DRONE;
    }

    /**
     * Returns whether the robot can be picked up.
     *
     * @return whether the robot can be picked up
     */
    public boolean canBePickedUp() {
        return this == MINER || this == LANDSCAPER || this == COW;
    }

    /**
     * Returns whether the robot is a building.
     * 
     * @return whether the robot is a building
     */
    public boolean isBuilding() {
        return (this == HQ || this == REFINERY || this == VAPORATOR ||
                this == DESIGN_SCHOOL || this == FULFILLMENT_CENTER ||
                this == NET_GUN);
    }

    RobotType(RobotType spawnSource, int cost, int dirtLimit, int soupLimit,
              float actionCooldown, int sensorRadiusSquared, int pollutionRadiusSquared, int pollutionAmount,
              int globalPollutionAmount, int maxSoupProduced, int bytecodeLimit) {
        this.spawnSource           = spawnSource;
        this.cost                  = cost;
        this.dirtLimit             = dirtLimit;
        this.soupLimit             = soupLimit;
        this.actionCooldown        = actionCooldown;
        this.sensorRadiusSquared = sensorRadiusSquared;
        this.pollutionRadiusSquared = pollutionRadiusSquared;
        this.pollutionAmount       = pollutionAmount;
        this.globalPollutionAmount = globalPollutionAmount;
        this.maxSoupProduced       = maxSoupProduced;
        this.bytecodeLimit         = bytecodeLimit;
    }
}
