package battlecode.world;

import battlecode.common.*;
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

    private final LiveMap gameMap;
    private final TeamInfo teamInfo;
    private final ObjectInfo objectInfo;

    private final RobotControlProvider controlProvider;
    private Random rand;

    private final GameMaker.MatchMaker matchMaker;

    @SuppressWarnings("unchecked")
    public GameWorld(LiveMap gm, RobotControlProvider cp,
                     long[][] oldTeamMemory, GameMaker.MatchMaker matchMaker) {

        this.currentRound = 0;
        this.idGenerator = new IDGenerator(gm.getSeed());
        this.gameStats = new GameStats();

        this.gameMap = gm;
        this.objectInfo = new ObjectInfo(gm);
        this.teamInfo = new TeamInfo(oldTeamMemory);

        this.controlProvider = cp;

        this.rand = new Random(gameMap.getSeed());

        this.matchMaker = matchMaker;

        controlProvider.matchStarted(this);

        // Add the robots contained in the LiveMap to this world.
        for(BodyInfo body : gameMap.getInitialBodies()){
            if(body.isRobot()){
                RobotInfo robot = (RobotInfo) body;
                spawnRobot(robot.ID, robot.type, robot.location, robot.team);
            }
        }

        // Write match header at beginning of match
        matchMaker.makeMatchHeader(gameMap);
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
                throw new RuntimeException("non-robot non-bullet body registered as dynamic");
            }
        });
    }

    private boolean updateRobot(InternalRobot robot) {
        robot.processBeginningOfTurn();
        this.controlProvider.runRobot(robot);
        robot.setBytecodesUsed(this.controlProvider.getBytecodesUsed(robot));

        if(robot.getHealth() > 0) { // Only processEndOfTurn if robot is still alive
            robot.processEndOfTurn();
        }

        // If the robot terminates but the death signal has not yet
        // been visited:
        if (this.controlProvider.getTerminated(robot) && objectInfo.getRobotByID(robot.getID()) != null) {
            destroyRobot(robot.getID());
        }
        return true;
    }

    // *********************************
    // ****** BASIC MAP METHODS ********
    // *********************************

    public int getMapSeed() {
        return gameMap.getSeed();
    }

    public LiveMap getGameMap() {
        return gameMap;
    }

    public TeamInfo getTeamInfo() {
        return teamInfo;
    }

    public GameStats getGameStats() {
        return gameStats;
    }

    public ObjectInfo getObjectInfo() {
        return objectInfo;
    }

    public GameMaker.MatchMaker getMatchMaker() {
        return matchMaker;
    }

    public Team getWinner() {
        return gameStats.getWinner();
    }

    public boolean isRunning() {
        return running;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    // *********************************
    // ****** GAMEPLAY *****************
    // *********************************

    public void processBeginningOfRound() {
        // Increment round counter
        currentRound++;

        // Process beginning of each robot's round
        objectInfo.eachRobot((robot) -> {
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
        }else if(objectInfo.getRobotCount(Team.B) == 0){
            setWinner(Team.A, DominationFactor.DESTROYED);
        }
    }

    public boolean timeLimitReached() {
        return currentRound >= gameMap.getRounds() - 1;
    }

    public void processEndOfRound() {
        // Process end of each robot's round
        objectInfo.eachRobot((robot) -> {
            robot.processEndOfRound();
            return true;
        });

        // Check for end of match
        if (timeLimitReached() && gameStats.getWinner() == null) {
            boolean victorDetermined = false;

            int bestRobotID = Integer.MIN_VALUE;
            Team bestRobotTeam = Team.A; // null; ARBITRARY

            // tiebreak by robot id
            if(!victorDetermined){
                setWinner(bestRobotTeam, DominationFactor.WON_BY_DUBIOUS_REASONS);
            }
        }

        // update the round statistics
        matchMaker.addTeamStat(Team.A, 0, 0);
        matchMaker.addTeamStat(Team.B, 0, 0);

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

        controlProvider.robotSpawned(robot);
        matchMaker.addSpawnedRobot(robot);
        return ID;
    }

    public int spawnRobot(RobotType type, MapLocation location, Team team){
        int ID = idGenerator.nextID();
        return spawnRobot(ID, type, location, team);
    }

    // *********************************
    // ****** DESTROYING ***************
    // *********************************

    public void destroyRobot(int id){
        InternalRobot robot = objectInfo.getRobotByID(id);

        controlProvider.robotKilled(robot);
        objectInfo.destroyRobot(id);

        setWinnerIfDestruction();

        matchMaker.addDied(id, false);
    }

}
