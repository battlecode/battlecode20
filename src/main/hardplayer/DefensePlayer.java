package hardplayer;

import hardplayer.component.BlasterAndSMG;
import hardplayer.goal.DefenseSpinGoal;
import hardplayer.goal.Goal;
import hardplayer.sensor.UnitSensor;

import battlecode.common.ComponentController;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Robot;
import battlecode.common.SensorController;
import battlecode.common.WeaponController;

import java.util.Arrays;

public class DefensePlayer extends BasePlayer {

	public DefensePlayer(RobotController rc) {
		super(rc);
		goals = new Goal [] { new DefenseSpinGoal() };
		ais.add(new BlasterAndSMG());
	}

	public void pollComponent(ComponentController c) {
		if(c instanceof SensorController) {
			sensor = (SensorController)c;
			sensorAI = new UnitSensor();
		}
		else if(c instanceof WeaponController) {
			weapons = Arrays.copyOf(weapons,weapons.length+1);
			weapons[weapons.length-1] = (WeaponController)c;
		}
		else {
			super.pollComponent(c);
		}
	}

	public void runloop() throws GameActionException {
		super.runloop();
		if(enemies.size==0) {
			SensorController tmpSensor = sensor;
			sensor = (SensorController)myRC.components()[1];
			sensorAI.sense();
			int i=allies.size;
			while(--i>=0) {
				if(!alliedInfos[i].on)
					myRC.turnOff();
			}
			sensor = tmpSensor;
		}
	}

}
