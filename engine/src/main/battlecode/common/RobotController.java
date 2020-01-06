package battlecode.common;
import java.util.ArrayList;

/**
 * A RobotController allows contestants to make their robot sense and interact
 * with the game world. When a contestant's <code>RobotPlayer</code> is
 * constructed, it is passed an instance of <code>RobotController</code> that
 * controls the newly created robot.
 */
@SuppressWarnings("unused")
public strictfp interface RobotController {

    // *********************************
    // ****** GLOBAL QUERY METHODS *****
    // *********************************

    /**
     * Returns the current round number, where round 1 is the first round of the
     * match.
     *
     * @return the current round number, where round 1 is the first round of the
     * match.
     *
     * @battlecode.doc.costlymethod
     */
    int getRoundNum();

    /**
     * Returns the team's total victory points.
     *
     * @return the team's total victory points.
     *
     * @battlecode.doc.costlymethod
     */
    int getTeamSoup();

    /**
     * Returns the number of robots on your team, including your archons.
     * If this number ever reaches zero, the opposing team will automatically
     * win by destruction.
     *
     * @return the number of robots on your team, including your archons.
     *
     * @battlecode.doc.costlymethod
     */
    int getRobotCount();

    /**
     * Returns a list of the INITIAL locations of the archons of a particular
     * team. The locations will be sorted by increasing x, with ties broken by
     * increasing y. Will return an empty list if you query for {@code Team.NEUTRAL}.
     *
     * @param t the team for which you want to query the initial archon
     * locations. Will return an empty list if you query for Team.NEUTRAL
     * @return a list of the INITIAL locations of the archons of that team, or
     * an empty list for Team.NEUTRAL.
     *
     * @battlecode.doc.costlymethod
     */
    // MapLocation[] getInitialArchonLocations(Team t);

    // *********************************
    // ****** UNIT QUERY METHODS *******
    // *********************************

    /**
     * Returns the ID of this robot.
     *
     * @return the ID of this robot.
     *
     * @battlecode.doc.costlymethod
     */
    int getID();

    /**
     * Returns this robot's Team.
     *
     * @return this robot's Team.
     *
     * @battlecode.doc.costlymethod
     */
    Team getTeam();

    /**
     * Returns this robot's type (SOLDIER, ARCHON, etc.).
     *
     * @return this robot's type.
     *
     * @battlecode.doc.costlymethod
     */
    RobotType getType();

    /**
     * Returns this robot's current location.
     *
     * @return this robot's current location.
     *
     * @battlecode.doc.costlymethod
     */
    MapLocation getLocation();

    /**
     * Returns the amount of soup this robot is carrying.
     *
     * @return the amount of soup this robot is carrying.
     *
     * @battlecode.doc.costlymethod
     */
    int getSoupCarrying();

    /**
     * Returns the amount of dirt this robot is carrying.
     *
     * @return the amount of dirt this robot is carrying.
     *
     * @battlecode.doc.costlymethod
     */
    int getDirtCarrying();

    /**
     * Returns whether the robot is currently holding a unit
     *
     * @return true if the robot is currently holding another unit, false otherwise
     *
     * @battlecode.doc.costlymethod
     */
    public boolean isCurrentlyHoldingUnit();

    // ***********************************
    // ****** GENERAL SENSOR METHODS *****
    // ***********************************

    /**
     * Senses whether a MapLocation is on the map. // Will throw an exception if
     * the location is not currently within sensor range.
     *
     * @param loc the location to check
     * @return true if the location is on the map; false otherwise.
     * // @throws GameActionException if the location is not within sensor range.
     *
     * @battlecode.doc.costlymethod
     */
    boolean onTheMap(MapLocation loc);

    /**
     * Senses whether the given location is within the robot's sensor range.
     *
     * @param loc the location to check
     * @return true if the given location is within the robot's sensor range; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canSenseLocation(MapLocation loc);

    /**
     * Senses whether a point at the given radius squared is within the robot's sensor range.
     *
     * @param radiusSquared the radius to check
     * @return true if the given location is within the robot's sensor range; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canSenseRadiusSquared(int radiusSquared);

    /**
     * Senses whether there is a robot at the given location.
     *
     * @param loc the location to check
     * @return true if there is a robot at the given location; false otherwise.
     * @throws GameActionException if the location is not within sensor range.
     *
     * @battlecode.doc.costlymethod
     */
    boolean isLocationOccupied(MapLocation loc) throws GameActionException;

    /**
     * Senses the robot at the given location, or null if there is no robot
     * there.
     *
     * @param loc the location to check
     * @return the robot at the given location.
     * @throws GameActionException if the location is not within sensor range.
     *
     * @battlecode.doc.costlymethod
     */
    RobotInfo senseRobotAtLocation(MapLocation loc) throws GameActionException;

    /**
     * Tests whether the given robot exists and any part of the given robot is
     * within this robot's sensor range.
     *
     * @param id the ID of the robot to query
     * @return true if the given robot is within this robot's sensor range; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canSenseRobot(int id);

    /**
     * Senses information about a particular robot given its ID.
     *
     * @param id the ID of the robot to query
     * @return a RobotInfo object for the sensed robot.
     * @throws GameActionException if the robot cannot be sensed (for example,
     * if it doesn't exist or is out of sight range).
     *
     * @battlecode.doc.costlymethod
     */
    RobotInfo senseRobot(int id) throws GameActionException;

    /**
     * Returns all robots within sense radius. The objects are returned in order of
     * increasing distance from your robot.
     *
     * @return sorted array of RobotInfo objects, which contain information about all
     * the robots you sensed.
     *
     * @battlecode.doc.costlymethod
     */
    RobotInfo[] senseNearbyRobots();

    /**
     * Returns all robots that can be sensed within a certain radius of this
     * robot. The objects are returned in order of increasing distance from
     * your robot.
     *
     * @param radius return robots this distance away from the center of
     * this robot. If -1 is passed, all robots within sense radius are returned.
     * @return sorted array of RobotInfo objects of all the robots you sensed.
     *
     * @battlecode.doc.costlymethod
     */
    RobotInfo[] senseNearbyRobots(int radius);

    /**
     * Returns all robots of a given team that can be sensed within a certain
     * radius of this robot. The objects are returned in order of increasing distance
     * from your robot.
     *
     * @param radius return robots this distance away from the center of
     * this robot. If -1 is passed, all robots within sense radius are returned
     * @param team filter game objects by the given team. If null is passed,
     * robots from any team are returned
     * @return sorted array of RobotInfo objects of all the robots you sensed.
     *
     * @battlecode.doc.costlymethod
     */
    RobotInfo[] senseNearbyRobots(int radius, Team team);

    /**
     * Returns all robots of a given team that can be sensed within a certain
     * radius of a specified location. The objects are returned in order of
     * increasing distance from the specified center.
     *
     * @param center center of the given search radius
     * @param radius return robots this distance away from the given center
     * location. If -1 is passed, all robots within sense radius are returned
     * @param team filter game objects by the given team. If null is passed,
     * objects from all teams are returned
     * @return sorted array of RobotInfo objects of the robots you sensed.
     *
     * @battlecode.doc.costlymethod
     */
    RobotInfo[] senseNearbyRobots(MapLocation center, int radius, Team team);

    /**
     * Returns the crude soup count at a given location, if the location is
     * within the sensor radius of the robot.
     *
     * @param loc the given location
     * @return the crude soup count at a given location, if the location is
     * within the sensor radius of the robot.
     *
     * @throws GameActionException if robot cannot sense the given location
     *
     * @battlecode.doc.costlymethod
     */
    int senseSoup(MapLocation loc) throws GameActionException;

    /**
     * Returns the pollution level at a given location, if the location is
     * within the sensor radius of the robot.
     *
     * @param loc the given location
     * @return the pollution level at a given location, if the location is
     * within the sensor radius of the robot.
     *
     * @throws GameActionException if the robot cannot sense the given location
     *
     * @battlecode.doc.costlymethod
     */
    int sensePollution(MapLocation loc) throws GameActionException;

    /**
     * Returns the elevation at a given location, if the location is
     * within the sensor radius of the robot.
     *
     * @param loc the given location
     * @return the elevation at a given location, if the location is
     * within the sensor radius of the robot.
     *
     * @throws GameActionException if the robot cannot sense the given location
     *
     * @battlecode.doc.costlymethod
     */
    int senseElevation(MapLocation loc) throws GameActionException;

    /**
     * Returns whether or not a given location is flooded, if the location is
     * within the sensor radius of the robot.
     *
     * @param loc the given location
     * @return whether or not a given location is flooded, if the location is
     * within the sensor radius of the robot.
     *
     * @throws GameActionException if the robot cannot sense the given location
     *
     * @battlecode.doc.costlymethod
     */
    boolean senseFlooding(MapLocation loc) throws GameActionException;

    /**
     * Returns the location adjacent to current location in the given direction.
     *
     * @param dir the given direction
     * @return the location adjacent to current location in the given direction.
     *
     * @battlecode.doc.costlymethod
     */
    MapLocation adjacentLocation(Direction dir);

    // ***********************************
    // ****** READINESS METHODS **********
    // ***********************************
    
    /**
     * Returns whether the robot's action cooldown has expired.
     * 
     * @return true if the robot's action cooldown has expired; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean isReady();

    /**
     * Returns the number of cooldown turns remaining before this unit can act again.
     * When this number is less than 1, isReady() is true.
     *
     * @return the number of cooldown turns remaining before this unit can act again.
     *
     * @battlecode.doc.costlymethod
     */
    float getCooldownTurns();

    // ***********************************
    // ****** MOVEMENT METHODS ***********
    // ***********************************

    /**
     * Tells whether this robot can move one step in the given direction.
     * Returns false if the robot is a building, if the target location
     * is not on the map, if the target location is occupied, if it is
     * flooded and this robot is not a drone, if the dirt difference is
     * too high and this robot is not a drone, and if the robot is ready
     * based on the cooldown.
     *
     * @param dir the direction to move in
     * @return true if it is possible to call <code>move</code> without an exception
     *
     * @battlecode.doc.costlymethod
     */
    boolean canMove(Direction dir);
    
    /**
     * Moves one step in the given direction.
     *
     * @param dir the direction to move in
     * @throws GameActionException if the robot cannot move one step in this
     * direction, such as cooldown being >= 1, the target location being
     * off the map, the target destination being occupied with either
     * another robot, and the robot attempting to climb too high or enter water.
     *
     * @battlecode.doc.costlymethod
     */
    void move(Direction dir) throws GameActionException;

    // ***********************************
    // ****** BUILDING/SPAWNING **********
    // ***********************************

    /**
     * Tests whether you have the dependencies to build the given
     * robot, and this robot is a valid builder for the target robot.
     *
     * @param type the type of robot to build
     * @return true if the requirements to build the given robot are met; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean hasRobotBuildRequirements(RobotType type);

    /**
     * Tests whether the robot can build a robot of the given type in the
     * given direction. Checks cooldown turns remaining,
     * whether the robot can build, and that the given direction is
     * not blocked.
     *
     * @param dir the direction to build in
     * @param type the type of robot to build
     * @return whether it is possible to build a robot of the given type in the
     * given direction.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canBuildRobot(RobotType type, Direction dir);

    /**
     * Builds a robot of the given type in the given direction.
     *
     * @param dir the direction to spawn the unit
     * @param type the type of robot to build
     * @throws GameActionException if you don't have enough currency, if
     * the robot is still in build cooldown, if the direction is not a
     * good build direction, or if this robot is not of an appropriate type.
     *
     * @battlecode.doc.costlymethod
     */
    void buildRobot(RobotType type, Direction dir) throws GameActionException;

    // ***********************************
    // ****** MINER METHODS **************
    // ***********************************

    /**
     * Tests whether the robot can mine soup in the given direction.
     * Checks cooldown turns remaining, whether the robot can mine,
     * and that the given direction has soup.
     *
     * @param dir the direction to mine
     * @return whether it is possible to mine soup in the given direction.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canMineSoup(Direction dir);

    /**
     * Mines soup in the given direction. Mines up to GameConstants.SOUP_MINING_RATE,
     * limited by how much soup the miner can still carry and how much soup exists at
     * the location.
     *
     * @param dir the direction to mine
     * @throws GameActionException if this robot is not a miner, if
     * the robot is still in cooldown, or if there is no soup to mine.
     *
     * @battlecode.doc.costlymethod
     */
    void mineSoup(Direction dir) throws GameActionException;

    /**
     * Tests whether the robot can deposit soup in the given direction.
     * Checks cooldown turns remaining, whether the robot can deposit soup, whether
     * the robot has crude soup, and that the given direction has a refinery.
     *
     * @param dir the direction to deposit soup
     * @return whether it is possible to deposit soup in the given direction.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canDepositSoup(Direction dir);

    /**
     * Deposits soup in the given direction (max up to specified amount).
     *
     * @param dir the direction to deposit soup
     * @param amount the amount of soup to deposit
     * @throws GameActionException if this robot is not a miner, if
     * the robot is still in cooldown, if there is no soup to deposit,
     * or if there is no refinery.
     *
     * @battlecode.doc.costlymethod
     */
    void depositSoup(Direction dir, int amount) throws GameActionException;

    // ***************************************
    // ********* LANDSCAPER METHODS **********
    // ***************************************

    /**
     * Tests whether the robot can dig dirt in the given direction.
     * Checks cooldown turns remaining, whether the robot can dig,
     * that the robot can carry more dirt, and that the location is valid.
     *
     * @param dir the direction to dig in
     * @return whether it is possible to dig dirt from the given direction.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canDigDirt(Direction dir);

    /**
     * Digs dirt in the given direction.
     *
     * @param dir the direction to dig in
     * @throws GameActionException if this robot is not a landscaper, if
     * the robot is still in cooldown, the robot cannot carry more dirt,
     * or the location is not valid (there is a robot/building).
     *
     * @battlecode.doc.costlymethod
     */
    void digDirt(Direction dir) throws GameActionException;

    /**
     * Tests whether the robot can deposit dirt in the given direction.
     * Checks cooldown turns remaining, whether the robot can deposit dirt,
     * and whether the robot has dirt.
     *
     * @param dir the direction to deposit
     * @return whether it is possible to deposit dirt in the given direction.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canDepositDirt(Direction dir);

    /**
     * Deposits 1 unit of dirt in the given direction.
     *
     * @param dir the direction to deposit
     * @throws GameActionException if this robot is not a landscaper, if
     * the robot is still in cooldown, if there is no dirt to deposit,
     *
     * @battlecode.doc.costlymethod
     */
    void depositDirt(Direction dir) throws GameActionException;

    // ***************************************
    // ******* DELIVERY DRONE METHODS ********
    // ***************************************

    /**
     * Tests whether a robot is able to pick up a specific unit.
     *
     * @param id the id of the robot to pick up
     * @return true if robot with the id can be picked up, false otherwise
     */
    boolean canPickUpUnit(int id);

    /**
     * Picks up another unit.
     *
     * @param id the id of the robot to pick up
     *
     * @throws GameActionException if the robot is not of type DELIVERY_DRONE or cannot pick up
     * a unit because it is already carrying a unit or if the unit to pick up is not in the radius
     * of this robot.
     */
    void pickUpUnit(int id) throws GameActionException;

    /**
     * Tests whether a robot is able to drop a unit in a specified direction.
     *
     * @param dir the specified direction
     * @return true if a robot can be dropped off, false otherwise
     */
    boolean canDropUnit(Direction dir);

    /**
     * Drops the unit that is currently picked up.
     *
     * @param dir the direction to drop in
     *
     * @throws GameActionException if the robot is not of type DELIVERY_DRONE or if the robot is not currently
     * holding a unit that it can drop.
     */
    void dropUnit(Direction dir) throws GameActionException;

    // ***************************************
    // ******* NET GUN METHODS ***************
    // ***************************************

    /**
     * Tests whether a robot is able to shoot down a specific unit.
     *
     * @param id the id of the robot to shoot
     * @return true if robot with the id can be shot down, false otherwise
     */
    boolean canShootUnit(int id);

    /**
     * Shoots down another unit.
     *
     * @param id the id of the unit to shoot
     *
     * @throws GameActionException if the robot is not of type NET_GUN,
     *  or the robot's action cooldown is not ready, or if the unit to
     *  shoot down is not in the radius of this robot, or if the unit
     *  cannot be shot down.
     */
    void shootUnit(int id) throws GameActionException;

    // ***********************************
    // ****** OTHER ACTION METHODS *******
    // ***********************************

    /**
     * Causes your team to lose the game. It's like typing "gg."
     *
     * @battlecode.doc.costlymethod
     */
    void resign();

    // ***********************************
    // ****** BLOCKCHAINNNNNNNNNNN *******
    // ***********************************

    /**
     * Checks that the robot can submit a transaction to the blockchain at the indicated cost.
     *
     * @param message the list of ints to send.
     * @param cost the price that the unit is willing to pay for the message
     * @return whether the transaction can be submitted or not
     */
    boolean canSubmitTransaction(int[] message, int cost);

    /**
     * Submits a transaction to the blockchain at the indicated cost.
     * 
     * @param message the list of ints to send.
     * @param cost the price that the unit is willing to pay for the message
     *
     * @throws GameActionException if the team does not have enough soup to cover the cost,
     *  or if the message exceeds the allowed limit
     */
    void submitTransaction(int[] message, int cost) throws GameActionException;


    /**
     * Get the block of messages that was approved at a given round.
     * The block will contain a list of transactions.
     *
     * @param roundNumber the round index.
     * @return an array of Transactions that were accepted into the blockchain
     *  at the given round, in no particular order.
     *
     * @throws GameActionException if the round is not available.
     */
    public Transaction[] getBlock(int roundNumber) throws GameActionException;

    // ***********************************
    // ******** DEBUG METHODS ************
    // ***********************************

    /**
     * Draw a dot on the game map for debugging purposes.
     *
     * @param loc the location to draw the dot.
     * @param red the red component of the dot's color.
     * @param green the green component of the dot's color.
     * @param blue the blue component of the dot's color.
     *
     * @battlecode.doc.costlymethod
     */
    void setIndicatorDot(MapLocation loc, int red, int green, int blue);

    /**
     * Draw a line on the game map for debugging purposes.
     *
     * @param startLoc the location to draw the line from.
     * @param endLoc the location to draw the line to.
     * @param red the red component of the line's color.
     * @param green the green component of the line's color.
     * @param blue the blue component of the line's color.
     *
     * @battlecode.doc.costlymethod
     */
    void setIndicatorLine(MapLocation startLoc, MapLocation endLoc, int red, int green, int blue);

}
