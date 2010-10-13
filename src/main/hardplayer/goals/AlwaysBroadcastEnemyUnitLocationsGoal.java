/*
package hardplayer.goals;

import hardplayer.BasePlayer;
import battlecode.common.Clock;

public class AlwaysBroadcastEnemyUnitLocationsGoal extends BroadcastEnemyUnitLocationsGoal {
	
	public AlwaysBroadcastEnemyUnitLocationsGoal(BasePlayer bp) {
		super(bp);
	}

	public int getPriority() {
		if(player.enemyArchons.size+
		   player.enemySoldiers.size+
		   player.enemyTurrets.size+
		   player.enemyChainers.size+
		   player.enemyWouts.size>0)
			return BROADCAST_ENEMY_UNIT_LOCATIONS;
		return NEVER;
	}
}
*/