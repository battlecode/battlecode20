package samuraiPlayer.Robot;

import battlecode.common.*;


public class BuildingRobot extends NavRobot {
	
	private BuildingRobotType myType = BuildingRobotType.NONE;
	
	private BuilderController buildController;
	private BroadcastController broadcastController;
	
	private boolean builtFactory = false;
	private int factoryBuildCount = 0;
	private int lastFactoryBuildRound = 0;
	
	
	

	public BuildingRobot(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
		
	}
	
	protected void myRun() throws GameActionException{	
		myRC.yield();
		//So, depending on what we have, change our robottype
		for(ComponentController c : myRC.components()){
			if(c.type() == ComponentType.FACTORY){
				myType = BuildingRobotType.FACTORY;
				buildController = (BuilderController)(c);
			}else if(c.type() == ComponentType.ARMORY){
				myType = BuildingRobotType.ARMORY;
				buildController = (BuilderController)(c);
			}else if(c.type() == ComponentType.RECYCLER){
				myType = BuildingRobotType.RECYCLER;
				buildController = (BuilderController)(c);
			}else if(c.componentClass().equals(ComponentClass.MOTOR)){
				moveController = (MovementController)(c);
			}else if(c.componentClass().equals(ComponentClass.SENSOR)){
				senseController = (SensorController)(c);
			}else if(c.componentClass().equals(ComponentClass.COMM)){
				broadcastController = (BroadcastController)(c);
			}
		}
		myRC.setIndicatorString(1, myLocation+"");
		
		switch(myType){
			case FACTORY:
				runFactory();
				break;
			case ARMORY:
				runArmory();
				break;
			case RECYCLER:
				runRecycler();
				break;
			default:
				break;
		
		}
	}
	
	protected void runFactory() throws GameActionException {
		//Factories build samurai. by which I mean heavies.
		if(!moveController.canMove(myDirection)){
			//We have to turn.
			if(!moveController.isActive()) moveController.setDirection(myDirection.rotateLeft());
		}
		
		//Otherwise we can try building
		else if(!buildController.isActive() && myFlux > Chassis.BUILDING.cost*1.5 && (lastFactoryBuildRound == 0 || myCurrentRound - lastFactoryBuildRound > 500)){
			factoryBuildCount++;
			lastFactoryBuildRound = myCurrentRound;
			buildController.build(Chassis.HEAVY, myLocation.add(myDirection));	
			//else{
				//buildController.build(Chassis.MEDIUM, myLocation.add(myDirection));	
			//}
		}
		
	}
	
	protected void runArmory() throws GameActionException{
		//Armories will build jump
		
		
		if(!moveController.isActive())moveController.setDirection(myDirection.opposite());

		//Look for robots.
		Robot[] nearbyRobots = senseController.senseNearbyGameObjects(Robot.class);
		
		boolean seeFactory = false;
		boolean seeArmory = false;
		
		for(Robot r : nearbyRobots){
			if(buildController.isActive()) break;
			if(r.getTeam().equals(myTeam)){
				RobotInfo rInfo = senseController.senseRobotInfo(r);
				if(!rInfo.location.isAdjacentTo(myLocation))continue;
				ComponentType[] rComponents = rInfo.components;
				
				switch(rInfo.chassis){
					case LIGHT:
						break;
					case MEDIUM:
						break;
					case HEAVY:
						//Build radar if possible
						boolean haveJump = false;
						for(ComponentType c : rComponents){
							if(c.equals(ComponentType.JUMP)){
								haveJump = true;
							}
						}
						
						
						if(!haveJump){
							if(myFlux > ComponentType.JUMP.cost){
								buildController.build(ComponentType.JUMP, rInfo.location, RobotLevel.ON_GROUND);
								
							}
						}
						break;
					default:
						break;
				
				}
				
				
			}
		}
	}
	
