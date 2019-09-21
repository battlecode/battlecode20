package dylantestbot;


import battlecode.common.*;

public class Gardner implements Robot{

    private RobotController rc;
    private boolean amGardening;
    private int closeCounter;
    private int lumberJackSpawned;

    private MapLocation gardenCenter;
    private Direction[] treeDirs;
    private Direction spawnDir;
    private float spawnDist;

    Gardner(RobotController rc){
        this.rc = rc;
        this.amGardening = false;
        this.closeCounter = 0;
        this.lumberJackSpawned = 0;

        this.gardenCenter = null;
        this.treeDirs = new Direction[]{
            Direction.getNorth(),
            Direction.getNorth().rotateRightDegrees(60),
            Direction.getNorth().rotateLeftDegrees(60),
            Direction.getNorth().rotateRightDegrees(120),
            Direction.getNorth().rotateLeftDegrees(120),
        };
        this.spawnDir = Direction.SOUTH;
        this.spawnDist = .05f;
    }

    @Override
    public void runRound() throws GameActionException {
        // Random variable gathering
        RobotInfo nearestPartner = Util.getClosestPartner(this.rc);

        // Check if can win with VPs
        Util.checkForVPWin(this.rc);

        // Buy VPs if wanting too
        Util.buyVPsForRound(this.rc);

        // Shake trees
        Util.checkForTreesToShake(this.rc);

        // Move
        move(nearestPartner);

        // Switch modes
        switchModes(nearestPartner);

        // Perform beginning stuff
        if(performBeginning()){
            // Spawn Trees
            spawnTreesIfShould();

            // Spawn LumberJacks
            spawnLumberJacks();
        }

        // Water Trees
        waterTrees();
    }

