package hardplayer.component;

import hardplayer.Static;

import battlecode.common.Clock;
import battlecode.common.Chassis;
import battlecode.common.ComponentType;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.Team;

public class Recycler extends Static implements ComponentAI {

	public static int CONSTRUCTOR_MAX = 500;

	public static boolean attackMode = false;

	public static ComponentType weaponToBuild = ComponentType.BLASTER;

	public void execute() {
		if(builder.isActive()) return;
		int l = myRC.components().length;
		try {
			if(myRC.getTeamResources()>1000) {
				attackMode = true;
				CONSTRUCTOR_MAX = 50;
			}
			else if(myRC.getTeamResources()<750) {
				attackMode = false;
				CONSTRUCTOR_MAX = 500;
			}
			if(l==3&&Clock.getRoundNum()>=500) {
				if(myRC.getTeamResources()>=ComponentType.RADAR.cost+1)
					builder.build(ComponentType.RADAR,myLoc,RobotLevel.ON_GROUND);
			}
			else if(l==4&&Clock.getRoundNum()>=500) {
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
							if(attackMode)
								buildIfPossible(ComponentType.RADAR,info.location,RobotLevel.ON_GROUND);
							else
								buildIfPossible(ComponentType.SIGHT,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl==2) {
							if(info.components[1]==ComponentType.RADAR) {
									buildIfPossible(weaponToBuild,info.location,RobotLevel.ON_GROUND);
									weaponToBuild = (rnd.nextInt(2)==0) ? ComponentType.BLASTER : ComponentType.SMG;
							}
							else
								buildIfPossible(ComponentType.CONSTRUCTOR,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl==3) {
							if(info.components[2]==ComponentType.BLASTER)
								buildIfPossible(ComponentType.SHIELD,info.location,RobotLevel.ON_GROUND);
							else if(info.components[2]==ComponentType.SMG)
								buildIfPossible(ComponentType.SMG,info.location,RobotLevel.ON_GROUND);
							else
								buildIfPossible(ComponentType.SMG,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl==4&&info.components[2]==ComponentType.SMG) {
							buildIfPossible(ComponentType.SHIELD,info.location,RobotLevel.ON_GROUND);
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
						else if(cl<=7) {
							buildIfPossible(ComponentType.BLASTER,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl<=12) {
							buildIfPossible(ComponentType.SHIELD,info.location,RobotLevel.ON_GROUND);
							return;
						}
						else if(cl<=14) {
							buildIfPossible(ComponentType.SMG,info.location,RobotLevel.ON_GROUND);
						}
					}
				}
				if(myRC.getTeamResources()>=3*Chassis.BUILDING.cost&&rnd.nextInt(Math.min((int)(sensor.senseIncome(myRC.getRobot())*Clock.getRoundNum()/20),CONSTRUCTOR_MAX))==0) {
					MapLocation frontLoc = myRC.getLocation().add(myRC.getDirection());
					if(builder.canBuild(Chassis.LIGHT,frontLoc))
						builder.build(Chassis.LIGHT,frontLoc);
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
