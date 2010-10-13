package refplayer.goals;

import refplayer.message.MessageHandler;
import refplayer.message.MessageSender;
import refplayer.message.SuperMessageStack;
import refplayer.navigation.BugNavigation;
import refplayer.BasePlayer;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;

public class FindEnemyGoal extends Goal implements MessageHandler {

	protected double xsum;
	protected double ysum;
	protected double n=1.;

	static final double DECAY_RATE = 7./8.;
	//static final double THRESHOLD = .007;

	SuperMessageStack enemyUnitMessages;

	public FindEnemyGoal(BasePlayer bp) {
		super(bp);
		enemyUnitMessages=bp.enemyUnitMessages;
		bp.handlers[MessageSender.messageTypeScoutSawEnemy]=this;
	}

	public void decay() {
		n*=DECAY_RATE;
		xsum*=DECAY_RATE;
		ysum*=DECAY_RATE;
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
				for(i=locs.length-1;i>0;i--) {
					loc=locs[i];
					w=1000000L/(loc.distanceSquaredTo(myLoc)+1);
					newn+=w;
					newxsum+=w*loc.getX();
					newysum+=w*loc.getY();
				}
			}
		}
		n+=newn;
		xsum+=newxsum-newn*myLoc.getX();
		ysum+=newysum-newn*myLoc.getY();
	}

	public int getMaxPriority() {
		return FIND_ENEMY_PRIORITY;
	}

	public int getPriority() {
		return player.atWar?FIND_ENEMY_PRIORITY:NEVER;
	}

	public MapLocation getEnemyLoc() {
		return new MapLocation((int)(xsum/n)+player.myLoc.getX(),(int)(ysum/n)+player.myLoc.getY());
	}

	public Direction [] getEnemyDirs() {
		return BugNavigation.directionsToward(new MapLocation(0,0),new MapLocation((int)(xsum/n), (int)(ysum/n)+player.myLoc.getY()));
	}

	public void tryToAccomplish() {
		player.myNav.moveToForward(getEnemyLoc());
	}

	public void debug_print(MapLocation loc) {
		System.out.println("got scout mesage "+loc);
		System.out.println("I am at "+player.myLoc);
	}

	public void receivedMessage(Message m) {
		MapLocation loc = m.locations[1];
		//debug_print(loc);
		n+=20000;
		xsum+=20000*(loc.getX()-player.myLoc.getX());
		ysum+=20000*(loc.getY()-player.myLoc.getY());
		// keep findEnemyGoal going for longer because
		// the enemy is farther away
		player.lastKnownEnemyTime=battlecode.common.Clock.getRoundNum()+(int)Math.sqrt(36.*player.myLoc.distanceSquaredTo(loc));
		player.atWar=true;
	}

}