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
    public int getRobotCount(){
        return gameWorld.getObjectInfo().getRobotCount(getTeam());
    }

    @Override
    public MapLocation[] getInitialArchonLocations(Team t){
        assertNotNull(t);
        if (t == Team.NEUTRAL) {
            return new MapLocation[0];
        } else {
            BodyInfo[] initialRobots = gameWorld.getGameMap().getInitialBodies();
            ArrayList<MapLocation> archonLocs = new ArrayList<>();
            for (BodyInfo initial : initialRobots) {
                if(initial.isRobot()){
                    RobotInfo robot = (RobotInfo) initial;
                    if (robot.type == RobotType.ARCHON && robot.team == t) {
                        archonLocs.add(robot.getLocation());
                    }
                }
            }
            MapLocation[] array = archonLocs.toArray(new MapLocation[archonLocs.size()]);
            Arrays.sort(array);
            return array;
        }
    }

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

    @Override
    public float getHealth(){
        return this.robot.getHealth();
    }
    
    @Override
    public int getAttackCount(){
        return this.robot.getAttackCount();
    }
    
    @Override
    public int getMoveCount(){
        return this.robot.getMoveCount();
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

    private void assertCanSensePartOfCircle(MapLocation center, float radius) throws GameActionException{
        if(!canSensePartOfCircle(center, radius)){
            throw new GameActionException(CANT_SENSE_THAT,
                    "Target circle not within sensor range");
        }
    }

    private void assertCanSenseAllOfCircle(MapLocation center, float radius) throws GameActionException{
        if(!canSenseAllOfCircle(center, radius)){
            throw new GameActionException(CANT_SENSE_THAT,
                    "Target circle not completely within sensor range");
        }
    }

    @Override
    public boolean onTheMap(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        return gameWorld.getGameMap().onTheMap(loc);
    }

    @Override
    public boolean onTheMap(MapLocation center, float radius) throws GameActionException{
        assertNotNull(center);
        assertCanSenseAllOfCircle(center, radius);
        return gameWorld.getGameMap().onTheMap(center, radius);
    }

    @Override
    public boolean canSenseLocation(MapLocation loc) {
        assertNotNull(loc);
        return this.robot.canSenseLocation(loc);
    }

    @Override
    public boolean canSenseRadius(float radius) {
        return this.robot.canSenseRadius(radius);
    }

    @Override
    public boolean canSensePartOfCircle(MapLocation center, float radius){
        assertNotNull(center);
        return canSenseRadius(getLocation().distanceTo(center)-radius);
    }

    @Override
    public boolean canSenseAllOfCircle(MapLocation center, float radius){
        assertNotNull(center);
        return canSenseRadius(getLocation().distanceTo(center)+radius);
    }

    @Override
    public boolean isLocationOccupied(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        return !gameWorld.getObjectInfo().isEmpty(loc, 0);
    }

    @Override
    public boolean isLocationOccupiedByRobot(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        return gameWorld.getObjectInfo().getRobotAtLocation(loc) != null;
    }

    @Override
    public boolean isCircleOccupied(MapLocation center, float radius) throws GameActionException{
        assertNotNull(center);
        assertCanSenseAllOfCircle(center, radius);
        return !gameWorld.getObjectInfo().isEmpty(center, radius);
    }

    @Override
    public boolean isCircleOccupiedExceptByThisRobot(MapLocation center, float radius) throws GameActionException{
        assertNotNull(center);
        assertCanSenseAllOfCircle(center, radius);
        return !gameWorld.getObjectInfo().isEmptyExceptForRobot(center, radius, robot);
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
        return canSensePartOfCircle(robot.getLocation(), robot.getType().bodyRadius);
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
    public RobotInfo[] senseNearbyRobots(float radius) {
        return senseNearbyRobots(radius, null);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(float radius, Team team) {
        return senseNearbyRobots(getLocation(), radius, team);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(MapLocation center, float radius, Team team) {
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
            if(!canSensePartOfCircle(sensedRobot.getLocation(), sensedRobot.getType().bodyRadius)){
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

    private void assertMoveReady() throws GameActionException{
        if(hasMoved()){
            throw new GameActionException(NOT_ACTIVE,
                    "This robot has already moved this turn.");
        }
    }

    private void assertIsWeaponReady() throws GameActionException{
        if(hasAttacked()){
            throw new GameActionException(NOT_ACTIVE,
                    "This robot has already attacked this turn.");
        }
    }
    
    private void assertIsBuildReady() throws GameActionException{
        if(!isBuildReady()){
            throw new GameActionException(NOT_ACTIVE,
                    "This robot's build cooldown has not expired.");
        }
    }

    @Override
    public boolean hasMoved() {
        return getMoveCount() > 0;
    }

    @Override
    public boolean hasAttacked() {
        return getAttackCount() > 0;
    }
    
    @Override
    public boolean isBuildReady() {
        return this.robot.getBuildCooldownTurns() == 0;
    }

    @Override
    public int getBuildCooldownTurns() {
        return this.robot.getBuildCooldownTurns();
    }

    // ***********************************
    // ****** MOVEMENT METHODS ***********
    // ***********************************

  /*  private void assertIsPathable(MapLocation loc) throws GameActionException{
        if(!onTheMap(loc, getType().bodyRadius) ||
                isCircleOccupiedExceptByThisRobot(loc, getType().bodyRadius)){
            throw new GameActionException(CANT_MOVE_THERE,
                    "Cannot move to target location " + loc + ".");
        }
    }*/
    private void assertCanMove(MapLocation loc) throws GameActionException{
        if(!canMove(loc))
            throw new GameActionException(CANT_MOVE_THERE,
                    "Cannot move to the target location " + loc +".");
    }

    @Override
    public boolean canMove(Direction dir) {
        return canMove(dir, 1f);
    }

    @Override
    public boolean canMove(Direction dir, float dist) {
        assertNotNull(dir);
        MapLocation center = getLocation().add(dir, dist);
        return canMove(center);
    }
    
    @Override
    public boolean canMove(MapLocation center) {
        assertNotNull(center);
        float dist = getLocation().distanceTo(center);
        if(dist > 1f) {
            Direction dir = getLocation().directionTo(center);
            center = getLocation().add(dir, 1f);
        }
        boolean newLocationIsEmpty;
        if(getType() != RobotType.TANK && getType() != RobotType.SCOUT) {
            newLocationIsEmpty = gameWorld.getObjectInfo().isEmptyExceptForRobot(center, getType().bodyRadius, robot);
        } else { // Tanks have special condition due to body attack, Scouts can just go over ...
            newLocationIsEmpty = gameWorld.getObjectInfo().noRobotsExceptForRobot(center, getType().bodyRadius, robot);
        }
        return gameWorld.getGameMap().onTheMap(center, getType().bodyRadius) &&
                newLocationIsEmpty;
    }

    @Override
    public void move(Direction dir) throws GameActionException {
        move(dir, getType().strideRadius);
    }

    @Override
    public void move(Direction dir, float dist) throws GameActionException {
        assertNotNull(dir);
        assertMoveReady();
        dist = Math.max(0, Math.min(dist, getType().strideRadius));
        MapLocation center = getLocation().add(dir, dist);
        move(center);
    }
    
    @Override
    public void move(MapLocation center) throws GameActionException {
        assertNotNull(center);
        assertMoveReady();
        float dist = getLocation().distanceTo(center);
        if(dist > getType().strideRadius) {
            Direction dir = getLocation().directionTo(center);
            center = getLocation().add(dir, getType().strideRadius);
        }
        assertCanMove(center);
        
        this.robot.incrementMoveCount();
        if(getType() == RobotType.TANK) { // If Tank, see if can actually move, as opposed to just body attack
            
        }
        this.robot.setLocation(center);

        gameWorld.getMatchMaker().addMoved(getID(), getLocation());
    }

    // ***********************************
    // ****** ATTACK METHODS *************
    // ***********************************

    private void assertNonNegative(float cost) throws GameActionException{
        if(cost < 0) {
            throw new GameActionException(CANT_DO_THAT,
                    "Can't purchase negative victory points");
        }
    }

    @Override
    public boolean canStrike() {
        boolean correctType = getType() == RobotType.LUMBERJACK;

        return correctType && !hasAttacked();
    }

    @Override
    public void strike() throws GameActionException {
        if(getType() != RobotType.LUMBERJACK){
            throw new GameActionException(CANT_DO_THAT,
                    "Only lumberjacks can strike");
        }
        assertIsWeaponReady();

        this.robot.incrementAttackCount(); // Striking counts as attack.

        // Hit adjacent robots
        for(InternalRobot hitRobot :
                gameWorld.getObjectInfo().getAllRobotsWithinRadius(getLocation(), GameConstants.LUMBERJACK_STRIKE_RADIUS)){
            if(hitRobot.equals(this.robot)){
                continue;
            }
            hitRobot.damageRobot(getType().attackPower);
        }

        gameWorld.getMatchMaker().addAction(getID(), Action.LUMBERJACK_STRIKE, -1);
    }

    private boolean canInteractWithLocation(MapLocation loc){
        assertNotNull(loc);
        return this.robot.canInteractWithLocation(loc);
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
        boolean validBuilder = getType() == type.spawnSource;
        return validBuilder;
    }

    @Override
    public boolean canBuildRobot(RobotType type, Direction dir) {
        assertNotNull(type);
        assertNotNull(dir);
        boolean hasBuildRequirements = hasRobotBuildRequirements(type);
        float spawnDist = getType().bodyRadius +
                GameConstants.GENERAL_SPAWN_OFFSET +
                type.bodyRadius;
        MapLocation spawnLoc = getLocation().add(dir, spawnDist);
        boolean isClear = gameWorld.getGameMap().onTheMap(spawnLoc, type.bodyRadius) &&
                gameWorld.getObjectInfo().isEmpty(spawnLoc, type.bodyRadius);
        boolean cooldownExpired = isBuildReady();

        // THIS IS LOOOOL
        isClear = gameWorld.getGameMap().onTheMap(spawnLoc, 0) &&
                gameWorld.getObjectInfo().isEmpty(spawnLoc, 0);
        return hasBuildRequirements && isClear && cooldownExpired;
    }
    
    @Override
    public boolean canHireGardener(Direction dir) {
        return canBuildRobot(RobotType.GARDENER, dir);
    }

    @Override
    public void hireGardener(Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanBuildRobot(RobotType.GARDENER, dir);

        this.robot.setBuildCooldownTurns(RobotType.GARDENER.buildCooldownTurns);
        
        float spawnDist = getType().bodyRadius +
                GameConstants.GENERAL_SPAWN_OFFSET +
                RobotType.GARDENER.bodyRadius;
        MapLocation spawnLoc = getLocation().add(dir, spawnDist);

        int robotID = gameWorld.spawnRobot(RobotType.GARDENER, spawnLoc, getTeam());

        gameWorld.getMatchMaker().addAction(getID(), Action.SPAWN_UNIT, robotID);
    }

    @Override
    public void buildRobot(RobotType type, Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanBuildRobot(type, dir);

        this.robot.setBuildCooldownTurns(type.buildCooldownTurns);
        
        float spawnDist = getType().bodyRadius +
                GameConstants.GENERAL_SPAWN_OFFSET +
                type.bodyRadius;
        MapLocation spawnLoc = getLocation().add(dir, spawnDist);

        int robotID = gameWorld.spawnRobot(type, spawnLoc, getTeam());

        gameWorld.getMatchMaker().addAction(getID(), Action.SPAWN_UNIT, robotID);
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
