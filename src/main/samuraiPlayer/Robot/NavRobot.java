package samuraiPlayer.Robot;

import battlecode.common.*;

public class NavRobot extends BasicRobot {
	
	protected MovementController moveController;
	protected JumpController jumpController;
	
	protected Direction lastDirection = Direction.NONE;
	protected MapLocation lastLocation = new MapLocation(0,0);

	public NavRobot(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	
	protected boolean moveTo(MapLocation location) throws GameActionException{
		
		if(myLocation.equals(location))return false;
		//Direction we'll end up moving in
		Direction moveDirection;
		
		//Here we set the move direction. Instead of this lame thing, we could write better nav code
		moveDirection = myLocation.directionTo(location);
		
		
		//If we have a jump core and we're close, jump to the exact location
		if(location.distanceSquaredTo(myLocation) < 16 && jumpController != null && senseController != null && !jumpController.isActive()){
			
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
				GameObject obj;
				if(senseController == null || !senseController.canSenseSquare(jumpLocation)){
					obj = null;
				}else{
					obj = senseController.senseObjectAtLocation(jumpLocation, RobotLevel.ON_GROUND);
				}
				
				if(jumpTile == null || jumpTile.equals(TerrainTile.VOID) || jumpTile.equals(TerrainTile.OFF_MAP) || obj != null){
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
		
		
		return move(moveDirection);
	}
	
	protected boolean move(Direction direction){
		if(direction.equals(Direction.NONE) || direction.equals(Direction.OMNI))return false;

		boolean jumping = jumpController != null && senseController != null;
		
		
		if(!jumping && moveController == null)return false;
		if((jumping && jumpController.isActive()) || (!jumping && moveController.isActive()))return true;//We can probably still move, but not yet.
		try{
			if(jumping){
				
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
					GameObject obj;
					if(senseController == null || !senseController.canSenseSquare(jumpLocation)){
						obj = null;
					}else{
						obj = senseController.senseObjectAtLocation(jumpLocation, RobotLevel.ON_GROUND);
					}
					
					if(jumpTile == null || jumpTile.equals(TerrainTile.VOID) || jumpTile.equals(TerrainTile.OFF_MAP) || obj != null){
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
				
				
				
			}else{
				Direction myDirection = myRC.getDirection();
				if((moveController.canMove(direction) && myDirection.equals(direction)) || (!lastDirection.equals(Direction.NONE) && lastDirection.equals(myDirection) && moveController.canMove(lastDirection) ) ){
					//We are facing the way we want to go and we can move!
						lastLocation = myLocation;
						moveController.moveForward();
						
						lastDirection = Direction.NONE;
					
				}else{
					boolean directionBlocked = true;
					while(directionBlocked){
						if(moveController.canMove(direction)){// && !myLocation.add(direction).isAdjacentTo(lastLocation)){
							directionBlocked = false;
							
						}else{
							direction = direction.rotateLeft();
							
						}
					}
					moveController.setDirection(direction);
					lastDirection = direction;
				}
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	

}
