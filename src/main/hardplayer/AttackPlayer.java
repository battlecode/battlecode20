package hardplayer;

import hardplayer.goal.Goal;
import hardplayer.goal.SightAttackGoal;
import hardplayer.goal.WanderGoal;
import hardplayer.sensor.WanderSensor;
import hardplayer.component.BlasterAndSMG;
import hardplayer.component.Constructor;

import java.util.Arrays;

import battlecode.common.BuilderController;
import battlecode.common.ComponentController;
import battlecode.common.RobotController;
import battlecode.common.WeaponController;

public class AttackPlayer extends BasePlayer {

	public AttackPlayer(RobotController rc) {
		super(rc);
		goals = new Goal [] { new SightAttackGoal(), new WanderGoal() };
		ais.add(new BlasterAndSMG());
		sensorAI = new WanderSensor();
	}

	public void pollComponent(ComponentController c) {
		if(c instanceof WeaponController) {
			weapons = Arrays.copyOf(weapons,weapons.length+1);
			weapons[weapons.length-1] = (WeaponController)c;
		}
		else {
			super.pollComponent(c);
		}
	}

}