    public boolean performBeginning() throws GameActionException {
        boolean haveScout = rc.readBroadcastBoolean(1);
        boolean haveLumberJack = rc.readBroadcastBoolean(2);
        if(!haveScout){
            if (this.rc.getBuildCooldownTurns() == 0 && rc.hasRobotBuildRequirements(RobotType.SCOUT)) {
                Direction[] spawnDirs = Util.getSpawnDirections(360);
                int randomStartIndex = (int) Math.floor(Math.random()*360);
                boolean shouldContinue = true;
                for (int i = randomStartIndex; i < 360; i++){
                    Direction dir = spawnDirs[i];
                    if (rc.canBuildRobot(RobotType.SCOUT, dir)){
                        rc.buildRobot(RobotType.SCOUT, dir);
                        rc.broadcastBoolean(1, true);
                        shouldContinue = false;
                        break;
                    }
                }
                if(shouldContinue){
                    for (int i = 0; i < randomStartIndex; i++){
                        Direction dir = spawnDirs[i];
                        if (rc.canBuildRobot(RobotType.SCOUT, dir)){
                            rc.buildRobot(RobotType.SCOUT, dir);
                            rc.broadcastBoolean(1, true);
                            break;
                        }
                    }
                }
            }
        }else if(!haveLumberJack){
            if (this.rc.getBuildCooldownTurns() == 0 && rc.hasRobotBuildRequirements(RobotType.LUMBERJACK)) {
                Direction[] spawnDirs = Util.getSpawnDirections(360);
                int randomStartIndex = (int) Math.floor(Math.random()*360);
                boolean shouldContinue = true;
                for (int i = randomStartIndex; i < 360; i++){
                    Direction dir = spawnDirs[i];
                    if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)){
                        rc.buildRobot(RobotType.LUMBERJACK, dir);
                        rc.broadcastBoolean(2, true);
                        shouldContinue = false;
                        break;
                    }
                }
                if(shouldContinue){
                    for (int i = 0; i < randomStartIndex; i++){
                        Direction dir = spawnDirs[i];
                        if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)){
                            rc.buildRobot(RobotType.LUMBERJACK, dir);
                            rc.broadcastBoolean(2, true);
                            break;
                        }
                    }
                }
            }
        }
        if(haveScout && haveLumberJack){
            return true;
        }
        return false;
    }

    public void move(RobotInfo nearestPartner) throws GameActionException {
        if(!this.amGardening && nearestPartner != null) {
            Direction[] dirsAwayFrom = Util.getMoveAwayFromDirections(this.rc, nearestPartner.getLocation());
            for (Direction dir : dirsAwayFrom) {
                if (rc.canMove(dir)){
                    rc.move(dir);
                    break;
                }
            }
        }
    }

    public void switchModes(RobotInfo nearestPartner) {
        if(!this.amGardening){
            if(nearestPartner == null || this.closeCounter > 20) {
                this.amGardening = true;
                this.gardenCenter = rc.getLocation();
            } else {
                this.closeCounter++;
            }
        }
    }

    public void spawnLumberJacks() throws GameActionException {
        if (this.amGardening && rc.getTeamBullets() > 200 && this.lumberJackSpawned < 1 && rc.getRobotCount() < 15) {
            if (rc.hasRobotBuildRequirements(RobotType.LUMBERJACK)) {
                Direction dir = this.spawnDir;
                if (!rc.isCircleOccupied(this.gardenCenter.add(dir,
                        rc.getType().bodyRadius+this.spawnDist+GameConstants.GENERAL_SPAWN_OFFSET+
                                RobotType.LUMBERJACK.bodyRadius),
                        RobotType.LUMBERJACK.bodyRadius) &&
                        rc.onTheMap(this.gardenCenter.add(dir,
                                rc.getType().bodyRadius+this.spawnDist+GameConstants.GENERAL_SPAWN_OFFSET+
                                        RobotType.LUMBERJACK.bodyRadius),
                                RobotType.LUMBERJACK.bodyRadius)) {
                    // Move there
                    if (rc.canMove(this.gardenCenter.add(dir, this.spawnDist))) {
                        rc.move(this.gardenCenter.add(dir, this.spawnDist));
                    }
                    // Spawn
                    if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                        rc.buildRobot(RobotType.LUMBERJACK, dir);
                        this.lumberJackSpawned += 1;
                    }
                }
            }
        }
    }

    public void spawnTreesIfShould() throws GameActionException {
        int treeCount = rc.getTreeCount();
        boolean planted = false;
        if (this.amGardening) {
            if (rc.hasTreeBuildRequirements()) {
                for (Direction dir : this.treeDirs){
                    // Check if spot clear
                    if (rc.isCircleOccupied(this.gardenCenter.add(dir,
                            rc.getType().bodyRadius+this.spawnDist+GameConstants.GENERAL_SPAWN_OFFSET+
                                    GameConstants.BULLET_TREE_RADIUS),
                            GameConstants.BULLET_TREE_RADIUS) ||
                            !rc.onTheMap(this.gardenCenter.add(dir,
                                    rc.getType().bodyRadius+this.spawnDist+GameConstants.GENERAL_SPAWN_OFFSET+
                                            GameConstants.BULLET_TREE_RADIUS),
                                    GameConstants.BULLET_TREE_RADIUS)){
                        continue;
                    }

                    // Move there
                    if(rc.canMove(this.gardenCenter.add(dir, this.spawnDist))){
                        rc.move(this.gardenCenter.add(dir, this.spawnDist));
                    }
                    // Spawn
                    if(rc.canPlantTree(dir)){
                        rc.plantTree(dir);
                        planted = true;
                    }
                    break;
                }
                if (!planted && (this.lumberJackSpawned == 1 || rc.getRobotCount() >= 15)){
                    // Move there
                    if(rc.canMove(this.gardenCenter.add(this.spawnDir, this.spawnDist))){
                        rc.move(this.gardenCenter.add(this.spawnDir, this.spawnDist));
                    }
                    // Spawn
                    if(rc.canPlantTree(this.spawnDir)){
                        rc.plantTree(this.spawnDir);
                    }
                }
            }
        }
    }

    public void waterTrees() throws GameActionException {
        TreeInfo[] trees = this.rc.senseNearbyTrees(rc.getType().bodyRadius+GameConstants.INTERACTION_DIST_FROM_EDGE,
                this.rc.getTeam());
        float minHealth = 1000;
        TreeInfo bestTree = null;
        for(TreeInfo tree: trees){
            if(tree.health < minHealth){
                bestTree = tree;
                minHealth = tree.health;
            }
        }
        if(bestTree != null && rc.canWater(bestTree.getID())){
            rc.water(bestTree.getID());
        }
        return;
    }

}
