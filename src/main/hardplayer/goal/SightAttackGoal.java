package hardplayer.goal;

import hardplayer.Static;

import battlecode.common.Chassis;
import battlecode.common.ComponentType;
import battlecode.common.Direction;
import battlecode.common.RobotLevel;
import battlecode.common.RobotInfo;

public class SightAttackGoal extends Static implements Goal {

	boolean isSMG;

	public int maxPriority() {
		return CONSTRUCTOR_ATTACK;
	}

	public int priority() {
		if(enemies.size>0)
			return CONSTRUCTOR_ATTACK;
		else if(debris.size>0)
			return ATTACK_DEBRIS;
		else
			return 0;
	}

	public void execute() {
		try {
			RobotInfo enemy = closestEnemy();
			if(enemy!=null) {
				Direction d = myLoc.directionTo(enemy.location);
				if(weapons[0].type()==ComponentType.SMG) {
					if(d==Direction.OMNI)
						return;
					if(d!=myRC.getDirection()) {
						motor.setDirection(d);
						return;
					}
					if(!motor.canMove(d.opposite()))
						return;
					if(myLoc.distanceSquaredTo(enemy.location.add(d))<=ComponentType.SMG.range)
						motor.moveBackward();

				}
				else {
					if(myLoc.distanceSquaredTo(enemy.location)>ComponentType.BLASTER.range) {
						myNav.moveToForward(enemy.location);
						return;
					}
					if(d==Direction.OMNI)
						return;
					if(d!=myRC.getDirection())
						motor.setDirection(d);
					else if(motor.canMove(d)&&!motor.canMove(d.opposite()))
						motor.moveForward();
				}
				return;
			}
			enemy = closest(debris);
			if(enemy!=null&&myLoc.distanceSquaredTo(enemy.location)>2)
				myNav.moveToForward(enemy.location);
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
