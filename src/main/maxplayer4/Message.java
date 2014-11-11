package maxplayer4;

import battlecode.common.*;

public class Message{
	static int bw = 4;//bandwidth for each channel
	static int combatChannelStart = 6;
	static int combatChannels = 10;//how many combat locations to store
	static int channelPersistence = 50;//a message this old is considered out of date
	
	static int mapstart = 100;
	
	static void postEnemyLoc(RobotController rc, MapLocation enemyLoc,int numberOfEnemies,int channel) throws GameActionException{
		int site = combatChannelStart+channel*bw;
		int lastEnemyTime = rc.readBroadcast(site);
		if(Clock.getRoundNum()>lastEnemyTime){//more recent --> overwrite
			writeToSite(rc, enemyLoc, numberOfEnemies, site);
			return;
		}else if(Clock.getRoundNum()==lastEnemyTime){//equally recent --> check if you see more enemies
			//is it in the same place as before?
			int dist = enemyLoc.distanceSquaredTo(readCombatLocation(rc,channel));
			if(dist<49){
				//can this robot see more enemies?
				int lastEnemyCount = rc.readBroadcast(site+3);
				if(lastEnemyCount<numberOfEnemies){
					//then overwrite the previous broadcast
					writeToSite(rc, enemyLoc, numberOfEnemies, site);
					return;
				}
			}
		}
		//if you still haven't posted, try the next channel
		if(channel<combatChannels){
			postEnemyLoc(rc,enemyLoc,numberOfEnemies,channel+1);
		}
	}
	
	private static void writeToSite(RobotController rc, MapLocation enemyLoc,int numberOfEnemies,int site) throws GameActionException {
		rc.broadcast(site,Clock.getRoundNum());
		writeLoc(rc,enemyLoc,site+1);
		rc.broadcast(site+3,numberOfEnemies);
	}
	
	public static void writeLoc(RobotController rc, MapLocation loc, int site) throws GameActionException{
		rc.broadcast(site,loc.x);
		rc.broadcast(site+1,loc.y);
	}

	static MapLocation readCombatLocation(RobotController rc, int channel) throws GameActionException{
		int x = rc.readBroadcast(combatChannelStart+1+channel*bw);
		int y = rc.readBroadcast(combatChannelStart+2+channel*bw);
		return new MapLocation(x,y);
	}
	
	static MapLocation readLocation(RobotController rc, int site) throws GameActionException{
		int x = rc.readBroadcast(site);
		int y = rc.readBroadcast(site+1);
		return new MapLocation(x,y);
	}

	public static void furbyCombatFraction(RobotController rc, double furbyCombatFraction) throws GameActionException {//HQ tells which furbies should fight
		rc.broadcast(1, (int) (((double)rc.getRobotTypeCount(RobotType.FURBY))*furbyCombatFraction));
		rc.broadcast(2,0);
	}
	
	static int furbyGetID(RobotController rc) throws GameActionException {
		int priorFurbies = rc.readBroadcast(2);
		int myID = priorFurbies;
		rc.broadcast(2, myID+1);
		return myID;
	}
	
	public static MapLocation readEnemy(RobotController rc) throws GameActionException{
		int site;
		int now = Clock.getRoundNum();
		MapLocation enemyLoc = null;
		for(int channel=0;channel<combatChannels;channel++){
			site = combatChannelStart+channel*bw;
			int lastEnemyTime = rc.readBroadcast(site);
			if(lastEnemyTime>(now-channelPersistence)){//TODO look for closest combat area loc?
				enemyLoc = Message.readCombatLocation(rc, channel);
				break;
			}
		}
		return enemyLoc;
	}

	public static void furbyReceiveCombat(RobotController rc, int myID)  throws GameActionException {
		MapLocation enemyLoc = readEnemy(rc);
		if(myID<rc.readBroadcast(1)){
			if(enemyLoc!=null){
				Direction toEnemy = rc.getLocation().directionTo(enemyLoc);
				Pathfinding.goTo(rc, toEnemy);
			}
		}
	}

	public static void initCombatList(RobotController rc) throws GameActionException {
		for(int channel=0;channel<combatChannels;channel++){
			int site = combatChannelStart+channel*bw;
			rc.broadcast(site, channelPersistence*-1);//prevents robots from seeing 0 0 0 as a robot location seen at time 0
		}
		//tell others which tech to go for
	}
	
	public static int arrayIndex(int width, int height, int i, int j,int startsite){
		return startsite+i+j*width;
	}
	static void broadcastInternalRepresentation(RobotController rc,MapLocation[] corners,int[][] internalMap) throws GameActionException{
		Message.writeLoc(rc, corners[0], mapstart);
		Message.writeLoc(rc, corners[1], mapstart+2);
		Message.writeLoc(rc, corners[2], mapstart+4);
		int width = corners[2].x;
		int height = corners[2].y;
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				rc.broadcast(Message.arrayIndex(width, height, i, j,mapstart+6),internalMap[i][j]);
			}
		}
		rc.broadcast(99, 1);//done broadcasting
	}
	static Direction readPostedDirection(RobotController rc) throws GameActionException{
		MapLocation zeropoint = readLocation(rc,mapstart);
		MapLocation widthheight = readLocation(rc,mapstart+4);
		MapLocation myLoc = rc.getLocation();
		MapLocation myLoca = new MapLocation(myLoc.x-zeropoint.x,myLoc.y-zeropoint.y);
		int ord = rc.readBroadcast(arrayIndex(widthheight.x,widthheight.y,myLoca.x,myLoca.y,mapstart+6));
		return Direction.values()[ord];
	}
}