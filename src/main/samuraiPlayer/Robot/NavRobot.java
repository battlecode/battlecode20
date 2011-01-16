package samuraiPlayer.Robot;

import battlecode.common.*;

public class NavRobot extends BasicRobot {
	
	protected MovementController moveController;
	protected JumpController jumpController;
	
	protected Direction lastDirection = Direction.NONE;
	protected MapLocation lastLocation = new MapLocation(0,0);
	protected MapLocation lastLastLocation = new MapLocation(0,0);
	
	protected boolean rotateLeft = true;
	
	//For new bug
	protected boolean trackingObstacle = false;
	protected double closestDistToGoal = 0;
	protected boolean trackingCW = true;
	protected boolean movingBackwards = false;
	protected MapLocation bugLocation = new MapLocation(0,0);

	public NavRobot(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	
	protected boolean moveTo(MapLocation location) throws GameActionException{
		
		if(myLocation.equals(location))return false;
		
		
		boolean jumping = jumpController != null && senseController != null;
		
		if(!jumping && moveController == null)return false;
		if((jumping && jumpController.isActive()) || (!jumping && moveController.isActive()))return true;//We can probably still move, but not yet.
		if(jumping){
			//If we have a jump core and we're close, jump to the exact location
			if(location.distanceSquaredTo(myLocation) < 16 && jumpController != null && senseController != null && !jumpController.isActive()){
				return jumpToLocation(location);
			}
			//Direction we'll end up moving in
			Direction moveDirection;
			//Here we set the move direction. Instead of this lame thing, we could write better nav code
			moveDirection = myLocation.directionTo(location);
			return jumpInDirection(moveDirection);
		}else{
			return newBugToLocation(location);
		}
		
	}
	private boolean jumpToLocation(MapLocation location) throws GameActionException{
		
		//Direction we'll end up moving in
		Direction moveDirection;
		
		//Here we set the move direction. Instead of this lame thing, we could write better nav code
		moveDirection = myLocation.directionTo(location);

		//Look before we jump
		if(!myDirection.equals(moveDirection)){
			if(!moveController.isActive())moveController.setDirection(moveDirection);
			return true;
		}
		Direction currentDirection = Direction.NORTH;
		MapLocation jumpLocation = location;
		boolean notAbleToJump = true;
		int offset = 0;
		while(notAbleToJump){
			TerrainTile jumpTile = myRC.senseTerrainTile(jumpLocation) ;
			GameObject obj = null;
			boolean cantSense = false;
			if(!senseController.canSenseSquare(jumpLocation)){
				cantSense = true;
			}else{
				obj = senseController.senseObjectAtLocation(jumpLocation, RobotLevel.ON_GROUND);
			}
			
			
			if(cantSense || jumpTile == null || jumpLocation.distanceSquaredTo(myLocation) > 16|| jumpTile.equals(TerrainTile.VOID) || jumpTile.equals(TerrainTile.OFF_MAP) || obj != null){
				//We can't jump
				jumpLocation = location.add(currentDirection);
				currentDirection.rotateLeft();
				offset++;
				if(offset > 8)notAbleToJump = false;
			}else{
				notAbleToJump = false;
			}
		}
		
		if(offset>8){
			//can't go in that direction.
			return false;
		}else{
			//We don't really need the else, as we would return otherwise, but we can jump! :D
			jumpController.jump(jumpLocation);
			return true;
		}
	}
	
	private boolean jumpInDirection(Direction direction) throws GameActionException{
		//Look before we jump
				if(!myDirection.equals(direction)){
					if(!moveController.isActive())moveController.setDirection(direction);
					return true;
				}
				
				int jumpDist = direction.isDiagonal() ? 2 : 4;
				int offset = 0;
				MapLocation jumpLocation = myLocation.add(direction, jumpDist);
				boolean notAbleToJump = true;
				
				while(notAbleToJump){
					TerrainTile jumpTile = myRC.senseTerrainTile(jumpLocation) ;
					GameObject obj = null;;
					boolean cantSense = false;
					if(senseController == null || !senseController.canSenseSquare(jumpLocation)){
						
						cantSense = true;
					}else{
						obj = senseController.senseObjectAtLocation(jumpLocation, RobotLevel.ON_GROUND);
					}
					
					if(cantSense || jumpTile == null || jumpTile.equals(TerrainTile.VOID) || jumpTile.equals(TerrainTile.OFF_MAP) || obj != null){
						//We can't jump
						jumpLocation = jumpLocation.add(direction.opposite());
						jumpDist--;
						if(jumpDist == 0){
							switch(offset){
							case 0:
								jumpDist = 2;
								jumpLocation = myLocation.add(direction, jumpDist).add(direction.rotateRight().rotateRight());
								offset = 1;

								break;
							case 1:
								offset = 2;
								jumpDist = 2;
								jumpLocation = myLocation.add(direction, jumpDist).add(direction.rotateLeft().rotateLeft());
								
								break;
							case 2:
								notAbleToJump = false;//Sort of true.
								break;
							}
							
						}
					}else{
						notAbleToJump = false;
					}
				}
				
				if(jumpDist == 0){
					//can't go in that direction.
					return false;
				}else{
					//We don't really need the else, as we would return otherwise, but we can jump! :D
					jumpController.jump(jumpLocation);
					//myRC.setIndicatorString(2, "Jumping from " + myLocation + " to " + jumpLocation);
					return true;
				}
	}
	
	private boolean bugToLocation(MapLocation location) throws GameActionException{
		Direction myDirection = myRC.getDirection();
		Direction direction = myLocation.directionTo(location);
		if((moveController.canMove(direction) && myDirection.equals(direction)) || (!lastDirection.equals(Direction.NONE) && lastDirection.equals(myDirection) && moveController.canMove(lastDirection) ) ){
			//We are facing the way we want to go and we can move!
				
				lastLastLocation = lastLocation;
				lastLocation = myLocation;
				moveController.moveForward();
				
				lastDirection = Direction.NONE;
			
		}else{
			
			boolean directionBlocked = true;
			Direction moveDirection = myDirection;
			while(true){
				if(moveController.canMove(moveDirection)){// && !myLocation.add(direction).isAdjacentTo(lastLocation)){
					
					break;
				}else{
					moveDirection = rotateLeft ? moveDirection.rotateLeft() : moveDirection.rotateRight();
					
				}
				if(moveDirection.equals(myDirection)){
					break;
				}
			}

			moveController.setDirection(moveDirection);
			lastDirection = moveDirection;
		}
		return true;
	}
	private boolean myMoveInDirection(Direction direction) throws GameActionException{
		if(moveController.canMove(direction)){
			if(myDirection.equals(direction)){
				moveController.moveForward();
				movingBackwards = false;
			}/*else if(myDirection.equals(direction.opposite())){
				moveController.moveBackward();
				movingBackwards = true;
			}*/else{
				moveController.setDirection(direction);
				movingBackwards = false;
			}
			return true;
		}
		return false;
	}
	protected void resetBug(){
		trackingObstacle = false;
	}
	private boolean newBugToLocation(MapLocation location) throws GameActionException{

		/*if(!bugLocation.equals(location)){
			trackingObstacle = false;
			bugLocation = location;
		}*/
		
		Direction directionToTarget = myLocation.directionTo(location);
		
		/*
		 * We are either tracking an obstacle
		 * or we are not. 
		 */
		
		if(trackingObstacle){
			//We will stop if we are closer to the goal and we can move to the goal.
			/*
			 * So, this is what's going to happen.
			 * 
			 * First, check to see if we are closer than we ever have been.
			 * If we are, and we can move towards the target, go for it.
			 */
			double distToTarget = myLocation.distanceSquaredTo(location);
			if(distToTarget < closestDistToGoal){
				//TODO: Make sure the robot leaves tracking properly.
				closestDistToGoal = distToTarget;
					//Move towards that direction
					if(myMoveInDirection(directionToTarget)){
						trackingObstacle = false;
						return true;
					}
					
				
			}
			
			/* 
			 * We are going to have a "hand" out following teh wallz.
			 * 
			 * If we're going CW, then our hand is going to be our location plus direction.rotateRight.
			 */	
			Direction moveDirection = movingBackwards ? myDirection.opposite() : myDirection;
			Direction handDirection = trackingCW ? moveDirection.rotateRight() : moveDirection.rotateLeft();
			//MapLocation handLocation = myLocation.add(handDirection);
			
			if(!myMoveInDirection(handDirection)){
				
				//If we can't move in that direction, just follow the wall.
				
				while(!myMoveInDirection(moveDirection)){
					moveDirection = trackingCW ? moveDirection.rotateLeft() : moveDirection.rotateRight();
					
					if(moveDirection.equals(handDirection))return false;
				}
				return true;
			}
			
		}else{
			//keep going to target. if we can't move, then start tracking obstacle.
			//DEAD RECKON
			if(!myMoveInDirection(directionToTarget)){
				//There's an obstacle--start tracking
				trackingObstacle = true;
				trackingCW = !trackingCW;
				
				closestDistToGoal = myLocation.distanceSquaredTo(location);
				return true;
			}
		}
		return true;
	}
	
	protected boolean move(Direction direction){
		if(direction.equals(Direction.NONE) || direction.equals(Direction.OMNI))return false;

		boolean jumping = jumpController != null && senseController != null;
		
		
		if(!jumping && moveController == null)return false;
		if((jumping && jumpController.isActive()) || (!jumping && moveController.isActive()))return true;//We can probably still move, but not yet.
		try{
			if(jumping){
				
				return jumpInDirection(direction);
				
				
				
			}else{
				return newBugToLocation(myLocation.add(direction, 2));
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	

}
