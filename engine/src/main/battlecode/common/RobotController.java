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
    // int getRoundLimit();
    // seems like we're not doing this this year?

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
     * Returns the team's total refined soup supply.
     *
     * @return the team's total refined soup supply.
     *
     * @battlecode.doc.costlymethod
     */
    int getTeamSoup();

    /**
     * Returns the team's total victory points.
     *
     * @return the team's total victory points.
     *
     * @battlecode.doc.costlymethod
     */
    // int getTeamVictoryPoints();
    // we don't have this either this year i think?

    /**
     * Returns your opponent's total victory points.
     *
     * @return your opponent's total victory points.
     *
     * @battlecode.doc.costlymethod
     */
    // int getOpponentVictoryPoints();

    /**
     * Returns the location of the querying team's HQ.
     *
     * @return the location of the querying team's HQ.
     *
     * @battlecode.doc.costlymethod
     */
    MapLocation[] getHQLocation();

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
     * Returns this robot's current elevation.
     *
     * @return this robot's current elevation.
     *
     * @battlecode.doc.costlymethod
     */
    int getElevation();
    
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
    // boolean canSenseRadius(float radius);
    // this seems not applicable?

    /**
     * Senses whether there is a robot or tree at the given location.
     *
     * @param loc the location to check
     * @return true if there is a robot or tree at the given location; false otherwise.
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
    RobotInfo senseLocation(MapLocation loc) throws GameActionException;

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
     * Returns all robots of a given team within sense radius. The objects are
     * returned in order of increasing distance from your robot.
     *
     * @param radius return robots this distance away from the center of
     * this robot. If -1 is passed, all robots within sense radius are returned
     * @param team filter game objects by the given team. If null is passed,
     * robots from any team are returned
     * @return sorted array of RobotInfo objects of all the robots you sensed.
     *
     * @battlecode.doc.costlymethod
     */
    RobotInfo[] senseNearbyRobots(Team team);

    /**
     * Returns an array of all the locations of the robots that have
     * broadcasted in the last round (unconstrained by sensor range or distance).
     *
     * @return an array of all the locations of the robots that have
     * broadcasted in the last round.
     *
     * @battlecode.doc.costlymethod
     */
    // MapLocation[] senseBroadcastingRobotLocations();
    // i'm pretty sure we have nothing similar this year

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
    // boolean hasAttacked();
    

    //TODO: COME BACK TO THIS AFTER WE DETERMINE HOW COOLDOWNS ARE CODED.

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
     * Tells whether this robot can move to the given location, without taking into account
     * if they have already moved. Takes into account only the positions of robots on the map
     * and the edge of the game map. Does not take into account whether this robot is currently
     * active.
     *
     * @param loc the location to move to
     * @return true if the robot can move to the given location; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canMove(MapLocation loc);
    
    /**
     * Moves to the given location.
     *
     * @param loc the location to move in; this should be one unit away from the
     * robot's current location.
     * @throws GameActionException if the robot cannot move to this location, such as already
     * moved that turn, the target location being off the map, the target destination being
     * occupied with another robot or a tree, or the target location not being one unit away.
     *
     * @battlecode.doc.costlymethod
     */
    void move(MapLocation loc) throws GameActionException;

    // ***********************************
    // ****** MINER METHODS *************
    // ***********************************

    /**
     * Tests whether a robot is able to mine a location. This takes into accout
     * the robot's type, if the robot has mined this turn, and location.
     *
     * @param loc the MapLocation to be mined
     * @return true if the robot is able to strike this turn; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canMine(MapLocation loc);

    /**
     * Mines a location.
     * 
     * @param loc the MapLocation to be mined
     * @throws GameActionException if the robot is not of type LUMBERJACK or
     * cannot attack due to having already attacked that turn.
     *
     * @battlecode.doc.costlymethod
     */
    void mine(MapLocation loc) throws GameActionException;

    /**
     * Returns amount of soup the miner is currently carrying. Zero if the robot is
     * not a miner.
     * 
     * @return the amount of soup the miner is carrying.
     */
    int getSoup();

    /**
     * Checks whether the id passed in is a valid refinery for the miner to give soup to.
     * 
     * @param id the id of the refinery to give soup to.
     * @return whether the refinery is a valid target
     */
    boolean canGiveSoup(int id);

    /**
     * Gives all soup the miner is currently carrying to a refinery.
     * 
     * @param id the id of the refinery to give soup to.
     * @throws GameActionException if the refinery is not adjacent to the robot, the id
     * is not valid, or the robot is not a miner.
     */
    void giveSoup(int id) throws GameActionException;

    // ***********************************
    // ****** LANDSCAPER METHODS ***************
    // ***********************************

    /**
     * Tests whether a robot is able to dig a location. This takes into accout
     * the robot's type, if the robot has mined this turn, and location.
     *
     * @param loc the MapLocation to be dug
     * @return true if can dig (location is adjacent, robot hasn't dug, carrying < capacity)
     *
     * @battlecode.doc.costlymethod
     */
    boolean canDig(MapLocation loc);

    /**
     * Digs a location.
     * 
     * @param loc the MapLocation to be dug
     * @throws GameActionException if the robot is not of type LANDSCAPER, has already dug,
     * or 
     *
     * @battlecode.doc.costlymethod
     */
    void dig(MapLocation loc) throws GameActionException;

    /**
     * Tests whether a robot is able to deposit dirt at a location. This takes into accout
     * the robot's type, if the robot has mined this turn, and location.
     *
     * @param loc the MapLocation to deposit dirt at
     * @return true if able to deposit (location is adjacent, robot has positive dirt, cooldown)
     *
     * @battlecode.doc.costlymethod
     */
    boolean canDeposit(MapLocation loc);

    /**
     * Deposits dirt at a location.
     * 
     * @battlecode.doc.costlymethod
     */
    void deposit(MapLocation loc) throws GameActionException;

    // ***********************************
    // ****** DRONE METHODS ***************
    // ***********************************

    /**
     * Tests whether a robot is able to pick up another robot
     *
     * @param id the id of the robot to pick up
     * @return true if able to deposit (drone, location is adjacent, robot has positive dirt, cooldown)
     *
     * @battlecode.doc.costlymethod
     */
    boolean canPickUp(int id);

    /**
     * Picks up robot
     * 
     * @battlecode.doc.costlymethod
     */
    void pickUp(int id) throws GameActionException;


    // ***********************************
    // ****** NET GUN METHODS **********
    // ***********************************

    /**
     * Tests whether a robot is able to shoot at a drone
     *
     * @param id the drone to shoot at
     * @return true if able to deposit (location is in radius, cooldown)
     *
     * @battlecode.doc.costlymethod
     */
    boolean canShoot(int id);

    /**
     * Shoots at a drone
     * 
     * @battlecode.doc.costlymethod
     */
    void shoot(int id) throws GameActionException;

    

    // ***********************************
    // ****** SIGNALING METHODS **********
    // ***********************************

    /**
     * Broadcasts an integer to the team-shared array at index channel.
     * The data is not written until the end of the robot's turn.
     *
     * @param channel the index to write to, from 0 to <code>BROADCAST_MAX_CHANNELS</code>
     * @param data one int of data to write
     * @throws GameActionException if the channel is invalid
     *
     * @see #broadcastInt(int channel, int data)
     *
     * @battlecode.doc.costlymethod
     */
    void broadcast(int channel, int data) throws GameActionException;

    /**
     * Retrieves the integer stored in the team-shared array at index channel.
     *
     * @param channel the index to query, from 0 to <code>BROADCAST_MAX_CHANNELS</code>
     * @return the data currently stored on the channel, interpreted as an int.
     * @throws GameActionException  if the channel is invalid
     *
     * @see #readBroadcastInt(int channel)
     *
     * @battlecode.doc.costlymethod
     */
    int readBroadcast(int channel) throws GameActionException;

    /**
     * Broadcasts a boolean to the team-shared array at index channel.
     * The data is not written until the end of the robot's turn.
     *
     * @param channel the index to write to, from 0 to <code>BROADCAST_MAX_CHANNELS</code>
     * @param data one int of data to write
     * @throws GameActionException if the channel is invalid
     *
     * @battlecode.doc.costlymethod
     */
    void broadcastBoolean(int channel, boolean data) throws GameActionException;

    /**
     * Retrieves the boolean stored in the team-shared array at index channel.
     *
     * @param channel the index to query, from 0 to <code>BROADCAST_MAX_CHANNELS</code>
     * @return the data currently stored on the channel, interpreted as a boolean.
     * @throws GameActionException  if the channel is invalid
     *
     * @battlecode.doc.costlymethod
     */
    boolean readBroadcastBoolean(int channel) throws GameActionException;

    /**
     * Broadcasts an int to the team-shared array at index channel.
     * The data is not written until the end of the robot's turn.
     *
     * @param channel the index to write to, from 0 to <code>BROADCAST_MAX_CHANNELS</code>
     * @param data one int of data to write
     * @throws GameActionException if the channel is invalid
     *
     * @see #broadcast(int channel, int data)
     *
     * @battlecode.doc.costlymethod
     */
    void broadcastInt(int channel, int data) throws GameActionException;

    /**
     * Retrieves the int stored in the team-shared array at index channel.
     *
     * @param channel the index to query, from 0 to <code>BROADCAST_MAX_CHANNELS</code>
     * @return the data currently stored on the channel, interpreted as an int.
     * @throws GameActionException  if the channel is invalid
     *
     * @see #readBroadcast(int channel)
     *
     * @battlecode.doc.costlymethod
     */
    int readBroadcastInt(int channel) throws GameActionException;

    /**
     * Broadcasts a float to the team-shared array at index channel.
     * The data is not written until the end of the robot's turn.
     *
     * @param channel the index to write to, from 0 to <code>BROADCAST_MAX_CHANNELS</code>
     * @param data one float of data to write
     * @throws GameActionException if the channel is invalid
     *
     * @battlecode.doc.costlymethod
     */
    void broadcastFloat(int channel, float data) throws GameActionException;

    /**
     * Retrieves the float stored in the team-shared array at index channel.
     *
     * @param channel the index to query, from 0 to <code>BROADCAST_MAX_CHANNELS</code>
     * @return the data currently stored on the channel, interpreted as a float.
     * @throws GameActionException  if the channel is invalid
     *
     * @battlecode.doc.costlymethod
     */
    float readBroadcastFloat(int channel) throws GameActionException;

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
     * Tests whether you have the bullets and dependencies to build a
     * bullet tree, and this robot is a valid builder for a bullet tree.
     *
     * @return true if the requirements to plant a tree are met; false otherwise.
     *
     * @battlecode.doc.costlymethod
     */
    boolean hasTreeBuildRequirements();

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
     * Tests whether the robot can build a bullet tree in the given direction.
     * Checks cooldown turns remaining, bullet costs, whether the robot can
     * plant, and that the given direction is not blocked
     *
     * @param dir the direction to build in
     * @return whether it is possible to build a bullet tree in the
     * given direction.
     *
     * @battlecode.doc.costlymethod
     */
    boolean canPlantTree(Direction dir);

    /**
     * Plants a bullet tree in the given direction.
     *
     * @param dir the direction to plant the bullet tree
     * @throws GameActionException if you don't have enough bullets, if
     * the robot is still in build cooldown, if the direction is not a good build
     * direction, or if this robot is not of an appropriate type.
     *
     * @battlecode.doc.costlymethod
     */
    void plantTree(Direction dir) throws  GameActionException;

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
     * Donates the given amount of bullets to the reforestation fund in
     * exchange for one victory point per ten bullets donated.  Note there
     * are no fractions of victory points, meaning, for example, donating
     * 11 bullets will only result in 1 victory point, not 1.1 victory points.
     *
     * @param bullets the amount of bullets you wish to donate
     * @throws GameActionException if you have less bullets in your bullet
     * supply than the amount of bullet you wish to donate.
     *
     * @battlecode.doc.costlymethod
     */
    void donate(float bullets) throws GameActionException;

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
