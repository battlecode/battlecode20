package refplayer;

import refplayer.message.MessageSender;

import battlecode.common.AuraType;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.RobotLevel;

public class AuraPlayer extends BasePlayer {

	static public final double MIN_FLUX = 20.*battlecode.common.GameConstants.ENERGON_TO_FLUX_CONVERSION;

	static public final int UPDATE_AURA_EVERY = 20;

	public AuraPlayer(RobotController RC) {
		super(RC);
	}

	public void chooseAura() {
		int i;
		int numSmaller=0;
		for(i=alliedAuras.size-1;i>=0;i--) {
			if(alliedAuraRobots[i].getID()<myID) numSmaller++;
		}
		if(numSmaller<=alliedAuras.size/2)
			myAura=AuraType.OFF;
		else
			myAura=AuraType.DEF;
		debug_setIndicatorStringObject(0,myAura);
	}

	AuraType myAura;

	public void run() {
		senseNearbyRobots();
		chooseAura();
		myRC.yield();
		while(true) {
			try {
				senseNearbyRobots();
				checkForEnemy();
				Message [] messages = myRC.getAllMessages();
				if(battlecode.common.Clock.getRoundNum()%UPDATE_AURA_EVERY==0)
					chooseAura();
				war:
				{
					peace:
					if(enemyArchons.size+enemyWouts.size+enemyChainers.size+
					   enemySoldiers.size+enemyTurrets.size==0) {
						int i;
						int [] ints;
						for(i=messages.length-1;i>=0;i--) {
							ints = messages[i].ints;
							if(ints!=null&&
							   ints.length>0&&
							   ints[0]==MessageSender.messageTypeEnemyUnits) {
								break peace;
							}
						}
						//transferFlux();
						break war;
					}
					double fluxNeeded = MIN_FLUX+myAura.fluxCost();
					if(myRC.getLastAura()!=myAura)
						fluxNeeded += myAura.switchCost();
					if(myRC.getFlux()>=fluxNeeded)
						myRC.setAura(AuraType.OFF);
				}
			} catch(Exception e) {
				BasePlayer.debug_stackTrace(e);
			}
			myRC.yield();
		}
	}

	/*
	public void transferFlux() {
		if(myRC.getFlux()==0) return;
		try {
			int i;
			int min=99999, mini=-1;
			for(i=alliedArchons.size-1;i>=0;i--) {
				if(alliedArchonInfos[i].location.distanceSquaredTo(myLoc)<=2&&alliedArchonRobots[i].getID()<min) {
					min=alliedArchonRobots[i].getID();
					mini=i;
				}
			}
			if(mini>=0) {
				myRC.transferFlux(myRC.getFlux(),alliedArchonInfos[i].location,RobotLevel.IN_AIR);
				return;
			}
			for(i=alliedWouts.size-1;i>=0;i--) {
				if(alliedWoutInfos[i].location.distanceSquaredTo(myLoc)<=2) {
					myRC.transferFlux(myRC.getFlux(),alliedWoutInfos[i].location,RobotLevel.ON_GROUND);	
				}
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}
	*/
}