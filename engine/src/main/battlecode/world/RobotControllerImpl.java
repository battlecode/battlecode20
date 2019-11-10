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

    @Override
    public boolean isCurrentlyHoldingUnit() {
        return this.robot.isCurrentlyHoldingUnit();
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
        System.out.println("I'm trying to go to " + center + "; " + getType().canMove() + " " + getLocation().distanceTo(center) + " " + gameWorld.getGameMap().onTheMap(center) + " " + gameWorld.getObjectInfo().isEmpty(center));
        return getType().canMove() && getLocation().distanceTo(center) <= 1 &&
               gameWorld.getGameMap().onTheMap(center) && gameWorld.getObjectInfo().isEmpty(center);
    }

    @Override
    public void move(Direction dir) throws GameActionException {
        MapLocation center = getLocation().add(dir);
        assertNotNull(center);
        assertIsReady();
        assertCanMove(center);
        this.robot.resetCooldownTurns();
        this.robot.setLocation(center);

        gameWorld.getMatchMaker().addMoved(getID(), getLocation());

        // also move the robot currently being picked up
        if (this.robot.isCurrentlyHoldingUnit()) {
            movePickedUpUnit(center);
        }
    }

    private void movePickedUpUnit(MapLocation center) throws GameActionException {
        int id = this.robot.getIdOfUnitCurrentlyHeld();
        InternalRobot robot = gameWorld.getObjectInfo().getRobotByID(id);
        robot.setLocation(center);

        gameWorld.getMatchMaker().addMoved(id, getLocation());
    }

    @Override
    public boolean canPickUpOtherUnits() {
        return getType() == RobotType.DELIVERY_DRONE;
    }

    @Override
    public boolean canPickUpUnit(int id) {
        return canPickUpOtherUnits() && unitWithinPickupDistance(id);
    }

    @Override
    public void pickUpUnit(int id) throws GameActionException {
        if (getType() != RobotType.DELIVERY_DRONE) {
            throw new GameActionException(CANT_DO_THAT, "Only delivery drones can pick up other units");
        } else if (this.robot.isCurrentlyHoldingUnit()) {
            throw new GameActionException(CANT_DO_THAT, "Delivery drone is already holding a unit");
        } else if (!unitWithinPickupDistance(id)) {
            throw new GameActionException(CANT_DO_THAT, "Cannot pick up; that unit is too far away");
        }

        InternalRobot robot = gameWorld.getObjectInfo().getRobotByID(id);
        robot.blockUnit();

        this.robot.pickUpUnit(id);
    }

    /**
     * Check whether the specified unit is within the pickup radius of the robot
     *
     * @param id the id of the robot to pick up
     */
    private boolean unitWithinPickupDistance(int id) {
        for (InternalRobot adjacentRobot :
                gameWorld.getObjectInfo().getAllRobotsWithinRadius(getLocation(), RobotType.DELIVERY_DRONE.bodyRadius + GameConstants.DELIVERY_DRONE_PICKUP_RADIUS)) {
            if (adjacentRobot.getID() == id) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void dropUnit() throws GameActionException {
        if (getType() != RobotType.DELIVERY_DRONE || !this.robot.isCurrentlyHoldingUnit()) {
            throw new GameActionException(CANT_DO_THAT, "Only delivery drones can drop other units");
        }

        int id = this.robot.getIdOfUnitCurrentlyHeld();
        InternalRobot robot = gameWorld.getObjectInfo().getRobotByID(id);
        robot.unBlockUnit();

        this.robot.dropUnit();

        // TODO: need to process killing
        if (false) {
            gameWorld.destroyRobot(id);
        }
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
    public void buildRobot(RobotType type, Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanBuildRobot(type, dir);

        this.robot.resetCooldownTurns();
        
        MapLocation spawnLoc = getLocation().add(dir); // TODO fix spawn dist

        int robotID = gameWorld.spawnRobot(type, spawnLoc, getTeam());

        gameWorld.getMatchMaker().addAction(getID(), Action.SPAWN_UNIT, robotID);
    }
    
    @Override
    public boolean canHireMiner(Direction dir) {
        return (getType() == RobotType.HQ && canBuildRobot(RobotType.MINER, dir));
    }

    @Override
    public void hireMiner(Direction dir) throws GameActionException {
        assert (canHireMiner(dir));
        buildRobot(RobotType.MINER, dir);
    }

    @Override
    public boolean canHireLandscaper(Direction dir) {
        return (getType() == RobotType.DESIGN_SCHOOL && canBuildRobot(RobotType.LANDSCAPER, dir));
    }

    @Override
    public void hireLandscaper(Direction dir) throws GameActionException {
        assert (canHireMiner(dir));
        buildRobot(RobotType.LANDSCAPER, dir);
    }

    @Override
    public boolean canBuildDrone(Direction dir) {
        return (getType() == RobotType.FULFILLMENT_CENTER && canBuildRobot(RobotType.DRONE, dir));
    }

    @Override
    public void buildDrone(Direction dir) throws GameActionException {
        assert (canHireMiner(dir));
        buildRobot(RobotType.DRONE, dir);
    }

    @Override
    public boolean canBuildDeliveryDrone(Direction dir) {
        return canBuildRobot(RobotType.DELIVERY_DRONE, dir);
    }

    @Override
    public void buildDeliveryDrone(Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanBuildRobot(RobotType.DELIVERY_DRONE, dir);

        this.robot.setBuildCooldownTurns(RobotType.DELIVERY_DRONE.buildCooldownTurns);

        float spawnDist = getType().bodyRadius +
                GameConstants.GENERAL_SPAWN_OFFSET +
                RobotType.DELIVERY_DRONE.bodyRadius;
        MapLocation spawnLoc = getLocation().add(dir, spawnDist);

        int robotID = gameWorld.spawnRobot(RobotType.DELIVERY_DRONE, spawnLoc, getTeam());

        gameWorld.getMatchMaker().addAction(getID(), Action.SPAWN_UNIT, robotID);

    }

    // **************************************
    // ********* DIRT MANIPULATION **********
    // **************************************


    @Override
    public boolean canDig(Direction dir) {
        assertNotNull(dir); //TODO: check soup/bytecode requirements
        return (getType() == RobotType.MINER);
    }

    @Override
    public void dig(Direction dir) {
        assert(canDig(dir)); //TODO: write methods in GameWorld to change dirt, interface with these
    }

    @Override
    public boolean canDeposit(Direction dir) {
        assertNotNull(dir); //TODO: check soup/bytecode requirements
        return (getType() == RobotType.MINER);
    }

    @Override
    public void deposit(Direction dir) {
        assert(canDeposit(dir)); //TODO: write methods in GameWorld to change dirt, interface with these
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
        return getType().canMine() && isReady() && gameWorld.getSoup(getLocation().add(dir)) > 0;
    }

    @Override
    public void mineSoup(Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanMineSoup(dir);
        this.robot.resetCooldownTurns();
        this.gameWorld.removeSoup(getLocation().add(dir));
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
        InternalRobot adjacentRobot = gameWorld.getObjectInfo().getRobotAtLocation(getLocation().add(dir));
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
        InternalRobot refinery = this.gameWorld.getObjectInfo().getRobotAtLocation(getLocation().add(dir));
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
