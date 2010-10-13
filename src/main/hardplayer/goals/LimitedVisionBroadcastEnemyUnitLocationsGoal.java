package hardplayer.goals;

import hardplayer.BasePlayer;
import battlecode.common.Clock;

public class LimitedVisionBroadcastEnemyUnitLocationsGoal extends BroadcastEnemyUnitLocationsGoal {
	
	public LimitedVisionBroadcastEnemyUnitLocationsGoal(BasePlayer bp, FindEnemyGoal g) {
		super(bp,g);
	}

	public int getPriority() {
		if((player.myID+Clock.getRoundNum())%6==0) {
			if(player.enemyArchons.size+
			   player.enemySoldiers.size+
			   player.enemyTurrets.size+
			   player.enemyChainers.size+
			   player.enemyWouts.size>0) {
				return BROADCAST_ENEMY_UNIT_LOCATIONS;
			}
		}
		return NEVER;
	}

}