package samuraiPlayer.Robot;

import java.util.ArrayList;

import battlecode.common.*;

public class HeavyRobot extends NavRobot {
	
	
	private enum ConquistadorState{
		SUIT_UP,
		EXPLORING,
		BUILDING_RECYCLER,
		EQUIPPING_RECYCLER;
	}
	
	private enum DiscipleState{
		SUIT_UP,
		QUESTING,
		RUNNING;
	
	}
	
	private enum SamuraiState{
		SUIT_UP,
		QUESTING,
		DUELING;
	
	}
	
	private MapLocation exploreLocation;
	private Direction exploreDirection;
	private MapLocation factoryLocation, recyclerLocation, armoryLocation, buildMineLocation;
	private BuilderController buildController;
//	private WeaponController attackController;
	private WeaponController[] healControllers = new WeaponController[10];
	private int numHeals = 0;
	private WeaponController[] attackControllers = new WeaponController[10];
	private int numAttacks = 0;
	private ConquistadorState currentConquistadorState = ConquistadorState.SUIT_UP;
	private DiscipleState currentDiscipleState = DiscipleState.SUIT_UP;
	private SamuraiState currentSamuraiState = SamuraiState.SUIT_UP;
	private HeavyRobotType myType = HeavyRobotType.NONE;

	public HeavyRobot(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	

	protected void myRun() throws GameActionException{	

		myRC.yield();
		
		numAttacks = 0;
		numHeals = 0;
		
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
				attackControllers[numAttacks] = ((WeaponController)(c));
				numAttacks++;
			}else if(c.type().equals(ComponentType.BLASTER)){
				attackControllers[numAttacks] = ((WeaponController)(c));
				numAttacks++;
				//We are a samurai if we have a blaster.
				myType = HeavyRobotType.SAMURAI;
			}else if(c.type().equals(ComponentType.MEDIC)){
				healControllers[numHeals] = ((WeaponController)(c));
				numHeals++;
				
				
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

						MapLocation aLocation = recycleMessage.locations[2];
						if(!aLocation.equals(new MapLocation(0,0)))armoryLocation = aLocation;
					}
					
				}
				recycleMessage = myRC.getNextMessage();
			}
		}
		

		
		if(recyclerLocation != null){
			if(!myLocation.isAdjacentTo(recyclerLocation)){
				if(myLocation.distanceSquaredTo(recyclerLocation) > 16)resetBug(); 
				moveTo(recyclerLocation.add(getRandomDirection()));
				
			}
		}
		
		
	}
	
	private WeaponController getAttackController(){
		WeaponController returnController = null;
		for(int i = 0; i < numAttacks; i++){
			WeaponController weapon = attackControllers[i];
			if(!weapon.isActive()){
				returnController = weapon;
				break;
			}
			
		}
		
		return returnController;
		
	}
	
	private WeaponController getHealController(){
		WeaponController returnController = null;
		for(int i = 0; i < numHeals; i++){
			WeaponController weapon = healControllers[i];
			if(!weapon.isActive()){
				returnController = weapon;
				break;
			}
			
		}
		
		return returnController;
		
	}
	
	private void runConquistador() throws GameActionException{
		

		myRC.setIndicatorString(0, "Conquistador: " + currentConquistadorState+" " + myLocation);
		if(recyclerLocation == null){
			Message recycleMessage = myRC.getNextMessage();
			while(recycleMessage != null){
				if(recycleMessage.ints != null && recycleMessage.ints[0] == 1277){//oh wtf magic numbers
					if(recycleMessage.locations != null && recycleMessage.locations.length >= 3){
						recyclerLocation = recycleMessage.locations[0];
						MapLocation fLocation = recycleMessage.locations[1];
						if(!fLocation.equals(new MapLocation(0,0)))factoryLocation = fLocation;
						
						MapLocation aLocation = recycleMessage.locations[2];
						if(!aLocation.equals(new MapLocation(0,0)))armoryLocation = aLocation;
					}
					
				}
				recycleMessage = myRC.getNextMessage();
			}
		}
		WeaponController attackController = null;
		
		switch(currentConquistadorState){
			case SUIT_UP:
				//We want a jump, constructor, and radar
				//Jump from armory, radar from recycler, and constructor from recycler.
				
				
				
				//Get sensor first
				if(senseController == null){
					if(recyclerLocation != null){
						if(!myLocation.isAdjacentTo(recyclerLocation)){
							if(myLocation.distanceSquaredTo(recyclerLocation) > 16)resetBug(); 
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
				attackController = getAttackController();
				
				
				if(attackController == null){
					
					if(recyclerLocation != null){
						if(!moveTo(recyclerLocation)){
							if(myLocation.distanceSquaredTo(recyclerLocation) > 16)resetBug(); 
							moveTo(recyclerLocation.add(Direction.NORTH));
						}
					}
					break;
				}
				
				
				//Then get jump
				if(jumpController == null){
					if(armoryLocation != null){
						if(!myLocation.isAdjacentTo(armoryLocation)){
							if(myLocation.distanceSquaredTo(armoryLocation) > 16)resetBug(); 
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
				
				if(!myLocation.isAdjacentTo(buildMineLocation)){
					moveTo(buildMineLocation.add(getRandomDirection()));//TODO: add the direction that would be to our base.
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
				
					attackController = getAttackController();
					
					if(mine != null){
						if(!mine.getTeam().equals(myTeam)){
							if(attackController != null)attackController.attackSquare(buildMineLocation, RobotLevel.ON_GROUND);
							break;
						}else{
							currentConquistadorState = ConquistadorState.EXPLORING;
							break;
						}
					}
				}
				
				if(!buildController.isActive() && myRC.getTeamResources() > Chassis.BUILDING.cost){
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
				
				Direction directionToBuild = myLocation.directionTo(buildMineLocation);
				if(!myDirection.equals(directionToBuild)){
					if(!moveController.isActive()){
						moveController.setDirection(directionToBuild);
					}
					break;
				}
				GameObject obj = senseController.senseObjectAtLocation(buildMineLocation, RobotLevel.ON_GROUND);
				if(obj == null || !obj.getTeam().equals(myTeam)){
					currentConquistadorState = ConquistadorState.EXPLORING;
					break;
					
				}
				
				if(!buildController.isActive() && myRC.getTeamResources() > ComponentType.RECYCLER.cost){
					buildController.build(ComponentType.RECYCLER, buildMineLocation, RobotLevel.ON_GROUND);
					currentConquistadorState = ConquistadorState.EXPLORING;
					break;
				}
				break;
		}
		
	}
	
	private void runDisciple() throws GameActionException{
		myRC.setIndicatorString(0, "Disciple: " + currentDiscipleState+" " + myLocation);
		
		if(recyclerLocation == null){
			Message recycleMessage = myRC.getNextMessage();
			while(recycleMessage != null){
				if(recycleMessage.ints != null && recycleMessage.ints[0] == 1277){//oh wtf magic numbers
					if(recycleMessage.locations != null && recycleMessage.locations.length >= 3){
						recyclerLocation = recycleMessage.locations[0];
						MapLocation fLocation = recycleMessage.locations[1];
						if(!fLocation.equals(new MapLocation(0,0)))factoryLocation = fLocation;
						
						MapLocation aLocation = recycleMessage.locations[2];
						if(!aLocation.equals(new MapLocation(0,0)))armoryLocation = aLocation;
					}
				}
				recycleMessage = myRC.getNextMessage();
			}
		}
		
		WeaponController healController = null;
		
		switch(currentDiscipleState){
			case SUIT_UP:
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
							if(myLocation.distanceSquaredTo(armoryLocation) > 16)resetBug(); 
							moveTo(armoryLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				//Then get heal
				if(numHeals < 1){
					if(factoryLocation != null){
						if(!myLocation.isAdjacentTo(factoryLocation)){
							if(myLocation.distanceSquaredTo(factoryLocation) > 16)resetBug(); 
							moveTo(factoryLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				
				//Otherwise, we're fully equipped, so start questing
				currentDiscipleState = DiscipleState.QUESTING;
				break;
			case QUESTING:
//Roam around and kill shite
				
				Robot robots[] = senseController.senseNearbyGameObjects(Robot.class);
				healController = getHealController();
				boolean healed = false;
				for(Robot r : robots){
					RobotInfo rInfo = senseController.senseRobotInfo(r);
					if(r.getTeam().equals(myTeam) && rInfo.hitpoints < rInfo.maxHp){
						MapLocation rLocation = rInfo.location;
						
						int healCount = 0;
						while(healController != null && healCount < numHeals){
							
							if(healController.withinRange(rLocation)){
								healController.attackSquare(rLocation, r.getRobotLevel());
								healed = true;
							}
							healController = getAttackController();
							healCount++;
						}
						if(healed){
							//Stay here.
							break;
						}
					}
				}
				
				if(!healed){//Stay if we're healing
					if(exploreLocation == null){
						exploreDirection = getRandomDirection();
						exploreLocation = myLocation.add(exploreDirection, 30);
					}
					if(!moveTo(exploreLocation)){
						exploreDirection = getRandomDirection();
						exploreLocation = myLocation.add(exploreDirection, 30);
					}
				}
				
				
				break;
		}
	}
	
	private void runSamurai() throws GameActionException{
		myRC.setIndicatorString(0, "Samurai: " + currentSamuraiState+" " + myLocation);
		
		if(recyclerLocation == null){
			Message recycleMessage = myRC.getNextMessage();
			while(recycleMessage != null){
				if(recycleMessage.ints != null && recycleMessage.ints[0] == 1277){//oh wtf magic numbers
					if(recycleMessage.locations != null && recycleMessage.locations.length >= 3){
						recyclerLocation = recycleMessage.locations[0];
						MapLocation fLocation = recycleMessage.locations[1];
						if(!fLocation.equals(new MapLocation(0,0)))factoryLocation = fLocation;
						
						MapLocation aLocation = recycleMessage.locations[2];
						if(!aLocation.equals(new MapLocation(0,0)))armoryLocation = aLocation;
					}
					
				}
				recycleMessage = myRC.getNextMessage();
			}
		}
		
		WeaponController attackController = null;
		switch(currentSamuraiState){
			case SUIT_UP:
				//We want a jump, radar, and blasters
				
				
				
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
				}
				*/
				
				//Then get attack
				if(numAttacks < 4){
					if(recyclerLocation != null){
						if(!moveTo(recyclerLocation)){
							moveTo(recyclerLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				
				
				//Then get jump
				if(jumpController == null){
					if(armoryLocation != null){
						if(!myLocation.isAdjacentTo(armoryLocation)){
							if(myLocation.distanceSquaredTo(armoryLocation) > 16)resetBug(); 
							moveTo(armoryLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				/*
				//Then get 
				if(healController == null){
					if(factoryLocation != null){
						if(!myLocation.isAdjacentTo(factoryLocation)){
							if(myLocation.distanceSquaredTo(factoryLocation) > 16)resetBug(); 
							moveTo(factoryLocation.add(getRandomDirection()));
						}
					}
					break;
				}
				*/
				
				//Otherwise, we're fully equipped, so start questing
				currentSamuraiState = SamuraiState.QUESTING;
				break;
			case QUESTING:
				//Roam around and kill shite
				
				Robot robots[] = senseController.senseNearbyGameObjects(Robot.class);
				attackController = getAttackController();
				boolean attacked = false;
				for(Robot r : robots){
					RobotInfo rInfo = senseController.senseRobotInfo(r);
					if(r.getTeam().equals(myTeam.opponent()) ){
						MapLocation rLocation = rInfo.location;
						
						int attackCount = 0;
						while(attackController != null && attackCount < numAttacks){
							
							if(attackController.withinRange(rLocation)){
								attackController.attackSquare(rLocation, r.getRobotLevel());
								attacked = true;
							}
							attackController = getAttackController();
							attackCount++;
						}
						if(attacked){
							if(exploreDirection != null)moveTo(myLocation.add(exploreDirection.opposite(), 30));
							break;
						}
					}
				}
				
				if(!attacked){
					if(exploreLocation == null){
						exploreDirection = getRandomDirection();
						exploreLocation = myLocation.add(exploreDirection, 30);
					}
					if(!moveTo(exploreLocation)){
						exploreDirection = getRandomDirection();
						exploreLocation = myLocation.add(exploreDirection, 30);
					}
				}
				
				
				break;
			case DUELING:
				
				break;
		}
	
	}

}
