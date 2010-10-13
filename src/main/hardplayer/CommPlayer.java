package hardplayer;

import hardplayer.goals.ChainerAttackGoal;
import hardplayer.goals.ChainerTurretExploreGoal;
import hardplayer.goals.FindEnemyGoal;
import hardplayer.goals.Goal;
import hardplayer.goals.SeekEnergonGoal;

import static hardplayer.goals.BroadcastEnemyUnitLocationsGoal.*;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import battlecode.common.Team;

import java.util.Arrays;

public class CommPlayer extends BasePlayer {

	static public final int [] unitTypeMasks = new int [] { ARCHON_TYPE, WOUT_TYPE, CHAINER_TYPE, SOLDIER_TYPE, TURRET_TYPE, WOUT_TYPE, WOUT_TYPE, WOUT_TYPE };

	public CommPlayer(RobotController RC) {
		super(RC);
	}

	int [] ints;
	MapLocation [] locs;

	public void run() {
		myLoc=myRC.getLocation();
		locs = new MapLocation [400];
		locs[0] = myLoc;
		ints = new int [402];
		Team myTeam = this.myTeam;
		int unitHealth;
		RobotInfo info;
		Robot [] robots;
		int i;
		int nRobots=0;
		boolean turnChanged=false;
		debug_setIndicatorStringObject(1,myLoc);
		while(true) {
			myRC.getAllMessages();
			try {
				turnChanged=false;
				robots = myRC.senseNearbyAirRobots();
				for(i=robots.length-1;i>=0;i--) {
					info = myRC.senseRobotInfo(robots[i]);
					if(info.team==myTeam) continue;
					unitHealth=((int)info.energonLevel)+2;
					if(unitHealth<0)
						ints[++nRobots]=(robots[i].getID()&ROBOT_ID_MASK|NEVER_MASK|ARCHON_TYPE|MESSAGE_DEFAULT_MASK);
					else
						ints[++nRobots]=(robots[i].getID()&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|ARCHON_TYPE|MESSAGE_DEFAULT_MASK;
					locs[nRobots]=info.location;
					if(Clock.getBytecodeNum()>=4000) {
						broadcast(nRobots);
						nRobots=0;
						turnChanged=true;
						myRC.yield();
					}
				}
				robots = myRC.senseNearbyGroundRobots();
				for(i=robots.length-1;i>=0;i--) {
					info = myRC.senseRobotInfo(robots[i]);
					if(info.team==myTeam) continue;
					if(info.type.isBuilding()) continue;
					unitHealth=((int)info.energonLevel);
					if(unitHealth<0)
						// don't bother figuring out the type of the robot
						ints[++nRobots]=(robots[i].getID()&ROBOT_ID_MASK)|NEVER_MASK|WOUT_TYPE|MESSAGE_DEFAULT_MASK;
					else
						ints[++nRobots]=(robots[i].getID()&ROBOT_ID_MASK)|(unitHealth<<HEALTH_OFFSET)|unitTypeMasks[info.type.ordinal()]|MESSAGE_DEFAULT_MASK;
					locs[nRobots]=info.location;
					if(Clock.getBytecodeNum()>=4000) {
						broadcast(nRobots);
						nRobots=0;
						turnChanged=true;
						myRC.yield();
					}
				}
				if(!turnChanged) {
					broadcast(nRobots);
					nRobots=0;
					myRC.yield();
				}
			} catch(Exception e) {
				debug_stackTrace(e);
				nRobots=0;
				myRC.yield();
			}
		}
	}
	
	public void broadcast(int size) {
		myRC.setIndicatorString(2,"locs[0]="+locs[0]);
		// TODO: make comm send a separate message so FindEnemyGoal
		// knows not to check
		locs[size+1]=myLoc;
		if(size>0) {
			mySender.updateRoundNum();
			mySender.sendEnemyUnits(Arrays.copyOf(ints,size+3),Arrays.copyOf(locs,size+2));
		}
	}

}