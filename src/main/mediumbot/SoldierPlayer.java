package mediumbot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class SoldierPlayer extends BasePlayer {
	MapLocation origTarget;
	public SoldierPlayer(RobotController rc) throws GameActionException {
		super(rc);
		
		origTarget = msg.readLoc(1);
		if(origTarget!=null && origTarget.y==100) origTarget=null;
	}
	public void run() throws GameActionException {
		
		if(!rc.isActive()) 
			return;
		
		MapLocation target = origTarget;
		if(target==null) {
			if(Clock.getRoundNum()<350) target = HQLoc.add(Direction.values()[myID%8], 3);
			else target = enemyHQLoc;
		}
		rc.setIndicatorString(1, "target: "+(target.x-rc.getLocation().x)+","+(target.y-rc.getLocation().y));
		
		
		// If on encampment, capture it
		if(rc.senseEncampmentSquare(rc.getLocation()) && rc.getTeamPower() >= rc.senseCaptureCost()) {
			if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation())-50>=rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation())) {
				rc.captureEncampment(RobotType.ARTILLERY);
			} else {
				int x = msg.read(5);
				rc.captureEncampment(x%3==0?RobotType.GENERATOR:RobotType.SUPPLIER);
				msg.write(5, x+1);
			}
			return;
		}
		
		// Compute relative power
		double sum = curHP;
		double sumToAttack = 0;
		RobotInfo enemy = Util.nearestEnemy(rc, 18);
		if(enemy!=null) {
			for(Robot r: rc.senseNearbyGameObjects(Robot.class, enemy.location, 18, myTeam)) {
				RobotInfo ri = rc.senseRobotInfo(r);
				if(ri.type!=RobotType.SOLDIER || ri.roundsUntilMovementIdle>1)
					continue;
				sum+=ri.energon;
			}
			for(Robot r: rc.senseNearbyGameObjects(Robot.class, curLoc, 18, enemyTeam)) {
				RobotInfo ri = rc.senseRobotInfo(r);
				if(ri.type!=RobotType.SOLDIER || ri.roundsUntilMovementIdle>1)
					continue;
				sum-=ri.energon;
			}
			rc.setIndicatorString(2, sum+"");
		}
		
		if(sum>=sumToAttack) {
			for(int i=0; i<8; i++) {
				MapLocation loc = rc.getLocation().add(Direction.values()[i]);
				GameObject go = rc.senseObjectAtLocation(loc);
				if(go!=null && go.getTeam()!=rc.getTeam())
					return;
			}
		}
		
		Direction dir = null;
		if(enemy==null) {
			dir = rc.getLocation().directionTo(target);
			
		} else if(sum>=sumToAttack) {
			dir = rc.getLocation().directionTo(enemy.location);
			
		} else {
			dir = rc.getLocation().directionTo(enemy.location).opposite();
			
		}
		
		// Wiggle
		int[] wiggle = new int[] {0, -1, 1};
		if(Math.random()<0.5) 
			for(int i=0; i<wiggle.length; i++) 
				wiggle[i]*=-1;
		for(int d: wiggle) {
			Direction wdir = Direction.values()[(dir.ordinal()+d+8)%8];
			if(rc.canMove(wdir)) {
				Team mine = rc.senseMine(curLoc.add(wdir));
				if(mine==null || mine==myTeam) {
					dir = wdir;
					break;
				}
			}
		}
		
		// If blocked, end turn
		if(dir.ordinal()<8 && !rc.canMove(dir))
			return;
		
		// If there is an enemy mine in the way, defuse it
		Team mineTeam = rc.senseMine(rc.getLocation().add(dir));
		if(mineTeam!=null && mineTeam!=rc.getTeam()) {
			rc.defuseMine(rc.getLocation().add(dir));
			return;
		}
		
		// Move in computed direction
		if(dir.ordinal()<8)
			rc.move(dir);
		
	}
}
