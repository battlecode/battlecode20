package battlecode.world;

import battlecode.common.*;
import battlecode.schema.Action;
import battlecode.server.ErrorReporter;
import battlecode.server.GameMaker;
import battlecode.server.GameState;
import battlecode.world.control.RobotControlProvider;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.*;

/**
 * The primary implementation of the GameWorld interface for containing and
 * modifying the game map and the objects on it.
 */
public strictfp class GameWorld {
    /**
     * The current round we're running.
     */
    protected int currentRound;

    /**
     * Whether we're running.
     */
    protected boolean running = true;

    protected final IDGenerator idGenerator;
    protected final GameStats gameStats;
    private final int[] initialSoup;
    private int[] soup;
    private int[] pollution;
    private int[] dirt;
    private int waterLevel;
    private boolean[] flooded;
    private InternalRobot[][] robots;
    private final LiveMap gameMap;
    private final TeamInfo teamInfo;
    private final ObjectInfo objectInfo;

    private final RobotControlProvider controlProvider;
    private Random rand;

    // the pool of messages not yet sent
    private PriorityQueue<BlockchainEntry> blockchainQueue;
    // the messages that have been broadcasted already
    public ArrayList<ArrayList<BlockchainEntry>> blockchain;

    private final GameMaker.MatchMaker matchMaker;

    @SuppressWarnings("unchecked")
    public GameWorld(LiveMap gm, RobotControlProvider cp, GameMaker.MatchMaker matchMaker) {
        this.initialSoup = gm.getSoupArray();
        this.soup = gm.getSoupArray();
        this.pollution = gm.getPollutionArray();
        // this.water = gm.getWaterArray();
        this.dirt = gm.getDirtArray();
        this.waterLevel = 0; // TODO!!
        this.flooded = new boolean[this.soup.length]; // TODO, need some kind if initialization that is not all false
        this.robots = new InternalRobot[gm.getWidth()][gm.getHeight()]; // if represented in cartesian, should be height-width, but this should allow us to index x-y
        this.currentRound = 0;
        this.idGenerator = new IDGenerator(gm.getSeed());
        this.gameStats = new GameStats();

        this.gameMap = gm;
        this.objectInfo = new ObjectInfo(gm);
        this.teamInfo = new TeamInfo(this);

        this.controlProvider = cp;

        this.rand = new Random(this.gameMap.getSeed());

        this.blockchainQueue = new PriorityQueue<BlockchainEntry>();
        this.blockchain = new ArrayList<ArrayList<BlockchainEntry>>();

        this.matchMaker = matchMaker;

        controlProvider.matchStarted(this);

        // Add the robots contained in the LiveMap to this world.
        for(RobotInfo robot : this.gameMap.getInitialBodies()){
            spawnRobot(robot.ID, robot.type, robot.location, robot.team);
        }

        // Write match header at beginning of match
        this.matchMaker.makeMatchHeader(this.gameMap);
    }

    /**
     * Run a single round of the game.
     *
     * @return the state of the game after the round has run.
     */
    public synchronized GameState runRound() {
        if (!this.isRunning()) {
            // Write match footer if game is done
            matchMaker.makeMatchFooter(gameStats.getWinner(), currentRound);
            return GameState.DONE;
        }

        try {
            this.processBeginningOfRound();
            this.controlProvider.roundStarted();

            updateDynamicBodies();

            this.controlProvider.roundEnded();
            this.processEndOfRound();

            if (!this.isRunning()) {
                this.controlProvider.matchEnded();
            }

        } catch (Exception e) {
            ErrorReporter.report(e);
            // TODO throw out file?
            return GameState.DONE;
        }
        // Write out round data
        matchMaker.makeRound(currentRound);
        return GameState.RUNNING;
    }

    private void updateDynamicBodies(){
        objectInfo.eachDynamicBodyByExecOrder((body) -> {
            if (body instanceof InternalRobot) {
                return updateRobot((InternalRobot) body);
            }
            else {
                throw new RuntimeException("non-robot body registered as dynamic");
            }
        });
    }

    private boolean updateRobot(InternalRobot robot) {
        if (robot.isBlocked()) // blocked robots don't get a turn
            return true;

        robot.processBeginningOfTurn();
        this.controlProvider.runRobot(robot);
        robot.setBytecodesUsed(this.controlProvider.getBytecodesUsed(robot));
        robot.processEndOfTurn();

        // If the robot terminates but the death signal has not yet
        // been visited:
        if (this.controlProvider.getTerminated(robot) && objectInfo.getRobotByID(robot.getID()) != null)
            destroyRobot(robot.getID());
        return true;
    }

    // *********************************
    // ****** BASIC MAP METHODS ********
    // *********************************

    public int getMapSeed() {
        return this.gameMap.getSeed();
    }

    public LiveMap getGameMap() {
        return this.gameMap;
    }

    public TeamInfo getTeamInfo() {
        return this.teamInfo;
    }

    public GameStats getGameStats() {
        return this.gameStats;
    }

    public ObjectInfo getObjectInfo() {
        return this.objectInfo;
    }

    public GameMaker.MatchMaker getMatchMaker() {
        return this.matchMaker;
    }

    public Team getWinner() {
        return this.gameStats.getWinner();
    }

    public boolean isRunning() {
        return this.running;
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    /**
     * Helper method that converts a location into an index.
     * 
     * @param loc the MapLocation
     */
    public int locationToIndex(MapLocation loc) {
        return loc.x - this.gameMap.getOrigin().x + (loc.y - this.gameMap.getOrigin().y) * this.gameMap.getWidth();
    }

    /**
     * Helper method that converts an index into a location.
     * 
     * @param idx the index
     */
    public MapLocation indexToLocation(int idx) {
        return new MapLocation(idx / this.gameMap.getWidth() + this.gameMap.getOrigin().x,
                               idx % this.gameMap.getWidth() + this.gameMap.getOrigin().y);
    }

    // ***********************************
    // ****** SOUP METHODS ***************
    // ***********************************

    public int initialSoupAtLocation(MapLocation loc) {
        return this.gameMap.onTheMap(loc) ? this.initialSoup[locationToIndex(loc)] : 0;
    }

    public int getSoup(MapLocation loc) {
        return this.gameMap.onTheMap(loc) ? this.soup[locationToIndex(loc)] : 0;
    }

    public void removeSoup(MapLocation loc) {
        removeSoup(loc, 1);
    }

    public void removeSoup(MapLocation loc, int amount) {
        if (this.gameMap.onTheMap(loc)) {
            int idx = locationToIndex(loc);
            int newSoup = Math.max(0, this.soup[idx] - amount);
            getMatchMaker().addSoupChanged(loc, newSoup - this.soup[idx]);
            this.soup[idx] = newSoup;
        }
    }

    // ***********************************
    // ****** POLLUTION METHODS **********
    // ***********************************

    public int getPollution(MapLocation loc) {
        return this.gameMap.onTheMap(loc) ? this.pollution[locationToIndex(loc)] : 0;
    }

    public void adjustPollution(MapLocation loc, int amount) {
        if (this.gameMap.onTheMap(loc))
            adjustPollution(locationToIndex(loc), amount);
    }

    public void adjustPollution(int idx, int amount) {
        int newPollution = Math.max(this.pollution[idx] + amount, 0);
        getMatchMaker().addPollutionChanged(indexToLocation(idx), newPollution - this.pollution[idx]);
        this.pollution[idx] = newPollution;
    }

    public void globalPollution(int amount) {
        for (int i = 0; i < this.pollution.length; i++)
            adjustPollution(i, amount);
    }

    // ***********************************
    // ****** DIRT METHODS ***************
    // ***********************************

    /**
     * Returns the amount of dirt at a location, or 0 if the location is invalid.
     * 
     * @param loc the location
     * @return the amount of dirt at a location, or 0 if the location is invalid
     */
    public int getDirt(MapLocation loc) {
        return this.gameMap.onTheMap(loc) ? this.dirt[locationToIndex(loc)] : 0;
    }

    /**
     * Returns the difference between the dirt levels of two locations.
     * 
     * @param loc1 the first location
     * @param loc2 the second location
     * @return the difference between the dirt levels of two locations
     */
    public int getDirtDifference(MapLocation loc1, MapLocation loc2) {
        return Math.abs(getDirt(loc1) - getDirt(loc2));
    }

    /**
     * Removes one unit of dirt from a location. If there is dirt on a building,
     *  remove dirt from the building; otherwise remove dirt from the ground.
     * ALSO ADDS THE ACTION TO MATCHMAKER.
     * 
     * @param robotID the id of the robot that initiated the action
     * @param loc the location
     */
    public void removeDirt(int robotID, MapLocation loc) {
        if (this.gameMap.onTheMap(loc)) {
            InternalRobot targetRobot = getRobot(loc);
            int targetID = -1;
            if (targetRobot != null && targetRobot.getType().isBuilding() && targetRobot.getDirtCarrying() > 0) {
                targetRobot.removeDirtCarrying(1);
                targetID = targetRobot.getID();
            }
            else {
                this.dirt[locationToIndex(loc)] -= 1;
                getMatchMaker().addDirtChanged(loc, -1);
            }
            getMatchMaker().addAction(robotID, Action.DIG_DIRT, targetID);
        }
    }

    /**
     * Deposits dirt to a location. If there is a building, the dirt is deposited
     *  onto the building; otherwise the dirt is deposited onto the ground. Potentially
     *  resurfaces a tile that has increased in elevation.
     * ALSO ADDS THE ACTION TO MATCHMAKER.
     * 
     * @param robotID the id of the robot that initiated the action
     * @param loc the location
     * @param amount the amount of dirt to deposit
     */
    public void addDirt(int robotID, MapLocation loc, int amount) {
        if (this.gameMap.onTheMap(loc)) {
            InternalRobot targetRobot = getRobot(loc);
            int targetID = -1;
            if (targetRobot != null && targetRobot.getType().isBuilding()) {
                targetRobot.addDirtCarrying(amount);
                targetID = targetRobot.getID();
            }
            else{
                this.dirt[locationToIndex(loc)] += amount;
                getMatchMaker().addDirtChanged(loc, amount);
                tryResurface(loc);
            }
            getMatchMaker().addAction(robotID, Action.DEPOSIT_DIRT, targetID);
        }
    }

    // ***********************************
    // ****** WATER METHODS **************
    // ***********************************

    /**
     * Returns whether or not a location is flooded, or false if the location is invalid.
     * 
     * @param loc the location
     * @return whether or not a location is flooded, or false if the location is invalid
     */
    public boolean isFlooded(MapLocation loc) {
        return this.gameMap.onTheMap(loc) ? this.flooded[locationToIndex(loc)] : false;
    }

    /**
     * Resurfaces a location if the elevation >= water level (set flooded to false).
     * 
     * @param loc the location
     */
    public void tryResurface(MapLocation loc) {
        int idx = locationToIndex(loc);
        if (this.dirt[idx] >= this.waterLevel)
            setFloodStatus(idx, false);
    }

    /**
     * Sets the flood status of the location at an index.
     * 
     * @param idx the index of the location
     * @param newStatus the new flood status of the location
     */
    public void setFloodStatus(int idx, boolean newStatus) {
        if (this.flooded[idx] != newStatus) {
            this.flooded[idx] = newStatus;
            getMatchMaker().addWaterChanged(indexToLocation(idx));
        }
    }

    // ***********************************
    // ****** ROBOT METHODS **************
    // ***********************************

    public InternalRobot getRobot(MapLocation loc) {
        return this.robots[loc.x][loc.y];
    }

    public void moveRobot(MapLocation start, MapLocation end) {
        addRobot(end, this.robots[start.x][start.y]);
        removeRobot(start);
    }

    public void addRobot(MapLocation loc, InternalRobot robot) {
        this.robots[loc.x][loc.y] = robot;
    }

    public void removeRobot(MapLocation loc) {
        this.robots[loc.x][loc.y] = null;
    }

    public InternalRobot[] getAllRobotsWithinRadius(MapLocation center, int radius) {
        ArrayList<InternalRobot> returnRobots = new ArrayList<InternalRobot>();
        for (MapLocation newLocation : getAllLocationsWithinRadius(center, radius))
            if (this.robots[newLocation.x][newLocation.y] != null)
                returnRobots.add(this.robots[newLocation.x][newLocation.y]);
        return returnRobots.toArray(new InternalRobot[returnRobots.size()]);
    }

    public ArrayList<MapLocation> getAllLocationsWithinRadius(MapLocation center, int radius) {
        ArrayList<MapLocation> returnLocations = new ArrayList<MapLocation>();
        int minX = Math.max(center.x - radius, 0);
        int minY = Math.max(center.y - radius, 0);
        int maxX = Math.min(center.x + radius, this.gameMap.getWidth() - 1);
        int maxY = Math.min(center.y + radius, this.gameMap.getHeight() - 1);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                MapLocation newLocation = new MapLocation(x, y);
                if (center.isWithinDistance(newLocation, radius))
                    returnLocations.add(newLocation);
            }
        }
        return returnLocations;
    }

    // *********************************
    // ****** GAMEPLAY *****************
    // *********************************

    public void processBeginningOfRound() {
        // Increment round counter
        currentRound++;
        this.teamInfo.addSoupIncome(GameConstants.BASE_INCOME_PER_ROUND);

        // Process beginning of each robot's round
        objectInfo.eachRobot((robot) -> {
            if (!robot.isBlocked()) // blocked robots don't do anything
                robot.processBeginningOfRound();
            return true;
        });
    }

    public void setWinner(Team t, DominationFactor d)  {
        gameStats.setWinner(t);
        gameStats.setDominationFactor(d);
    }

    public void setWinnerIfDestruction(){
        if(objectInfo.getRobotCount(Team.A) == 0){
            setWinner(Team.B, DominationFactor.DESTROYED);
        } else if(objectInfo.getRobotCount(Team.B) == 0){
            setWinner(Team.A, DominationFactor.DESTROYED);
        }
    }

    public boolean timeLimitReached() {
        return currentRound >= this.gameMap.getRounds() - 1;
    }


    public void processEndOfRound() {
        // Process end of each robot's round
        objectInfo.eachRobot((robot) -> {
            if (!robot.isBlocked()) // blocked robots don't do anything
                robot.processEndOfRound();
            return true;
        });

        // process blockchain messages
        processBlockchain();

        // Check for end of match
        if (timeLimitReached() && gameStats.getWinner() == null) {
            boolean victorDetermined = false;

            // TODO: tiebreakers

            int bestRobotID = Integer.MIN_VALUE;
            Team bestRobotTeam = Team.A; // null; ARBITRARY

            // tiebreak by robot id
            if(!victorDetermined){
                setWinner(bestRobotTeam, DominationFactor.WON_BY_DUBIOUS_REASONS);
            }
        }

        // update the round statistics

        matchMaker.addTeamStat(Team.A, teamInfo.getSoup(Team.A)); // TODO: change to soup
        matchMaker.addTeamStat(Team.B, teamInfo.getSoup(Team.B));

        if (gameStats.getWinner() != null) {
            running = false;
        }
    }

    // *********************************
    // ****** SPAWNING *****************
    // *********************************

    public int spawnRobot(int ID, RobotType type, MapLocation location, Team team){
        InternalRobot robot = new InternalRobot(this, ID, type, location, team);
        objectInfo.spawnRobot(robot);
        addRobot(location, robot);

        controlProvider.robotSpawned(robot);
        matchMaker.addSpawnedRobot(robot);
        return ID;
    }

    public int spawnRobot(RobotType type, MapLocation location, Team team){
        int ID = idGenerator.nextID();
        return spawnRobot(ID, type, location, team);
    }

    // *********************************
    // ****** BLOCKCHAIN *************** 
    // *********************************

    /**
     * Add new message to the priority queue of messages, and also add them
     * to the matchmaker.
     * @param cost
     * @param message
     */
    public void addNewMessage(BlockchainEntry block) {
        getMatchMaker().addNewMessage(block.cost, block.serializedMessage);

        // add it to the priority queue 
        blockchainQueue.add(block);
    }

    public void processBlockchain() {
        // process messages, take the K first ones!
        ArrayList<BlockchainEntry> thisRoundMessages = new ArrayList<BlockchainEntry>();
        for (int i = 0; i < GameConstants.NUMBER_OF_BROADCASTED_MESSAGES; i++) {
            if (blockchainQueue.size() > 0) {
                BlockchainEntry block = blockchainQueue.poll();
                // send this to match maker!
                matchMaker.addBroadcastedMessage(block.cost, block.serializedMessage);
                // also add it to this round's list of messages!
                thisRoundMessages.add(block);
            }
        }
        // add this to the blockchain!
        blockchain.add(thisRoundMessages);
    }
   
    // *********************************
    // ****** DESTROYING ***************
    // *********************************

    public void destroyRobot(int id) {
        InternalRobot robot = objectInfo.getRobotByID(id);
        removeRobot(robot.getLocation());

        try {
            // if a delivery drone is killed, it drops unit at current location
            if (robot.getType().canDropOffUnits() && robot.isCurrentlyHoldingUnit())
                robot.getController().dropUnit(null, false);
        } catch (GameActionException e) {}

        controlProvider.robotKilled(robot);
        objectInfo.destroyRobot(id);

        setWinnerIfDestruction();

        matchMaker.addDied(id);
    }
}


