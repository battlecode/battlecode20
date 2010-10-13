package basicplayer.goals;

import basicplayer.BasePlayer;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;

import static battlecode.common.GameConstants.TELEPORT_FLUX_COST;

public class WoutDistributeFluxGoal extends Goal {

	static public final int flux_per_teleporter=TELEPORT_FLUX_COST*2;
	static public final int min_flux=200;

	MapLocation targetLoc;

	public WoutDistributeFluxGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return DISTRIBUTE_FLUX;
	}

	public int getPriority() {
		          double  myFlux = myRC.getFlux();
		if(myFlux<min_flux)
			return NEVER;
		// transferrning flux isn't an action,
		// so we can transfer even if we
		// decide to try to accomplish a
		// different goal
		try {
			int i;
			MapLocation myLoc = player.myLoc;
			RobotInfo [] infos = player.alliedTeleporterInfos;
			RobotInfo info;
			int d, min=99999;
			double fluxToTransfer;
			MapLocation targetLoc=null;
			for(i=player.alliedTeleporters.size-1;i>=0;i--) {
				info = infos[i];
				if(info.flux<flux_per_teleporter) {
					if((d=info.location.distanceSquaredTo(myLoc))<=2) {
						fluxToTransfer=Math.min(flux_per_teleporter-info.flux,myFlux);
						myRC.transferFlux(fluxToTransfer,info.location,RobotLevel.ON_GROUND);
						myFlux-=fluxToTransfer;
						if(myFlux==0) return NEVER;
					}
					else if(d<min) {
						min=d;
						targetLoc=info.location;
					}
				}
			}
			if(targetLoc!=null) {
				this.targetLoc = targetLoc;
				return DISTRIBUTE_FLUX;
			}
			else if(myFlux<min_flux) return NEVER;
			infos = player.alliedArchonInfos;
			Robot [] robots = player.alliedArchonRobots;
			for(i=player.alliedArchons.size-1;i>=0;i--) {
				if(robots[i].getID()<min) {
					min = robots[i].getID();
					targetLoc = infos[i].location;
				}
			}
			if(targetLoc!=null) {
				if(myLoc.distanceSquaredTo(targetLoc)<=2) {
					myRC.transferFlux(myFlux,targetLoc,RobotLevel.IN_AIR);	
				}
				else {
					this.targetLoc = targetLoc;
					return DISTRIBUTE_FLUX;
				}
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
		return NEVER;
	}

	public void tryToAccomplish() {
		player.myNav.moveToASAP(targetLoc);
	}

}