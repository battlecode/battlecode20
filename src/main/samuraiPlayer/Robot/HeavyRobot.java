package samuraiPlayer.Robot;

import battlecode.common.*;

public class HeavyRobot extends NavRobot {
	
	
	private enum ConquistadorState{
		GATHERING_EQUIPMENT,
		EXPLORING,
		BUILDING_RECYCLER,
		EQUIPPING_RECYCLER;
	}
	
	private enum DiscipleState{
		GATHERING_EQUIPMENT,
		QUESTING,
		RUNNING;
	
	}
	
	private enum SamuraiState{
		GATHERING_EQUIPMENT,
		QUESTING;
	
	}
	
	private MapLocation exploreLocation;
	private Direction exploreDirection;
	private MapLocation factoryLocation, recyclerLocation, armoryLocation, buildMineLocation;
	private BuilderController buildController;
	private WeaponController attackController;
	private WeaponController healController;
	private ConquistadorState currentConquistadorState = ConquistadorState.GATHERING_EQUIPMENT;
	private DiscipleState currentDiscipleState = DiscipleState.GATHERING_EQUIPMENT;
	private SamuraiState currentSamuraiState = SamuraiState.GATHERING_EQUIPMENT;
	private HeavyRobotType myType = HeavyRobotType.NONE;

