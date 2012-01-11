package basicplayer;

import basicplayer.goal.*;

import basicplayer.message.MessageSender;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

public class ArchonPlayer extends BasePlayer {
	
	public static final double MIN_FLUX = 2.*RobotType.ARCHON.moveCost; 

	public static final double SUPPORT_RATIO = .7;

	public static RobotLevel wantToSpawn;

	public ArchonPlayer(RobotController RC) {
		super(RC);
	}

	public void setGoals() {
	
		goals = new Goal [] {
			new FleeGoal(),
			new MakeArmyGoal(),
			new GetHelpGoal(),
			//new StayTogetherGoal(),
			new ArchonFindEnemyGoal(),
			new ArchonExploreGoal(),
		};

	}

	public void transferFlux() {
		int i;
		RobotInfo r;
		double transferAmount;
		double freeFlux=myRC.getFlux()-MIN_FLUX;
		if(enemies.size<0&&ArchonExploreGoal.target!=null&&myLoc.distanceSquaredTo(ArchonExploreGoal.target)<=2)
			freeFlux-=RobotType.TOWER.spawnCost;
		//System.out.println("Flux: "+freeFlux);
		//System.out.println("Allies: "+allies.size);
		if(freeFlux<=0)
			return;
		try {
			for(i=allies.size;i>=0;i--) {
				r=alliedInfos[i];
				if(r.type!=RobotType.ARCHON&&myLoc.isAdjacentTo(r.location)) {
					transferAmount=Math.min(r.type.maxFlux-r.flux,freeFlux);
					if(transferAmount>0) {
						myRC.transferFlux(r.location,r.type.level,transferAmount);
						freeFlux-=transferAmount;
						if(freeFlux<=0) return;
					}
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void sendEnemies() {
		int i = enemies.size+1, j = enemies.size-enemyTowers.size+1;
		MapLocation [] locs = new MapLocation [j];
		while(--i>=0) {
			if(enemyInfos[i].type!=RobotType.TOWER)
				locs[--j]=enemyInfos[i].location;
		}
		mySender.sendEnemyUnits(locs);
	}

	public void broadcast() {
		/*
		if(enemies.size>=0) {
			RobotInfo enemyInfo = closestNoTower(enemies,base);
			if(enemyInfo!=null)
				mySender.sendFindEnemy(enemyInfo.location,enemies.size+1);
		}
		*/
		if(enemies.size>enemyTowers.size)
			sendEnemies();
		else if(ArchonExploreGoal.target!=null) {
			mySender.sendExplore(ArchonExploreGoal.target);
		}
	}

	public void senseNearbyRobots() {
		archons = myRC.senseAlliedArchons();
		super.senseNearbyRobots();
	}

}
