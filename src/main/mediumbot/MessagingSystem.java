package mediumbot;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class MessagingSystem {
	RobotController rc;
	int teamShift;
	public MessagingSystem(RobotController rc) {
		this.rc = rc;
		this.teamShift = (rc.getTeam()==Team.A)?1977:5261;
	}
	
	public int read(int key) throws GameActionException {
		int ch = key%GameConstants.BROADCAST_MAX_CHANNELS;
		for(int i=0; i<3; i++) {
			ch = (ch*3211+teamShift)%GameConstants.BROADCAST_MAX_CHANNELS;
			int n = rc.readBroadcast(ch);
			int val = n<<8>>>8;
			if(n>>>24==hash(key, val)) {
				System.out.println((n>>>24)+" hoho "+val);
				return val;
			}
		}
		return -1;
	}
	
	public MapLocation readLoc(int key) throws GameActionException {
		int val = read(key);
		if(val==-1) return null;
		System.out.println(val);
		return new MapLocation(val/256, val%256);
	}
	
	/** value must be 0 to 2^24-1 */
	public void write(int key, int value) throws GameActionException {
		int ch = key%GameConstants.BROADCAST_MAX_CHANNELS;
		int data = (hash(key, value)<<24)+value;
		for(int i=0; i<3; i++) {
			ch = (ch*3211+teamShift)%GameConstants.BROADCAST_MAX_CHANNELS;
			rc.broadcast(ch, data);
		}
	}
	
	public void write(int key, MapLocation loc) throws GameActionException {
		write(key, loc.x*256+loc.y);
	}
	
	private int hash(int key, int n) {
		return (key%16249*32141+n*1249+11317)%256;
	}
}
