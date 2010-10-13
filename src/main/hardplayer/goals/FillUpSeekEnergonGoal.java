package hardplayer.goals;

import hardplayer.BasePlayer;
import battlecode.common.MapLocation;

public class FillUpSeekEnergonGoal extends SeekEnergonGoal
{

	int highestPriority;

	public FillUpSeekEnergonGoal(BasePlayer bp)
	{
		super(bp);
	}

	public int getPriority()
	{
		if(myRC.getEventualEnergonLevel()>=myRC.getMaxEnergonLevel()) {
			highestPriority=NEVER;
		}
		int priority=super.getPriority();
		if(priority>highestPriority)
			highestPriority=priority;
		return highestPriority;
	}

}
