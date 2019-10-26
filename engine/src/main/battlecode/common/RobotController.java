package battlecode.common;

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
     * Returns the number of rounds in the game. After this many rounds, if neither
     * team has been destroyed, tiebreakers will be used.
     *
     * @return the number of rounds in the game.
     *
     * @battlecode.doc.costlymethod
     */
    int getRoundLimit();

    /**
     * Returns the current round number, where round 0 is the first round of the
     * match.
     *
     * @return the current round number, where round 0 is the first round of the
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
    int getTeamVictoryPoints();

    /**
     * Returns your opponent's total victory points.
     *
     * @return your opponent's total victory points.
     *
     * @battlecode.doc.costlymethod
     */
    int getOpponentVictoryPoints();

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
    MapLocation[] getInitialArchonLocations(Team t);

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
     * Returns this robot's current health.
     *
     * @return this robot's current health.
     *
     * @battlecode.doc.costlymethod
     */
    float getHealth();
    
    /**
     * Returns the number of times the robot has attacked this turn.
     * 
     * @return the number of times the robot has attacked this turn.
     *
     * @battlecode.doc.costlymethod
     */
    int getAttackCount();
    
    /**
     * Returns the number of times the robot has moved this turn.
     * 
     * @return the number of times the robot has moved this turn.
     *
     * @battlecode.doc.costlymethod
     */
    int getMoveCount();

    // ***********************************
    // ****** GENERAL SENSOR METHODS *****
    // ***********************************

    /**
     * Senses whether a MapLocation is on the map. Will throw an exception if
     * the location is not currently within sensor range.
     *
     * @param loc the location to check
     * @return true if the location is on the map; false otherwise.
     * @throws GameActionException if the location is not within sensor range.
     *
     * @battlecode.doc.costlymethod
     */
    boolean onTheMap(MapLocation loc) throws GameActionException;

    /**
     * Senses whether a given circle is completely on the map. Will throw an exception if
     * the circle is not completely within sensor range.
     *
     * @param center the center of the circle to check
     * @param radius the radius of the circle to check
     * @return true if the circle is completely on the map; false otherwise.
     * @throws GameActionException if any portion of the given circle is not within sensor range.
     *
     * @battlecode.doc.costlymethod
     */
    boolean onTheMap(MapLocation center, float radius) throws GameActionException;

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
     * Senses whether a point at the given radius is within the robot's sensor range.
     *
     * @param radius the radius to check
     * @return true if the given location is within the robot's sensor range; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canSenseRadius(float radius);

    /**
     * Senses whether any portion of the given circle is within the robot's sensor range.
     *
     * @param center the center of the circle to check
     * @param radius the radius of the circle to check
     * @return true if a portion of the circle is within the robot's sensor range; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canSensePartOfCircle(MapLocation center, float radius);

    /**
     * Senses whether all of the given circle is within the robot's sensor range.
     *
     * @param center the center of the circle to check
     * @param radius the radius of the circle to check
     * @return true if all of the circle is within the robot's sensor range; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canSenseAllOfCircle(MapLocation center, float radius);

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
     * Senses whether there is a robot at the given location.
     *
     * @param loc the location to check
     * @return true if there is a robot at the given location; false otherwise.
     * @throws GameActionException if the location is not within sensor range.
     *
     * @battlecode.doc.costlymethod
     */
    boolean isLocationOccupiedByRobot(MapLocation loc) throws GameActionException;

    /**
     * Senses whether there is any robot within a given circle.
     *
     * @param center the center of the circle to check
     * @param radius the radius of the circle to check
     * @return true if there is a robot in the given circle; false otherwise.
     * @throws GameActionException if any portion of the given circle is not within sensor range.
     *
     * @battlecode.doc.costlymethod
     */
    boolean isCircleOccupied(MapLocation center, float radius) throws GameActionException;

    /**
     * Senses whether there is any robot within a given circle, ignoring this robot
     * if it itself occupies the circle.
     *
     * @param center the center of the circle to check
     * @param radius the radius of the circle to check
     * @return true if there is a robot in the given circle; false otherwise.
     * @throws GameActionException if any portion of the given circle is not within sensor range.
     *
     * @battlecode.doc.costlymethod
     */
    boolean isCircleOccupiedExceptByThisRobot(MapLocation center, float radius) throws GameActionException;

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
    RobotInfo[] senseNearbyRobots(float radius);

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
    RobotInfo[] senseNearbyRobots(float radius, Team team);

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
    RobotInfo[] senseNearbyRobots(MapLocation center, float radius, Team team);


    // ***********************************
    // ****** MOVEMENT METHODS ***********
    // ***********************************
    
    /**
     * Returns whether the robot has moved this turn.
     * 
     * @return true if the robot has moved this turn; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean hasMoved();
    
    /**
     * Returns whether the robot has attacked this turn.
     * 
     * @return true if the robot has attacked this turn; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean hasAttacked();
    
    /**
     * Returns whether the robot's build cooldown has expired.
     * 
     * @return true if the robot's build cooldown has expired; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean isBuildReady();

    /**
     * Returns the number of cooldown turns remaining before this unit can build() again.
     * When this number is 0, isBuildReady() is true.
     *
     * @return the number of cooldown turns remaining before this unit can build() again.
     *
     * @battlecode.doc.costlymethod
     */
    int getBuildCooldownTurns();

    /**
     * Tells whether this robot can move one stride in the given direction,
     * without taking into account if they have already moved. Takes into account only
     * the positions of other robots, and the edge of the
     * game map. Does not take into account whether this robot is currently
     * active. Note that one stride is equivalent to this robot's {@code strideRadius}.
     *
     * @param dir the direction to move in
     * @return true if there is no external obstruction to prevent this robot
     * from moving one stride in the given direction; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canMove(Direction dir);

    /**
     * Tests whether this robot can move {@code distance} units in the given
     * direction, without taking into account if they have already moved. Takes into
     * account only the positions of other robots, and the
     * edge of the game map. Does not take into account whether this robot is
     * currently active. Note that one stride is equivalent to this robot's
     * {@code strideRadius}.
     *
     * @param dir the direction to move in
     * @param distance the distance of a move you wish to check. Must be
     * in [0, RobotType.strideRadius]
     * @return true if there is no external obstruction to prevent this robot
     * from moving distance in the given direction; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canMove(Direction dir, float distance);
    
    /**
     * Tests whether this robot can move to the target MapLocation. If
     * the location is outside the robot's {@code strideRadius}, the location
     * is rescaled to be at the {@code strideRadius}. Takes into account only
     * the positions of other robots, and the edge of the game map. Does
     * not take into account whether this robot is currently active.
     * 
     * @param center the MapLocation to move to
     * @return true if there is no external obstruction to prevent this robot
     * from moving to this MapLocation (or in the direction of this MapLocation
     * if it is too far); false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canMove(MapLocation center);
    
    /**
     * Moves one stride in the given direction. Note that one stride is equivalent
     * to this robot's {@code strideRadius}.
     *
     * @param dir the direction to move in
     * @throws GameActionException if the robot cannot move one stride in this
     * direction, such as already moved that turn, the target location being
     * off the map, and the target destination being occupied with either
     * another robot.
     *
     * @battlecode.doc.costlymethod
     */
    void move(Direction dir) throws GameActionException;

    /**
     * Moves distance in the given direction. If the distance exceeds the robot's
     * {@code strideRadius}, it is rescaled to {@code strideRadius}.
     *
     * @param dir the direction to move in
     * @param distance the distance to move in that direction
     * @throws GameActionException if the robot cannot move distance in this
     * direction, such as already moved that turn, the target location being
     * off the map, and the target destination being occupied with either
     * another robot.
     *
     * @battlecode.doc.costlymethod
     */
    void move(Direction dir, float distance) throws GameActionException;
    
    /**
     * Moves to the target MapLocation. If the target location is outside the robot's
     * {@code strideRadius}, it is rescaled to be {@code strideRadius} away.
     * 
     * @param center the MapLocation to move to (or toward)
     * @throws GameActionException if the robot can not move to the target MapLocation,
     * such as already having moved that turn, the target location being off the map,
     * or a target destination being occupied with either another robot.
     *
     * @battlecode.doc.costlymethod
     */
    void move(MapLocation center) throws GameActionException;

    // ***********************************
    // ****** ATTACK METHODS *************
    // ***********************************

    /**
     * Tests whether a robot is able to strike this turn. This takes into accout
     * the robot's type, and if the robot has attacked this turn.
     *
     * @return true if the robot is able to strike this turn; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canStrike();

    /**
     * Strikes and deals damage to all other robots within
     * {@link GameConstants#LUMBERJACK_STRIKE_RADIUS} of this robot. Note that only Lumberjacks
     * can perform this function.
     *
     * @throws GameActionException if the robot is not of type LUMBERJACK or
     * cannot attack due to having already attacked that turn.
     *
     * @battlecode.doc.costlymethod
     */
    void strike() throws GameActionException;

    // ***********************************
    // ****** BUILDING/SPAWNING **********
    // ***********************************

    /**
     * Tests whether you have the bullets and dependencies to build the given
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
     * given direction. Checks cooldown turns remaining, bullet costs,
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
     * @throws GameActionException if you don't have enough bullets, if
     * the robot is still in build cooldown, if the direction is not a
     * good build direction, or if this robot is not of an appropriate type.
     *
     * @battlecode.doc.costlymethod
     */
    void buildRobot(RobotType type, Direction dir) throws GameActionException;

    /**
     * Tests whether the robot can hire a Gardener in the given direction.
     * Checks cooldown turns remaining, bullet costs, whether the robot can
     * hire, and that the given direction is not blocked.
     * 
     * @param dir the direction to build in
     * @return whether it is possible to hire a gardener in the given direction.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canHireGardener(Direction dir);
    
    /**
     * Hires a Gardener in the given direction.
     *
     * @param dir the direction to spawn the Gardener
     * @throws GameActionException if you don't have enough bullets, if
     * the robot is still in build cooldown, if the direction is not a good build
     * direction, or if this robot is not of an appropriate type.
     *
     * @battlecode.doc.costlymethod
     */
    void hireGardener(Direction dir) throws GameActionException;

    // ***********************************
    // ****** OTHER ACTION METHODS *******
    // ***********************************

    /**
     * Returns the current cost of a victory point in bullets. This varies based
     * on the round number, and is equal to {@link GameConstants#VP_BASE_COST} +
     * NumRounds * {@link GameConstants#VP_INCREASE_PER_ROUND}.
     *
     * @return the current cost of a victory point in bullets
     *
     * @battlecode.doc.costlymethod
     */
    float getVictoryPointCost();

    /**
     * Kills your robot and ends the current round. Never fails.
     *
     * @battlecode.doc.costlymethod
     */
    void disintegrate();

    /**
     * Causes your team to lose the game. It's like typing "gg."
     *
     * @battlecode.doc.costlymethod
     */
    void resign();

    // ***********************************
    // **** INDICATOR STRING METHODS *****
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

    // ***********************************
    // ******** TEAM MEMORY **************
    // ***********************************

    /**
     * Sets the team's "memory", which is saved for the next game in the match.
     * The memory is an array of {@link GameConstants#TEAM_MEMORY_LENGTH} longs.
     * If this method is called more than once with the same index in the same
     * game, the last call is what is saved for the next game.
     *
     * @param index the index of the array to set
     * @param value the data that the team should remember for the next game
     * @throws java.lang.ArrayIndexOutOfBoundsException if {@code index} is less
     * than zero or greater than or equal to
     * {@link GameConstants#TEAM_MEMORY_LENGTH}.
     * @see #getTeamMemory
     * @see #setTeamMemory(int, long, long)
     *
     * @battlecode.doc.costlymethod
     */
    void setTeamMemory(int index, long value);

    /**
     * Sets this team's "memory". This function allows for finer control than
     * {@link #setTeamMemory(int, long)} provides. For example, if
     * {@code mask == 0xFF} then only the eight least significant bits of the
     * memory will be set.
     *
     * @param index the index of the array to set
     * @param value the data that the team should remember for the next game
     * @param mask indicates which bits should be set
     * @throws java.lang.ArrayIndexOutOfBoundsException if {@code index} is less
     * than zero or greater than or equal to
     * {@link GameConstants#TEAM_MEMORY_LENGTH}.
     * @see #getTeamMemory
     * @see #setTeamMemory(int, long)
     *
     * @battlecode.doc.costlymethod
     */
    void setTeamMemory(int index, long value, long mask);

    /**
     * Returns the team memory from the last game of the match. The return value
     * is an array of length {@link GameConstants#TEAM_MEMORY_LENGTH}. If
     * setTeamMemory was not called in the last game, or there was no last game,
     * the corresponding long defaults to 0.
     *
     * @return the team memory from the the last game of the match.
     * @see #setTeamMemory(int, long)
     * @see #setTeamMemory(int, long, long)
     *
     * @battlecode.doc.costlymethod
     */
    long[] getTeamMemory();

    // ***********************************
    // ******** DEBUG METHODS ************
    // ***********************************

    /**
     * Gets this robot's 'control bits' for debugging purposes. These bits can
     * be set manually by the user, so a robot can respond to them. To set these
     * bits, you must run the client in lockstep mode and right click the
     * units.
     *
     * @return this robot's control bits.
     *
     * @battlecode.doc.costlymethod
     */
    long getControlBits();

}
