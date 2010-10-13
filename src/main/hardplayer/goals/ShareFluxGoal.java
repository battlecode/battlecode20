package hardplayer.goals;

import hardplayer.ArchonPlayer;
import hardplayer.BasePlayer;
import hardplayer.WoutPlayer;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

import static hardplayer.WoutPlayer.*;

public class ShareFluxGoal extends Goal {

    MapLocation target;
    //static final double peacetimeEnergonFactor = 1.8;
    //static final double wartimeEnergonFactor = 1.2;
    
    //static final double peacetimeArchonLowEnergon = ArchonPlayer.MIN_IN_PEACETIME - 3.;
    //static final double wartimeArchonLowEnergon = ArchonPlayer.MIN_WHEN_SAW_ENEMY - 3.;
	static final double ARCHON_LOW_ENERGON = ArchonPlayer.MIN_ENERGON - 3.;
	//static final int wartimeUnseenDuration = 50;

	static final double MIN_FLUX = 200.;
    
    public ShareFluxGoal(BasePlayer bp)
    {
		super(bp);
    }
	
    public int getMaxPriority() { return CAMPING_SHARE_FLUX; }
	
    public int getPriority() {
		if(myRC.getFlux()<500.) return NEVER;
		int i;
		RobotInfo info;
		RobotInfo lowest=null;
		int lowID=player.myID;
		for(i=player.alliedWouts.size-1;i>=0;i--) {
			info=player.alliedWoutInfos[i];
			if(info.id<lowID) {
				lowID=info.id;
				lowest=info;
			}
		}
		if(lowest==null) {
			return NEVER;
		}
		else {
			target=lowest.location;
			return CAMPING_SHARE_FLUX;
		}
    }
	
    public void tryToAccomplish() {
		if(player.myLoc.distanceSquaredTo(target)>2)
			player.myNav.moveToASAP(target);
    }
}