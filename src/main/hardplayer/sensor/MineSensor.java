package hardplayer.sensor;

import hardplayer.Static;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Mine;
import battlecode.common.TerrainTile;

public class MineSensor extends WanderSensor {

	public void sense() {
		super.sense();
		mines = sensor.senseNearbyGameObjects(Mine.class);
	}

}
