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
    public int getRoundLimit() {
        return gameWorld.getGameMap().getRounds();
    }

    @Override
    public int getRoundNum() {
        return gameWorld.getCurrentRound();
    }

    @Override
    public int getTeamSoup() {
        return gameWorld.getTeamInfo().getSoup(getTeam());
    }

    @Override
    public int getRobotCount() {
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
    public int getID() {
        return this.robot.getID();
    }

    @Override
    public Team getTeam() {
        return this.robot.getTeam();
    }

    @Override
    public RobotType getType() {
        return this.robot.getType();
    }

    @Override
    public MapLocation getLocation() {
        return this.robot.getLocation();
    }

    @Override
    public int getSoupCarrying() {
        return this.robot.getSoupCarrying();
    }

    @Override
    public int getDirtCarrying() {
        return this.robot.getDirtCarrying();
    }

    // ***********************************
    // ****** GENERAL SENSOR METHODS *****
    // ***********************************

    private void assertCanSenseLocation(MapLocation loc) throws GameActionException {
        if(!canSenseLocation(loc)){
            throw new GameActionException(CANT_SENSE_THAT,
                    "Target location not within sensor range");
        }
    }

    @Override
    public boolean onTheMap(MapLocation loc) {
        assertNotNull(loc);
        // assertCanSenseLocation(loc);
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
        return this.gameWorld.getRobot(loc) != null;
    }

    @Override
    public RobotInfo senseRobotAtLocation(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        InternalRobot bot = gameWorld.getRobot(loc);
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
        InternalRobot[] allSensedRobots = gameWorld.getAllRobotsWithinRadius(center,
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

    @Override
    public MapLocation adjacentLocation(Direction dir) {
        return getLocation().add(dir);
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
        return canMove(adjacentLocation(dir));
    }

    @Override
    public boolean canMove(MapLocation center) {
        try {
            assertNotNull(center);
            return getType().canMove() && getLocation().distanceTo(center) <= 1 &&
                onTheMap(center) && !isLocationOccupied(center);
        } catch (GameActionException e) { return false; }
    }

    @Override
    public void move(Direction dir) throws GameActionException {
        MapLocation center = adjacentLocation(dir);
        assertNotNull(center);
        assertIsReady();
        assertCanMove(center);
        this.robot.resetCooldownTurns();
        this.gameWorld.moveRobot(getLocation(), center);
        this.robot.setLocation(center);

        this.gameWorld.getMatchMaker().addMoved(getID(), getLocation());
    }

    // ***********************************
    // ****** BUILDING/SPAWNING **********
    // ***********************************

    private void assertCanBuildRobot(RobotType type, Direction dir) throws GameActionException{
        if(!canBuildRobot(type, dir)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't build desired robot in given direction, possibly due to " +
                            "insufficient currency, this robot can't build, " +
                            "cooldown not expired, or the spawn location is occupied.");
        }
    }

    @Override
    public boolean hasRobotBuildRequirements(RobotType type) {
        assertNotNull(type);
        return getType().canBuild(type) &&
               gameWorld.getTeamInfo().getSoup(getTeam()) >= type.cost;
    }

    @Override
    public boolean canBuildRobot(RobotType type, Direction dir) {
        try {
            assertNotNull(type);
            assertNotNull(dir);
            boolean hasBuildRequirements = hasRobotBuildRequirements(type);
            MapLocation spawnLoc = adjacentLocation(dir);
            boolean isClear = onTheMap(spawnLoc) && !isLocationOccupied(spawnLoc);
            boolean cooldownExpired = isReady();
            return hasBuildRequirements && isClear && cooldownExpired;
        } catch (GameActionException e) { return false; }
    }

    @Override
    public void buildRobot(RobotType type, Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanBuildRobot(type, dir);

        this.robot.resetCooldownTurns();

        int robotID = gameWorld.spawnRobot(type, adjacentLocation(dir), getTeam());

        gameWorld.getMatchMaker().addAction(getID(), Action.SPAWN_UNIT, robotID);
    }

    // ***********************************
    // ****** MINER METHODS **************
    // ***********************************

    private void assertCanMineSoup(Direction dir) throws GameActionException{
        if(!canMineSoup(dir)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't mine soup in given direction, possibly due to " +
                            "cooldown not expired, this robot can't mine, " +
                            "or the mine location doesn't contain soup.");
        }
    }

    @Override
    public boolean canMineSoup(Direction dir) {
        MapLocation center = adjacentLocation(dir);
        return getType().canMine() && isReady() && onTheMap(center) && gameWorld.getSoup(center) > 0;
    }

    @Override
    public void mineSoup(Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanMineSoup(dir);
        this.robot.resetCooldownTurns();
        this.gameWorld.removeSoup(adjacentLocation(dir));
        this.robot.addSoupCarrying(1);

        this.gameWorld.getMatchMaker().addAction(getID(), Action.MINE_SOUP, -1);
    }

    private void assertCanRefineSoup(Direction dir) throws GameActionException{
        if(!canRefineSoup(dir)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't refine soup in given direction, possibly due to " +
                            "cooldown not expired, this robot can't refine, " +
                            "this robot doesn't have crude soup, " +
                            "or the location doesn't have a refinery.");
        }
    }

    @Override
    public boolean canRefineSoup(Direction dir) {
        MapLocation center = adjacentLocation(dir);
        if (!onTheMap(center))
            return false;
        InternalRobot adjacentRobot = this.gameWorld.getRobot(center);
        return getType().canRefine() && isReady() && getSoupCarrying() > 0 &&
               adjacentRobot != null && adjacentRobot.getType() == RobotType.REFINERY;
    }

    @Override
    public void refineSoup(Direction dir, int amount) throws GameActionException {
        assertNotNull(dir);
        assertCanRefineSoup(dir);
        if (amount > this.getSoupCarrying())
            amount = this.getSoupCarrying();
        this.robot.resetCooldownTurns();
        this.robot.removeSoupCarrying(amount);
        InternalRobot refinery = this.gameWorld.getRobot(adjacentLocation(dir));
        refinery.addSoupCarrying(amount);

        this.gameWorld.getMatchMaker().addAction(getID(), Action.REFINE_SOUP, refinery.getID());
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
    // ******** DEBUG METHODS ************
    // ***********************************

    @Override
    public long getControlBits() {
        return robot.getControlBits();
    }
}
