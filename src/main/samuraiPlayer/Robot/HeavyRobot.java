package samuraiPlayer.Robot;

import battlecode.common.*;

public class HeavyRobot extends NavRobot {
	
	
	private enum ConquistadorState{
		GATHERING_EQUIPMENT,
		EXPLORING,
		BUILDING_RECYCLER,
		EQUIPPING_RECYCLER;
	
	}
	private MapLocation exploreLocation;
	private Direction exploreDirection;
	private MapLocation recyclerLocation, armoryLocation, buildMineLocation;
	private BuilderController buildController;
	private WeaponController attackController;
	private ConquistadorState currentConquistadorState = ConquistadorState.GATHERING_EQUIPMENT;
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
			}else if(c.componentClass().equals(ComponentClass.SENSOR)){
				senseController = (SensorController)(c);
			}else if(c.type().equals(ComponentType.CONSTRUCTOR)){
				buildController = (BuilderController)(c);
			}else if(c.type().equals(ComponentType.JUMP)){
				jumpController = (JumpController)(c);
			}else if(c.type().equals(ComponentType.SMG)){
				attackController = (WeaponController)(c);
			}
		}
		myType = HeavyRobotType.CONQUISTADOR;
		
		switch(myType){
			case CONQUISTADOR:
				runConquistador();
				break;
			default:
				break;
		
		}
	}
	
	private void runConquistador() throws GameActionException{
		myRC.setIndicatorString(1, myLocation+"");
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
		myRC.setIndicatorString(0, currentConquistadorState+"");
		switch(currentConquistadorState){
			case GATHERING_EQUIPMENT:
				//We want a jump, constructor, and radar
				//Jump from armory, radar from recycler, and constructor from recycler.
				
				
				
				//Get sensor first
				if(senseController == null){
					myRC.setIndicatorString(2, "Looking for sense");
					if(recyclerLocation != null){
						if(!moveTo(recyclerLocation)){
							moveTo(recyclerLocation.add(Direction.NORTH));
						}
					}
					
					break;
				}
				
				//Then get Constructor.
				if(buildController == null){
					myRC.setIndicatorString(2, "Looking for build");
					if(recyclerLocation != null){
						if(!moveTo(recyclerLocation)){
							moveTo(recyclerLocation.add(Direction.NORTH));
						}
					}
					break;
				}
				
				//Then get Constructor.
				if(attackController == null){
					myRC.setIndicatorString(2, "Looking for weapon");
					if(recyclerLocation != null){
						if(!moveTo(recyclerLocation)){
							moveTo(recyclerLocation.add(Direction.NORTH));
						}
					}
					break;
				}
				
				//Then get jump
				if(jumpController == null){
					myRC.setIndicatorString(2, "Looking for jump");
					if(armoryLocation != null){
						if(!moveTo(armoryLocation)){
							moveTo(armoryLocation.add(Direction.NORTH));
						}
					}
					break;
				}
				
				myRC.setIndicatorString(2, "Looking for nothing");
				
				
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
				if(!myLocation.isAdjacentTo(buildMineLocation)){
					moveTo(buildMineLocation.add(Direction.NORTH));//TODO: add the direction that would be to our base.
					break;
				}
				
				
				Direction directionToMine = myLocation.directionTo(buildMineLocation);
				//We should check to see if the recycler is the enemy's, and if so, destroy it.
				if(!myDirection.equals(directionToMine)){
					if(!moveController.isActive())moveController.setDirection(directionToMine);
				}else{
					Robot[] rs = senseController.senseNearbyGameObjects(Robot.class);
					GameObject mine = senseController.senseObjectAtLocation(buildMineLocation, RobotLevel.ON_GROUND);
					
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

}
