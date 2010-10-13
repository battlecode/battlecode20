package teleportingplayer.goals;

import teleportingplayer.BasePlayer;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;

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
						fluxToTransfer=Math.min(battlecode.common.GameConstants.ENERGON_TO_FLUX_CONVERSION*(10.-info.energonReserve),myFlux);
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
			RobotInfo targetInfo = null;
			for(i=player.alliedArchons.size-1;i>=0;i--) {
				if(robots[i].getID()<min) {
					min = robots[i].getID();
					targetInfo = infos[i];
				}
			}
			if(targetInfo!=null) {
				double amountToTransfer = Math.min(myFlux,RobotType.ARCHON.maxFlux()-targetInfo.flux);
				if(amountToTransfer < min_flux)
					return NEVER;
				if(myLoc.distanceSquaredTo(targetInfo.location)<=2) {
					myRC.transferFlux(myFlux,targetInfo.location,RobotLevel.IN_AIR);	
				}
				else {
					this.targetLoc = targetInfo.location;
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