package hardplayer.goals;

import hardplayer.message.MessageHandler;
import hardplayer.message.MessageSender;
import hardplayer.message.SuperMessageStack;
import hardplayer.navigation.BugNavigation;
import hardplayer.WoutPlayer;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;

public class WoutFindEnemyGoal extends FindEnemyGoal {

	// We want scouts to go looking for the enemy if we've seen them
	// recently but lost them.

	// Use coordinates rather than displacement for telling about far away units

	protected double absxsum;
	protected double absysum;
	
	WoutPlayer splayer;

	public WoutFindEnemyGoal(WoutPlayer sp) {
		super(sp);
		splayer=sp;
		// Don't go way out in front of army and get shot
		sp.handlers[MessageSender.messageTypeScoutSawEnemy]=null;
	}

	public void decay() {
		n*=DECAY_RATE;
		xsum*=DECAY_RATE;
		ysum*=DECAY_RATE;
		absxsum*=DECAY_RATE;
		absysum*=DECAY_RATE;
	}

	public void read() {
		Message [] stack=enemyUnitMessages.messages[SuperMessageStack.t];
		MapLocation [] locs;
		MapLocation myLoc=player.myLoc, loc;
		int i, j;
		long newn=0;
		long newxsum=0;
		long newysum=0;
		long w;
		RobotInfo [] robotArray;
		computeSum: {
			// Give preference to archons.  I don't think it's worth the
			// bytecodes to check the message ones though.
			robotArray=player.enemyArchonInfos;
			for(i=player.enemyArchons.size-1;i>=0;i--) {
				loc=robotArray[i].location;
				w=6000000L/(loc.distanceSquaredTo(myLoc)+1);
				newn+=w;
				newxsum+=w*loc.getX();
				newysum+=w*loc.getY();
			}
			if(Clock.getBytecodeNum()>=3500) break computeSum;
			// Also give preference to stuff you've seen.
			robotArray=player.enemyTurretInfos;
			for(i=player.enemyTurrets.size-1;i>=0;i--) {
				loc=robotArray[i].location;
				w=2000000L/(loc.distanceSquaredTo(myLoc)+1);
				newn+=w;
				newxsum+=w*loc.getX();
				newysum+=w*loc.getY();
			}
			if(Clock.getBytecodeNum()>=3500) break computeSum;
			robotArray=player.enemyChainerInfos;
			for(i=player.enemyChainers.size-1;i>=0;i--) {
				loc=robotArray[i].location;
				w=2000000L/(loc.distanceSquaredTo(myLoc)+1);
				newn+=w;
				newxsum+=w*loc.getX();
				newysum+=w*loc.getY();

			}
			if(Clock.getBytecodeNum()>=3500) break computeSum;
			robotArray=player.enemySoldierInfos;
			for(i=player.enemySoldiers.size-1;i>=0;i--) {
				loc=robotArray[i].location;
				w=2000000L/(loc.distanceSquaredTo(myLoc)+1);
				newn+=w;
				newxsum+=w*loc.getX();
				newysum+=w*loc.getY();

			}
			if(Clock.getBytecodeNum()>=3500) break computeSum;
			// wouts can move pretty far so give them
			// a slight penalty
			robotArray=player.enemyWoutInfos;
			for(i=player.enemyWouts.size-1;i>=0;i--) {
				loc=robotArray[i].location;
				w=1500000L/(loc.distanceSquaredTo(myLoc)+1);
				newn+=w;
				newxsum+=w*loc.getX();
				newysum+=w*loc.getY();
			}
			for(j=enemyUnitMessages.lengths[SuperMessageStack.t]-1;j>=0;j--) {
				if(Clock.getBytecodeNum()>=3500) break computeSum;
				locs=stack[j].locations;
				loc=locs[locs.length-1];
				w=1000000L/(loc.distanceSquaredTo(myLoc)+1);
				newn+=w;
				newxsum+=w*loc.getX();
				newysum+=w*loc.getY();
			}
		}
		n+=newn;
		xsum+=newxsum-newn*myLoc.getX();
		ysum+=newysum-newn*myLoc.getY();
		absxsum+=newxsum;
		absysum+=newysum;

	}

	public int getPriority() {
		int t=Clock.getRoundNum();
		if(t-splayer.lastKnownFighterTime>=5&&
		   player.atWar&&
		   !splayer.scouting) {
			return FIND_ENEMY_PRIORITY;
		}
		return NEVER;
	}

}