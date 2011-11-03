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
