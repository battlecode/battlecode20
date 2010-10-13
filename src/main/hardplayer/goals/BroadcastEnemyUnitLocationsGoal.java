package hardplayer.goals;

import hardplayer.BasePlayer;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public abstract class BroadcastEnemyUnitLocationsGoal extends Goal {

	//static final int expectedBroadcastsPerTurn=2;

	// Only cannons use this list for shooting; others just need to avoid
	// cannons and know approximately where the enemy is.  So don't
	// broadcast units unnecessarily if we don't have any cannons nearby.
	// We assume units with 360 degree sight radius will see cannons if
	// there are any, but others don't know.
	boolean alwaysBroadcastAll;

	/**
	 * Bit order - least significant to most significant
	 * robot ID mod 1024
	 * type of robot (for identification purposes)
	 * health
	 * can shoot without turning
	 * one-hit kill
	 * type of robot (for shooting priority purposes)
	 * don't need to move to shoot it
	 * just spawned
	 * saw it more recently
	 * never want to shoot it (out of range or dead)
	 */
	
	public static final int ROBOT_ID_BITS = 10;
	public static final int TYPE_ID_BITS = 2;
	public static final int HEALTH_BITS = 7;
	public static final int SAWIT_BITS = 2;
	public static final int TYPE_PRIORITY_BITS = 2;
	public static final int TYPE_ID_OFFSET = ROBOT_ID_BITS;
	public static final int HEALTH_OFFSET = TYPE_ID_OFFSET + TYPE_ID_BITS;
	public static final int SPLASH_OFFSET = HEALTH_OFFSET + HEALTH_BITS;
	public static final int NOTURN_OFFSET = SPLASH_OFFSET + 1;
	public static final int ONEHIT_OFFSET = NOTURN_OFFSET + 1;
	public static final int TYPE_PRIORITY_OFFSET = ONEHIT_OFFSET + 1;
	public static final int NOMOVE_OFFSET = TYPE_PRIORITY_OFFSET + TYPE_PRIORITY_BITS; 
	public static final int JUSTSPAWNED_OFFSET = NOMOVE_OFFSET + 1;
	public static final int SAWIT_OFFSET = JUSTSPAWNED_OFFSET + 1;
	public static final int NEVER_OFFSET = SAWIT_OFFSET + 1;
	public static final int ROBOT_ID_MASK = (1<<ROBOT_ID_BITS)-1;
	public static final int TYPE_ID_MASK = ((1<<TYPE_ID_BITS)-1)<<TYPE_ID_OFFSET;
	public static final int HEALTH_MASK = ((1<<HEALTH_BITS)-1)<<HEALTH_OFFSET;
	public static final int SPLASH_MASK = 1<<SPLASH_OFFSET;
	public static final int NOTURN_MASK = 1<<NOTURN_OFFSET;
	public static final int ONEHIT_MASK = 1<<ONEHIT_OFFSET;
	public static final int TYPE_PRIORITY_MASK = ((1<<TYPE_PRIORITY_BITS)-1)<<TYPE_PRIORITY_OFFSET;
	public static final int NOMOVE_MASK = 1<<NOMOVE_OFFSET;
	public static final int JUSTSPAWNED_MASK = 1<<JUSTSPAWNED_OFFSET;
	public static final int SAWIT_MASK = ((1<<SAWIT_BITS)-1)<<SAWIT_OFFSET;
	public static final int NEVER_MASK = 1<<NEVER_OFFSET;
	public static final int TYPE_MASK = TYPE_ID_MASK | TYPE_PRIORITY_MASK;
	public static final int ARCHON_TYPE = 0;
	public static final int TURRET_TYPE = (1<<TYPE_ID_OFFSET)|(1<<TYPE_PRIORITY_OFFSET);
	public static final int SOLDIER_TYPE = (3<<TYPE_ID_OFFSET)|(1<<TYPE_PRIORITY_OFFSET);
	public static final int CHAINER_TYPE = SOLDIER_TYPE;
	public static final int WOUT_TYPE = (3<<TYPE_ID_OFFSET)|(2<<TYPE_PRIORITY_OFFSET);
	public static final int COMM_TYPE = WOUT_TYPE;
	public static final int TELEPORTER_TYPE = WOUT_TYPE;
	public static final int AURA_TYPE = (2<<TYPE_ID_OFFSET)|(1<<TYPE_PRIORITY_OFFSET);
	// for bits that should be set by default
	public static final int EXTRA_MASK = ONEHIT_MASK;
	public static final int DEFAULT_TRUE_MASK = ONEHIT_MASK;
	public static final int MESSAGE_DEFAULT_MASK = DEFAULT_TRUE_MASK;
	// archons are the only air units
	public static final int AIRBORNE_MASK = TYPE_MASK;
	public static final int ONEHIT_AND_HEALTH_MASK = ONEHIT_MASK | HEALTH_MASK;
	public static final int UNIT_SETS_MASK = NOTURN_MASK | NOMOVE_MASK | SAWIT_MASK;
	
	public static final int JUSTSPAWNED_THRESH = RobotType.CHAINER.attackDelay();

	FindEnemyGoal findEnemyGoal;

	public BroadcastEnemyUnitLocationsGoal(BasePlayer bp, FindEnemyGoal g){
	    super(bp);
		findEnemyGoal=g;
	}
	
	public int getMaxPriority() { return BROADCAST_ENEMY_UNIT_LOCATIONS; }

	public void tryToAccomplish() {
		MapLocation [] locs;
		int [] ints;
		int unitHealth;
		RobotInfo info;
		// broadcast everything to cannons
		// cannons at front, archons at back for easy reference
		// shoot comms+teleporters if you see them, but don't bother messaging
		int numEnemies=player.enemyArchons.size+
			player.enemySoldiers.size+
			player.enemyTurrets.size+
			player.enemyChainers.size+
			player.enemyWouts.size+
			player.enemyAuras.size;
		locs = new MapLocation [numEnemies+2];
		ints = new int [numEnemies+3];
		//locs[0]=myRC.getLocation();
		int i,j=0;
		RobotInfo [] infos = player.enemyArchonInfos;
		//Robot [] robots = player.enemyArchonRobots;
		for(i=player.enemyArchons.size-1;i>=0;i--) {
			info=infos[i];
			unitHealth=((int)info.energonLevel)+2;
			if(unitHealth<0)
				ints[++j]=(info.id&ROBOT_ID_MASK)|NEVER_MASK|ARCHON_TYPE|MESSAGE_DEFAULT_MASK;
			else
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|ARCHON_TYPE|MESSAGE_DEFAULT_MASK;
			locs[j]=info.location;
		}
		infos=player.enemyWoutInfos;
		//robots=player.enemyWoutRobots;
		for(i=player.enemyWouts.size-1;i>=0;i--) {
			info=infos[i];
			unitHealth=(int)info.energonLevel;
			if(unitHealth<0)
				ints[++j]=(info.id&ROBOT_ID_MASK)|NEVER_MASK|WOUT_TYPE|MESSAGE_DEFAULT_MASK;
			else if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|WOUT_TYPE|MESSAGE_DEFAULT_MASK|JUSTSPAWNED_MASK;
			else
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|WOUT_TYPE|MESSAGE_DEFAULT_MASK;
			locs[j]=info.location;
		}
		infos=player.enemySoldierInfos;
		//robots=player.enemySoldierRobots;
		for(i=player.enemySoldiers.size-1;i>=0;i--) {
			info=infos[i];
			unitHealth=(int)info.energonLevel;
			if(unitHealth<0)
				ints[++j]=(info.id&ROBOT_ID_MASK)|NEVER_MASK|SOLDIER_TYPE|MESSAGE_DEFAULT_MASK;
			else if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|SOLDIER_TYPE|MESSAGE_DEFAULT_MASK|JUSTSPAWNED_MASK;
			else
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|SOLDIER_TYPE|MESSAGE_DEFAULT_MASK;
			locs[j]=info.location;
		}
		infos=player.enemyChainerInfos;
		//robots=player.enemyChainerRobots;
		for(i=player.enemyChainers.size-1;i>=0;i--) {
			info=infos[i];
			unitHealth=(int)info.energonLevel;
			if(unitHealth<0)
				ints[++j]=(info.id&ROBOT_ID_MASK)|NEVER_MASK|CHAINER_TYPE|MESSAGE_DEFAULT_MASK;
			else if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|CHAINER_TYPE|MESSAGE_DEFAULT_MASK|JUSTSPAWNED_MASK;
			else
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|CHAINER_TYPE|MESSAGE_DEFAULT_MASK;
			locs[j]=info.location;
		}
		infos=player.enemyAuraInfos;
		//robots=player.enemyAuraRobots;
		for(i=player.enemyAuras.size-1;i>=0;i--) {
			info=infos[i];
			unitHealth=(int)info.energonLevel;
			if(unitHealth<0)
				ints[++j]=(info.id&ROBOT_ID_MASK)|NEVER_MASK|AURA_TYPE|MESSAGE_DEFAULT_MASK;
			else
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|AURA_TYPE|MESSAGE_DEFAULT_MASK;
			locs[j]=info.location;
		}
		infos=player.enemyTurretInfos;
		//robots=player.enemyTurretRobots;
		for(i=player.enemyTurrets.size-1;i>=0;i--) {
			info=infos[i];
			unitHealth=(int)info.energonLevel;
			if(unitHealth<0)
				ints[++j]=(info.id&ROBOT_ID_MASK)|NEVER_MASK|TURRET_TYPE|MESSAGE_DEFAULT_MASK;
			else if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|TURRET_TYPE|MESSAGE_DEFAULT_MASK|JUSTSPAWNED_MASK;
			else
				ints[++j]=(info.id&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|TURRET_TYPE|MESSAGE_DEFAULT_MASK;			
			locs[j]=info.location;
		}
		locs[locs.length-1]=findEnemyGoal.getEnemyLoc();
		player.mySender.sendEnemyUnits(ints,locs);
	}	

}
