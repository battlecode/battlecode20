package hardplayer;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import hardplayer.goal.*;

public class AggroArchonPlayer extends ArchonPlayer {

	public AggroArchonPlayer(RobotController rc) {
		super(rc);
	}

	public void setGoals() {

		goals = new Goal [] {
			new FleeGoal(),
			new MakeArmyGoal(),
			new AggroArchonFindEnemyGoal(),
			new AggroArchonExploreGoal()
		};

	}

	public boolean repurpose() {
		//debug_setIndicatorStringFormat(2,"%s %s",myLoc,myRC.senseAlliedArchons()[0]);
		return myLoc.equals(myRC.senseAlliedArchons()[0]);
	}

	public void broadcast() {
		if(enemies.size>=0) {
			RobotInfo enemyInfo = closestNoTower(enemies,base);
			if(enemyInfo!=null)
				mySender.sendFindEnemy(enemyInfo.location,enemies.size+1);
		}
		else
			AggroArchonExploreGoal.broadcast();
	}

}
