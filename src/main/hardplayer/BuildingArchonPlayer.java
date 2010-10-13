package hardplayer;

import static hardplayer.goals.BroadcastEnemyUnitLocationsGoal.*;
import static hardplayer.UnitBalance.*;
import hardplayer.goals.ArchonExploreGoal;
import hardplayer.goals.ArchonFindEnemyGoal;
import hardplayer.goals.BroadcastGoInThisDirectionGoal;
import hardplayer.goals.FindEnemyGoal;
import hardplayer.goals.Goal;
import hardplayer.goals.GoodVisionBroadcastEnemyUnitLocationsGoal;
import hardplayer.goals.StayTogetherGoal;
import hardplayer.goals.MakeChainGoal;
import hardplayer.message.SuperMessageStack;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

import static battlecode.common.GameConstants.ENERGON_TO_FLUX_CONVERSION;
import static battlecode.common.GameConstants.TURRET_DEPLOY_UPKEEP_REDUCTION;

public class BuildingArchonPlayer extends ArchonPlayer {

	public BuildingArchonPlayer(RobotController RC) {
		super(RC);
	}

	public void setGoals() {

		findEnemyGoal = new ArchonFindEnemyGoal(this);

		movementGoals = new Goal [] {
			//new StayTogetherGoal(this),
			new MakeChainGoal(this)
		};

		broadcastGoals = new Goal [] {
			new GoodVisionBroadcastEnemyUnitLocationsGoal(this, findEnemyGoal)
		};
	}

	//boolean camping=true;

	public RobotType chooseTypeToSpawn() {
		//if(!camping) return super.chooseTypeToSpawn();
		if(myRC.getFlux()>=RobotType.COMM.spawnFluxCost()&&alliedComms.size==0) {
			return RobotType.COMM;
		}
		if(myRC.getFlux()>=RobotType.AURA.spawnFluxCost()&&alliedAuras.size<2) {
			return RobotType.AURA;
		}
		else if(6*alliedWouts.size<=3*alliedTurrets.size+2*alliedSoldiers.size) {
			return RobotType.WOUT;
		}
		else if(atWar) {
			if(alliedTurrets.size<alliedSoldiers.size)
				return RobotType.TURRET;
			else
				return RobotType.SOLDIER;
		}
		else
			return RobotType.TURRET;
	}

	public static double turretUpkeep = RobotType.TURRET.energonUpkeep()-TURRET_DEPLOY_UPKEEP_REDUCTION;

	public boolean canSupportAnother(RobotType type) {
		return .5*(alliedArchons.size+1)>=
			RobotType.WOUT.energonUpkeep()*alliedWouts.size+RobotType.CHAINER.energonUpkeep()*alliedChainers.size+RobotType.SOLDIER.energonUpkeep()*alliedSoldiers.size+turretUpkeep*alliedTurrets.size+type.energonUpkeep();
	}

}