	public HeavyRobot(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	

	protected void myRun() throws GameActionException{	

		myRC.yield();
		//So, depending on what we have, change our robottype
		for(ComponentController c : myRC.components()){
			if(c.componentClass().equals(ComponentClass.MOTOR)){
				moveController = (MovementController)(c);
			}else if(c.type().equals(ComponentType.RADAR)){
				senseController = (SensorController)(c);
				
				//We are a disciple if the first thing we have is a radar.
				if(myType == HeavyRobotType.NONE)myType = HeavyRobotType.DISCIPLE;
				
			}else if(c.type().equals(ComponentType.CONSTRUCTOR)){
				buildController = (BuilderController)(c);
				
				//We are a conquistador if we have a constructor
				myType = HeavyRobotType.CONQUISTADOR;
				
			}else if(c.type().equals(ComponentType.JUMP)){
				jumpController = (JumpController)(c);
			}else if(c.type().equals(ComponentType.SMG)){
				attackController = (WeaponController)(c);
				
			}else if(c.type().equals(ComponentType.BLASTER)){
				attackController = (WeaponController)(c);
				
				//We are a samurai if we have a blaster.
				myType = HeavyRobotType.SAMURAI;
			}else if(c.type().equals(ComponentType.MEDIC)){
				healController = (WeaponController)(c);
				
				//We are a samurai if we have a blaster.
				myType = HeavyRobotType.SAMURAI;
			}
			
		}
		
		
		switch(myType){
			case NONE:
				runNone();
				break;
			case CONQUISTADOR:
				runConquistador();
				break;
			case DISCIPLE:
				runDisciple();
				break;
			case SAMURAI:
				runSamurai();
				break;
			default:
				break;
		
		}
	}
	
	private void runNone() throws GameActionException{
		//We just find the nearest recycler and go to it
		if(recyclerLocation == null){
			Message recycleMessage = myRC.getNextMessage();
			while(recycleMessage != null){
				if(recycleMessage.ints != null && recycleMessage.ints[0] == 1277){//oh wtf magic numbers
					if(recycleMessage.locations != null && recycleMessage.locations.length >= 3){
						recyclerLocation = recycleMessage.locations[0];
						armoryLocation = recycleMessage.locations[2];
					}
					
				}
				recycleMessage = myRC.getNextMessage();
			}
		}
		
		if(recyclerLocation != null){
			if(!myLocation.isAdjacentTo(recyclerLocation)){
				moveTo(recyclerLocation.add(getRandomDirection()));
			}
		}
		
		
	}
	
	private void runConquistador() throws GameActionException{
		

		myRC.setIndicatorString(0, "Conquistador: " + currentConquistadorState+"");
		
		if(recyclerLocation == null){
			Message recycleMessage = myRC.getNextMessage();
			while(recycleMessage != null){
				if(recycleMessage.ints != null && recycleMessage.ints[0] == 1277){//oh wtf magic numbers
					if(recycleMessage.locations != null && recycleMessage.locations.length >= 3){
						recyclerLocation = recycleMessage.locations[0];
						factoryLocation = recycleMessage.locations[1];
						armoryLocation = recycleMessage.locations[2];
					}
					
				}
				recycleMessage = myRC.getNextMessage();
			}
		}
		switch(currentConquistadorState){
			case GATHERING_EQUIPMENT:
				//We want a jump, constructor, and radar
				//Jump from armory, radar from recycler, and constructor from recycler.
				
				
				
				//Get sensor first
				if(senseController == null){
					if(recyclerLocation != null){
						if(!myLocation.isAdjacentTo(recyclerLocation)){
							moveTo(recyclerLocation.add(getRandomDirection()));
						}
					}
					
					break;
				}
				
				/*
				//Then get Constructor.
				if(buildController == null){
					myRC.setIndicatorString(2, "Looking for build");
					if(recyclerLocation != null){
						if(!myLocation.isAdjacentTo(recyclerLocation)){
							moveTo(recyclerLocation.add(getRandomDirection()));
						}
					}
					break;
				}*/
				
				//Then get attack
				if(attackController == null){
					if(recyclerLocation != null){
						if(!moveTo(recyclerLocation)){
							moveTo(recyclerLocation.add(Direction.NORTH));
						}
					}
					break;
				}
				
				
				//Then get jump
				if(jumpController == null){
					if(armoryLocation != null){
						if(!myLocation.isAdjacentTo(armoryLocation)){
							moveTo(armoryLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				
				//Otherwise, we're fully equipped, so just gogogogo.
				currentConquistadorState = ConquistadorState.EXPLORING;
				
				break;
			case EXPLORING:
				//First, check to see if we can see any mines without recyclers. If so, break out
				//And go into moving to mine mode
				Mine[] nearbyMines = senseController.senseNearbyGameObjects(Mine.class);
				boolean foundEmptyMine = false;
				for(Mine m : nearbyMines){
					MapLocation mineLocation = m.getLocation();
					GameObject obj = senseController.senseObjectAtLocation(mineLocation, RobotLevel.ON_GROUND);
					if(obj == null || !obj.getTeam().equals(myTeam)){
						foundEmptyMine = true;
						buildMineLocation = mineLocation;
					}
					
				}
				
				if(foundEmptyMine){
					currentConquistadorState = ConquistadorState.BUILDING_RECYCLER;
					break;
				}
				
				//Otherwise, just gogogoglhf.
				
				
				if(exploreLocation == null){
					exploreDirection = getRandomDirection();
					exploreLocation = myLocation.add(exploreDirection, 30);
				}
				if(!moveTo(exploreLocation)){
					exploreDirection = getRandomDirection();
					exploreLocation = myLocation.add(exploreDirection, 30);
				}
				break;
			case BUILDING_RECYCLER:
				myRC.setIndicatorString(2, myLocation +  " 0 " + buildMineLocation + " " + myCurrentRound);
				
				if(!myLocation.isAdjacentTo(buildMineLocation)){
					moveTo(buildMineLocation.add(getRandomDirection()));//TODO: add the direction that would be to our base.
					break;
				}
				myRC.setIndicatorString(2, myLocation +  " 1 " + buildMineLocation + " " + myCurrentRound);
				
				
				Direction directionToMine = myLocation.directionTo(buildMineLocation);
				//We should check to see if the recycler is the enemy's, and if so, destroy it.
				if(!myDirection.equals(directionToMine)){
					if(!moveController.isActive())moveController.setDirection(directionToMine);
					break;
				}else{
					Robot[] rs = senseController.senseNearbyGameObjects(Robot.class);
					GameObject mine = senseController.senseObjectAtLocation(buildMineLocation, RobotLevel.ON_GROUND);
					myRC.setIndicatorString(2, myLocation +  " 2 " + buildMineLocation + " " + myCurrentRound);
					if(mine!= null)myRC.setIndicatorString(2, mine+ " " + mine.getTeam());
					
					if(mine != null){
						if(!mine.getTeam().equals(myTeam)){
							if(!attackController.isActive())attackController.attackSquare(buildMineLocation, RobotLevel.ON_GROUND);
							break;
						}else{
							currentConquistadorState = ConquistadorState.EXPLORING;
							break;
						}
					}
				}
				
				if(!buildController.isActive() && myFlux > Chassis.BUILDING.cost*1.5){
					buildController.build(Chassis.BUILDING, buildMineLocation);
					currentConquistadorState = ConquistadorState.EQUIPPING_RECYCLER;
					break;
				}
				
				break;
			case EQUIPPING_RECYCLER:
				if(!myLocation.isAdjacentTo(buildMineLocation)){
					moveTo(buildMineLocation);
					break;
				}
				
				if(!buildController.isActive() && myFlux > ComponentType.RECYCLER.cost){
					buildController.build(ComponentType.RECYCLER, buildMineLocation, RobotLevel.ON_GROUND);
					currentConquistadorState = ConquistadorState.EXPLORING;
					break;
				}
				break;
		}
		
	}
	
	private void runDisciple() throws GameActionException{
		myRC.setIndicatorString(0, "Disciple: " + currentDiscipleState+"");
		
		if(recyclerLocation == null){
			Message recycleMessage = myRC.getNextMessage();
			while(recycleMessage != null){
				if(recycleMessage.ints != null && recycleMessage.ints[0] == 1277){//oh wtf magic numbers
					if(recycleMessage.locations != null && recycleMessage.locations.length >= 3){
						recyclerLocation = recycleMessage.locations[0];
						factoryLocation = recycleMessage.locations[1];
						armoryLocation = recycleMessage.locations[2];
					}
				}
				recycleMessage = myRC.getNextMessage();
			}
		}
		
		switch(currentDiscipleState){
			case GATHERING_EQUIPMENT:
				//We want a jump, radar, and medic
				
				/*
				
				//Get sensor first
				if(senseController == null){
					if(recyclerLocation != null){
						if(!myLocation.isAdjacentTo(recyclerLocation)){
							moveTo(recyclerLocation.add(getRandomDirection()));
						}
					}
					
					break;
				}
				
				
				//Then get Constructor.
				if(buildController == null){
					myRC.setIndicatorString(2, "Looking for build");
					if(recyclerLocation != null){
						if(!myLocation.isAdjacentTo(recyclerLocation)){
							moveTo(recyclerLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				//Then get attack
				if(attackController == null){
					if(recyclerLocation != null){
						if(!moveTo(recyclerLocation)){
							moveTo(recyclerLocation.add(Direction.NORTH));
						}
					}
					break;
				}
				*/
				
				//Then get jump
				if(jumpController == null){
					if(armoryLocation != null){
						if(!myLocation.isAdjacentTo(armoryLocation)){
							moveTo(armoryLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				//Then get heal
				if(healController == null){
					if(factoryLocation != null){
						if(!myLocation.isAdjacentTo(factoryLocation)){
							moveTo(factoryLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				
				//Otherwise, we're fully equipped, so start questing
				currentDiscipleState = DiscipleState.QUESTING;
				break;
			case QUESTING:
				break;
		}
	}
	
	private void runSamurai() throws GameActionException{
		myRC.setIndicatorString(0, "Samurai: " + currentSamuraiState+"");
		
		if(recyclerLocation == null){
			Message recycleMessage = myRC.getNextMessage();
			while(recycleMessage != null){
				if(recycleMessage.ints != null && recycleMessage.ints[0] == 1277){//oh wtf magic numbers
					if(recycleMessage.locations != null && recycleMessage.locations.length >= 3){
						recyclerLocation = recycleMessage.locations[0];
						factoryLocation = recycleMessage.locations[1];
						armoryLocation = recycleMessage.locations[2];
					}
					
				}
				recycleMessage = myRC.getNextMessage();
			}
		}
		
		switch(currentSamuraiState){
			case GATHERING_EQUIPMENT:
				//We want a jump, radar, and blasters
				
				/*
				
				//Get sensor first
				if(senseController == null){
					if(recyclerLocation != null){
						if(!myLocation.isAdjacentTo(recyclerLocation)){
							moveTo(recyclerLocation.add(getRandomDirection()));
						}
					}
					
					break;
				}
				
				
				//Then get Constructor.
				if(buildController == null){
					myRC.setIndicatorString(2, "Looking for build");
					if(recyclerLocation != null){
						if(!myLocation.isAdjacentTo(recyclerLocation)){
							moveTo(recyclerLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				//Then get attack
				if(attackController == null){
					if(recyclerLocation != null){
						if(!moveTo(recyclerLocation)){
							moveTo(recyclerLocation.add(Direction.NORTH));
						}
					}
					break;
				}
				*/
				
				//Then get jump
				if(jumpController == null){
					if(armoryLocation != null){
						if(!myLocation.isAdjacentTo(armoryLocation)){
							moveTo(armoryLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				//Then get heal
				if(healController == null){
					if(factoryLocation != null){
						if(!myLocation.isAdjacentTo(factoryLocation)){
							moveTo(factoryLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				
				//Otherwise, we're fully equipped, so start questing
				currentSamuraiState = SamuraiState.QUESTING;
				break;
			case QUESTING:
				break;
		}
	
	}

}
