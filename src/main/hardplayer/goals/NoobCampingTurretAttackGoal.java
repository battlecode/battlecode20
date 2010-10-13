package hardplayer.goals;

import static hardplayer.goals.BroadcastEnemyUnitLocationsGoal.*;
import static hardplayer.message.SuperMessageStack.KEEP_TIME;
import hardplayer.message.SuperMessageStack;
import hardplayer.BasePlayer;
import hardplayer.util.FastList;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class NoobCampingTurretAttackGoal extends TurretAttackGoal {

	static public final int TURRET_MAX_RADIUS = RobotType.TURRET.attackRadiusMaxSquared();

	public NoobCampingTurretAttackGoal(BasePlayer bp) {
		super(bp);
	}

	public boolean attackItGround(FastList robots) {
		int i;
		MapLocation loc;
		for(i=robots.size-1;i>=0;i--) {
			loc=robots.robotInfos[i].location;
			if(myRC.canAttackSquare(loc)) {
				try {
					myRC.attackGround(loc);
					return true;
				} catch(Exception e) {
					BasePlayer.debug_stackTrace(e);
				}
			}
		}
		return false;
	}

	public boolean attackItAir(FastList robots) {
		int i;
		MapLocation loc;
		for(i=robots.size-1;i>=0;i--) {
			loc=robots.robotInfos[i].location;
			if(myRC.canAttackSquare(loc)) {
				try {
					myRC.attackAir(loc);
					return true;
				} catch(Exception e) {
					BasePlayer.debug_stackTrace(e);
				}
			}
		}
		return false;
	}

	public MapLocation turn(FastList robots) {
		int i;
		MapLocation loc;
		for(i=robots.size-1;i>=0;i--) {
			loc=robots.robotInfos[i].location;
			int d=player.myLoc.distanceSquaredTo(loc);
			if(d<=TURRET_MAX_RADIUS&&d>2) {
				return loc;
			}
		}
		return null;
	}

	public void shootOnly() {
		if(attackItAir(player.enemyArchons)) return;
		if(attackItGround(player.enemyChainers)) return;
		if(attackItGround(player.enemySoldiers)) return;
		if(attackItGround(player.enemyTurrets)) return;
		if(attackItGround(player.enemyAuras)) return;
		if(attackItGround(player.enemyComms)) return;
		if(attackItGround(player.enemyWouts)) return;
		if(attackItGround(player.enemyTeleporters)) return;
		Message m;
		int i,j,k;
		int [] ints;
		MapLocation [] locs;
		Message [] stack;
		SuperMessageStack messages=this.messages;
		int stop=SuperMessageStack.t;
		int sawItBits;
		int index;
		try {
			for(k=SuperMessageStack.KEEP_TIME;k>0;k--) {
				index=(SuperMessageStack.t+k)%SuperMessageStack.KEEP_TIME;
				stack=messages.messages[index];
				for(j=messages.lengths[index]-1;j>=0;j--) {
					locs=stack[j].locations;
					ints=stack[j].ints;
					for(i=locs.length-1;i>0;i--) {
						if(myRC.canAttackSquare(locs[i])) {
							if((ints[i]&AIRBORNE_MASK)==0)
								myRC.attackAir(locs[i]);
							else
								myRC.attackGround(locs[i]);
							return;
						}
					}
				}
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

	public int getPriority() {
		if(!myRC.isAttackActive()) {
			shootOnly();
			if(myRC.hasActionSet()) return ATTACK_SHOOT;
		}
		target=turn(player.enemyArchons);
		if(target!=null) return ATTACK_MOVE;
		target=turn(player.enemyChainers);
		if(target!=null) return ATTACK_MOVE;
		target=turn(player.enemySoldiers);
		if(target!=null) return ATTACK_MOVE;
		target=turn(player.enemyTurrets);
		if(target!=null) return ATTACK_MOVE;
		target=turn(player.enemyAuras);
		if(target!=null) return ATTACK_MOVE;
		target=turn(player.enemyComms);
		if(target!=null) return ATTACK_MOVE;
		target=turn(player.enemyWouts);
		if(target!=null) return ATTACK_MOVE;
		target=turn(player.enemyTeleporters);
		if(target!=null) return ATTACK_MOVE;
		Message m;
		int i,j,k;
		int [] ints;
		MapLocation [] locs;
		Message [] stack;
		SuperMessageStack messages=this.messages;
		int stop=SuperMessageStack.t;
		int sawItBits;
		int index;
		for(k=SuperMessageStack.KEEP_TIME;k>0;k--) {
			sawItBits=(SuperMessageStack.KEEP_TIME-k+1)<<SAWIT_OFFSET;
			index=(SuperMessageStack.t+k)%SuperMessageStack.KEEP_TIME;
			stack=messages.messages[index];
			for(j=messages.lengths[index]-1;j>=0;j--) {
				ints=stack[j].ints;
				locs=stack[j].locations;
				for(i=locs.length-1;i>0;i--) {
					int d=player.myLoc.distanceSquaredTo(locs[i]);
					if(d<=TURRET_MAX_RADIUS&&d>2) {
						target=locs[i];
						return ATTACK_MOVE;
					}
				}
			}
		}
		return NEVER;
	}

	public void tryToAccomplish() {
		if(myRC.hasActionSet()) return;
		try {
			Direction d=player.myLoc.directionTo(target);
			myRC.setDirection(d);
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}
}