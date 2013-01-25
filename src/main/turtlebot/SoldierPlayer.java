package turtlebot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class SoldierPlayer extends BasePlayer {
	enum State {
		CAP, FIGHT, DEFEND, RETURN, HEAL, ATTACK;
	}
	MapLocation capTarget;
	State state;
	int turnsNoEnemy;
	boolean attackSignal;
	public SoldierPlayer(RobotController rc) throws GameActionException {
		super(rc);
		
		capTarget = msg.readLoc(1);
		if(capTarget!=null && capTarget.y==100) capTarget=null;
		if(capTarget==null) state = State.DEFEND;
		else state = State.CAP;
		
		turnsNoEnemy = 0;
		attackSignal = false;
	}
	public void run() throws GameActionException {
		if(!rc.isActive()) 
			return;
		
		if(!attackSignal && msg.read(13)==1) attackSignal = true;
		RobotInfo nearestEnemy = Util.nearestEnemy(rc, 999);
		if(nearestEnemy==null) turnsNoEnemy++;
		else turnsNoEnemy = 0;
		int nearestEnemyDist = nearestEnemy==null?9999:nearestEnemy.location.distanceSquaredTo(curLoc);
		MapLocation HQNearestEnemyLoc = msg.readLoc(7);
		if(HQNearestEnemyLoc!=null && HQNearestEnemyLoc.x==0 && HQNearestEnemyLoc.y==100) HQNearestEnemyLoc = null;
		int HQNearestEnemyDist = HQNearestEnemyLoc==null?9999:HQNearestEnemyLoc.distanceSquaredTo(HQLoc);
		MapLocation medbayLoc = msg.readLoc(11);
		if(medbayLoc!=null && medbayLoc.x==0 && medbayLoc.y==100) HQNearestEnemyLoc = null;
		if(medbayLoc!=null) {
			if(!rc.canSenseSquare(medbayLoc)) medbayLoc = null;
			else {
				GameObject go = rc.senseObjectAtLocation(medbayLoc);
				if(go==null) medbayLoc = null;
				else {
					RobotInfo ri = rc.senseRobotInfo((Robot)go);
					if(ri.team!=myTeam || ri.type!=RobotType.MEDBAY)
						medbayLoc = null;
				}
			}
		}
		
		if(medbayLoc!=null && curHP<=12) state = State.HEAL;
		else if(nearestEnemyDist<=2) state = State.FIGHT;
		else if(state==State.HEAL && curHP<maxHP) state = State.HEAL;
		else if(attackSignal) state = State.ATTACK;
		else if(state==State.CAP) state = State.CAP;
		else if(medbayLoc!=null && curHP<=24) state = State.HEAL;
		else if(HQNearestEnemyDist<64 && curLoc.distanceSquaredTo(HQLoc)<81) state = State.DEFEND;
		else if(medbayLoc!=null && curHP<maxHP) state = State.HEAL;
		else state = State.RETURN;
		
		rc.setIndicatorString(1, "State: "+state);
		
		// Capture encampment
		if(state==State.CAP && rc.senseEncampmentSquare(rc.getLocation()) && 
				rc.getTeamPower() >= rc.senseCaptureCost()+rc.senseNearbyGameObjects(Robot.class, 9999, myTeam).length*2) {
			RobotType type = RobotType.ARTILLERY;
			if(rc.senseCaptureCost()<=GameConstants.CAPTURE_POWER_COST)
				type = RobotType.MEDBAY;
			rc.captureEncampment(type);
			return;
		}
		
		// Fight
		if(state==State.FIGHT) {
			standStill();
			return;
		}
				
		// Mine
		if(state==State.RETURN && turnsNoEnemy>10 && Util.randDouble()<0.1 && rc.senseMine(curLoc)==null) {
			rc.layMine();
			return;
		}
		
		MapLocation target = null;
		if(state==State.CAP) {
			target = capTarget;
		} else if(state==State.HEAL) {
			target = medbayLoc;
		} else if(state==State.RETURN) {
			target = HQLoc.add(HQLoc.directionTo(enemyHQLoc), 2).
					add(Direction.values()[myID%8], 2).
					add(Direction.values()[(int)(Util.randDouble()*8)]);
		} else if(state==State.ATTACK) {
			target = enemyHQLoc;
		}
		
		Direction dir = null;
		if(target!=null) {
			dir = rc.getLocation().directionTo(target);
		} else {
			// Compute relative power
			double sum = curHP;
			double sumToAttack = 0;
			MapLocation enemyLoc = nearestEnemy!=null ? nearestEnemy.location : HQNearestEnemyLoc;
			for(Robot r: rc.senseNearbyGameObjects(Robot.class, enemyLoc, 21, myTeam)) {
				RobotInfo ri = rc.senseRobotInfo(r);
				if(ri.type!=RobotType.SOLDIER || ri.roundsUntilMovementIdle>1 || ri.energon<=24)
					continue;
				sum+=ri.energon;
			}
			for(Robot r: rc.senseNearbyGameObjects(Robot.class, curLoc, 21, enemyTeam)) {
				RobotInfo ri = rc.senseRobotInfo(r);
				if(ri.type!=RobotType.SOLDIER || ri.roundsUntilMovementIdle>1)
					continue;
				sum-=ri.energon;
			}
			rc.setIndicatorString(2, sum+"");
			
			if(sum>=sumToAttack) {
				for(int i=0; i<8; i++) {
					MapLocation loc = rc.getLocation().add(Direction.values()[i]);
					GameObject go = rc.senseObjectAtLocation(loc);
					if(go!=null && go.getTeam()!=rc.getTeam())
						return;
				}
			}
			if(sum>=sumToAttack) {
				dir = rc.getLocation().directionTo(enemyLoc);
			} else {
				dir = rc.getLocation().directionTo(enemyLoc).opposite();
			}
		}
		
		// If at target, end turn
		if(dir==null || dir==Direction.NONE || dir==Direction.OMNI) {
			standStill();
			return;
		}
		
		// Wiggle
		Direction toDefuse = null;
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
				} else if(toDefuse==null) {
					toDefuse = wdir;
				}
			}
		}

		// If there is an enemy mine in the way, defuse it
		if(toDefuse!=null) {
			rc.defuseMine(rc.getLocation().add(toDefuse));
			return;
		}
		
		// If blocked, end turn
		if(!rc.canMove(dir)) {
			standStill();
			return;
		}
		
		// Move in computed direction
		if(dir.ordinal()<8)
			rc.move(dir);
		
	}
	void standStill() throws GameActionException {
		Team mine = rc.senseMine(curLoc);
		if(mine!=null && mine!=myTeam) {
			Direction dir = Direction.values()[(int)(Util.randDouble()*8)];
			for(int i=0; i<8; i++) {
				dir = dir.rotateLeft();
				if(rc.canMove(dir)) {
					Team team = rc.senseMine(curLoc.add(dir));
					if(team==null || team==myTeam) {
						rc.move(dir);
						break;
					}
				}
			}
		}
			
	}
}
