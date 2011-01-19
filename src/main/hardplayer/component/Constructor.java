package hardplayer.component;

import hardplayer.Static;

import battlecode.common.Clock;
import battlecode.common.Chassis;
import battlecode.common.ComponentType;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;

public class Constructor extends Static implements ComponentAI {

	public static final double BUILDING_MIN_RESOURCES = Chassis.BUILDING.cost + ComponentType.CONSTRUCTOR.cost + 1.;

	public void execute() {
		if(builder.isActive())
			return;
		try {
			MapLocation frontLoc = myLoc.add(myRC.getDirection());
			Robot frontRobot = (Robot)sensor.senseObjectAtLocation(frontLoc,RobotLevel.ON_GROUND);
			if(frontRobot==null) {
				if(myRC.getTeamResources() > BUILDING_MIN_RESOURCES) {
					if(sensor.senseObjectAtLocation(frontLoc,RobotLevel.MINE) != null) {
						builder.build(Chassis.BUILDING, frontLoc);
					}
					else if(rnd.nextInt()%(Clock.getRoundNum()/200+1)==0) {
						int i = allies.size;
						while(--i>0) {
							RobotInfo info = alliedInfos[i];
							if(info.chassis == Chassis.BUILDING && info.on && info.components.length >= 3 && info.components[2] == ComponentType.RECYCLER && frontLoc.distanceSquaredTo(info.location)<=2) {
								builder.build(Chassis.BUILDING, frontLoc);
								return;
							}	
						}	
					}
				}
			}
			else {
				if(myRC.getTeamResources() > ComponentType.RECYCLER.cost + 1.) {
					RobotInfo info = sensor.senseRobotInfo(frontRobot);
					if(info.chassis!=Chassis.BUILDING) return;
					if(sensor.senseObjectAtLocation(frontLoc,RobotLevel.MINE)==null) return;
					ComponentType [] ct = info.components;
					if(ct==null) return;
					for(ComponentType c : ct) {
						if(c==ComponentType.RECYCLER) return;
					}
					builder.build(ComponentType.RECYCLER, frontLoc, RobotLevel.ON_GROUND);
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
