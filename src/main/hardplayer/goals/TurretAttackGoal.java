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

public class TurretAttackGoal extends Goal {
	protected MapLocation target;
	protected int targetInt;

	SuperMessageStack messages;

	static public final int ONEHIT = 1; /*(int)battlecode.common.RobotType.TURRET.attackPower()*/;
	static public final int ONEHIT_HEALTH = ONEHIT<<HEALTH_OFFSET;
	static public final int RSQ = battlecode.common.RobotType.TURRET.attackRadiusMaxSquared();

	public TurretAttackGoal(BasePlayer bp) {
		super(bp);
		messages = bp.enemyUnitMessages;
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
		RobotInfo [] robotInfos;
		//Robot [] robots;
		RobotInfo info;
		boolean [] seen = new boolean [1 << ROBOT_ID_BITS];
		seentarget:
		{
			robotInfos=player.enemyArchonInfos;
			//robots=player.enemyArchonRobots;
			for(i=player.enemyArchons.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel+2;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|ARCHON_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|ARCHON_TYPE|DEFAULT_TRUE_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			if(best<=(1<<TYPE_PRIORITY_OFFSET))
				break seentarget;
			robotInfos=player.enemyTurretInfos;
			//robots=player.enemyTurretRobots;
			for(i=player.enemyTurrets.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|TURRET_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|TURRET_TYPE|DEFAULT_TRUE_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				//debug_printInt(theInt);
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robotInfos=player.enemyAuraInfos;
			//robots=player.enemyAuraRobots;
			for(i=player.enemyAuras.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|AURA_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|AURA_TYPE|DEFAULT_TRUE_MASK;
				//debug_printInt(theInt);
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robotInfos=player.enemySoldierInfos;
			//robots=player.enemySoldierRobots;
			for(i=player.enemySoldiers.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|SOLDIER_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|SOLDIER_TYPE|DEFAULT_TRUE_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robotInfos=player.enemyChainerInfos;
			//robots=player.enemyChainerRobots;
			for(i=player.enemyChainers.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|CHAINER_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|CHAINER_TYPE|DEFAULT_TRUE_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			if(best<=(2<<TYPE_PRIORITY_OFFSET))
				break seentarget;
			robotInfos=player.enemyWoutInfos;
			//robots=player.enemyWoutRobots;
			for(i=player.enemyWouts.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|WOUT_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|WOUT_TYPE|DEFAULT_TRUE_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robotInfos=player.enemyWoutInfos;
			//robots=player.enemyWoutRobots;
			for(i=player.enemyWouts.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|WOUT_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|WOUT_TYPE|DEFAULT_TRUE_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robotInfos=player.enemyCommInfos;
			//robots=player.enemyCommRobots;
			for(i=player.enemyComms.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|COMM_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|COMM_TYPE|DEFAULT_TRUE_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robotInfos=player.enemyTeleporterInfos;
			//robots=player.enemyTeleporterRobots;
			for(i=player.enemyTeleporters.size-1;i>=0;i--) {
				info=robotInfos[i];
				theInt=info.id&ROBOT_ID_MASK;
				seen[theInt]=true;
				if(!myRC.canAttackSquare(info.location))
					continue;
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				if(unitHealth<ONEHIT)
					theInt|=((unitHealth<<HEALTH_OFFSET)|TELEPORTER_TYPE|DEFAULT_TRUE_MASK)^ONEHIT_AND_HEALTH_MASK;
				else
					theInt|=(unitHealth<<HEALTH_OFFSET)|TELEPORTER_TYPE|DEFAULT_TRUE_MASK;
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
				for(i=locs.length-2;i>0;i--) {
					theInt=ints[i];
					if(seen[theInt&ROBOT_ID_MASK]) continue;
					seen[theInt&ROBOT_ID_MASK]=true;
					if(!myRC.canAttackSquare(locs[i])) continue;
					if((theInt&HEALTH_MASK)<ONEHIT_HEALTH)
						theInt=(theInt^ONEHIT_AND_HEALTH_MASK)|sawItBits;
					else
						theInt|=sawItBits;
					//debug_printInt(theInt);
					if(theInt<best) {
						best=theInt;
						tmpTarget=locs[i];
					}
				}
			}
		}
		//debug_setTargetIndicatorString(tmpTarget,best);
		if(best<NEVER_MASK) {
			try {
				if((best&AIRBORNE_MASK)==0) {
					player.debug_setIndicatorString(2,"attacking air");
					myRC.attackAir(tmpTarget);
				}
				else {
					player.debug_setIndicatorString(2,"attacking ground");
					myRC.attackGround(tmpTarget);
				}
				if((best&ONEHIT_MASK)==0)
					player.mySender.sendIShotThat(best|NEVER_MASK,tmpTarget);
				else
					player.mySender.sendIShotThat(best-HEALTH_MASK,tmpTarget);
			} catch(Exception e) {
				BasePlayer.debug_stackTrace(e);
			}
		}
	}

	public void debug_setTargetIndicatorString(MapLocation l, int best) {
		myRC.setIndicatorString(1,myRC.getLocation()+" "+l+" "+((best&SAWIT_MASK)!=0)+Clock.getRoundNum());
	}

	public int getMaxPriority() {
		if(player.attacking) return ATTACK_MOVE;
		else return ATTACK_SHOOT;
	}

	public static void debug_printInt(int theInt) {
		System.out.println("Robot ID: "+(theInt&ROBOT_ID_MASK));
		switch(theInt&TYPE_MASK) {
		case ARCHON_TYPE:
			System.out.println("Archon");
			break;
		case TURRET_TYPE:
			System.out.println("Turret");
			break;
		case SOLDIER_TYPE:
			System.out.println("Soldier or Chainer");
			break;
		case WOUT_TYPE:
			System.out.println("Wout");
			break;
		default:
			System.out.println("I don't recognize this unit!");
		}
		if((theInt&ONEHIT_MASK)==0) {
			System.out.println("I can one-hit this robot");
			System.out.println("Health: "+(((theInt^ONEHIT_AND_HEALTH_MASK)&HEALTH_MASK)>>HEALTH_OFFSET));
		}
		else {
			System.out.println("Health: "+((theInt&HEALTH_MASK)>>HEALTH_OFFSET));
		}
		if((theInt&NOMOVE_MASK)==0) {
			if((theInt&NOTURN_MASK)==0)
				System.out.println("I can attack this robot without turning");
			else
				System.out.println("I need to turn to attack this robot");
		}
		else
			System.out.println("I need to move to attack this robot");
		if((theInt&JUSTSPAWNED_MASK)!=0)
			System.out.println("This robot spawned recently");
		if((theInt&SAWIT_MASK)==0)
			System.out.println("I can see this robot");
		else
			System.out.println("I heard about this robot "+(((theInt&SAWIT_MASK)>>SAWIT_OFFSET)-1)+" turns ago");
		if((theInt&NEVER_MASK)>0)
			System.out.println("This robot is dead");
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
			myNoMoveMask=JUSTSPAWNED_MASK;
		}
		else {
			myNoTurnMask=NOTURN_MASK;
			myNoMoveMask=NOMOVE_MASK;
		}
		int d;
		MapLocation loc;
		MapLocation tmpTarget=null;
		RobotInfo [] robotInfos;
		Robot [] robots;
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
				if((d=myLoc.distanceSquaredTo(info.location))>RSQ||d<=2)
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
				if((d=myLoc.distanceSquaredTo(info.location))>RSQ||d<=2)
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
				if((d=myLoc.distanceSquaredTo(info.location))>RSQ||d<=2)
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
				if((d=myLoc.distanceSquaredTo(info.location))>RSQ||d<=2)
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
				if((d=myLoc.distanceSquaredTo(info.location))>RSQ||d<=2)
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
				if((d=myLoc.distanceSquaredTo(info.location))>RSQ||d<=2)
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
				if((d=myLoc.distanceSquaredTo(info.location))>RSQ||d<=2)
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
				if((d=myLoc.distanceSquaredTo(info.location))>RSQ||d<=2)
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
				for(i=locs.length-2;i>0;i--) {
					theInt=ints[i];
					if(seen[theInt&ROBOT_ID_MASK]) continue;
					seen[theInt&ROBOT_ID_MASK]=true;
					if((d=myLoc.distanceSquaredTo(locs[i]))>41) continue;
					if((theInt&HEALTH_MASK)<ONEHIT_HEALTH)
						theInt=(theInt^ONEHIT_AND_HEALTH_MASK)|sawItBits;
					else
						theInt|=sawItBits;
					if(d>RSQ||d<=2)
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
				else if(myLoc.distanceSquaredTo(tmpTarget)<=RSQ)
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
				for(i=locs.length-2;i>0&&((theInt=ints[i])&TYPE_MASK)==TURRET_TYPE;i--) {
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
			/*
			Direction dir=player.myLoc.directionTo(target);
			if((targetInt&(TYPE_MASK|JUSTSPAWNED_MASK))==SOLDIER_TYPE) {
				// soldiers and chainers have a shorter attack range
				// than turrets, so back away from them
				if(myRC.getDirection()==dir) {
					if((!player.moving)&&myRC.canMove(dir.opposite())&&myRC.canAttackSquare(player.myLoc.add(dir.opposite()))) {
						myRC.moveBackward();
						return;
					}
				}
				else {
					myRC.setDirection(dir);
					return;
				}
			}
			*/
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
				else if(d<=RSQ) {
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