package maxplayer3;

import java.util.ArrayList;

import battlecode.common.*;

public class Technology{

	static ArrayList<RobotType> types = new ArrayList<RobotType>();

	public static void init() {
		for(RobotType r:RobotType.values())
			types.add(r);
	}
	
	public static void achieveTech(RobotType robottype,RobotController rc, boolean finalTech) throws GameActionException {
		if(robottype==null)
			return;
		RobotType desiredBuilding;
		if(robottype.isBuilding){
			desiredBuilding=robottype;
		}else{
			desiredBuilding=robottype.spawnSource;
		}
		if(rc.getRobotTypeCount(desiredBuilding)>0){//tech already achieved
			if(!(finalTech&&rc.getTeamOre()>(robottype.oreCost*2+robottype.FURBY.oreCost)))//build duplicate finaltech buildings to increase production rate
				return;
		}
		achieveTech(desiredBuilding.dependency1,rc,false);
		achieveTech(desiredBuilding.dependency2,rc,false);
		if((desiredBuilding.dependency1==null||rc.getRobotTypeCount(desiredBuilding.dependency1)>0)
				&&(desiredBuilding.dependency2==null||rc.getRobotTypeCount(desiredBuilding.dependency2)>0)){//this check may be unnecessary
			//now no dependencies needed; just need the desired building
			Direction buildDir = Pathfinding.findAvailableSite(rc);
			if(buildDir!=null){
				rc.setIndicatorString(1, "want to build "+desiredBuilding+" "+buildDir);
				Verified.build(rc, buildDir, desiredBuilding);
			}else{//site not found; go look for another
				Direction rd = Pathfinding.randomDir();
				Verified.move(rc,rd);
			}
		}
	}

	static int techCost(RobotType robottype,RobotController rc) throws GameActionException {
		if(robottype==null)
			return 0;
		RobotType desiredBuilding;
		if(robottype.isBuilding){
			desiredBuilding=robottype;
		}else{
			desiredBuilding=robottype.spawnSource;
		}
		if(rc.getRobotTypeCount(desiredBuilding)>0){//tech already achieved
			return 0;
		}else{
			ArrayList<RobotType> requiredBuildings = new ArrayList<RobotType>();
			addBuilding(desiredBuilding,requiredBuildings,rc);
			//total up the costs of all the buildings
			int runningTotal = 0;
			for(RobotType r:requiredBuildings){
//				System.out.println("$"+r.oreCost +" "+r);
				runningTotal+=r.oreCost;
			}
//			System.out.println("-------------------------");
//			System.out.println("$"+runningTotal+" Total");
			return runningTotal;
		}
	}
	
	private static void addBuilding(RobotType desiredBuilding, ArrayList<RobotType> requiredBuildings,RobotController rc) {
		if(desiredBuilding!=null){
			if(rc.getRobotTypeCount(desiredBuilding)==0){
				int loc = requiredBuildings.indexOf(desiredBuilding);
				if(loc==-1){//this building is not yet added to the list
					requiredBuildings.add(desiredBuilding);
					addBuilding(desiredBuilding.dependency1,requiredBuildings,rc);
					addBuilding(desiredBuilding.dependency2,requiredBuildings,rc);
				}
			}
		}
	}

	
	
}