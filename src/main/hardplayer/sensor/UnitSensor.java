package hardplayer.sensor;

import hardplayer.Static;
import hardplayer.FastList;

import battlecode.common.*;

public class UnitSensor extends Sensor {

	public void sense() {
		try {
			Robot [] robots = sensor.senseNearbyGameObjects(Robot.class);
			Robot robot;
			FastList fl;
			int i=robots.length;
			allies.size = -1;
			enemies.size = -1;
			debris.size = -1;
			while(--i>=0) {
				robot = robots[i]; 
				fl = allUnits[robot.getTeam().ordinal()];
				fl.robotInfos[++fl.size] = sensor.senseRobotInfo(robots[i]);
			}
			allies.size++;
			enemies.size++;
			debris.size++;
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}
}
