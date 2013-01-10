package mediumbot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public abstract class BasePlayer {
	RobotController rc;
	RobotType myType;
	Team myTeam;
	Team neutralTeam;
	Team enemyTeam;
	int width;
	int height;
	MapLocation curLoc;
	double curHP;
	double curShields;
	
	public BasePlayer(RobotController rc) {
		this.rc = rc;
		this.myType = rc.getType();
		this.myTeam = rc.getTeam();
		this.neutralTeam = Team.NEUTRAL;
		this.enemyTeam = myTeam.opponent();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
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
		curShields = rc.getShields();
	}
}
