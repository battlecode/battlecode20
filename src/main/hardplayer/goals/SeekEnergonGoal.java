package hardplayer.goals;

import hardplayer.BasePlayer;
import battlecode.common.MapLocation;

public class SeekEnergonGoal extends Goal
{
	MapLocation archonPosition;

	double moveDelayTimesUpkeep;
	
	public int getMaxPriority() { return SEEK_ENERGON_PRIORITY; }

	public SeekEnergonGoal(BasePlayer bp)
	{
		super(bp);
		moveDelayTimesUpkeep=player.myType.moveDelayOrthogonal()*player.myType.energonUpkeep();
	}

	public int getPriority()
	{

		// Note that this goal can have a high priority even if the unit is
		// right next to the archon.  This prevents the unit from moving.
		// Units will still attack because the attack goal has ALMOST_CRITICAL
		// priority when next to an archon.

		int priority;
		archonPosition=player.nearestAlliedArchon();
		int d=player.myLoc.distanceSquaredTo(archonPosition);
		double level = myRC.getEventualEnergonLevel()-Math.sqrt((double)d)*moveDelayTimesUpkeep;
		priority=(int)(SEEK_ENERGON_PRIORITY*(1.-level/(myRC.getMaxEnergonLevel())));
		if(priority>=SEEK_ENERGON_BY_ARCHON&&d<=2)
			return SEEK_ENERGON_BY_ARCHON;
		else if(priority>=SEEK_ENERGON_PRIORITY)
		    return SEEK_ENERGON_PRIORITY;
		else if(priority<NEVER)
		    return NEVER;
		else
			return priority;
	}

	public void tryToAccomplish() {
		if(myRC.getLocation().distanceSquaredTo(archonPosition)>2) {
			player.myNav.moveToBackward(archonPosition);
		}
	}
}
