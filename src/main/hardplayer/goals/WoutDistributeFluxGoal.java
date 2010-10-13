package hardplayer.goals;

import hardplayer.BasePlayer;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;

import static battlecode.common.GameConstants.TELEPORT_FLUX_COST;

public class WoutDistributeFluxGoal extends Goal {

	static public final int flux_per_aura=100;
	static public final int min_flux=200;

	MapLocation targetLoc;

	public WoutDistributeFluxGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return DISTRIBUTE_FLUX_WOUT;
	}

	public int getPriority() {
		double  myFlux = myRC.getFlux();
		if(myFlux<min_flux) return NEVER;
		try {
			int i;
			MapLocation myLoc = player.myLoc;
			RobotInfo [] infos = player.alliedAuraInfos;
			RobotInfo info;
			int d, min=99999;
			double  fluxToTransfer;
			MapLocation targetLoc=null;
			// distributing flux to buildings is handled by TransportEnergonGoal
			infos = player.alliedArchonInfos;
			//Robot [] robots = player.alliedArchonRobots;
			for(i=player.alliedArchons.size-1;i>=0;i--) {
				if(infos[i].id<min) {
					min = infos[i].id;
					targetLoc = infos[i].location;
				}
			}
			if(targetLoc!=null) {
				this.targetLoc = targetLoc;
				return DISTRIBUTE_FLUX_WOUT;
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