	protected void runRecycler() throws GameActionException{
		
		if(!moveController.isActive())moveController.setDirection(myDirection.opposite());

		//Look for robots.
		Robot[] nearbyRobots = senseController.senseNearbyGameObjects(Robot.class);
		
		MapLocation factoryLocation = new MapLocation(0,0);
		MapLocation armoryLocation = new MapLocation(0,0);
		
		for(Robot r : nearbyRobots){
			if(buildController.isActive()) break;
			if(r.getTeam().equals(myTeam)){
				RobotInfo rInfo = senseController.senseRobotInfo(r);
				if(!rInfo.location.isAdjacentTo(myLocation))continue;
				ComponentType[] rComponents = rInfo.components;
				
				switch(rInfo.chassis){
					case BUILDING:
						for(ComponentType c : rComponents){
							if(c.equals(ComponentType.FACTORY)){
								factoryLocation = rInfo.location;
							}else if(c.equals(ComponentType.ARMORY)){
								armoryLocation = rInfo.location;
							}
						}
						break;
					case LIGHT:
						break;
					case MEDIUM:
						break;
					case HEAVY:
						//Build radar if possible
						boolean haveRadar = false;
						boolean haveConstructor = false;
						boolean haveWeapon = false;
						for(ComponentType c : rComponents){
							if(c.equals(ComponentType.RADAR)){
								haveRadar = true;
							}else if(c.equals(ComponentType.CONSTRUCTOR)){
								haveConstructor = true;
							}
							else if(c.equals(ComponentType.SMG)){
								haveWeapon = true;
							}
						}
						
						
						if(!haveRadar){
							if(myFlux > ComponentType.RADAR.cost){
								buildController.build(ComponentType.RADAR, rInfo.location, RobotLevel.ON_GROUND);
								
							}
						}else if(!haveConstructor){
							if(myFlux > ComponentType.CONSTRUCTOR.cost){
								buildController.build(ComponentType.CONSTRUCTOR, rInfo.location, RobotLevel.ON_GROUND);
							}
						}else if(!haveWeapon){
							if(myFlux > ComponentType.SMG.cost){
								buildController.build(ComponentType.SMG, rInfo.location, RobotLevel.ON_GROUND);
							}
						}
						break;
					default:
						break;
				
				}
				
				
			}
		}
		
		//See if we need to build a broadcaster
		if(broadcastController == null){
			if(!buildController.isActive()){
				buildController.build(ComponentType.ANTENNA, myLocation, RobotLevel.ON_GROUND);
			}
		}else if(!broadcastController.isActive()){
			
			
			Message echoMsg = new Message();
			echoMsg.locations = new MapLocation[3];
			echoMsg.locations[0] = myLocation;
			echoMsg.locations[1] = factoryLocation;
			echoMsg.locations[2] = armoryLocation;
			
			echoMsg.ints = new int[1];
			echoMsg.ints[0] = 1277;//Eh. Whatev.
			
			
			broadcastController.broadcast(echoMsg);
		}
		
		//Ignore the stuff below for now
		

		//The recycler is simple.
		/* First, turn to face away from a mine. */
		/*
		
		//Get the mines
		Mine nearbyMines[] = senseController.senseNearbyGameObjects(Mine.class);
		int minesLength = nearbyMines.length;
		
		//Find out which ways we can't point.
		Direction[] inviableDirections = new Direction[minesLength];
		int i = 0;
		for(Mine m : nearbyMines){
			inviableDirections[i] =  myLocation.directionTo(m.getLocation());
			i++;
		}
		
		Direction turnDirection = myDirection;
		boolean stillTurning = true;
		boolean inviable = false;
		int numTurns = 0;
		while(stillTurning){
			
			inviable = false;
			
			//First, see if it's in the inviable directions
			for(i = 0; i < minesLength; i++){
				if(inviableDirections[i].equals(turnDirection)){
					inviable = true;
				}
			}
			
			//See if we can't spawn in the direction
			if(!moveController.canMove(turnDirection)){
				inviable = true;
			}
			
			//If inviable, we rotate.
			if(inviable){
				turnDirection = turnDirection.rotateLeft();
			}else{
				stillTurning = false;
				//Otherwise, we're facing a proper direction
			}
			numTurns++;
			if(numTurns > 10)stillTurning = false;
			
		}
		*/
		
		//We are now facing a legit direction if numTurns < 10
		//Turn if need be.
		/* Apparently we can spawn in any direction
		if(!myDirection.equals(turnDirection)){
			moveController.setDirection(turnDirection);
		}*/
		

		/* Oh wtf. Recyclers can't build buildings.
		
		//Spawn a factory if we haven't built one (this should be more like, if there isn't one nearby)
		if(!builtFactory && numTurns < 10 && myFlux > Chassis.BUILDING.cost*1.5){
			myRC.setIndicatorString(0, myFlux+"");
			
			
			buildController.build(Chassis.BUILDING,myLocation.add(turnDirection));
			myRC.setIndicatorString(0, myFlux+" lalala");
			builtFactory = true;
		}
		
		
		*/
		
		
	}
	

}
