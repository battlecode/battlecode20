package samuraiPlayer.Robot.copy;

import battlecode.common.ComponentController;
import battlecode.common.ComponentType;
import battlecode.common.RobotController;


public class BuildingRobot extends BasicRobot {
	
	private BuildingRobotType myType = BuildingRobotType.NONE;

	public BuildingRobot(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
	}
	
	protected void myRun(){	
		//So, depending on what we have, change our robottype
		for(ComponentController c : myRC.components()){
			if(c.type() == ComponentType.FACTORY){
				myType = BuildingRobotType.FACTORY;
			}else if(c.type() == ComponentType.ARMORY){
				myType = BuildingRobotType.ARMORY;
			}
		}
		
		switch(myType){
			case FACTORY:
				
				break;
			case ARMORY:
				break;
			default:
				break;
		
		}
	}
	
	protected void runFactory(){
		
	}
	
	protected void runArmory(){
		
	}
	

}
