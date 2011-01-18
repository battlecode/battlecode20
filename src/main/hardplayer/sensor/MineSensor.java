package hardplayer.sensor;

import hardplayer.Static;

import battlecode.common.Mine;

public class MineSensor extends UnitSensor {

	public void sense() {
		super.sense();
		mines = sensor.senseNearbyGameObjects(Mine.class);
	}

}
