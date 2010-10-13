package basicplayer.goals;

import basicplayer.BasePlayer;

import static basicplayer.goals.WoutDistributeFluxGoal.min_flux;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;

import static battlecode.common.GameConstants.ENERGON_RESERVE_SIZE;

public class WoutSuicideGoal extends Goal {

	static public final int suicide_thresh = 20;
	
	int timesOverLimit;

	public WoutSuicideGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return WOUT_SUICIDE;
	}

	public int getPriority() {
		int numArchons=(player.alliedArchons.size==0)?1:player.alliedArchons.size;
		if(player.alliedWouts.size>=8*numArchons) {
			timesOverLimit++;
			if(timesOverLimit>=suicide_thresh&&
			   myRC.getFlux()<=WoutDistributeFluxGoal.min_flux)
				return WOUT_SUICIDE;
		}
		else
			timesOverLimit=0;
		return NEVER;
	}

	public void tryToAccomplish() {
		BasePlayer.debug_println("Goodbye!");
		int i;
		MapLocation myLoc=myRC.getLocation(), loc;
		double myEnergon=myRC.getEnergonLevel();
		double transferAmount;
		          double  flux = myRC.getFlux();
		try {
			for(i=player.alliedArchons.size-1;i>=0;i--) {
				if(myEnergon<=0.) myRC.suicide();
				loc = player.alliedArchonInfos[i].location;
				if(myLoc.distanceSquaredTo(loc)<=2) {
					transferAmount=Math.min(ENERGON_RESERVE_SIZE-player.alliedArchonInfos[i].energonReserve,myEnergon);
					myRC.transferUnitEnergon(transferAmount,loc,RobotLevel.IN_AIR);
					myEnergon-=transferAmount;
					if(flux>0) {
						myRC.transferFlux(flux,loc,RobotLevel.IN_AIR);
						flux=0;
					}
				}
			}
			for(i=7;i>=0;i--) {
				if(myEnergon<=0.) myRC.suicide();
				loc = myLoc.add(BasePlayer.directions[i]);
				Robot r = myRC.senseGroundRobotAtLocation(loc);
				RobotInfo info;
				if(r!=null&&(info=myRC.senseRobotInfo(r)).team==player.myTeam) {
					transferAmount=Math.min(ENERGON_RESERVE_SIZE-info.energonReserve,myEnergon);
					myRC.transferUnitEnergon(transferAmount,loc,RobotLevel.ON_GROUND);
					myEnergon-=transferAmount;
					if(flux>0) {
						myRC.transferFlux(flux,loc,RobotLevel.ON_GROUND);
						flux=0;
					}
				}
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
		myRC.suicide();
	}

}