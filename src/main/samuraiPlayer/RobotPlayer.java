package samuraiPlayer;

import samuraiPlayer.Robot.*;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {

	private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

	public void run() {
		ComponentController [] components = myRC.newComponents();
		System.out.println(java.util.Arrays.toString(components));
		System.out.flush();
		
		BasicRobot robot;
		
    	
    	switch(myRC.getChassis()){
    		case BUILDING:
    			robot = new BuildingRobot(myRC);
    			break;
    		case LIGHT:
    			robot = new LightRobot(myRC);
        		break;
    		case MEDIUM:
    			robot = new MediumRobot(myRC);
        		break;
    		case HEAVY:
    			robot = new HeavyRobot(myRC);
        		break;
    		case FLYING:
    			robot = new BasicRobot(myRC);
        		break;
        	default:
        		robot = new BasicRobot(myRC);
        		break;
    	}
    	
    	robot.run();
    	
    	
	}

}
