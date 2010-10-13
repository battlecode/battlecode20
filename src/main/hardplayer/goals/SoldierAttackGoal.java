package hardplayer.goals;

import static hardplayer.goals.BroadcastEnemyUnitLocationsGoal.*;
import static hardplayer.message.SuperMessageStack.KEEP_TIME;
import hardplayer.message.SuperMessageStack;
import hardplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class SoldierAttackGoal extends Goal {
	MapLocation target;
	int targetInt;

	SuperMessageStack messages;

	static public final int ONEHIT = (int)battlecode.common.RobotType.CHAINER.attackPower();
	static public final int ONEHIT_HEALTH = ONEHIT<<HEALTH_OFFSET;

	public SoldierAttackGoal(BasePlayer bp) {
		super(bp);
		messages = bp.enemyUnitMessages;
	}

	public int getMaxPriority() {
		//if(player.attacking) return ATTACK_TURN;
		//else return ATTACK_SHOOT;
		return ATTACK_SHOOT;
	}

	public void shootOnly() {
		int i, j, k;
		int theInt;
		int unitHealth;
		int best=NEVER_MASK;
		MapLocation loc;
		// it takes fewer bytecodes to access the stack
		// than to access member variables
		MapLocation tmpTarget=null;
		RobotController myRC=this.myRC;
		RobotInfo [] robots;
		RobotInfo info;
		findtarget:
		{
			robots=player.enemyArchonInfos;
			for(i=player.enemyArchons.size-1;i>=0;i--) {
				info=robots[i];
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel+3;
				if(unitHealth<0) continue;
				theInt=ARCHON_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			if(best<=(1<<TYPE_PRIORITY_OFFSET))
				break findtarget;
			robots=player.enemyTurretInfos;
			for(i=player.enemyTurrets.size-1;i>=0;i--) {
				info=robots[i];
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=TURRET_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemyAuraInfos;
			for(i=player.enemyAuras.size-1;i>=0;i--) {
				info=robots[i];
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=AURA_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemySoldierInfos;
			for(i=player.enemySoldiers.size-1;i>=0;i--) {
				info=robots[i];
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=SOLDIER_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemyChainerInfos;
			for(i=player.enemyChainers.size-1;i>=0;i--) {
				info=robots[i];
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=CHAINER_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			if(best<=(2<<TYPE_PRIORITY_OFFSET))
				break findtarget;
			robots=player.enemyWoutInfos;
			for(i=player.enemyWouts.size-1;i>=0;i--) {
				info=robots[i];
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=WOUT_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemyCommInfos;
			for(i=player.enemyComms.size-1;i>=0;i--) {
				info=robots[i];
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=COMM_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemyTeleporterInfos;
			for(i=player.enemyTeleporters.size-1;i>=0;i--) {
				info=robots[i];
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=TELEPORTER_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
		}
		if(best<NEVER_MASK) {
			try {
				if((best&AIRBORNE_MASK)==0) {
					myRC.attackAir(tmpTarget);
				}
				else {
					myRC.attackGround(tmpTarget);
				}
			} catch(Exception e) {
				BasePlayer.debug_stackTrace(e);
			}
		}
	}

	public int getPriority() {
		MapLocation myLoc=player.myLoc;
		int i, j, k;
		int theInt;
		int unitHealth;
		int best=NEVER_MASK;
		int d;
		MapLocation loc;
		MapLocation tmpTarget=null;
		RobotInfo [] robots;
		RobotInfo info;
		findtarget:
		{
			robots=player.enemyArchonInfos;
			for(i=player.enemyArchons.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=ARCHON_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(myLoc.distanceSquaredTo(info.location)>2)
					theInt|=NOMOVE_MASK;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=NOTURN_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			//if(best<=SAWIT_MASK)
			//	break findtarget;
			Message [] stack;
			SuperMessageStack messages=this.messages;
			Message m;
			int [] ints;
			int t=SuperMessageStack.t;
			int sawitBits;
			MapLocation [] locs;
			for(k=SuperMessageStack.KEEP_TIME-1;k>=0;k--) {
				stack=messages.messages[k];
				sawitBits=((t-k+KEEP_TIME)%KEEP_TIME+1)<<SAWIT_OFFSET;
				if(best<=sawitBits) continue;
				for(j=messages.lengths[k]-1;j>=0;j--) {
					ints=stack[j].ints;
					locs=stack[j].locations;
					for(i=locs.length-2;i>0;i--) {
						if((d=myLoc.distanceSquaredTo(locs[i]))>2) continue;
						theInt=ints[i]|sawitBits;
						if((theInt&HEALTH_MASK)<ONEHIT_HEALTH)
							theInt^=ONEHIT_AND_HEALTH_MASK;
						if(d>2)
							theInt|=NOMOVE_MASK;
						else if(!myRC.canAttackSquare(locs[i]))
							theInt|=NOTURN_MASK;
						if(theInt<best) {
							best=theInt;
							tmpTarget=locs[i];
						}
					}
				}
			}
			if(best<=(1<<TYPE_PRIORITY_OFFSET))
				break findtarget;
			robots=player.enemyTurretInfos;
			for(i=player.enemyTurrets.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=TURRET_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(myLoc.distanceSquaredTo(info.location)>2)
					theInt|=NOMOVE_MASK;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=NOTURN_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemyAuraInfos;
			for(i=player.enemyAuras.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=AURA_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(myLoc.distanceSquaredTo(info.location)>2)
					theInt|=NOMOVE_MASK;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=NOTURN_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemySoldierInfos;
			for(i=player.enemySoldiers.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=SOLDIER_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(myLoc.distanceSquaredTo(info.location)>2)
					theInt|=NOMOVE_MASK;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=NOTURN_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemyChainerInfos;
			for(i=player.enemyChainers.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=CHAINER_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(myLoc.distanceSquaredTo(info.location)>2)
					theInt|=NOMOVE_MASK;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=NOTURN_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			if(best<=(2<<TYPE_PRIORITY_OFFSET))
				break findtarget;
			robots=player.enemyWoutInfos;
			for(i=player.enemyWouts.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=WOUT_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(myLoc.distanceSquaredTo(info.location)>2)
					theInt|=NOMOVE_MASK;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=NOTURN_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemyCommInfos;
			for(i=player.enemyComms.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=COMM_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(myLoc.distanceSquaredTo(info.location)>2)
					theInt|=NOMOVE_MASK;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=NOTURN_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemyTeleporterInfos;
			for(i=player.enemyTeleporters.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=TELEPORTER_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				if(myLoc.distanceSquaredTo(info.location)>2)
					theInt|=NOMOVE_MASK;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=NOTURN_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
		}
		if(best<NEVER_MASK) {
			target=tmpTarget;
			targetInt=best;
			if((!player.attacking)&&myRC.canAttackSquare(tmpTarget)) {
				return ATTACK_SHOOT;
			}
			if((targetInt&TYPE_MASK)==ARCHON_TYPE&&player.enemySoldiers.size+player.enemyChainers.size+player.enemyTurrets.size==0)
				return ATTACK_SHOOT;
			else if(myLoc.distanceSquaredTo(tmpTarget)<=2)
				return ATTACK_TURN;
			else
				return ATTACK_MOVE;
		}
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		try {
			if((!player.attacking)&&myRC.canAttackSquare(target)) {
				if((targetInt&AIRBORNE_MASK)==0)
					myRC.attackAir(target);
				else
					myRC.attackGround(target);
			}
			else {
				int d;
				if((d=player.myLoc.distanceSquaredTo(target))>2) {
						player.myNav.moveToForward(target);
				}
				else if(d>0)
					myRC.setDirection(player.myLoc.directionTo(target));
				}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}
}