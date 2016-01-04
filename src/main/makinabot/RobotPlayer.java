package makinabot;
import battlecode.common.*;
import java.util.Arrays;

public class RobotPlayer {
    
	static RobotController rc = null;

	
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    public static void run(RobotController grisaia) {
        rc = grisaia;
		// You can instantiate variables here.
		
		
		
        if (rc.getType() == RobotType.ARCHON) {
            try {
				// Any code here gets executed exactly once at the beginning of the game.
				rc.build(Direction.EAST, RobotType.SOLDIER);
				for (int i=0; i<20; i++) {
					Clock.yield();
				}
				rc.move(Direction.SOUTH);
				
            } catch (Exception e) {
                // Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
                // Caught exceptions will result in a bytecode penalty.
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
				try {
					Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.GUARD) {
            try {
				// Any code here gets executed exactly once at the beginning of the game.
                rc.move(Direction.EAST);
            } catch (Exception e) {
                // Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
                // Caught exceptions will result in a bytecode penalty.
				System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
                try {
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (rc.getType() == RobotType.TURRET) {
            try {
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
				try {
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
			}
        } else if (rc.getType() == RobotType.VIPER) {
			try {
				// Any code here gets executed exactly once at the beginning of the game.
            } catch (Exception e) {
                // Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
                // Caught exceptions will result in a bytecode penalty.
				System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
                try {
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
		}
	}
}