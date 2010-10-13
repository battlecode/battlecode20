package hardplayer.goals;

import hardplayer.BasePlayer;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;

public class ArchonDistributeFluxGoal extends Goal {

	static public final int flux_per_aura=100;
	static public final int min_flux=200;

	MapLocation targetLoc;

	public ArchonDistributeFluxGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return DISTRIBUTE_FLUX_ARCHON;
	}

	public int getPriority() {
		double myFlux = myRC.getFlux();
		// transferrning flux isn't an action,
		// so we can transfer even if we
		// decide to try to accomplish a
		// different goal
		try {
			if(myFlux<min_flux) return NEVER;
			int i;
			MapLocation myLoc = player.myLoc;
			RobotInfo [] infos;
			RobotInfo info;
			double fluxToTransfer;
		    RobotInfo targetInfo=null;
			infos = player.alliedArchonInfos;
			//Robot [] robots = player.alliedArchonRobots;
			int min = player.myID, ID;
			for(i=player.alliedArchons.size-1;i>=0;i--) {
				ID = infos[i].id;
				if(ID<min) {
					min = ID;
					targetInfo = infos[i];
				}
			}
			if(targetInfo!=null) {
				if(targetInfo.flux<=targetInfo.type.maxFlux()-min_flux) {
					targetLoc=targetInfo.location;
					return DISTRIBUTE_FLUX_ARCHON;
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