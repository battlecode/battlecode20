package battlecode.world;

import battlecode.common.*;
import static battlecode.common.GameActionExceptionType.*;
import battlecode.instrumenter.RobotDeathException;
import battlecode.schema.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The actual implementation of RobotController. Its methods *must* be called
 * from a player thread.
 *
 * It is theoretically possible to have multiple for a single InternalRobot, but
 * that may cause problems in practice, and anyway why would you want to?
 *
 * All overriden methods should assertNotNull() all of their (Object) arguments,
 * if those objects are not explicitly stated to be nullable.
 */
public final strictfp class RobotControllerImpl implements RobotController {

    /**
     * The world the robot controlled by this controller inhabits.
     */
    private final GameWorld gameWorld;

    /**
     * The robot this controller controls.
     */
    private final InternalRobot robot;

    /**
     * Create a new RobotControllerImpl
     *
     * @param gameWorld the relevant world
     * @param robot the relevant robot
     */
    public RobotControllerImpl(GameWorld gameWorld, InternalRobot robot) {
        this.gameWorld = gameWorld;
        this.robot = robot;
    }

    // *********************************
    // ******** INTERNAL METHODS *******
    // *********************************

    /**
     * @return the robot this controller is connected to
     */
    public InternalRobot getRobot() {
        return robot;
    }

    /**
     * Throw a null pointer exception if an object is null.
     *
     * @param o the object to test
     */
    private static void assertNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException("Argument has an invalid null value");
        }
    }

    @Override
    public int hashCode() {
        return robot.getID();
    }

    // *********************************
    // ****** GLOBAL QUERY METHODS *****
    // *********************************

    @Override
    public int getRoundLimit(){
        return gameWorld.getGameMap().getRounds();
    }

    @Override
    public int getRoundNum(){
        return gameWorld.getCurrentRound();
    }

    @Override

    public int getTeamSoup(){
        return gameWorld.getTeamInfo().getSoup(getTeam());
    }

    @Override

    public int getRobotCount(){
        return gameWorld.getObjectInfo().getRobotCount(getTeam());
    }

    // @Override
    // public MapLocation[] getInitialArchonLocations(Team t){
    //     assertNotNull(t);
    //     if (t == Team.NEUTRAL) {
    //         return new MapLocation[0];
    //     } else {
    //         BodyInfo[] initialRobots = gameWorld.getGameMap().getInitialBodies();
    //         ArrayList<MapLocation> archonLocs = new ArrayList<>();
    //         for (BodyInfo initial : initialRobots) {
    //             if(initial.isRobot()){
    //                 RobotInfo robot = (RobotInfo) initial;
    //                 if (robot.type == RobotType.ARCHON && robot.team == t) {
    //                     archonLocs.add(robot.getLocation());
    //                 }
    //             }
    //         }
    //         MapLocation[] array = archonLocs.toArray(new MapLocation[archonLocs.size()]);
    //         Arrays.sort(array);
    //         return array;
    //     }
    // }

    // *********************************
    // ****** UNIT QUERY METHODS *******
    // *********************************

    @Override
    public int getID(){
        return this.robot.getID();
    }

    @Override
    public Team getTeam(){
        return this.robot.getTeam();
    }

    @Override
    public RobotType getType(){
        return this.robot.getType();
    }

    @Override
    public MapLocation getLocation(){
        return this.robot.getLocation();
    }    

    // ***********************************
    // ****** GENERAL SENSOR METHODS *****
    // ***********************************

    private void assertCanSenseLocation(MapLocation loc) throws GameActionException{
        if(!canSenseLocation(loc)){
            throw new GameActionException(CANT_SENSE_THAT,
                    "Target location not within sensor range");
        }
    }

    @Override
    public boolean onTheMap(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        return gameWorld.getGameMap().onTheMap(loc);
    }

    @Override
    public boolean canSenseLocation(MapLocation loc) {
        assertNotNull(loc);
        return this.robot.canSenseLocation(loc);
    }

    @Override
    public boolean canSenseRadius(int radius) {
        return this.robot.canSenseRadius(radius);
    }

    @Override
    public boolean isLocationOccupied(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        return !gameWorld.getObjectInfo().isEmpty(loc);
    }

    @Override
    public RobotInfo senseRobotAtLocation(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        InternalRobot bot = gameWorld.getObjectInfo().getRobotAtLocation(loc);
        if(bot != null) {
            return bot.getRobotInfo();
        }
        return null;
    }

    @Override
    public boolean canSenseRobot(int id) {
        if(!gameWorld.getObjectInfo().existsRobot(id)){
            return false;
        }
        InternalRobot robot = gameWorld.getObjectInfo().getRobotByID(id);
        return canSenseLocation(robot.getLocation()); // TODO
    }

    @Override
    public RobotInfo senseRobot(int id) throws GameActionException {
        if(!canSenseRobot(id)){
            throw new GameActionException(CANT_SENSE_THAT,
                    "Can't sense given robot; It may not exist anymore");
        }
        return gameWorld.getObjectInfo().getRobotByID(id).getRobotInfo();
    }

    @Override
    public RobotInfo[] senseNearbyRobots() {
        return senseNearbyRobots(-1);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(int radius) {
        return senseNearbyRobots(radius, null);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(int radius, Team team) {
        return senseNearbyRobots(getLocation(), radius, team);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(MapLocation center, int radius, Team team) {
        assertNotNull(center);
        InternalRobot[] allSensedRobots = gameWorld.getObjectInfo().getAllRobotsWithinRadius(center,
                radius == -1 ? getType().sensorRadius : radius);
        List<RobotInfo> validSensedRobots = new ArrayList<>();
        for(InternalRobot sensedRobot : allSensedRobots){
            // check if this robot
            if(sensedRobot.equals(this.robot)){
                continue;
            }
            // check if can sense
            if(!canSenseLocation(sensedRobot.getLocation())){
                continue;
            }
            // check if right team
            if(team != null && sensedRobot.getTeam() != team){
                continue;
            }

            validSensedRobots.add(sensedRobot.getRobotInfo());
        }
        return validSensedRobots.toArray(new RobotInfo[validSensedRobots.size()]);
    }

    // ***********************************
    // ****** READINESS METHODS **********
    // ***********************************
    
    private void assertIsReady() throws GameActionException{
        if(!isReady()){
            throw new GameActionException(NOT_ACTIVE,
                    "This robot's action cooldown has not expired.");
        }
    }
    
    @Override
    public boolean isReady() {
        return this.robot.getCooldownTurns() == 0;
    }

    @Override
    public int getCooldownTurns() {
        return this.robot.getCooldownTurns();
    }

    // ***********************************
    // ****** MOVEMENT METHODS ***********
    // ***********************************

    private void assertCanMove(MapLocation loc) throws GameActionException{
        if(!canMove(loc))
            throw new GameActionException(CANT_MOVE_THERE,
                    "Cannot move to the target location " + loc +".");
    }

    @Override
    public boolean canMove(Direction dir) {
        assertNotNull(dir);
        return canMove(getLocation().add(dir));
    }
    
    @Override
    public boolean canMove(MapLocation center) {
        assertNotNull(center);
        return getLocation().distanceTo(center) <= 1 && gameWorld.getGameMap().onTheMap(center) &&
                gameWorld.getObjectInfo().isEmpty(center);
    }

    @Override
    public void move(Direction dir) throws GameActionException {
        MapLocation center = getLocation().add(dir);
        assertNotNull(center);
        assertIsReady();
        assertCanMove(center);
        this.robot.setLocation(center);

        gameWorld.getMatchMaker().addMoved(getID(), getLocation());
    }

    // ***********************************
    // ****** BUILDING/SPAWNING **********
    // ***********************************

    private void assertCanBuildRobot(RobotType type, Direction dir) throws GameActionException{
        if(!canBuildRobot(type, dir)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't build desired robot in given direction, possibly due to " +
                            "insufficient currency, this robot can't build, " +
                            "cooldown not expired, or the spawn location is occupied");
        }
    }

    @Override
    public boolean hasRobotBuildRequirements(RobotType type) {
        assertNotNull(type);
        return getType() == type.spawnSource &&
               gameWorld.getTeamInfo().getSoup(getTeam()) >= type.cost;
    }

    @Override
    public boolean canBuildRobot(RobotType type, Direction dir) {
        assertNotNull(type);
        assertNotNull(dir);
        boolean hasBuildRequirements = hasRobotBuildRequirements(type);
        MapLocation spawnLoc = getLocation().add(dir);
        boolean isClear = gameWorld.getGameMap().onTheMap(spawnLoc) &&
                gameWorld.getObjectInfo().isEmpty(spawnLoc);
        boolean cooldownExpired = isReady();
        return hasBuildRequirements && isClear && cooldownExpired;
    }
    
    @Override
    public boolean canHireMiner(Direction dir) {
        return canBuildRobot(RobotType.MINER, dir);
    }

    @Override
    public void buildRobot(RobotType type, Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanBuildRobot(type, dir);

        this.robot.setCooldownTurns(type.actionCooldown);
        
        MapLocation spawnLoc = getLocation().add(dir); // TODO fix spawn dist

        int robotID = gameWorld.spawnRobot(type, spawnLoc, getTeam());

        gameWorld.getMatchMaker().addAction(getID(), Action.SPAWN_UNIT, robotID);
    }


    // ***********************************
    // ****** BLOCKCHAINNNNNNNNNNN *******
    // ***********************************

    /**
     * Sends a message to the blockchain at the indicated cost.
     * 
     * @param message the message to send.
     * @param proofOfStake the price that the unit is willing to pay for the message. If
     * the team does not have that much soup, the message will not be sent.
     * 
     */
    @Override
    public void sendMessage(int[] messageArray, int cost) throws GameActionException {
        if (messageArray.length > GameConstants.MAX_BLOCKCHAIN_MESSAGE_LENGTH) {
            throw new GameActionException(TOO_LONG_BLOCKCHAIN_MESSAGE,
                    "Can only send " + Integer.toString(GameConstants.MAX_BLOCKCHAIN_MESSAGE_LENGTH) + " integers in one message.");
        }
        int teamSoup = gameWorld.getTeamInfo().getSoup(getTeam());
        if (gameWorld.getTeamInfo().getSoup(getTeam()) < cost) {
            throw new GameActionException(NOT_ENOUGH_RESOURCE, 
                    "Tried to pay " + Integer.toString(cost) + " units of soup for a message, only has " + Integer.toString(teamSoup) + ".");
        }
        // pay!
        gameWorld.getTeamInfo().adjustSoup(getTeam(), -cost);
        // create a block chain entry
        BlockchainEntry bcentry = new BlockchainEntry(cost, messageArray);
        // add
        gameWorld.addNewMessage(bcentry);
    }

    /**
     * Gets all messages that were sent at a given round.
     * @param roundNumber the round index.
     * @throws GameActionException
     */
    @Override
    public String getRoundMessages(int roundNumber) throws GameActionException {
        if (roundNumber < 0) {
            throw new GameActionException(ROUND_OUT_OF_RANGE, "You cannot get the messages sent at round " + Integer.toString(roundNumber)
                + "; in fact, no negative round numbers are allowed at all.");
        }
        if (roundNumber >= gameWorld.currentRound) {
            throw new GameActionException(ROUND_OUT_OF_RANGE, "You cannot get the messages sent at round " + Integer.toString(roundNumber)
                + "; you can only query previous rounds, and this is round " + Integer.toString(roundNumber) + ".");
        }
        // just get it!
        ArrayList<BlockchainEntry> d = gameWorld.blockchain.get(roundNumber);
        System.out.println(d);
        BlockchainEntry[] d2 = d.toArray(new BlockchainEntry[d.size()]);
        String[] stringMessageArray = new String[d2.length];
        for (int i = 0; i < d2.length; i++) {
            stringMessageArray[i] = d2[i].serializedMessage;
        }
        String serializedMessage = String.join(" ", stringMessageArray);
        return serializedMessage;
    }


    // ***********************************
    // ****** OTHER ACTION METHODS *******
    // ***********************************

    @Override
    public void disintegrate(){
        throw new RobotDeathException();
    }

    @Override
    public void resign(){
        gameWorld.getObjectInfo().eachRobot((robot) -> {
            if(robot.getTeam() == getTeam()){
                gameWorld.destroyRobot(robot.getID());
            }
            return true;
        });
    }

    // ***********************************
    // **** INDICATOR STRING METHODS *****
    // ***********************************

    @Override
    public void setIndicatorDot(MapLocation loc, int red, int green, int blue) {
        assertNotNull(loc);
        gameWorld.getMatchMaker().addIndicatorDot(getID(), loc, red, green, blue);
    }

    @Override
    public void setIndicatorLine(MapLocation startLoc, MapLocation endLoc, int red, int green, int blue) {
        assertNotNull(startLoc);
        assertNotNull(endLoc);
        gameWorld.getMatchMaker().addIndicatorLine(getID(), startLoc, endLoc, red, green, blue);
    }

    // ***********************************
    // ******** TEAM MEMORY **************
    // ***********************************

    @Override
    public void setTeamMemory(int index, long value) {
        gameWorld.getTeamInfo().setTeamMemory(robot.getTeam(), index, value);
    }

    @Override
    public void setTeamMemory(int index, long value, long mask) {
        gameWorld.getTeamInfo().setTeamMemory(robot.getTeam(), index, value, mask);
    }

    @Override
    public long[] getTeamMemory() {
        long[] arr = gameWorld.getTeamInfo().getOldTeamMemory()[robot.getTeam().ordinal()];
        return Arrays.copyOf(arr, arr.length);
    }

    // ***********************************
    // ******** DEBUG METHODS ************
    // ***********************************

    @Override
    public long getControlBits() {
        return robot.getControlBits();
    }
}
