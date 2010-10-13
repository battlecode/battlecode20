package hardplayer.goals;

import static hardplayer.goals.BroadcastEnemyUnitLocationsGoal.*;
import static hardplayer.message.SuperMessageStack.KEEP_TIME;
import hardplayer.message.SuperMessageStack;
import hardplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class CampingTurretAttackGoal extends TurretAttackGoal {

	public CampingTurretAttackGoal(BasePlayer bp) {
		super(bp);
	}

	public int getPriority() {
		MapLocation myLoc=player.myLoc;
		int i, j, k;
		int theInt;
		int unitHealth;
		int best=NEVER_MASK;
		int myNoTurnMask;
		int myNoMoveMask;
		if(myRC.isDeployed()) {
			myNoTurnMask=NOMOVE_MASK;
			myNoMoveMask=NEVER_MASK;
		}
		else {
			myNoTurnMask=NOTURN_MASK;
			myNoMoveMask=NOMOVE_MASK;
		}
		int d;
		MapLocation loc;
		MapLocation tmpTarget=null;
		RobotInfo [] robotInfos;
		//Robot [] robots;
		boolean [] seen = new boolean [ 1 << ROBOT_ID_BITS ];
		RobotInfo info;
		seentarget:
		{
			//robots=player.enemyArchonRobots;
			robotInfos=player.enemyArchonInfos;
			for(i=player.enemyArchons.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				unitHealth=(int)info.energonLevel+2;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|ARCHON_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|ARCHON_TYPE|DEFAULT_TRUE_MASK;
				if((d=myLoc.distanceSquaredTo(info.location))>25||d<=2)
					theInt|=myNoMoveMask;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=myNoTurnMask;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			if(best<=(1<<TYPE_PRIORITY_OFFSET))
				break seentarget;
			//robots=player.enemyTurretRobots;
			robotInfos=player.enemyTurretInfos;
			for(i=player.enemyTurrets.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|TURRET_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|TURRET_TYPE|DEFAULT_TRUE_MASK;
				if((d=myLoc.distanceSquaredTo(info.location))>25||d<=2)
					theInt|=myNoMoveMask;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=myNoTurnMask;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				//debug_printInt(theInt);
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			//robots=player.enemyAuraRobots;
			robotInfos=player.enemyAuraInfos;
			for(i=player.enemyAuras.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|AURA_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|AURA_TYPE|DEFAULT_TRUE_MASK;
				if((d=myLoc.distanceSquaredTo(info.location))>25||d<=2)
					theInt|=myNoMoveMask;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=myNoTurnMask;
				//debug_printInt(theInt);
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			//robots=player.enemySoldierRobots;
			robotInfos=player.enemySoldierInfos;
			for(i=player.enemySoldiers.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[info.id&ROBOT_ID_MASK]=true;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|SOLDIER_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|SOLDIER_TYPE|DEFAULT_TRUE_MASK;
				if((d=myLoc.distanceSquaredTo(info.location))>25||d<=2)
					theInt|=myNoMoveMask;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=myNoTurnMask;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			//robots=player.enemyChainerRobots;
			robotInfos=player.enemyChainerInfos;
			for(i=player.enemyChainers.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|CHAINER_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|CHAINER_TYPE|DEFAULT_TRUE_MASK;
				if((d=myLoc.distanceSquaredTo(info.location))>25||d<=2)
					theInt|=myNoMoveMask;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=myNoTurnMask;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			if(best<=(2<<TYPE_PRIORITY_OFFSET))
				break seentarget;
			//robots=player.enemyWoutRobots;
			robotInfos=player.enemyWoutInfos;
			for(i=player.enemyWouts.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|WOUT_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|WOUT_TYPE|DEFAULT_TRUE_MASK;
				if((d=myLoc.distanceSquaredTo(info.location))>25||d<=2)
					theInt|=myNoMoveMask;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=myNoTurnMask;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				//debug_printInt(theInt);
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			//robots=player.enemyCommRobots;
			robotInfos=player.enemyCommInfos;
			for(i=player.enemyComms.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|COMM_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|COMM_TYPE|DEFAULT_TRUE_MASK;
				if((d=myLoc.distanceSquaredTo(info.location))>25||d<=2)
					theInt|=myNoMoveMask;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=myNoTurnMask;
				//debug_printInt(theInt);
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			//robots=player.enemyTeleporterRobots;
			robotInfos=player.enemyTeleporterInfos;
			for(i=player.enemyTeleporters.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|TELEPORTER_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|TELEPORTER_TYPE|DEFAULT_TRUE_MASK;
				if((d=myLoc.distanceSquaredTo(info.location))>25||d<=2)
					theInt|=myNoMoveMask;
				else if(!myRC.canAttackSquare(info.location))
					theInt|=myNoTurnMask;
				//debug_printInt(theInt);
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
		}
		Message m;
		int [] ints;
		MapLocation [] locs;
		Message [] stack;
		SuperMessageStack messages=this.messages;
		int stop=SuperMessageStack.t;
		int sawItBits;
		int index;
		for(k=SuperMessageStack.KEEP_TIME;k>0;k--) {
			sawItBits=(SuperMessageStack.KEEP_TIME-k+1)<<SAWIT_OFFSET;
			if(best<=sawItBits) break;
			index=(SuperMessageStack.t+k)%SuperMessageStack.KEEP_TIME;
			stack=messages.messages[index];
			for(j=messages.lengths[index]-1;j>=0;j--) {
				ints=stack[j].ints;
				locs=stack[j].locations;
				for(i=locs.length-1;i>0;i--) {
					theInt=ints[i];
					if(seen[theInt&ROBOT_ID_MASK]) continue;
					seen[theInt&ROBOT_ID_MASK]=true;
					if((d=myLoc.distanceSquaredTo(locs[i]))>41) continue;
					if((theInt&HEALTH_MASK)<ONEHIT_HEALTH)
						theInt=(theInt^ONEHIT_AND_HEALTH_MASK)|sawItBits;
					else
						theInt|=sawItBits;
					if(d>25||d<=2)
						theInt|=myNoMoveMask;
					else if(!myRC.canAttackSquare(locs[i]))
						theInt|=myNoTurnMask;
					//debug_printInt(theInt);
					if(theInt<best) {
						best=theInt;
						tmpTarget=locs[i];
					}
				}
			}
		}
		if(best<NEVER_MASK) {
			target=tmpTarget;
			targetInt=best;
			if(!player.attacking) {
				if(myRC.canAttackSquare(tmpTarget))
					return ATTACK_SHOOT;
				else if(myLoc.distanceSquaredTo(tmpTarget)<=25)
					return ATTACK_TURN;
				else
					return ATTACK_MOVE;
			}
			else if(myRC.getRoundsUntilAttackIdle()<=2)
				return ATTACK_MOVE;
			else // don't move toward the enemy if we can't shoot yet
				return FIND_ENEMY_PRIORITY;
		}
		else
			return NEVER;
	}

	public boolean isItSafeToMove() {
		MapLocation myLoc=player.myLoc;
		boolean [] seen = new boolean [1 << ROBOT_ID_BITS];
		int i;
		RobotInfo [] infos=player.enemyTurretInfos;
		//Robot [] robots=player.enemyTurretRobots;
		for(i=player.enemyTurrets.size-1;i>=0;i--) {
			seen[infos[i].id&ROBOT_ID_MASK]=true;
			if(infos[i].roundsUntilAttackIdle<=12) {
				return false;
			}
		}
		infos=player.enemySoldierInfos;
		//robots=player.enemySoldierRobots;
		for(i=player.enemySoldiers.size-1;i>=0;i--) {
			seen[infos[i].id&ROBOT_ID_MASK]=true;
			if(infos[i].roundsUntilAttackIdle<=12) {
				return false;
			}
		}
		infos=player.enemyChainerInfos;
		//robots=player.enemyChainerRobots;
		for(i=player.enemyChainers.size-1;i>=0;i--) {
			seen[infos[i].id&ROBOT_ID_MASK]=true;
			if(infos[i].roundsUntilAttackIdle<=12) {
				return false;
			}
		}
		int j, k, theInt, index;
		Message[] stack;
		int [] ints;
		MapLocation [] locs;
		for(k=SuperMessageStack.KEEP_TIME;k>0;k--) {
			index=(SuperMessageStack.t+k)%SuperMessageStack.KEEP_TIME;
			stack=messages.messages[index];
			for(j=messages.lengths[index]-1;j>=0;j--) {
				ints=stack[j].ints;
				locs=stack[j].locations;
				for(i=locs.length-1;i>0&&((theInt=ints[i])&TYPE_MASK)==TURRET_TYPE;i--) {
					if(seen[theInt&ROBOT_ID_MASK]) continue;
					seen[theInt&ROBOT_ID_MASK]=true;
					if((theInt&JUSTSPAWNED_MASK)==0&&myLoc.distanceSquaredTo(locs[i])<=41) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void tryToAccomplish() {
		try {
			if((!player.attacking)&&myRC.canAttackSquare(target)) {
				if((targetInt&AIRBORNE_MASK)==0)
					myRC.attackAir(target);
				else
					myRC.attackGround(target);
				if((targetInt&ONEHIT_MASK)==0)
					player.mySender.sendIShotThat(targetInt|NEVER_MASK,target);
				else
					player.mySender.sendIShotThat((targetInt-HEALTH_MASK)&~UNIT_SETS_MASK,target);
			}
			else {
				int d;
				if((d=player.myLoc.distanceSquaredTo(target))<=2) {
					player.myNav.moveToBackward(player.myLoc.add(target.directionTo(player.myLoc)));
				}
				else if(d<=25) {
					Direction dir=player.myLoc.directionTo(target);
					// SOLDIER_TYPE and CHAINER_TYPE are the same so this
					// covers both
					if((targetInt&(TYPE_MASK|JUSTSPAWNED_MASK))==SOLDIER_TYPE) {
						// soldiers and chainers have a shorter attack range
						// than turrets, so back away from them
						if(myRC.getDirection()==dir) {
							if(myRC.canAttackSquare(player.myLoc.add(dir.opposite())))
								myRC.moveBackward();
						}
						else
							myRC.setDirection(dir);
					}
					else {
						if(myRC.getDirection()==dir) {
							if(d>8&&
							   myRC.canMove(dir)&&
							   !myRC.canMove(dir.opposite())&&
							   isItSafeToMove())
								myRC.moveForward();
						}
						else
							myRC.setDirection(dir);
					}
				}
				else {
					Direction dir=player.myLoc.directionTo(target);
					if(myRC.getRoundsUntilAttackIdle()<=(myRC.getDirection()==dir?1:2)||
					   isItSafeToMove())
						player.myNav.moveToForward(target);
					else if(myRC.getDirection()!=dir)
						myRC.setDirection(dir);
					else if(player.myTeam==battlecode.common.Team.A)
						myRC.deploy();
				}
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}
}