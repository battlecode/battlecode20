package hardplayer;

import hardplayer.component.Blaster;
import hardplayer.component.Recycler;
import hardplayer.goal.BuildingSpinGoal;
import hardplayer.goal.Goal;
import hardplayer.sensor.UnitSensor;

import battlecode.common.BuilderController;
import battlecode.common.ComponentController;
import battlecode.common.GameActionException;
import battlecode.common.Mine;
import battlecode.common.RobotController;
import battlecode.common.RobotLevel;
import battlecode.common.SensorController;
import battlecode.common.WeaponController;

public class MinePlayer extends BasePlayer {

	public MinePlayer(RobotController rc) {
		super(rc);
		goals = new Goal [] { };
	}

	public void pollComponent(ComponentController c) {
		if(c instanceof SensorController) {
			sensor = (SensorController)c;
			sensorAI = new UnitSensor();
		}
		if(c instanceof BuilderController) {
			builder = (BuilderController)c;
			ais.add(new Recycler());
		}
		else if(c instanceof WeaponController) {
			goals = new Goal [] { new BuildingSpinGoal() };
			ais.add(new Blaster((WeaponController)c));
		}
		else {
			super.pollComponent(c);
		}
	}

	public void runloop() throws GameActionException {
		if(sensor.senseMineInfo((Mine)sensor.senseObjectAtLocation(myRC.getLocation(),RobotLevel.MINE)).roundsLeft<=-350)
			myRC.turnOff();
		super.runloop();
	}

}
