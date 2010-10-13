package refplayer.message;

import java.util.Arrays;
import java.util.Random;

import refplayer.BasePlayer;
import refplayer.util.FastList;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class EvilSender extends MessageSender implements MessageHandler {

	public boolean angry = false;
	public Message intercepted;

	Random rand = new Random();

	static final RobotInfo infoWithNullLoc = new RobotInfo(0, null, null, null,
															0., 0., 0., 0, 0,
															null, 0., false, false, null);

	static final FastList flNull = new FastList(1);

	static {
		flNull.robotInfos[0]=infoWithNullLoc;
		flNull.size=1;
	}

	public EvilSender(BasePlayer bp) {
		super(bp);
		if(myRC.getTeam()==Team.B) {
			idFactor = 102181;
		}
		else {
			idFactor = 102253;
		}
		idFactor+=toString().indexOf('.')-9;
		myIDEncoded = (myRC.getRobot().getID()%ID_MODULUS)*idFactor;
	}

	public void receivedMessage(Message m) {
		if(intercepted==null&&isValid(m)) {
			intercepted=m;
			angry=true;
		}
	}

	public void send() {
		try {
			if(intercepted!=null) {
				int i;
				FastList fl;
				if(player.enemyArchons.size>0)
					fl=player.enemyArchons;
				else if(player.enemyChainers.size>0)
					fl=player.enemyChainers;
				else if(player.enemyTurrets.size>0)
					fl=player.enemyTurrets;
				else if(player.enemySoldiers.size>0)
					fl=player.enemySoldiers;
				else
					fl=flNull;
				//System.out.println(fl.robotInfos);
				//System.out.println(fl.robotInfos[rand.nextInt(fl.size)]);
				//System.out.println(fl.robotInfos[rand.nextInt(fl.size)].location);
				for(i=intercepted.locations.length-1;i>0;i--) {
					intercepted.locations[i]=fl.robotInfos[rand.nextInt(fl.size)].location;
				}
				send(intercepted);
				intercepted=null;
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

}
