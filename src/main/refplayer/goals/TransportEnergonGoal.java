package refplayer.goals;

import refplayer.ArchonPlayer;
import refplayer.BasePlayer;
import refplayer.WoutPlayer;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

import static refplayer.WoutPlayer.*;

public class TransportEnergonGoal extends Goal {

    MapLocation target;
    //static final double peacetimeEnergonFactor = 1.8;
    //static final double wartimeEnergonFactor = 1.2;
    
    //static final double peacetimeArchonLowEnergon = ArchonPlayer.MIN_IN_PEACETIME - 3.;
    //static final double wartimeArchonLowEnergon = ArchonPlayer.MIN_WHEN_SAW_ENEMY - 3.;
	static final double ARCHON_LOW_ENERGON = ArchonPlayer.MIN_ENERGON - 3.;
	//static final int wartimeUnseenDuration = 50;

	static final double MIN_FLUX = 200.;
    
    public TransportEnergonGoal(BasePlayer bp)
    {
		super(bp);
    }
	
    public int getMaxPriority() { return TRANSPORT_ENERGON; }
	
    public int getPriority() {
		RobotInfo info;
		RobotInfo closest=null;
		MapLocation myLoc=myRC.getLocation();
		double archonLowEnergon;
		double myEnergon=myRC.getEnergonLevel();
		int d, dmin=999999, i;
		for(i=player.alliedArchons.size-1;i>=0;i--) {
			info=player.alliedArchonInfos[i];
			if(info.eventualEnergon<ARCHON_LOW_ENERGON) {
				d=myLoc.distanceSquaredTo(info.location);
				if(d<dmin) {
					dmin=d;
					closest=info;
				}
			}
		}
		if(closest!=null) {
			target=closest.location;
			return TRANSPORT_ENERGON;
		}
		for(i=player.alliedTurrets.size-1;i>=0;i--) {
			info=player.alliedTurretInfos[i];
			if(info.eventualEnergon*TURRET_ENERGON_RATIO<myEnergon) {
				d=myLoc.distanceSquaredTo(info.location);
				if(d<dmin) {
					dmin=d;
					closest=info;
				}
			}
		}
		for(i=player.alliedSoldiers.size-1;i>=0;i--) {
			info=player.alliedSoldierInfos[i];
			if(info.eventualEnergon*SOLDIER_ENERGON_RATIO<myEnergon) {
				d=myLoc.distanceSquaredTo(info.location);
				if(d<dmin) {
					dmin=d;
					closest=info;
				}
			}
		}
		for(i=player.alliedChainers.size-1;i>=0;i--) {
			info=player.alliedChainerInfos[i];
			if(info.eventualEnergon*CHAINER_ENERGON_RATIO<myEnergon) {
				d=myLoc.distanceSquaredTo(info.location);
				if(d<dmin) {
					dmin=d;
					closest=info;
				}
			}
		}
		if(myRC.getFlux()>=MIN_FLUX) {
			for(i=player.alliedAuras.size-1;i>=0;i--) {
				info=player.alliedAuraInfos[i];
				if(info.eventualEnergon<200.) {
					d=myLoc.distanceSquaredTo(info.location);
					if(d<dmin) {
						dmin=d;
						closest=info;
					}
				}
			}
			for(i=player.alliedComms.size-1;i>=0;i--) {
				info=player.alliedCommInfos[i];
				if(info.eventualEnergon<200.) {
					d=myLoc.distanceSquaredTo(info.location);
					if(d<dmin) {
						dmin=d;
						closest=info;
					}
				}
			}
		}
		if(closest==null) {
			return NEVER;
		}
		else {
			target=closest.location;
			return TRANSPORT_ENERGON;
		}
    }
	
    public void tryToAccomplish() {
		if(player.myLoc.distanceSquaredTo(target)>2)
			player.myNav.moveToASAP(target);
    }
}