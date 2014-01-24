package yourmom;

import battlecode.common.Clock;
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

	public void writeUShorts(ChannelType channelType, int[] data) {
		writeUShorts(channelType.ordinal(), data);
	}
	
	public void writeUShorts(int channel, int[] data) {
		for (int i = 0; i < data.length; ++i) {
			try {
				rc.broadcast(channel+i, (signature << 31) | data[i]);
			} catch (Exception e) {}
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
	
	// All code below this point is for pre-computing channels
	
//	  /**
//	   * Right now, we're using this for pre-generating a list of channels so we don't have to calculate them during the game.
//	   * This writes to PrecomputedChannelNos.java
//	   * @param args
//	   */
//	  public static void main(String[] args) {
//		  try {
//			  String filename = System.getProperty("user.dir") + "\\teams\\baseTurgid\\PrecomputedChannelNos.java";
//			  FileWriter fw = new FileWriter(filename);
//			  BufferedWriter bw = new BufferedWriter(fw, 100000000);
//			  
//			  bw.write("package baseTurgid;\n\n");
//			  bw.write("public class PrecomputedChannelNos {\n\n");
//			  bw.write("public static int[][][] precomputedChannelNos =\n");
//			  bw.write("\t{");
//			  for (int constant = 0; constant < Constants.MAX_PRECOMPUTED_ROUNDS / Constants.CHANNEL_CYCLE; constant++) {
//				  if (constant > 0) {
//					  bw.write("\t");
//				  }
//				  bw.write("{");
//				  for (int channel = 0; channel < ChannelType.size; channel++) {
//					  int[] channelNos = getChannelNos(ChannelType.values()[channel], constant);
//					  bw.write("{");
//					  for (int i = 0; i < channelNos.length; i++) {
//						  bw.write(Integer.toString(channelNos[i]));
//						  if (i < channelNos.length - 1) {
//							  bw.write(", ");
//						  }
//					  }
//					  bw.write("}");
//					  if (channel < ChannelType.size - 1) {
//						  bw.write(", ");
//					  }
//				  }
//				  bw.write("}");
//				  if (constant < GameConstants.ROUND_MAX_LIMIT - 1) {
//					  bw.write(", ");
//				  }
//				  
//				  bw.write("\n");
//			  }
//			  bw.write("};\n\n");
//			  bw.write("}\n");
//			  bw.flush();
//			  bw.close();
//		  } catch (IOException e) {
//			  e.printStackTrace();
//		  }
//	  }
}
