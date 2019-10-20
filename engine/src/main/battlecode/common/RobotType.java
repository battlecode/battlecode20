package battlecode.common;

/**
 * Contains details on various attributes of the different robots. All of this information is in the specs in a more organized form.
 */
public enum RobotType {

    // spawnSource, buildCooldown, dirtLimit, soupLimit, movementCooldown, digCooldown, dropCooldown, mineCooldown, sensorRadius, pollutionRadius, bytecodeLimit                       
    /**
     * Miners extract crude soup and bring it to the refineries.
     *
     * @battlecode.doc.robottype
     */
    MINER           (BASE,  20,  0,  40,  2,  0,  0,  5,  8,  0,  0,  15000), // chef?
    //               
    /**
     * Landscapers take dirt from adjacent (decreasing the elevation)
     * squares or deposit dirt onto adjacent squares, including
     * into water (increasing the elevation).
     * @battlecode.doc.robottype
     */
    LANDSCAPER      (DESIGN_SCHOOL,  20,  40,  0,  4,  4,  8,  0,  4,  0,  15000),    
    /**
     * Drones pick up any unit and drop them somewhere else.
     * @battlecode.doc.robottype
     */
    DRONE           (FULFILLMENT_CENTER,  20,  0,  0,  8,  0,  0,  0,  4,  0,  15000),
    /**
     * Cows produce pollution (and they moo).
     * @battlecode.doc.robottype
     */
    COW             (null,  0,  0,  0,  6,  0,  0,  0,  0, 0,  0),
    ;
    
    /**
     * For units, this is the structure that spawns it. For non-spawnable robots, this is null.
     */
    public final BuildingType spawnSource;

    /**
     * Cooldown turns for structure that spawns robot.
     */
    public final int buildCooldown;

     /**
     * Limit for amount of dirt robot can hold.
     */
    public final int dirtLimit;

     /**
     * Limit for amount of crude soup robot can hold.
     */
    public final int soupLimit;

     /**
     * Cooldown turns for how long before a robot can move again.
     */
    public final int movementCooldown;

     /**
     * Cooldown turns for how long before a robot can dig again.
     */
    public final int digCooldown;

     /**
     * Cooldown turns for how long before a robot can drop dirt again.
     */
    public final int dropCooldown;

    /**
     * Cooldown turns for how long before a robot can mine again.
     */
    public final int mineCooldown;

    /**
     * Range for sensing robots and trees.
     */
    public final int sensorRadius;

    /**
     * How many units a cow pollutes.
     */
    public final int pollutionRadius;

    /**
     * Base bytecode limit of this robot.
     */
    public final int bytecodeLimit;


    /**
     * Returns whether the robot can build buildings.
     *
     * @return whether the robot can build.
     */
    public boolean canBuild() {
        return this == MINER;
    }

    /**
     * Returns whether the robot can pick up units.
     *
     * @return whether the robot can pick up units.
     */
    public boolean canPickUpUnits() {
        return this == DRONE;
    }

    RobotType(BuildingType spawnSource, int buildCooldown, int dirtLimit, int soupLimit, int movementCooldown, int digCooldown, int dropCooldown, int mineCooldown, int sensorRadius, int pollutionRadius, int bytecodeLimit) {
        this.spawnSource        = spawnSource;
        this.buildCooldown      = buildCooldown;
        this.dirtLimit          = dirtLimit;
        this.soupLimit          = soupLimit;
        this.movementCooldown   = movementCooldown;
        this.digCooldown        = digCooldown;
        this.dropCooldown       = dropCooldown;
        this.mineCooldown       = mineCooldown
        this.sensorRadius       = sensorRadius;
        this.pollutionRadius    = pollutionRadius;
        this.bytecodeLimit      = bytecodeLimit;
    }
}
