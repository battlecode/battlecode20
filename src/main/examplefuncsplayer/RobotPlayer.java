package examplefuncsplayer;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {

    private MovementController motor;
	private BuilderController builder;
	private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
		for(ComponentController comp : myRC.newComponents()) {
			if(comp instanceof MovementController)
				motor = (MovementController) comp;
			else if(comp instanceof BuilderController)
				builder = (BuilderController) comp;
		}
    }

	public void run() {
		if(myRC.getChassis()==Chassis.BUILDING)
			runBuilder();
		else
			runMotor();
	}

	public void runBuilder() {
	
		while (true) {
            try {

				myRC.yield();

				if(!motor.canMove(myRC.getDirection()))
					motor.setDirection(myRC.getDirection().rotateRight());
				else if(myRC.getTeamResources()>=Chassis.MEDIUM.cost)
					builder.build(Chassis.MEDIUM,myRC.getLocation().add(myRC.getDirection()));

            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}

    public void runMotor() {
        
        while (true) {
            try {
                /*** beginning of main loop ***/
                while (motor.isActive()) {
                    myRC.yield();
                }

                if (motor.canMove(myRC.getDirection())) {
                    System.out.println("about to move");
                    motor.moveForward();
                } else {
                    motor.setDirection(myRC.getDirection().rotateRight());
                }
                myRC.yield();

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}
