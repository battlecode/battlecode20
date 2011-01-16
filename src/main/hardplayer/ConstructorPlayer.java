package hardplayer;

import hardplayer.goal.BuildMineGoal;
import hardplayer.goal.Goal;
import hardplayer.sensor.MineSensor;
import hardplayer.component.Constructor;

import battlecode.common.BuilderController;
import battlecode.common.ComponentController;
import battlecode.common.RobotController;

public class ConstructorPlayer extends BasePlayer {

	public ConstructorPlayer(RobotController rc) {
		super(rc);
		goals = new Goal [] { new BuildMineGoal() };
		sensorAI = new MineSensor();
	}

	public void pollComponent(ComponentController c) {
		if(c instanceof BuilderController) {
			builder = (BuilderController)c;
			ais.add(new Constructor());
		}
		else {
			super.pollComponent(c);
		}
	}

}
