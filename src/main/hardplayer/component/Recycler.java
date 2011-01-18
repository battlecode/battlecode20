package hardplayer.component;

import hardplayer.Static;

import battlecode.common.Clock;
import battlecode.common.Chassis;
import battlecode.common.ComponentType;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;

public class Recycler extends Static implements ComponentAI {
	
	public void execute() {
		if(builder.isActive()) return;
		int l = myRC.components().length;
		try {
			if(l==3) {
				if(myRC.getTeamResources()>=ComponentType.SIGHT.cost+1)
					builder.build(ComponentType.SIGHT,myLoc,RobotLevel.ON_GROUND);
			}
			else if(l==4) {
				if(myRC.getTeamResources()>=ComponentType.BLASTER.cost+1)
					builder.build(ComponentType.BLASTER,myLoc,RobotLevel.ON_GROUND);
			}
			else {
				int i = allies.size;
				RobotInfo info;
				while(--i>=0) {
					info = alliedInfos[i];
					if(myLoc.distanceSquaredTo(info.location)>2) continue;
					int cl = info.components.length;
					if(info.chassis==Chassis.LIGHT) {
						if(cl==1) {
							buildIfPossible(ComponentType.SIGHT,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl==2) {
							buildIfPossible(ComponentType.CONSTRUCTOR,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl==3) {
							buildIfPossible(ComponentType.SMG,info.location,RobotLevel.ON_GROUND);
							return;
						}
					}
					else if(info.chassis==Chassis.BUILDING) {
						if(sensor.senseObjectAtLocation(info.location,RobotLevel.MINE)!=null)
							continue;
						if(cl==2) {
							buildIfPossible(ComponentType.RADAR,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl<=6) {
							buildIfPossible(ComponentType.BLASTER,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl<=11) {
							buildIfPossible(ComponentType.SHIELD,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl<=13) {
							buildIfPossible(ComponentType.SMG,info.location,RobotLevel.ON_GROUND);
						}
					}
				}
				if(myRC.getTeamResources()>=3*Chassis.BUILDING.cost&&rnd.nextInt()%((int)(sensor.senseIncome(myRC.getRobot())*Clock.getRoundNum()/40))==0) {
					MapLocation frontLoc = myRC.getLocation().add(myRC.getDirection());
					if(canBuild(frontLoc,RobotLevel.ON_GROUND))
						builder.build(Chassis.LIGHT,frontLoc);
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
