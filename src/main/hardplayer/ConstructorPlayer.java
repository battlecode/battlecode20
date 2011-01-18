package hardplayer;

import hardplayer.goal.BuildConstructorGoal;
import hardplayer.goal.BuildMineGoal;
import hardplayer.goal.ConstructorAttackGoal;
import hardplayer.goal.Goal;
import hardplayer.sensor.MineSensor;
import hardplayer.component.Blaster;
import hardplayer.component.Constructor;

import battlecode.common.BuilderController;
import battlecode.common.ComponentController;
import battlecode.common.RobotController;
import battlecode.common.WeaponController;

public class ConstructorPlayer extends BasePlayer {

	public ConstructorPlayer(RobotController rc) {
		super(rc);
		goals = new Goal [] { new BuildConstructorGoal(), new BuildMineGoal() };
		sensorAI = new MineSensor();
	}

	public void pollComponent(ComponentController c) {
		if(c instanceof BuilderController) {
			builder = (BuilderController)c;
			ais.add(new Constructor());
		}
		else if(c instanceof WeaponController) {
			ais.add(new Blaster((WeaponController)c));
			goals = new Goal [] { new BuildConstructorGoal(), new ConstructorAttackGoal(), new BuildMineGoal() };
		}
		else {
			super.pollComponent(c);
		}
	}

}
