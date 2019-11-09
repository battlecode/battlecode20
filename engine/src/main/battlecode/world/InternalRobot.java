package battlecode.world;

import battlecode.common.*;
import battlecode.schema.Action;

/**
 * The representation of a robot used by the server.
 */
public strictfp class InternalRobot {
    private final RobotControllerImpl controller;
    private final GameWorld gameWorld;

    private final int ID;
    private Team team;
    private RobotType type;
    private MapLocation location;
    private float health;

    private long controlBits;
    private int currentBytecodeLimit;
    private int bytecodesUsed;
    private int prevBytecodesUsed;
    private boolean isLocked;

    private int roundsAlive;
    private int repairCount;
    private int attackCount;
    private int moveCount;
    
    private int buildCooldownTurns;

    private boolean currentlyHoldingUnit;
    private int idOfUnitCurrentlyHeld;

    private boolean blocked;

    private boolean healthChanged = false;

    /**
     * Used to avoid recreating the same RobotInfo object over and over.
     */
    private RobotInfo cachedRobotInfo;

    /**
     * Create a new internal representation of a robot
     *
     * @param gw the world the robot exists in
     * @param type the type of the robot
     * @param loc the location of the robot
     * @param team the team of the robot
     */
    @SuppressWarnings("unchecked")
    public InternalRobot(GameWorld gw, int id, RobotType type, MapLocation loc, Team team) {
        this.ID = id;
        this.team = team;
        this.type = type;
        this.location = loc;

        this.health = type.getStartingHealth();

        this.controlBits = 0;
        this.currentBytecodeLimit = type.bytecodeLimit;
        this.bytecodesUsed = 0;
        this.prevBytecodesUsed = 0;

        this.roundsAlive = 0;
        this.repairCount = 0;
        this.attackCount = 0;
        this.moveCount = 0;
        
        this.buildCooldownTurns = 0;

        this.currentlyHoldingUnit = false;
        this.idOfUnitCurrentlyHeld = -1;

        this.blocked = false;

        this.gameWorld = gw;
        this.controller = new RobotControllerImpl(gameWorld, this);
    }

    // ******************************************
    // ****** GETTER METHODS ********************
    // ******************************************

    public RobotControllerImpl getController() {
        return controller;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public int getID() {
        return ID;
    }

    public Team getTeam() {
        return team;
    }

    public RobotType getType() {
        return type;
    }

    public MapLocation getLocation() {
        return location;
    }

    public float getHealth() {
        return health;
    }

    public long getControlBits() {
        return controlBits;
    }

    public int getBytecodesUsed() {
        return bytecodesUsed;
    }

    public int getPrevBytecodesUsed() {
        return prevBytecodesUsed;
    }

    public boolean getIsLocked() {
        return isLocked;
    }

    public int getRoundsAlive() {
        return roundsAlive;
    }

    public int getRepairCount() {
        return repairCount;
    }
    
    public int getAttackCount() {
        return attackCount;
    }
    
    public int getMoveCount() {
        return moveCount;
    }
    
    public int getBuildCooldownTurns() {
        return buildCooldownTurns;
    }

    public boolean isCurrentlyHoldingUnit() {
        return currentlyHoldingUnit;
    }

    public int getIdOfUnitCurrentlyHeld() {
        return idOfUnitCurrentlyHeld;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public RobotInfo getRobotInfo() {
        if (this.cachedRobotInfo != null
                && this.cachedRobotInfo.ID == ID
                && this.cachedRobotInfo.team == team
                && this.cachedRobotInfo.type == type
                && this.cachedRobotInfo.location.equals(location)
                && this.cachedRobotInfo.health == health
                && this.cachedRobotInfo.attackCount == attackCount
                && this.cachedRobotInfo.moveCount == moveCount) {
            return this.cachedRobotInfo;
        }
        return this.cachedRobotInfo = new RobotInfo(
                ID, team, type, location, health, attackCount, moveCount);
    }

    // **********************************
    // ****** CHECK METHODS *************
    // **********************************

    public boolean canSenseLocation(MapLocation toSense){
        return this.location.distanceTo(toSense) <= this.type.sensorRadius;
    }

    public boolean canSenseRadius(float radius) {
        return radius <= this.type.sensorRadius;
    }

    public boolean canInteractWithLocation(MapLocation toInteract){
        return this.location.distanceTo(toInteract) <= (this.type.bodyRadius + GameConstants.INTERACTION_DIST_FROM_EDGE);
    }

    // ******************************************
    // ****** UPDATE METHODS ********************
    // ******************************************

    public void setLocation(MapLocation loc){
        this.gameWorld.getObjectInfo().moveRobot(this, loc);
        this.location = loc;
    }

    public void incrementRepairCount() {
        this.repairCount++;
    }
    
    public void incrementAttackCount() {
        this.attackCount++;
    }
    
    public void incrementMoveCount() {
        this.moveCount++;
    }
    
    public void setBuildCooldownTurns(int newTurns) {
        this.buildCooldownTurns = newTurns;
    }

    public void repairRobot(float healAmount){
        this.health = Math.min(this.health + healAmount, this.type.maxHealth);
        if(health > this.type.maxHealth){
            this.health = this.type.maxHealth;
        }
        this.healthChanged = true;
    }

    public void damageRobot(float damage){
        this.health = Math.max(this.health - damage, 0);
        this.healthChanged = true;
        killRobotIfDead();
    }

    public boolean killRobotIfDead(){
        if(this.health == 0){
            gameWorld.destroyRobot(this.ID);
            return true;
        }
        return false;
    }

    public void pickUpUnit(int id) {
        this.currentlyHoldingUnit = true;
        this.idOfUnitCurrentlyHeld = id;
    }

    public void dropUnit() {
        this.currentlyHoldingUnit = false;
        this.idOfUnitCurrentlyHeld = -1;
    }

    public void blockUnit() {
        this.blocked = true;
    }

    public void unBlockUnit() {
        this.blocked = false;
    }

    // *********************************
    // ****** GAMEPLAY METHODS *********
    // *********************************

    // should be called at the beginning of every round
    public void processBeginningOfRound() {
        this.healthChanged = false;
    }

    public void processBeginningOfTurn() {
        attackCount = 0;
        moveCount = 0;
        repairCount = 0;
        if(buildCooldownTurns > 0) {
            buildCooldownTurns--;
        }
        if(getRoundsAlive() < 20 && this.type.isBuildable()){
            this.repairRobot(.04f * getType().maxHealth);
        }
        this.currentBytecodeLimit = getType().bytecodeLimit;
    }

    public void processEndOfTurn() {
        gameWorld.getMatchMaker().addBytecodes(ID, this.bytecodesUsed);
        this.prevBytecodesUsed = this.bytecodesUsed;
        roundsAlive++;
    }

    public void processEndOfRound() {
        if(this.healthChanged){
            gameWorld.getMatchMaker().addHealthChanged(getID(), getHealth());
        }
    }

    // *********************************
    // ****** BYTECODE METHODS *********
    // *********************************

    public boolean canExecuteCode() {
        if (getHealth() <= 0.0)
            return false;
        if (getIsLocked())
            return false;
        if(type.isBuildable())
            return roundsAlive >= 20;
        return true;
    }

    public void setBytecodesUsed(int numBytecodes) {
        bytecodesUsed = numBytecodes;
    }

    public int getBytecodeLimit() {
        return canExecuteCode() ? this.currentBytecodeLimit : 0;
    }

    // *********************************
    // ****** VARIOUS METHODS **********
    // *********************************

    public void suicide(){
        gameWorld.destroyRobot(getID());

        gameWorld.getMatchMaker().addAction(getID(), Action.DIE_SUICIDE, -1);
    }

    // *****************************************
    // ****** MISC. METHODS ********************
    // *****************************************

    @Override
    public boolean equals(Object o) {
        return o != null && (o instanceof InternalRobot)
                && ((InternalRobot) o).getID() == ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public String toString() {
        return String.format("%s:%s#%d", getTeam(), getType(), getID());
    }
}
