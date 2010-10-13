package hardplayer.goals;

import hardplayer.ArchonPlayer;
import hardplayer.BasePlayer;
import hardplayer.message.SuperMessageStack;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;

public class ArchonFindEnemyGoal extends FindEnemyGoal {

	public ArchonFindEnemyGoal(BasePlayer bp) {
		super(bp);
	}

	public int getPriority() {
		if(player.atWar&&
		   player.alliedSoldiers.size+
		   player.alliedTurrets.size+
		   player.alliedChainers.size>=6&&
		   myRC.getEnergonLevel()>=ArchonPlayer.MIN_ENERGON)
			return FIND_ENEMY_PRIORITY;
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		if(player.enemyArchons.size+
		   player.enemySoldiers.size+
		   player.enemyChainers.size+
		   player.enemyTurrets.size>0) {
			int i;
			MapLocation enemyLoc=getEnemyLoc(), best=null, loc;
			int d, dmin=0x7FFFFFFF;
			for(i=player.alliedTurrets.size-1;i>=0;i--) {
				loc=player.alliedTurretInfos[i].location;
				d=loc.distanceSquaredTo(enemyLoc);
				if(d<dmin) {
					dmin=d;
					best=loc;
				}
			}
			for(i=player.alliedChainers.size-1;i>=0;i--) {
				loc=player.alliedChainerInfos[i].location;
				d=loc.distanceSquaredTo(enemyLoc);
				if(d<dmin) {
					dmin=d;
					best=loc;
				}
			}
			if(best==null)
				return;
			if(dmin<=player.myLoc.distanceSquaredTo(enemyLoc))
				player.myNav.moveToForward(best);
			else
				player.myNav.moveToBackward(best);
		}
		else
			super.tryToAccomplish();

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
				for(i=locs.length-2;i>=0;i--) {
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

}