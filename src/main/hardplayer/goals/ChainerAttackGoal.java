package hardplayer.goals;

import static hardplayer.goals.BroadcastEnemyUnitLocationsGoal.*;
import static hardplayer.message.SuperMessageStack.KEEP_TIME;
import hardplayer.message.SuperMessageStack;
import hardplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class ChainerAttackGoal extends Goal {
	MapLocation target;
	int targetInt;

	SuperMessageStack messages;

	static public final int ONEHIT = (int)battlecode.common.RobotType.CHAINER.attackPower();
	static public final int ONEHIT_HEALTH = ONEHIT<<HEALTH_OFFSET;

	/*
	static private final int [][][] splashMask;
	static private final int [][][] splashMaskShootOnly;

	static {
		// need to move to attack
		static final int M = NOMOVE_MASK;
		// need to turn to attack, and can only splash then
		static final int U = NOTURN_MASK|SPLASH_MASK;
		// need to turn to attack
		static final int T = NOTURN_MASK;
		// can only splash
		static final int S = SPLASH_MASK;
		// can only splash, but enemy is close enough that we treat it
		// as a direct hit for priority purposes
		static final int I = 0;
		// direct hit
		static final int H = 0;
		// splashing things that are directly on top of you is bad
		static final int N = NEVER_MASK;

		int [][] splashMaskNorth = new int [][] { 
			{M,M,M,S,S,S,M,M,M},
			{M,S,S,S,H,S,S,S,M},
			{M,S,H,H,H,H,H,S,M},
			{U,I,I,H,H,H,I,I,U},
			{U,T,T,T,N,T,T,T,U},
			{U,U,T,T,T,T,T,U,U},
			{M,U,T,T,T,T,T,U,M},
			{M,U,U,U,T,U,U,U,M},
			{M,M,M,U,U,U,M,M,M}};

		int [][] splashMaskNortheast = new int [][] {
			{M,M,M,S,S,S,M,M,M},
			{M,U,U,S,H,S,S,S,M},
			{M,U,T,I,H,H,H,S,M},
			{U,U,T,T,H,H,H,S,S},
			{U,T,T,T,N,H,H,H,S},
			{U,U,T,T,T,T,I,S,S},
			{M,U,T,T,T,T,T,U,M},
			{M,U,U,U,T,U,U,U,M},
			{M,M,M,U,U,U,M,M,M}};
		
		int [][] splashMaskEast = new int [9][9];
		int [][] splashMaskSoutheast = new int [9][9];
		int [][] splashMaskSouth = new int [9][9];
		int [][] splashMaskSouthwest = new int [9][9];
		int [][] splashMaskWest = new int [9][9];
		int [][] splashMaskNorthwest = new int [9][9];

		int i,j;
		for(i=8;i>=0;i--)
			for(j=8;j>=0;j--) {
				splashMaskWest[8-j][i]=splashMaskSouth[8-i][8-j]=splashMaskEast[j][8-i]=splashMaskNorth[i][j];
				splashMaskNorthwest[8-j][i]=splashMaskSouthwest[8-i][8-j]=splashMaskSoutheast[j][8-i]=splashMaskNortheast[i][j];
			}


	}
	*/

	boolean [] seen;

	static public final int HITCOUNT_BITS = 4;
	static public final long HITCOUNT_MASK = (1L<<HITCOUNT_BITS)-1L;
	static public final long SPLASH_MULTIPLIER = (1L<<(2*HITCOUNT_BITS))+(1L<<HITCOUNT_BITS)+1;

	// y runs from 0-8 and x runs from 1-9 except east and west
	// where y runs from 1-9 and x runs from 0-8.
	// The things I will do for bytecodes...
	// give preference to locations that we think are closer
	// to the enemy
	static public final int [][][] attackable = new int [][][] {
		{ {7,2},{3,2},{6,2},{4,2},{5,2},{5,1} },
		{ {7,4},{5,2},{8,4},{5,1},{7,3},{6,2},{7,2} },
		{ {6,7},{6,3},{6,6},{6,4},{6,5},{7,5} },
		{ {5,6},{7,4},{5,7},{8,4},{6,6},{7,5},{7,6} },
		{ {3,6},{7,6},{4,6},{6,6},{5,6},{5,7} },
		{ {3,4},{5,6},{2,4},{5,7},{3,5},{4,6},{3,6} },
		{ {2,3},{2,7},{2,4},{2,6},{2,5},{1,5} },
		{ {5,2},{3,4},{5,1},{2,4},{4,2},{3,3},{3,2} }};

		/*
		{ {5,1}, {3,2}, {4,2}, {5,2}, {6,2}, {7,2} },
		{ {5,1}, {5,2}, {6,2}, {7,2}, {7,3}, {7,4}, {8,4} },
		{ {7,5}, {6,3}, {6,4}, {6,5}, {6,6}, {6,7} },
		{ {8,4}, {7,4}, {7,5}, {7,6}, {6,6}, {5,6}, {5,7} },
		{ {5,7}, {7,6}, {6,6}, {5,6}, {6,6}, {3,6} },
		{ {5,7}, {5,6}, {4,6}, {3,6}, {3,5}, {3,4}, {2,4} },
		{ {1,5}, {2,7}, {2,6}, {2,5}, {2,4}, {2,3} },
		{ {2,4}, {3,4}, {3,3}, {3,2}, {4,2}, {5,2}, {5,1} } };
		*/

	static void printAttackable() {
		int [][] v1 = { {0,-3}, {0,-2}, {-1,-2}, {1,-2}, {-2,-2}, {2,-2} };
		int [][] v2 = { {2,-2}, {1,-2}, {2,-1}, {0,-3}, {3,0}, {0,-2}, {2,0} };

		int [] v;

		for(int i=v1.length-1;i>=0;i--) {
			v=v1[i];
			System.out.print("{"+(v[0]+5)+","+(v[1]+4)+"},");
		}
		System.out.println("");
		for(int i=v2.length-1;i>=0;i--) {
			v=v2[i];
			System.out.print("{"+(v[0]+5)+","+(v[1]+4)+"},");
		}
		System.out.println("");
		for(int i=v1.length-1;i>=0;i--) {
			v=v1[i];
			System.out.print("{"+((-v[1])+4)+","+(v[0]+5)+"},");
		}
		System.out.println("");
		for(int i=v2.length-1;i>=0;i--) {
			v=v2[i];
			System.out.print("{"+((-v[1])+5)+","+(v[0]+4)+"},");
		}
		System.out.println("");
		for(int i=v1.length-1;i>=0;i--) {
			v=v1[i];
			System.out.print("{"+((-v[0])+5)+","+((-v[1])+4)+"},");
		}
		System.out.println("");
		for(int i=v2.length-1;i>=0;i--) {
			v=v2[i];
			System.out.print("{"+((-v[0])+5)+","+((-v[1])+4)+"},");
		}
		System.out.println("");
		for(int i=v1.length-1;i>=0;i--) {
			v=v1[i];
			System.out.print("{"+(v[1]+4)+","+((-v[0])+5)+"},");
		}
		System.out.println("");
		for(int i=v2.length-1;i>=0;i--) {
			v=v2[i];
			System.out.print("{"+(v[1]+5)+","+((-v[0])+4)+"},");
		}
		System.out.println("");
	}

	public ChainerAttackGoal(BasePlayer bp) {
		super(bp);
		messages = bp.enemyUnitMessages;
	}

	public int getMaxPriority() {
		//if(player.attacking) return ATTACK_TURN;
		//else return ATTACK_SHOOT;
		return ATTACK_SHOOT;
	}

	public void spreadOutNorthOrWest(long [] hits, long [] splash) {
		hits[0]*=SPLASH_MULTIPLIER;
		hits[1]*=SPLASH_MULTIPLIER;
		hits[2]*=SPLASH_MULTIPLIER;
		hits[3]*=SPLASH_MULTIPLIER;
		splash[1]=(splash[2]=hits[1]+hits[2])+hits[0];
		splash[2]+=hits[3];
	}

	public void spreadOutSouthOrEast(long [] hits, long [] splash) {
		hits[8]*=SPLASH_MULTIPLIER;
		hits[7]*=SPLASH_MULTIPLIER;
		hits[6]*=SPLASH_MULTIPLIER;
		hits[5]*=SPLASH_MULTIPLIER;
		splash[7]=(splash[6]=hits[7]+hits[6])+hits[8];
		splash[6]+=hits[5];
	}

	public void spreadOutNorthwestOrNortheast(long [] hits, long [] splash) {
		hits[0]*=SPLASH_MULTIPLIER;
		hits[1]*=SPLASH_MULTIPLIER;
		hits[2]*=SPLASH_MULTIPLIER;
		hits[3]*=SPLASH_MULTIPLIER;
		hits[4]*=SPLASH_MULTIPLIER;
		hits[5]*=SPLASH_MULTIPLIER;
		splash[1]=(splash[2]=hits[1]+hits[2])+hits[0];
		splash[2]+=hits[3];
		splash[3]=(splash[4]=hits[3]+hits[4])+hits[2];
		splash[4]+=hits[5];
	}

	public void spreadOutSouthwestOrSoutheast(long [] hits, long [] splash) {
		hits[8]*=SPLASH_MULTIPLIER;
		hits[7]*=SPLASH_MULTIPLIER;
		hits[6]*=SPLASH_MULTIPLIER;
		hits[5]*=SPLASH_MULTIPLIER;
		hits[4]*=SPLASH_MULTIPLIER;
		hits[3]*=SPLASH_MULTIPLIER;
		splash[7]=(splash[6]=hits[7]+hits[6])+hits[8];
		splash[6]+=hits[5];
		splash[5]=(splash[4]=hits[5]+hits[4])+hits[6];
		splash[4]+=hits[3];
	}

	//static public final long default = 0x8421;

	public void debug_printEnemies1() {
	}

	public void shootOnly() {
		//boolean seeChainers = player.myTeam==battlecode.common.Team.A&&player.alliedChainers.size>0;
		//boolean seeChainers=false;
		//player.debug_setIndicatorString(0,Boolean.toString(seeChainers));
		boolean [] seen = new boolean[1024];
		this.seen = seen;
		long [] airhitcount = new long [9];
		long [] groundhitcount = new long [9];
		long [] newairhitcount = new long [9];
		long [] newgroundhitcount = new long [9];
		int x=player.myLoc.getX(), y=player.myLoc.getY(), dx, dy;
		int i;
		RobotInfo [] robots=player.enemyArchonInfos;
		RobotInfo info;
		// Yeah, I'm special casing for east and west just
		// to save a few hundred bytecodes.
		int [][] squares = attackable[myRC.getDirection().ordinal()];
		if(myRC.getDirection().ordinal()%4!=2) {
			for(i=player.enemyArchons.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				airhitcount[dy+4]|=1L<<(4*(dx+4));
			}
			//if(!seeChainers) {
			robots=player.enemyChainerInfos;
			for(i=player.enemyChainers.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dy+4]|=1L<<(4*(dx+4));
			}
			robots=player.enemyTurretInfos;
			for(i=player.enemyTurrets.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dy+4]|=1L<<(4*(dx+4));		
			}
			robots=player.enemySoldierInfos;
			for(i=player.enemySoldiers.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dy+4]|=1L<<(4*(dx+4));
			}
			robots=player.enemyAuraInfos;
			for(i=player.enemyAuras.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dy+4]|=1L<<(4*(dx+4));	
			}
			robots=player.enemyCommInfos;
			for(i=player.enemyComms.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dy+4]|=1L<<(4*(dx+4));	
			}
			robots=player.enemyTeleporterInfos;
			for(i=player.enemyTeleporters.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dy+4]|=1L<<(4*(dx+4));	
			}
			robots=player.enemyWoutInfos;
			for(i=player.enemyWouts.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dy+4]|=1L<<(4*(dx+4));	
			}
			//} end brace for seeChainers
			Message m;
			int [] ints;
			MapLocation [] locs;
			Message [] stack;
			SuperMessageStack messages=this.messages;
			int index;
			int j,k;
			int theInt;
			messages:
			for(k=SuperMessageStack.KEEP_TIME;k>0;k--) {
				index=(SuperMessageStack.t+k)%SuperMessageStack.KEEP_TIME;
				stack=messages.messages[index];
				for(j=messages.lengths[index]-1;j>=0;j--) {
					//if(player.myTeam==battlecode.common.Team.B&&Clock.getRoundNum()>5000)
					//	break messages;
					ints=stack[j].ints;
					locs=stack[j].locations;
					for(i=locs.length-2;i>0;i--) {
						theInt=ints[i];
						if(seen[theInt&ROBOT_ID_MASK]) continue;
						seen[theInt&ROBOT_ID_MASK]=true;
						if((theInt&NEVER_MASK)!=0) continue;
						dx=locs[i].getX()-x;
						dy=locs[i].getY()-y;
						if(dx*dx+dy*dy>18) continue;
						//System.out.println((locs[0].getX()-x)+","+(locs[0].getY()-y)+"@"+ints[ints.length-2]+"_"+(theInt&ROBOT_ID_MASK)+":"+dx+","+dy);
						if((theInt&AIRBORNE_MASK)==0)
							airhitcount[dy+4]|=1L<<(4*(dx+4));
						else /*if(!seeChainers)*/
							groundhitcount[dy+4]|=1L<<(4*(dx+4));
					}
				}
			}
			//player.debug_startTiming();
			switch(myRC.getDirection()) {
			case NORTH:
				spreadOutNorthOrWest(airhitcount,newairhitcount);
				spreadOutNorthOrWest(groundhitcount,newgroundhitcount);
				break;
			case SOUTH:
				spreadOutSouthOrEast(airhitcount,newairhitcount);
				spreadOutSouthOrEast(groundhitcount,newgroundhitcount);
				break;
			case NORTH_WEST:
			case NORTH_EAST:
				spreadOutNorthwestOrNortheast(airhitcount,newairhitcount);
				spreadOutNorthwestOrNortheast(groundhitcount,newgroundhitcount);
				break;
			case SOUTH_WEST:
			case SOUTH_EAST:
				spreadOutSouthwestOrSoutheast(airhitcount,newairhitcount);
				spreadOutSouthwestOrSoutheast(groundhitcount,newgroundhitcount);
				break;
			}
			//player.debug_stopTiming();
			int best=0;
			long bestval=0L;
			long val;
			boolean bestair=false;
			int [] square;
			for(i=squares.length-1;i>=0;i--) {
				square=squares[i];
				val=(newairhitcount[square[1]]>>(HITCOUNT_BITS*square[0]))&HITCOUNT_MASK;
				if(val>bestval) {
					bestval=val;
					best=i;
					bestair=true;
				}
			}
			for(i=squares.length-1;i>=0;i--) {
				square=squares[i];
				val=(newgroundhitcount[square[1]]>>(HITCOUNT_BITS*square[0]))&HITCOUNT_MASK;
				if(val>bestval) {
					bestval=val;
					best=i;
				bestair=false;
				}
			}
			if(bestval>0) {
				MapLocation loc = new MapLocation(x+squares[best][0]-5,y+squares[best][1]-4);
				try {
					if(bestair) myRC.attackAir(loc);
					else myRC.attackGround(loc);
				} catch(Exception e) {
					BasePlayer.debug_stackTrace(e);
				}
			}
		}
		else {
			for(i=player.enemyArchons.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				airhitcount[dx+4]|=1L<<(4*(dy+4));
			}
			//if(!seeChainers) {
			robots=player.enemyChainerInfos;
			for(i=player.enemyChainers.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dx+4]|=1L<<(4*(dy+4));
			}
			robots=player.enemyTurretInfos;
			for(i=player.enemyTurrets.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dx+4]|=1L<<(4*(dy+4));		
			}
			robots=player.enemySoldierInfos;
			for(i=player.enemySoldiers.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dx+4]|=1L<<(4*(dy+4));
			}
			robots=player.enemyAuraInfos;
			for(i=player.enemyAuras.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dx+4]|=1L<<(4*(dy+4));	
			}
			robots=player.enemyCommInfos;
			for(i=player.enemyComms.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dx+4]|=1L<<(4*(dy+4));	
			}
			robots=player.enemyTeleporterInfos;
			for(i=player.enemyTeleporters.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dx+4]|=1L<<(4*(dy+4));	
			}
			robots=player.enemyWoutInfos;
			for(i=player.enemyWouts.size-1;i>=0;i--) {
				info=robots[i];
				seen[info.id%1024]=true;
				if(info.energonLevel<-1.) continue;
				dx=info.location.getX()-x;
				dy=info.location.getY()-y;
				//if(dx*dx+dy*dy>18) continue;
				groundhitcount[dx+4]|=1L<<(4*(dy+4));	
			}
			//} end brace for seeChainers
			Message m;
			int [] ints;
			MapLocation [] locs;
			Message [] stack;
			SuperMessageStack messages=this.messages;
			int index;
			int j,k;
			int theInt;
			for(k=SuperMessageStack.KEEP_TIME;k>0;k--) {
				index=(SuperMessageStack.t+k)%SuperMessageStack.KEEP_TIME;
				stack=messages.messages[index];
				for(j=messages.lengths[index]-1;j>=0;j--) {
					ints=stack[j].ints;
					locs=stack[j].locations;
					for(i=locs.length-2;i>0;i--) {
						theInt=ints[i];
						if(seen[theInt&ROBOT_ID_MASK]) continue;
						seen[theInt&ROBOT_ID_MASK]=true;
						if((theInt&NEVER_MASK)!=0) continue;
						dx=locs[i].getX()-x;
						dy=locs[i].getY()-y;
						if(dx*dx+dy*dy>18) continue;
						if((theInt&AIRBORNE_MASK)==0)
							airhitcount[dx+4]|=1L<<(4*(dy+4));
						else /*if(!seeChainers)*/
							groundhitcount[dx+4]|=1L<<(4*(dy+4));
					}
				}
			}
			//player.debug_startTiming();
			switch(myRC.getDirection()) {
			case WEST:
				spreadOutNorthOrWest(airhitcount,newairhitcount);
				spreadOutNorthOrWest(groundhitcount,newgroundhitcount);
				break;
			case EAST:
				spreadOutSouthOrEast(airhitcount,newairhitcount);
				spreadOutSouthOrEast(groundhitcount,newgroundhitcount);
				break;
			}
			//player.debug_stopTiming();
			int [] square;
			int best=0;
			long bestval=0L;
			long val;
			boolean bestair=false;
			for(i=squares.length-1;i>=0;i--) {
				square=squares[i];
				val=(newairhitcount[square[0]]>>(HITCOUNT_BITS*square[1]))&HITCOUNT_MASK;
				if(val>bestval) {
					bestval=val;
					best=i;
					bestair=true;
				}
			}
			for(i=squares.length-1;i>=0;i--) {
				square=squares[i];
				val=(newgroundhitcount[square[0]]>>(HITCOUNT_BITS*square[1]))&HITCOUNT_MASK;
				if(val>bestval) {
					bestval=val;
					best=i;
				bestair=false;
				}
			}
			if(bestval>0) {
				MapLocation loc = new MapLocation(x+squares[best][0]-4,y+squares[best][1]-5);
				try {
					if(bestair) myRC.attackAir(loc);
					else myRC.attackGround(loc);
				} catch(Exception e) {
					BasePlayer.debug_stackTrace(e);
				}
			}
		}
	}

	public int getPriority() {
		if(!player.attacking) {
			shootOnly();
			if(myRC.hasActionSet()) {
				target=null;
				return ATTACK_SHOOT;
			}		
		}
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
				//if(myLoc.distanceSquaredTo(info.location)>18)
				//	theInt|=NOMOVE_MASK;
				//else if(!myRC.canAttackSquare(info.location))
				//	theInt|=NOTURN_MASK;
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
						//if((d=myLoc.distanceSquaredTo(locs[i]))>18) continue;
						theInt=ints[i]|sawitBits;
						if((theInt&HEALTH_MASK)<ONEHIT_HEALTH)
							theInt^=ONEHIT_AND_HEALTH_MASK;
						if((d=myLoc.distanceSquaredTo(locs[i]))>18)
							theInt|=NOMOVE_MASK;
						//else if(!myRC.canAttackSquare(locs[i]))
						//	theInt|=NOTURN_MASK;
						if(theInt<best) {
							best=theInt;
							tmpTarget=locs[i];
						}
					}
				}
			}
			if(best<=(1<<TYPE_PRIORITY_OFFSET))
				break findtarget;
			robots=player.enemyAuraInfos;
			for(i=player.enemyAuras.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=AURA_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				//if(myLoc.distanceSquaredTo(info.location)>18)
				//	theInt|=NOMOVE_MASK;
				//else if(!myRC.canAttackSquare(info.location))
				//	theInt|=NOTURN_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
			robots=player.enemyTurretInfos;
			for(i=player.enemyTurrets.size-1;i>=0;i--) {
				info=robots[i];
				unitHealth=(int)info.energonLevel;
				if(unitHealth<0) continue;
				theInt=TURRET_TYPE|DEFAULT_TRUE_MASK;
				theInt|=(unitHealth<<HEALTH_OFFSET);
				if(unitHealth<ONEHIT)
					theInt^=ONEHIT_AND_HEALTH_MASK;
				//if(myLoc.distanceSquaredTo(info.location)>18)
				//	theInt|=NOMOVE_MASK;
				//else if(!myRC.canAttackSquare(info.location))
				//	theInt|=NOTURN_MASK;
				if(info.roundsUntilAttackIdle>JUSTSPAWNED_THRESH)
					theInt|=JUSTSPAWNED_MASK;
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
				//if(myLoc.distanceSquaredTo(info.location)>18)
				//	theInt|=NOMOVE_MASK;
				//else if(!myRC.canAttackSquare(info.location))
				//	theInt|=NOTURN_MASK;
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
				//if(myLoc.distanceSquaredTo(info.location)>18)
				//	theInt|=NOMOVE_MASK;
				//else if(!myRC.canAttackSquare(info.location))
				//	theInt|=NOTURN_MASK;
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
				//if(myLoc.distanceSquaredTo(info.location)>18)
				//	theInt|=NOMOVE_MASK;
				//else if(!myRC.canAttackSquare(info.location))
				//	theInt|=NOTURN_MASK;
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
				//if(myLoc.distanceSquaredTo(info.location)>18)
				//	theInt|=NOMOVE_MASK;
				//else if(!myRC.canAttackSquare(info.location))
				//	theInt|=NOTURN_MASK;
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
				//if(myLoc.distanceSquaredTo(info.location)>18)
				//	theInt|=NOMOVE_MASK;
				//else if(!myRC.canAttackSquare(info.location))
				//	theInt|=NOTURN_MASK;
				if(theInt<best) {
					best=theInt;
					tmpTarget=info.location;
				}
			}
		}
		if(best<NEVER_MASK) {
			target=tmpTarget;
			targetInt=best;
			//return ATTACK_SHOOT;
			if(myLoc.distanceSquaredTo(tmpTarget)<=18) {
				return ATTACK_TURN;
			}
			else if(target!=null)
				return ATTACK_MOVE;
			else
				return NEVER;
			/*
			if((!player.attacking)&&myRC.canAttackSquare(tmpTarget)) {
				return ATTACK_SHOOT;
			}
			else if(myLoc.distanceSquaredTo(tmpTarget)<=9)
				return ATTACK_TURN;
			else
				return ATTACK_MOVE;
			*/
		}
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		if(target==null) {
			return;
		}
		try {
			if((!player.attacking)&&myRC.canAttackSquare(target)) {
				MapLocation farther = target.add(player.myLoc.directionTo(target));
				if(myRC.canAttackSquare(farther)) target=farther;
				if((targetInt&AIRBORNE_MASK)==0)
					myRC.attackAir(target);
				else
					myRC.attackGround(target);
			}
			else {
				Direction d;
				int dist=player.myLoc.distanceSquaredTo(target);
				//myRC.setIndicatorString(2,myLoc+" "+target);
				if(dist>18&&myRC.getRoundsUntilAttackIdle()<=2&&player.isThereAnArchonNear(target,dist+5)) {
						player.myNav.moveToForward(target);
				}
				else if((d=player.myLoc.directionTo(target))!=myRC.getDirection())
					myRC.setDirection(d);
				else if(myRC.canMove(d)&&dist<=25&&(targetInt&TYPE_MASK)==TURRET_TYPE)
					myRC.moveForward();
				/*
				else if(myRC.canMove(d)&&!myRC.canMove(d.opposite())) {
					MapLocation newLoc = player.myLoc.add(d);
					int i;
					RobotInfo [] infos = player.enemySoldierInfos;
					for(i=player.enemySoldiers.size-1;i>=0;i--) {
						if(newLoc.distanceSquaredTo(infos[i].location)<=2)
							return;
					}
					myRC.moveForward();
				}
				*/
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}
}