package hardplayer.goals;

import hardplayer.BasePlayer;
import hardplayer.message.TeleporterMessageHandler;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import java.util.Random;

import static battlecode.common.GameConstants.FLUX_RADIUS_SQUARED;

public class WoutSpreadOutGoal extends Goal {

	TeleporterMessageHandler tmh;
	MapLocation teleporterLoc;
	Random rand;

	public WoutSpreadOutGoal(BasePlayer bp, TeleporterMessageHandler h) {
		super(bp);
		rand = new Random();
		tmh = h;
	}

	public int getMaxPriority() {
		return CAMPING_WOUT_SPREAD_OUT;
	}

	public int getPriority() {
		return CAMPING_WOUT_SPREAD_OUT;
	}

	public void tryToAccomplish() {
		if(myRC.getFlux()>=RobotType.TELEPORTER.spawnFluxCost()&&myRC.canMove(myRC.getDirection())) {
			try {
				MapLocation closest;
				if(tmh.locs!=null) {
					closest = player.nearestOneOf(tmh.locs);
					MapLocation spawnLoc=closest.add(myRC.getDirection());
					int d=player.myLoc.distanceSquaredTo(spawnLoc);
					if(d>25) {
						player.myNav.moveToASAPPreferFwd(closest);
						return;
					}
					else {
						if(myRC.canSenseSquare(closest)&&myRC.senseGroundRobotAtLocation(closest)==null) {
							tmh.locs=null;
						}
						else if(d<16) {
							// move backward because turning around and
							// moving forward could move us a lot
							// farther away
							player.myNav.moveToBackward(BasePlayer.awayFrom(player.myLoc,closest));
							return;
						}
					}
				}
				myRC.spawn(RobotType.TELEPORTER);
				return;
			} catch(Exception e) {
				BasePlayer.debug_stackTrace(e);
			}
		}
		else {
			if(player.alliedArchons.size>0&&teleporterLoc==null&&tmh.locs!=null) {
				teleporterLoc = tmh.locs[rand.nextInt(tmh.locs.length)];
			}
			if((teleporterLoc!=null)&&(player.myLoc.distanceSquaredTo(teleporterLoc)<=16)) {
				teleporterLoc = null;
			}
			if(teleporterLoc!=null)
				player.myNav.moveToASAPPreferFwd(teleporterLoc);
			else
				wander();
		}
	}

	public void wander() {
		try {
			int z = rand.nextInt(10);
			if(z==0) myRC.setDirection(myRC.getDirection().rotateRight());
			else if(z==1) myRC.setDirection(myRC.getDirection().rotateLeft());
			if (myRC.canMove(myRC.getDirection())) {
				myRC.moveForward();
			} else {
				myRC.setDirection(myRC.getDirection().rotateRight());
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

}