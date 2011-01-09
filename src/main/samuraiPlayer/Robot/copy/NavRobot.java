package samuraiPlayer.Robot.copy;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.MovementController;
import battlecode.common.RobotController;

public class NavRobot extends BasicRobot {
	
	private MovementController moveController;

	public NavRobot(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	
	protected void moveTo(MapLocation location){
		//Check to make sure we have a moveController
		if(moveController == null) return;
		
		//Check to make sure it's not active
		if(moveController.isActive())return;
		
		//Direction we'll end up moving in
		Direction moveDirection;
		
		//Here we set the move direction. Instead of this lame thing, we could write better nav code
		moveDirection = myRC.getLocation().directionTo(location);
		
		move(moveDirection);
	}
	
	protected void move(Direction direction){
		try{
			
			Direction myDirection = myRC.getDirection();
			if(myDirection == myRC.getDirection()){
				//We are facing the way we want to go
				if(moveController.canMove(direction)){
					//We can move!
					moveController.moveForward();
				}
			}else{
				moveController.setDirection(direction);
			}
		}catch(Exception e){}
	}

}
