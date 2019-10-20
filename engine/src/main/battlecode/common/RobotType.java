package battlecode.common;

/**
 * Contains details on various attributes of the different robots. All of this information is in the specs in a more organized form.
 */
public enum RobotType {

    // spawnSource, deliveryLimit, dirtLimit, soupLimit, movementCooldown, digCooldown, dropCooldown, mineCooldown, sensorRadius, pollutionRadius, bytecodeLimit                       
    /**
     * Miners extract crude soup and bring it to the refineries.
     *
     * @battlecode.doc.robottype
     */
    MINER           (BASE,  0,  0,  40,  2,  0,  0,  5,  8,  0,  0,  15000), // chef?
    //               
    /**
     * Landscapers take dirt from adjacent (decreasing the elevation)
     * squares or deposit dirt onto adjacent squares, including
     * into water (increasing the elevation).
     * @battlecode.doc.robottype
     */
    LANDSCAPER      (DESIGN_SCHOOL,  0,  40,  0,  4,  4,  8,  0,  4,  0,  15000),    
    /**
     * Drones pick up any unit and drop them somewhere else.
     * @battlecode.doc.robottype
     */
    DRONE           (FULFILLMENT_CENTER,  1,  0,  0,  8,  0,  0,  0,  4,  0,  15000),
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
     * Cooldown turns for structure that spawns it.
     */
    public final int buildCooldownTurns;
    
    /**
     * Maximum health for the robot.
     */
    public final int maxHealth;

    /**
     * Cost for creating the robot.
     */
    public final int bulletCost;

    /**
     * Radius for the robot.
     */
    public final float bodyRadius;

    /**
     * Speed of bullets produced from the robot.
     */
    public final float bulletSpeed;

    /**
     * Base damage per attack.
     */
    public final float attackPower;

    /**
     * Range for sensing robots and trees.
     */
    public final float sensorRadius;

    /**
     * Range for sensing bullets.
     */
    public final float bulletSightRadius;

    /**
     * Maximum distance the robot can move per turn.
     */
    public final float strideRadius;

    /**
     * Base bytecode limit of this robot.
     */
    public final int bytecodeLimit;

    /**
     * Returns whether the robot can attack.
     *
     * @return whether the robot can attack.
     */
    public boolean canAttack() {
        return attackPower > 0;
    }

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

    /**
     * Returns whether the robot is hireable.
     *
     * @return whether the robot is hireable.
     */
    public boolean isHireable() {
        return spawnSource == ARCHON;
    }

    /**
     * Returns whether the robot is buildable.
     *
     * @return whether the robot is buildable.
     */
    public boolean isBuildable() { return spawnSource == GARDENER; }

    /**
     * Returns the starting health of this type of robot.
     *
     * @return the starting health of this type of robot.
     */
    public float getStartingHealth() {
        return this == RobotType.ARCHON || this == RobotType.GARDENER ? this.maxHealth : GameConstants.PLANTED_UNIT_STARTING_HEALTH_FRACTION * this.maxHealth;
    }
    
    RobotType(RobotType spawnSource, int buildCooldownTurns, int maxHealth, int bulletCost, float bodyRadius, float bulletSpeed, float attackPower,
              float sensorRadius, float bulletSightRadius, float strideRadius, int bytecodeLimit) {
        this.spawnSource        = spawnSource;
        this.buildCooldownTurns = buildCooldownTurns;
        this.maxHealth          = maxHealth;
        this.bulletCost         = bulletCost;
        this.bodyRadius         = bodyRadius;
        this.bulletSpeed        = bulletSpeed;
        this.attackPower        = attackPower;
        this.sensorRadius       = sensorRadius;
        this.bulletSightRadius  = bulletSightRadius;
        this.strideRadius       = strideRadius;
        this.bytecodeLimit      = bytecodeLimit;
    }
}
