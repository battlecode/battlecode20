package maxplayer;

import battlecode.common.*;

public class RobotPlayer {

	public static RobotController myRC;
	public static int alliedBots = 0;
	public static int ID = -1;
	public static int status = 0;
	public static int FLUX_MIN = 1;
	public static int fluxFraction = 2;//dont fill new units to the top
//status:
//0 start looking for towers
//1 build army
	public static Direction marchDir = null;
	public static MapLocation goal = null;
	public static MapLocation origin = null;//what's the function for the base??
	public static int pause = -1;
	public static boolean alreadyBroadcast = false;
	public static int unitThreshold = 12; //was 12
	
//	public RobotPlayer(RobotController rc) {
//		myRC = rc;
//	}

	public static void run(RobotController rc) {
		myRC = rc;
		if (myRC.getTeam()==Team.A) unitThreshold = 18;//was 18
		//System.out.println("STARTING");
		while (true) {
			try {
				alreadyBroadcast = false;
				if(myRC.getType()==RobotType.ARCHON){
					/*** beginning of main loop ***/
					myRC.setIndicatorString(2,"march "+marchDir);
					if(Clock.getRoundNum()==0){
						//set up ID
						sendMessage(myRC.getLocation(),0);
						Message [] m = myRC.getAllMessages();
						ID = m.length;
						if (ID==0){
							origin = myRC.getLocation();
							if (myRC.getTeam()==Team.A){
								myRC.setIndicatorString(2," "+RobotType.SOLDIER.attackDelay+" "+RobotType.SCORCHER.attackDelay+" "+RobotType.SCOUT.attackDelay+" "+RobotType.DISRUPTER.attackDelay);
								//+" "+r.attackAngle+" "+r.attackRadiusMaxSquared+" "+r.sensorRadiusSquared+" "+r.sensorAngle+" "+r.moveDelayOrthogonal+" "+r.moveCost+" "+r.spawnCost+" "+r.maxEnergon+" "+r.maxFlux+" "
							}
						}else{
							origin = m[0].locations[0];
						}
						//get first marching direction
						MapLocation targetNode = closestLoc(myRC.senseCapturablePowerNodes());
						marchDir = origin.directionTo(targetNode);
						goal = getGoal(ID,marchDir);
					}
					

//					while (myRC.isMovementActive()) {
//						myRC.yield();
//					}
//
//					if (myRC.canMove(myRC.getDirection())) {
//						//System.out.println("about to move");
//						myRC.moveForward();
//					} else {
//						myRC.setDirection(myRC.getDirection().rotateRight());
//					}
					

					
//					PowerNode p = (PowerNode)myRC.senseObjectAtLocation(loc,RobotLevel.MINE);
//					myRC.senseConnected(p);
					
//					MapLocation [] archons = myRC.senseAlliedArchons();
					
//					myRC.senseAlliedPowerNodes()
					
//					RobotType.TOWER
					
//					MapLocation [] adjacent = myRC.senseCapturablePowerNodes();
					
					alliedBots = countDisrupters();//count disrupters and transfer flux if necessary
					myRC.setIndicatorString(0,alliedBots+" allied robots nearby, delay");
					//delay sending attack message until all bots awake
					pause--;
					if(pause<0&&alliedBots>=unitThreshold){//TODO count allied archons to scale army
						pause = 21;//wake delay+1
					}else if(pause==0&&alliedBots>=unitThreshold){
						if(alreadyBroadcast){
							pause++;
						}else{
							sendMessage(addLots(myRC.getLocation(),marchDir),2);
						}
					}
					
					if(Clock.getRoundNum()<150){
						if(tryToGo(goal,0)==1){
							if(myRC.getDirection()!= marchDir){//facing wrong way
								if(!myRC.isMovementActive()){
									myRC.setDirection(marchDir);
								}
							}else{
								//finished aligning
							}
						}
					}else{
//						if(!myRC.isMovementActive()) donateFlux();
						makeDisrupter();
					}

					if(Clock.getRoundNum()%6==ID&&ID==0&&!alreadyBroadcast){
						sendMessage(addLots(myRC.getLocation(),marchDir),1);
					}

					/***
					if(alliedBots<=3&&status==0){//find tower
						MapLocation targetNode = closestLoc(myRC.senseCapturablePowerNodes());
						if(tryToGo(targetNode)==1){//arrived
							makeTower(targetNode);
						}
						//donate flux to one archon
						//if(ID!=0){
						if(!myRC.isMovementActive())	donateFlux();
						//}
//						if(myRC.getFlux()>RobotType.TOWER.spawnCost+.1){
//							status=1;
//						}
					}else if(alliedBots<10&&status==1){//build army
						makeDisrupter();
					}else if(status==1){//send commands to army
						if(Clock.getRoundNum()%6==ID){
							sendMessage(myRC.senseCapturablePowerNodes()[1]);
						}
					}
					***/
				/*** end of main loop ***/
				}else if(myRC.getType()==RobotType.DISRUPTER||myRC.getType()==RobotType.SOLDIER){
					disrupter();
				}
			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}

			myRC.yield();

		}
	}
	public static MapLocation addLots(MapLocation start,Direction dir) throws GameActionException{
		int i = 20;
		while(--i>=0) {
			start = start.add(dir);
		}
		return start;
	}
	public static MapLocation getGoal(int ID,Direction marchDir){
		Direction line = marchDir.rotateRight().rotateRight();
		if (myRC.getTeam()==Team.A) line = line.opposite();
		MapLocation corner = origin.add(marchDir).add(marchDir).add(marchDir).add(marchDir);
		corner = corner.add(line).add(line).add(line);
		int i = ID;
		while(--i>=0) {
			corner = corner.add(line.opposite());
		}
		return corner;
	}
	public static void disrupter() throws GameActionException{
		myRC.setIndicatorString(2,"status "+status);
		Message [] m = myRC.getAllMessages();
		if(m!=null&&m.length!=0){
			//need to parse all messages
			int i = m.length;
			while(--i>=0){
				if(m[i].locations!=null&&m[i].ints!=null){
					if(m[i].ints[0]==1){//set march direction
						goal = m[i].locations[0];
						myRC.setIndicatorString(0,"goal acquired");
						marchDir = myRC.getLocation().directionTo(goal);
					}else if (m[i].ints[0]==2){
						status = 1;
					}
				}
			}
		}
		if(status ==0){//prepare to move out
			if(!myRC.isMovementActive()&&marchDir!=null){
				if(myRC.getDirection()!=marchDir){//wrong way
					myRC.setDirection(marchDir);
				}else if(archonWithin(5)>0&&myRC.canMove(marchDir)){//right way facing and archon is adjacent
					myRC.moveForward();
				}
			}else{
				if(!myRC.isMovementActive()){
					myRC.setDirection(myRC.getDirection().rotateRight());
				}
			}
		}else if (status==1){//charge
			myRC.setIndicatorString(1,"charge!");
//			if(!myRC.isMovementActive()&&myRC.canMove(myRC.getDirection())&&myRC.getFlux()>1){
//				myRC.moveForward();
//			}
			MapLocation enemyLoc = closestEnemy();
			if(enemyLoc==null){
				tryToGo(addLots(myRC.getLocation(),marchDir),0);
			}else{
				tryToGo(addLots(myRC.getLocation(),myRC.getLocation().directionTo(enemyLoc)),4);
			}
		}
		tryToShoot();
//		if(goal!=null){
//			tryToGo(goal,2);
//		}
	}
	public static void tryToShoot() throws GameActionException{
		if (!myRC.isAttackActive()){
			Robot [] robots = myRC.senseNearbyGameObjects(Robot.class);
			int i=robots.length;
			int enemyBots = 0;
			double hitPoints = 0;
			double lowestHitPoints = 10000;
			MapLocation target = null;
			RobotLevel targetLevel = null;
			while(--i>=0) {
				RobotInfo info = myRC.senseRobotInfo(robots[i]);
				if (info.team!=myRC.getTeam()){
					enemyBots++;
					if(myRC.canAttackSquare(info.location)){//inside firing range
						hitPoints = info.energon;
						if(hitPoints<lowestHitPoints&&hitPoints>0){
							lowestHitPoints = hitPoints;
							target = info.location;
							targetLevel = info.type.level;
						}
					}
				}
			}
			if (target!=null){
				myRC.attackSquare(target,targetLevel);
			}
		}
	}
	public static int archonWithin(int thresholdDist) throws GameActionException{
		MapLocation [] alliedArchons = myRC.senseAlliedArchons();
		int i = alliedArchons.length;
		int numWithin = 0;
		int dist = 0;
		while(--i>=0) {
			dist = myRC.getLocation().distanceSquaredTo(alliedArchons[i]);
			if (dist<thresholdDist){
				numWithin++;
			}
		}
		return numWithin;
	}
	public static MapLocation closestLoc(MapLocation [] locs) throws GameActionException{
		int i = locs.length;
		int closestDist = 10000;
		int dist = 10000;
		MapLocation bestLoc = null;
		while(--i>=0) {
			dist = myRC.getLocation().distanceSquaredTo(locs[i]);
			if (dist<closestDist){
				closestDist = dist;
				bestLoc = locs[i];
			}
		}
		return bestLoc;
	}
	public static void sendMessage(MapLocation loc,int typeOfMessage) throws GameActionException{
		Message m = new Message();
		m.locations = new MapLocation [1];
		m.locations[0] = loc;
		m.ints = new int [1];
		m.ints[0] = typeOfMessage;
		//if(!myRC.hasMessageBroadcast()){
		myRC.broadcast(m);
		alreadyBroadcast = true;
		//}
	}
	public static int tryToGo(MapLocation destination,int howClose) throws GameActionException{
		//returns 0 if not yet arrived
		//returns 1 if arrived
		int dist = myRC.getLocation().distanceSquaredTo(destination);
		if (dist>howClose){
			Direction toTower = myRC.getLocation().directionTo(destination);
//			myRC.setIndicatorString(0,toTower.toString());
			if(!myRC.isMovementActive()&&myRC.getFlux()>1.2){//no cooldown
				if(myRC.canMove(toTower)){//free path
					moveDir(toTower);
				}else if(myRC.canMove(toTower.rotateRight())){//blocked path
					moveDir(toTower.rotateRight());
				}else if(myRC.canMove(toTower.rotateLeft())){//blocked path
					moveDir(toTower.rotateLeft());
				}
			}
			return 0;
		}else{
			return 1;
		}
	}
	public static void moveDir(Direction dir) throws GameActionException{
		if(myRC.getDirection()!=dir){
			myRC.setDirection(dir);
		}else{
			myRC.moveForward();
		}
	}
	public static int countDisrupters() throws GameActionException{
		Robot [] robots = myRC.senseNearbyGameObjects(Robot.class);
		int i=robots.length;
		int alliedBots = 0;
		double fluxNeed = 0;
		double greatestFluxNeed = 0;
		RobotInfo fluxInfo = null;
		while(--i>=0) {
			RobotInfo info = myRC.senseRobotInfo(robots[i]);
			if (info.team==myRC.getTeam()&&(info.type==RobotType.DISRUPTER||info.type==RobotType.SOLDIER)){
				alliedBots++;
				if(myRC.getLocation().distanceSquaredTo(info.location)<=2){//inside flux transfer range
					fluxNeed = info.type.maxFlux/fluxFraction-info.flux;
					if(fluxNeed>greatestFluxNeed){
						greatestFluxNeed = fluxNeed;
						fluxInfo = info;
					}
				}
			}
		}
		transferFlux(fluxInfo,Math.min(greatestFluxNeed,myRC.getFlux()-FLUX_MIN));
		return alliedBots;
	}
	public static MapLocation closestEnemy() throws GameActionException{
		Robot [] robots = myRC.senseNearbyGameObjects(Robot.class);
		int i=robots.length;
		int enemyBots = 0;
		MapLocation enemyLoc = null;
		while(--i>=0) {
			RobotInfo info = myRC.senseRobotInfo(robots[i]);
			if (info.team!=myRC.getTeam()){
				enemyBots++;
				enemyLoc = info.location;
			}
		}
		return enemyLoc;
	}
	public static void transferFlux(RobotInfo fluxInfo, double amount) throws GameActionException{
		if(fluxInfo!= null&&amount>0){
			myRC.transferFlux(fluxInfo.location,fluxInfo.type.level,amount);
		}
	}
	public static void tryToTurn() throws GameActionException{
		if(!myRC.isMovementActive()){
			myRC.setDirection(myRC.getDirection().rotateRight());
		}
	}
	public static void makeDisrupter() throws GameActionException{
		RobotType unit = RobotType.DISRUPTER;
		if(myRC.getTeam()==Team.A){
			unit = RobotType.SOLDIER;
		}
		if(myRC.canMove(marchDir)&&myRC.getDirection()!=marchDir&&!myRC.isMovementActive()){//prioritize march dir
			myRC.setDirection(marchDir);
		}
		if(myRC.canMove(myRC.getDirection())){
			if(myRC.getFlux()>=unit.spawnCost&&!myRC.isMovementActive()){
				myRC.spawn(unit);
			}
		}else{
			tryToTurn();
		}
	}
	public static void makeTower(MapLocation targetNode) throws GameActionException{
		Direction towerDir = myRC.getLocation().directionTo(targetNode);
		if(myRC.getDirection()!= towerDir){//facing wrong way
			if(!myRC.isMovementActive()){
				myRC.setDirection(towerDir);
			}
		}else{//facing correct direction
			if(myRC.canMove(towerDir)){
				if(myRC.getFlux()>=RobotType.TOWER.spawnCost){
					myRC.spawn(RobotType.TOWER);
				}
			}
		}
		
	}
	public static int donateFlux() throws GameActionException{
		Robot [] robots = myRC.senseNearbyGameObjects(Robot.class);
		int i=robots.length;
		int alliedBots = 0;
		double fluxNeed = 0;
		double greatestFluxNeed = 10000;
		RobotInfo fluxInfo = null;
		while(--i>=0) {
			RobotInfo info = myRC.senseRobotInfo(robots[i]);
			if (info.team==myRC.getTeam()&&info.type==RobotType.ARCHON){
				alliedBots++;
				if(myRC.getLocation().distanceSquaredTo(info.location)<=2){//inside flux transfer range
					fluxNeed = info.type.maxFlux-info.flux;
					if(fluxNeed<greatestFluxNeed){
						greatestFluxNeed = fluxNeed;
						fluxInfo = info;
					}
				}
			}
		}
		transferFlux(fluxInfo,Math.min(greatestFluxNeed,myRC.getFlux()-FLUX_MIN));
		return alliedBots;
	}
}
