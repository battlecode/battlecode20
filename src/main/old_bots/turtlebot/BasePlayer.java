package turtlebot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public abstract class BasePlayer {
	RobotController rc;
	RobotType myType;
	int myID;
	Team myTeam;
	Team neutralTeam;
	Team enemyTeam;
	int width;
	int height;
	MapLocation curLoc;
	double curHP;
	double curEnergon;
	double curShields;
	double maxHP;
	double maxEnergon;
	MapLocation HQLoc;
	MapLocation enemyHQLoc;
	MessagingSystem msg;
	
	public BasePlayer(RobotController rc) {
		this.rc = rc;
		this.myType = rc.getType();
		this.maxEnergon = myType.maxEnergon;
		this.maxHP = maxEnergon;
		this.myID = rc.getRobot().getID();
		this.myTeam = rc.getTeam();
		this.neutralTeam = Team.NEUTRAL;
		this.enemyTeam = myTeam.opponent();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		HQLoc = rc.senseHQLocation();
		enemyHQLoc = rc.senseEnemyHQLocation();
		
		msg = new MessagingSystem(rc);
		
		updateCurVariables();
	}
	
	public abstract void run() throws GameActionException;
	
	public void loop() {
		while(true) {
			try {
				rc.setIndicatorString(0, "Delay: "+rc.roundsUntilActive());
				
				// Update variables
				updateCurVariables();
				
				// Execute turn
				run();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// End turn
			rc.yield();
		}
	}
	
	void updateCurVariables() {
		curLoc = rc.getLocation();
		curHP = rc.getEnergon();
		curEnergon = curHP;
		curShields = rc.getShields();
	}
}
