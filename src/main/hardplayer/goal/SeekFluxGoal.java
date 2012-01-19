package hardplayer.goal;

import hardplayer.Static;

import battlecode.common.MapLocation;

public class SeekFluxGoal extends Static implements Goal {

	static MapLocation target;

	public int maxPriority() { return SEEK_FLUX_HIGH; }

	public int priority() {
		target = closest(myRC.senseAlliedArchons());
		if(target==null)
			return 0;
		int dx = myLoc.x - target.x;
		if(dx<0) dx=-dx;
		int dy = myLoc.y - target.y;
		int d;
		if(dy<0) dy=-dy;
		if(dx<dy)
			d = dx;
		else
			d = dy;
		double spareMoves = myRC.getFlux()/myType.moveCost - d;
		if(spareMoves<0.)
			return SEEK_FLUX_HIGH;
		else if(spareMoves<6.)
			return SEEK_FLUX_LOW+(int)((SEEK_FLUX_HIGH-SEEK_FLUX_LOW)*(1.-spareMoves/6.));
		else
			return SEEK_FLUX_LOW;
	}

	public void execute() {
		if(target!=null&&myLoc.distanceSquaredTo(target)>2)
			myNav.moveToBackward(target);
	}

}
