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

    private long controlBits;
    private int currentBytecodeLimit;
    private int bytecodesUsed;

    private int roundsAlive;
    private int soupCarrying; // amount of soup the robot is carrying (miners)
    private int dirtCarrying; // amount of dirt the robot is carrying (landscapers and buildings)
    
    private int cooldownTurns;

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

        this.controlBits = 0;
        this.currentBytecodeLimit = type.bytecodeLimit;
        this.bytecodesUsed = 0;

        this.roundsAlive = 0;
        this.soupCarrying = 0;
        this.dirtCarrying = 0;
        
        this.cooldownTurns = 0;

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

    public long getControlBits() {
        return controlBits;
    }

    public int getBytecodesUsed() {
        return bytecodesUsed;
    }

    public int getRoundsAlive() {
        return roundsAlive;
    }

    public int getSoupCarrying() {
        return soupCarrying;
    }

    public int getDirtCarrying() {
        return dirtCarrying;
    }
    
    public int getCooldownTurns() {
        return cooldownTurns;
    }

    public RobotInfo getRobotInfo() {
        if (this.cachedRobotInfo != null
                && this.cachedRobotInfo.ID == ID
                && this.cachedRobotInfo.team == team
                && this.cachedRobotInfo.type == type
                && this.cachedRobotInfo.location.equals(location)) {
            return this.cachedRobotInfo;
        }
        return this.cachedRobotInfo = new RobotInfo(
                ID, team, type, location);
    }

    // **********************************
    // ****** CHECK METHODS *************
    // **********************************

    /**
     * Returns whether this robot can sense the given location.
     * 
     * @param toSense the MapLocation to sense
     */
    public boolean canSenseLocation(MapLocation toSense){
        return this.location.distanceTo(toSense) <= this.type.sensorRadius;
    }

    /**
     * Returns whether this robot can sense something a given radius away.
     * 
     * @param radius the distance to sense
     */
    public boolean canSenseRadius(int radius) {
        return radius <= this.type.sensorRadius;
    }

    // ******************************************
    // ****** UPDATE METHODS ********************
    // ******************************************

    /**
     * Sets the location of the robot.
     * 
     * @param loc the new location of the robot
     */
    public void setLocation(MapLocation loc) {
        this.gameWorld.getObjectInfo().moveRobot(this, loc);
        this.location = loc;
    }

    /**
     * Resets the action cooldown using the formula cooldown = type_cooldown + pollution_at_location.
     */
    public void resetCooldownTurns() {
        setCooldownTurns(this.type.actionCooldown + this.gameWorld.getPollution(this.location));
    }
    
    /**
     * Sets the action cooldown given the number of turns.
     * 
     * @param newTurns the number of cooldown turns
     */
    public void setCooldownTurns(int newTurns) {
        this.cooldownTurns = newTurns;
    }

    // TODO!!
    // public boolean killRobotIfDead(){
    //     if(this.health == 0){
    //         gameWorld.destroyRobot(this.ID);
    //         return true;
    //     }
    //     return false;
    // }

    // *********************************
    // ****** GAMEPLAY METHODS *********
    // *********************************

    // should be called at the beginning of every round
    public void processBeginningOfRound() {
        // this.healthChanged = false;
    }

    public void processBeginningOfTurn() {
        if (this.cooldownTurns > 0) {
            this.cooldownTurns--;
        }
        // if(getRoundsAlive() < 20 && this.type.isBuildable()){
        //     this.repairRobot(.04f * getType().maxHealth);
        // }
        this.currentBytecodeLimit = getType().bytecodeLimit;
    }

    public void processEndOfTurn() {
        this.gameWorld.getMatchMaker().addBytecodes(ID, this.bytecodesUsed);
        this.roundsAlive++;
    }

    public void processEndOfRound() {
        // if(this.healthChanged){
        //     gameWorld.getMatchMaker().addHealthChanged(getID(), getHealth());
        // }
    }

    // *********************************
    // ****** BYTECODE METHODS *********
    // *********************************

    // TODO
    public boolean canExecuteCode() {
        // if (getHealth() <= 0.0)
        //     return false;
        // if(type.isBuildable())
        //     return roundsAlive >= 20;
        return true;
    }

    public void setBytecodesUsed(int numBytecodes) {
        this.bytecodesUsed = numBytecodes;
    }

    public int getBytecodeLimit() {
        return canExecuteCode() ? this.currentBytecodeLimit : 0;
    }

    // *********************************
    // ****** VARIOUS METHODS **********
    // *********************************

    public void suicide(){
        this.gameWorld.destroyRobot(getID());

        this.gameWorld.getMatchMaker().addAction(getID(), Action.DIE_SUICIDE, -1);
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
