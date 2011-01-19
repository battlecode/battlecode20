package hardplayer.sensor;

import hardplayer.Static;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Mine;
import battlecode.common.TerrainTile;

public class MineSensor extends UnitSensor {

	public static int top, bot, left, right;

	public MineSensor() {
		myLoc = myRC.getLocation();
		top = myLoc.y + GameConstants.MAP_MAX_HEIGHT;
		bot = myLoc.y - GameConstants.MAP_MAX_HEIGHT;
		left = myLoc.x - GameConstants.MAP_MAX_WIDTH;
		right = myLoc.x + GameConstants.MAP_MAX_WIDTH;
	}

	public void sense() {
		super.sense();
		mines = sensor.senseNearbyGameObjects(Mine.class);
		Direction d = myRC.getDirection();
		int xx, yy;
		if(d.dx>0) {
			xx = myLoc.x+3;
			if(xx<right&&myRC.senseTerrainTile(new MapLocation(xx,myLoc.y))==TerrainTile.OFF_MAP)
				right = xx;
		}
		else if(d.dx<0) {
			xx = myLoc.x-3;
			if(xx>left&&myRC.senseTerrainTile(new MapLocation(xx,myLoc.y))==TerrainTile.OFF_MAP)
				left = xx;
		}
		if(d.dy>0) {
			yy = myLoc.y+3;
			if(yy<top&&myRC.senseTerrainTile(new MapLocation(myLoc.x,yy))==TerrainTile.OFF_MAP)
				top = yy;
		}
		else if(d.dx<0) {
			yy = myLoc.y-3;
			if(yy>bot&&myRC.senseTerrainTile(new MapLocation(myLoc.x,yy))==TerrainTile.OFF_MAP)
				bot = yy;
		}
	}

}
