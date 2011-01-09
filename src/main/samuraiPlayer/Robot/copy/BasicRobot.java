package samuraiPlayer.Robot.copy;

import battlecode.common.RobotController;

public class BasicRobot {
	protected final RobotController myRC;
	
	
	
	public BasicRobot(RobotController rc){
		myRC = rc;
	}
	
	public void run(){
		while (true){
			try{
				//Main run loop
				myRun();
				
				
			}catch(Exception e){
				
			}
			
		}
	}
	
	protected void myRun(){
		
		
	}
}
