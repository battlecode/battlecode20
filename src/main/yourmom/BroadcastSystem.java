package yourmom;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * BroadcastSystem for keeping track of BroadcastChannels. Robots
 * can query this system for getting instances of BroadcastChannels
 * to write and read.
 */
public class BroadcastSystem {
	BaseRobot robot;
	RobotController rc;
	public static byte signature = 0x1;
	
	/**
	 * Initializes BroadcastSystem by setting rc
	 * @param myRobot
	 */
	public BroadcastSystem(BaseRobot myRobot) {
		robot = myRobot;
		rc = robot.rc;
	}
	
	/**
	 * Reads a message on channelType. Checks if signature is correct.
	 * @param channelType
	 * @return
	 */
	public Message read(ChannelType channelType) {
		try {
			if (rc != null) {
				for (int channelNo : getChannelNos(channelType)) {
					int rawMessage = rc.readBroadcast(channelNo);
					if (rawMessage == 0) {
						return new Message(false, true);
					}
					return new Message(rawMessage, true, false);
				}
			}
			return new Message(false, false);
		} catch (Exception e) {
			return new Message(false, false);
		}
	}
	
	/**
	 * Writes a message to channelType.
	 * WARNING: Only can use 24 low-order bits from the body
	 * @param channelType
	 * @param header
	 * @param body
	 */
	public void write(ChannelType channelType, int body) {
		int result = (signature << 31) | body;
		try {
			for (int channelNo : getChannelNos(channelType)) {
				rc.broadcast(channelNo, result);
			}
		} catch (Exception e) {
//				  e.printStackTrace();
		}
	}
	
	/**
	 * Use hashing of the current time and channelType to calculate what channels to use
	 * @param channelType
	 * @return channelNos
	 */
	public int[] getChannelNos(ChannelType channelType) {
		int round = Clock.getRoundNum();
		int round_cycle = round / Constants.CHANNEL_CYCLE;		  
		return getChannelNos(channelType, round_cycle);
	}
	
	/**
	 * Use hashing of the current time and channelType to calculate what channels to use (from the last cycle)
	 * @param channelType
	 * @return
	 */
	public int[] getChannelNosLastCycle(ChannelType channelType) {
		int round = Clock.getRoundNum();
		int round_cycle = round / Constants.CHANNEL_CYCLE - 1;		      
		return getChannelNos(channelType, round_cycle);
	}
	
	public Message readLastCycle(ChannelType channelType) {
		try {
			if (rc != null) {
				for (int channelNo : getChannelNosLastCycle(channelType)) {
					int rawMessage = rc.readBroadcast(channelNo);
					if (rawMessage == 0) {
						return new Message(false, true);
					}
					return new Message(rawMessage, true, false);
				}
			}
			return new Message(false, false);
		} catch (Exception e) {
			return new Message(false, false);
		}
	}
	
	/**
	 * Writes constant.MAX_MESSAGE into the channel
	 * @param channelType
	 */
	public void writeMaxMessage(ChannelType channelType) {
		write(channelType, Constants.MAX_MESSAGE);
	}
	
	public int[] getChannelNos(ChannelType channelType, int constant) {
		int[] channelNos = new int[Constants.REDUNDANT_CHANNELS];
		int rangeStart = channelType.ordinal() * ChannelType.range;
		constant += 1;
		for (int i = 0; i < Constants.REDUNDANT_CHANNELS; i++) {
			int offset = ((Integer.toString(((constant << 4 + 17 * channelType.ordinal()) << 4 + i)).hashCode())+rc.getTeam().ordinal()) % ChannelType.range;
			// ensure that the offset is nonnegative
			if (offset < 0) {
				offset += ChannelType.range;
			}
			channelNos[i] = rangeStart + offset;
		}
		return channelNos;
	}

	public void broadcastNoisetowerLocations(MapLocation[] locs) throws GameActionException {
		switch (locs.length) {
		default:
		case 3:
			rc.broadcast(
				ChannelType.NOISETOWER3.ordinal(),
				BroadcastSystem.locationToInt(locs[2])
			);
		case 2:
			rc.broadcast(
				ChannelType.NOISETOWER2.ordinal(),
				BroadcastSystem.locationToInt(locs[1])
			);
		case 1:
			rc.broadcast(
				ChannelType.NOISETOWER1.ordinal(),
				BroadcastSystem.locationToInt(locs[0])
			);
		case 0:
			break;
		}
	}

	public void broadcastPastrLocations(MapLocation[] locs) throws GameActionException {
		switch (locs.length) {
		default:
		case 3:
			rc.broadcast(
				ChannelType.PASTR3.ordinal(),
				BroadcastSystem.locationToInt(locs[2])
			);
		case 2:
			rc.broadcast(
				ChannelType.PASTR2.ordinal(),
				BroadcastSystem.locationToInt(locs[1])
			);
		case 1:
			rc.broadcast(
				ChannelType.PASTR1.ordinal(),
				BroadcastSystem.locationToInt(locs[0])
			);
		case 0:
			break;
		}
	}

	static int locationToInt(MapLocation loc) {
		return (loc.x << 8) | loc.y;
	}

	static MapLocation intToLocation(int l) {
		return new MapLocation(l >> 8, l & 0xFF);
	}
}
