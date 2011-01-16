package hardplayer.sensor;

import hardplayer.Static;

import battlecode.common.Mine;

public class MineSensor extends Sensor {

	public void sense() {
		mines = sensor.senseNearbyGameObjects(Mine.class);
	}

}
