package samuraiPlayer.Robot;

import battlecode.common.*;

public class LightRobot extends NavRobot {
	
	private LightRobotType myType = LightRobotType.NONE;
	
	private boolean hasAntenna;
	
	/* For the constructor */
	private BuilderController buildController;
	
	
	private enum ConstructorState{
		FIND_EQUIPMENT,
		EXPLORING,
		MOVING_TO_MINE,
		BUILDING_FACTORY,
		BUILDING_ARMORY,
		BUILDING_RECYCLER,
		EQUIPPING_RECYCLER,
		EQUIPPING_FACTORY,
		EQUIPPING_ARMORY;
		
	};
	
	private ConstructorState currentConstructorState = ConstructorState.EXPLORING;
	
	//For Explore state
	private Direction exploreDirection = Direction.NONE;
	private MapLocation exploreLocation;
	
	//For Moving to mine state
	private MapLocation buildMineLocation;
	
	//For Building Armory/Factory state
	private MapLocation buildLocation;
	
	/* For the disciple */
	
	public LightRobot(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	
	protected void myRun() throws GameActionException{	
		myRC.yield();
		hasAntenna = false;
		
		//So, depending on what we have, change our robottype
		for(ComponentController c : myRC.components()){
			if(c.type() == ComponentType.CONSTRUCTOR){
				myType = LightRobotType.CONSTRUCTOR;
				buildController = (BuilderController)(c);
			}else if(c.componentClass().equals(ComponentClass.MOTOR)){
				moveController = (MovementController)(c);
			}else if(c.componentClass().equals(ComponentClass.SENSOR)){
				if(senseController == null)senseController = (SensorController)(c);
			}
		}
		
		
		switch(myType){
			case CONSTRUCTOR:
				runConstructor();
				break;
			default:
				break;
		
		}
	}
	
	protected void runConstructor() throws GameActionException{
		
		Mine[] nearbyMines;
		Direction buildDirection;
		Direction randDirection;
		boolean foundEmptyMine;
		myRC.setIndicatorString(0, "Current state: " + currentConstructorState);
		switch(currentConstructorState){
			case FIND_EQUIPMENT:
				
				
				break;
			case EXPLORING:
				
				
				//First, check to see if we can see any mines without recyclers. If so, break out
				//And go into moving to mine mode
				nearbyMines = senseController.senseNearbyGameObjects(Mine.class);
				foundEmptyMine = false;
				for(Mine m : nearbyMines){
					MapLocation mineLocation = m.getLocation();
					GameObject obj = senseController.senseObjectAtLocation(mineLocation, RobotLevel.ON_GROUND);
					if(obj == null){
						foundEmptyMine = true;
						buildMineLocation = mineLocation;
					}
					
				}
				
				if(foundEmptyMine){
					currentConstructorState = ConstructorState.MOVING_TO_MINE;
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
				myRC.setIndicatorString(1, exploreDirection+"");

				break;
			case MOVING_TO_MINE:
				if(buildMineLocation == null){
					currentConstructorState = ConstructorState.EXPLORING;
					break;
				}
				
				nearbyMines = senseController.senseNearbyGameObjects(Mine.class);
				foundEmptyMine = false;
				for(Mine m : nearbyMines){
					MapLocation mineLocation = m.getLocation();
					GameObject obj = senseController.senseObjectAtLocation(mineLocation, RobotLevel.ON_GROUND);
					if(obj == null){
						foundEmptyMine = true;
						buildMineLocation = mineLocation;
					}
					
				}
				
				if(myLocation.isAdjacentTo(buildMineLocation)){
					currentConstructorState = ConstructorState.BUILDING_RECYCLER;
					break;
				}
				
				moveTo(buildMineLocation);

				break;
			case BUILDING_RECYCLER:
				if(!myLocation.isAdjacentTo(buildMineLocation)){
					currentConstructorState = ConstructorState.MOVING_TO_MINE;
					break;
				}
				
				Direction directionToMine = myLocation.directionTo(buildMineLocation);
				//We should check to see if the recycler is the enemy's, and if so, destroy it.
				if(!myDirection.equals(directionToMine)){
					if(!moveController.isActive())moveController.setDirection(directionToMine);
					break;
				}else{
					Robot[] rs = senseController.senseNearbyGameObjects(Robot.class);
					GameObject mine = senseController.senseObjectAtLocation(buildMineLocation, RobotLevel.ON_GROUND);
					
					if(mine != null){
						//there's a frackin' mine in the way :(
						
						currentConstructorState = ConstructorState.EXPLORING;
						//Go back to explorin' the frontier.
					}
				}
				
				//We should check to see if the recycler is the enemy's, and if so, destroy it.
				
				if(!buildController.isActive() && myFlux > Chassis.BUILDING.cost){
					buildController.build(Chassis.BUILDING, buildMineLocation);
					currentConstructorState = ConstructorState.EQUIPPING_RECYCLER;
					break;
				}
				
				break;
			case EQUIPPING_RECYCLER:
				if(!myLocation.isAdjacentTo(buildMineLocation)){
					currentConstructorState = ConstructorState.MOVING_TO_MINE;
					break;
				}
				
				if(!buildController.isActive() && myFlux > ComponentType.RECYCLER.cost){
					try{
						buildController.build(ComponentType.RECYCLER, buildMineLocation, RobotLevel.ON_GROUND);
						currentConstructorState = ConstructorState.BUILDING_ARMORY;
					}catch(Exception e){
						currentConstructorState = ConstructorState.BUILDING_ARMORY;
					}
					break;
				}
				break;
			case BUILDING_ARMORY:
			case BUILDING_FACTORY:
				Message recycleMessage = myRC.getNextMessage();
				while(recycleMessage != null){
					if(recycleMessage.ints != null && recycleMessage.ints[0] == 1277){//oh wtf magic numbers
						if(recycleMessage.locations == null || recycleMessage.locations.length < 3)continue;
						if(currentConstructorState == ConstructorState.BUILDING_ARMORY && !recycleMessage.locations[2].equals(new MapLocation(0,0))){
							currentConstructorState = ConstructorState.EXPLORING;
							break;
						}
						if(currentConstructorState == ConstructorState.BUILDING_FACTORY && !recycleMessage.locations[1].equals(new MapLocation(0,0))){
							currentConstructorState = ConstructorState.EXPLORING;
							break;
						}
					}
					recycleMessage = myRC.getNextMessage();
				}
				//Find a factory location (nearby empty spot)
				if(buildLocation == null)buildLocation = buildMineLocation.add(getRandomDirection());
				
				
				if(!myLocation.isAdjacentTo(buildLocation)){
					moveTo(buildLocation.add(getRandomDirection()));
					break;
				}
				resetBug();
				
				Direction directionToBuild = myLocation.directionTo(buildLocation);
				
				if(!myDirection.equals(directionToBuild)){
					if(!moveController.isActive())moveController.setDirection(directionToBuild);
					break;
				}
				
				if(senseController.senseObjectAtLocation(buildLocation, RobotLevel.MINE) != null){
					buildLocation = buildMineLocation.add(getRandomDirection());
					break;
				}
				
				if(!moveController.canMove(directionToBuild)){
					//Find a new build location
					buildLocation = buildMineLocation.add(getRandomDirection());
					break;
				}
				
				//Otherwise we can try building
				if(!buildController.isActive() && myFlux > Chassis.BUILDING.cost){
					buildController.build(Chassis.BUILDING, buildLocation);
					currentConstructorState = currentConstructorState == ConstructorState.BUILDING_FACTORY ? ConstructorState.EQUIPPING_FACTORY : ConstructorState.EQUIPPING_ARMORY;
					break;
				}
				
				break;
			case EQUIPPING_FACTORY:
				if(!myLocation.isAdjacentTo(buildLocation)){
					//TODO: do a validation and move elsewhere if we can't get to this spot.
					moveTo(buildLocation);
					break;
				}
				
				if(!buildController.isActive() && myFlux > ComponentType.FACTORY.cost){
					buildController.build(ComponentType.FACTORY, buildLocation, RobotLevel.ON_GROUND);
					currentConstructorState = ConstructorState.EXPLORING;
					buildLocation = null;
					
					break;
				}
				break;
			case EQUIPPING_ARMORY:
				if(!myLocation.isAdjacentTo(buildLocation)){
					moveTo(buildLocation);
					break;
				}
				
				if(!buildController.isActive() && myFlux > ComponentType.ARMORY.cost){
					buildController.build(ComponentType.ARMORY, buildLocation, RobotLevel.ON_GROUND);
					currentConstructorState = ConstructorState.BUILDING_FACTORY;
					buildLocation = null;
					
					break;
				}
			default:
				break;
		}
	}

}
