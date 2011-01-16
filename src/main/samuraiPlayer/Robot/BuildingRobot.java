package samuraiPlayer.Robot;

import battlecode.common.*;


public class BuildingRobot extends NavRobot {
	
	private BuildingRobotType myType = BuildingRobotType.NONE;
	
	private BuilderController buildController;
	private BroadcastController broadcastController;
	
	private boolean builtFactory = false;
	private int factoryBuildCount = 0;
	private int recyclerEquipCount = 0;
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
		else if((factoryBuildCount < 1 || myDeltaFlux > 5.0 || myFlux > 1000) && !buildController.isActive() && myRC.getTeamResources() > Chassis.BUILDING.cost*1.5 && (lastFactoryBuildRound == 0 || myCurrentRound - lastFactoryBuildRound > 10)){
			factoryBuildCount++;
			lastFactoryBuildRound = myCurrentRound;
			buildController.build(Chassis.HEAVY, myLocation.add(myDirection));	
			
			//else{
				//buildController.build(Chassis.MEDIUM, myLocation.add(myDirection));	
			//}
		}
		
		//Look for robots.
		Robot[] nearbyRobots = senseController.senseNearbyGameObjects(Robot.class);
		
		
		for(Robot r : nearbyRobots){
			if(buildController.isActive()) break;
			if(r.getTeam().equals(myTeam)){
				RobotInfo rInfo = senseController.senseRobotInfo(r);
				if(!rInfo.location.isAdjacentTo(myLocation))continue;
				ComponentType[] rComponents = rInfo.components;
				
				switch(rInfo.chassis){
					case BUILDING:
						for(ComponentType c : rComponents){
							if(c.equals(ComponentType.ARMORY)){
								MapLocation armoryLocation = rInfo.location;
								if(!rInfo.on && myLocation.isAdjacentTo(armoryLocation))myRC.turnOn(armoryLocation, RobotLevel.ON_GROUND);
								
							}
						}
						break;
					case LIGHT:
						break;
					case MEDIUM:
						break;
					case HEAVY:
						//Lessee.
						//We will build different things based on what they have.
						/*
						 * Samurai have:
						 * 1 jump
						 * 1 radar
						 * 4 blasters
						 * 
						 * Conquistadors have:
						 * 1 jump
						 * 1 smg
						 * 1 radar
						 * 1 constructor
						 * 
						 * Disciples have:
						 * 2 jump
						 * 1 radar
						 * 1 medic
						 * 
						 */
						
						int blasterCount = 0;
						int jumpCount = 0;
						int smgCount = 0;
						int medicCount = 0;
						
						boolean haveRadar = false;
						boolean haveConstructor = false;
						
						for(ComponentType c : rComponents){
							if(c.equals(ComponentType.RADAR)){
								haveRadar = true;
							}else if(c.equals(ComponentType.CONSTRUCTOR)){
								haveConstructor = true;
							}
							else if(c.equals(ComponentType.SMG)){
								smgCount++;
							}
							else if(c.equals(ComponentType.JUMP)){
								jumpCount++;
							}
							else if(c.equals(ComponentType.MEDIC)){
								medicCount++;
							}
							else if(c.equals(ComponentType.BLASTER)){
								blasterCount++;
							}
						}
						
						
						if(haveConstructor){
							//Conquistador has a constructor
						}else if(blasterCount > 0){
							//Samurai have blasters
						}else if(haveRadar){
							//It's important that this is last, as the others also have a radar
							//However, disciples have a radar and not the other two
							if(medicCount == 0)buildComponent(ComponentType.MEDIC, rInfo.location);
						}
						break;
					default:
						break;
				
				}
				
				
			}
		}
		
	}
	
	protected void runArmory() throws GameActionException{
		//Armories will build jump
		
		
		if(!moveController.isActive())moveController.setDirection(myDirection.opposite());

		//Look for robots.
		Robot[] nearbyRobots = senseController.senseNearbyGameObjects(Robot.class);
		
		
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
								MapLocation factoryLocation = rInfo.location;
								
								if(!rInfo.on && myLocation.isAdjacentTo(factoryLocation))myRC.turnOn(factoryLocation, RobotLevel.ON_GROUND);
							}
						}
						break;
					case LIGHT:
						break;
					case MEDIUM:
						break;
					case HEAVY:
						//Lessee.
						//We will build different things based on what they have.
						/*
						 * Samurai have:
						 * 1 jump
						 * 1 radar
						 * 4 blasters
						 * 
						 * Conquistadors have:
						 * 1 jump
						 * 1 smg
						 * 1 radar
						 * 1 constructor
						 * 
						 * Disciples have:
						 * 2 jump
						 * 1 radar
						 * 1 medic
						 * 
						 */
						
						int blasterCount = 0;
						int jumpCount = 0;
						int smgCount = 0;
						int medicCount = 0;
						
						boolean haveRadar = false;
						boolean haveConstructor = false;
						
						for(ComponentType c : rComponents){
							if(c.equals(ComponentType.RADAR)){
								haveRadar = true;
							}else if(c.equals(ComponentType.CONSTRUCTOR)){
								haveConstructor = true;
							}
							else if(c.equals(ComponentType.SMG)){
								smgCount++;
							}
							else if(c.equals(ComponentType.JUMP)){
								jumpCount++;
							}
							else if(c.equals(ComponentType.MEDIC)){
								medicCount++;
							}
							else if(c.equals(ComponentType.BLASTER)){
								blasterCount++;
							}
						}
						
						
						if(haveConstructor){
							//Conquistador has a constructor
							if(jumpCount == 0)buildComponent(ComponentType.JUMP, rInfo.location);
						}else if(blasterCount > 0){
							//Samurai have blasters
							if(jumpCount == 0)buildComponent(ComponentType.JUMP, rInfo.location);
						}else if(haveRadar){
							//It's important that this is last, as the others also have a radar
							//However, disciples have a radar and not the other two
							if(jumpCount == 0)buildComponent(ComponentType.JUMP, rInfo.location);
						}
						break;
					default:
						break;
				
				}
				
				
			}
		}
	}
	
	protected void buildComponent(ComponentType cType, MapLocation location) throws GameActionException{
		//We assume that the buildController is not active, and that we're building on the ground
		if(!buildController.isActive() && myRC.getTeamResources() > cType.cost){
			buildController.build(cType, location, RobotLevel.ON_GROUND);
		}
	}
	
	protected void runRecycler() throws GameActionException{
		
		//The recycler will decide what type of robot will be built.
		
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
								
								if(!rInfo.on && myLocation.isAdjacentTo(factoryLocation))myRC.turnOn(factoryLocation, RobotLevel.ON_GROUND);
							}else if(c.equals(ComponentType.ARMORY)){
								armoryLocation = rInfo.location;
								if(!rInfo.on && myLocation.isAdjacentTo(armoryLocation))myRC.turnOn(armoryLocation, RobotLevel.ON_GROUND);
								
							}
						}
						break;
					case LIGHT:
						break;
					case MEDIUM:
						break;
					case HEAVY:
							//Lessee.
							//We will build different things based on what they have.
							/*
							 * Samurai have:
							 * 1 jump
							 * 1 radar
							 * 4 blasters
							 * 
							 * Conquistadors have:
							 * 1 jump
							 * 1 smg
							 * 1 radar
							 * 1 constructor
							 * 
							 * Disciples have:
							 * 2 jump
							 * 1 radar
							 * 1 medic
							 * 
							 */
							
							int blasterCount = 0;
							int jumpCount = 0;
							int smgCount = 0;
							int medicCount = 0;
							
							boolean haveRadar = false;
							boolean haveConstructor = false;
							
							for(ComponentType c : rComponents){
								if(c.equals(ComponentType.RADAR)){
									haveRadar = true;
								}else if(c.equals(ComponentType.CONSTRUCTOR)){
									haveConstructor = true;
								}
								else if(c.equals(ComponentType.SMG)){
									smgCount++;
								}
								else if(c.equals(ComponentType.JUMP)){
									jumpCount++;
								}
								else if(c.equals(ComponentType.MEDIC)){
									medicCount++;
								}
								else if(c.equals(ComponentType.BLASTER)){
									blasterCount++;
								}
							}
							
							
							if(haveConstructor){
								//Conquistador has a constructor
								if(!haveRadar)buildComponent(ComponentType.RADAR, rInfo.location);
								else if(smgCount == 0)buildComponent(ComponentType.SMG, rInfo.location);
							}else if(blasterCount > 0){
								//Samurai have blasters
								if(!haveRadar)buildComponent(ComponentType.RADAR, rInfo.location);
								else if(blasterCount < 4)buildComponent(ComponentType.BLASTER, rInfo.location);
							}else if(haveRadar){
								//It's important that this is last, as the others also have a radar
								//However, disciples have a radar and not the other two
								//Haha. Turns out disciple grabs stuff from the armory and factory.
							}else{
								//The unit probably doesn't have much. So, we can start adding to it.
								//Decide what to make it by using our equip count
								int modCount = recyclerEquipCount % 4;
								if(recyclerEquipCount < 4){
									//Make conquistadors first
									buildComponent(ComponentType.CONSTRUCTOR, rInfo.location);
								}else if(modCount == 0){
									//Every so often afterwards, make a conquistador
									buildComponent(ComponentType.CONSTRUCTOR, rInfo.location);
								}else if(modCount < 3){
									//Build more samurai than disciples
									buildComponent(ComponentType.BLASTER, rInfo.location);
								}else if(modCount == 3){
									//Build a disciple
									buildComponent(ComponentType.RADAR, rInfo.location);
								}
								recyclerEquipCount++;
							}
						
						
						break;
					default:
						break;
				
				}
				
				
			}
		}
		
		boolean shouldBroadcast = !factoryLocation.equals(new MapLocation(0,0)) || !armoryLocation.equals(new MapLocation(0,0));
		
		//See if we need to build a broadcaster
		if(broadcastController == null && shouldBroadcast){
			if(!buildController.isActive() && myRC.getTeamResources() > ComponentType.ANTENNA.cost){
				buildController.build(ComponentType.ANTENNA, myLocation, RobotLevel.ON_GROUND);
			}
		}else if(shouldBroadcast && !broadcastController.isActive() && myRC.getTeamResources() > 10){
			
			
			Message echoMsg = new Message();
			echoMsg.locations = new MapLocation[3];
			echoMsg.locations[0] = myLocation;
			echoMsg.locations[1] = factoryLocation;
			echoMsg.locations[2] = armoryLocation;
			
			echoMsg.ints = new int[1];
			echoMsg.ints[0] = 1277;//Eh. Whatev.
			
			echoMsg.strings = new String[0];
			
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
