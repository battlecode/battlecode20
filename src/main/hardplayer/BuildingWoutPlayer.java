package hardplayer;

import hardplayer.goals.BroadcastScoutingFindGoal;
import hardplayer.goals.GoScoutingGoal;
import hardplayer.goals.Goal;
import hardplayer.goals.GoodVisionBroadcastEnemyUnitLocationsGoal;
import hardplayer.goals.SeekEnergonGoal;
import hardplayer.goals.SpotForArmyGoal;
import hardplayer.goals.TransportEnergonGoal;
import hardplayer.goals.WoutFindEnemyGoal;
import hardplayer.message.SuperMessageStack;

import static hardplayer.goals.BroadcastEnemyUnitLocationsGoal.*;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import battlecode.common.Message;

import static battlecode.common.GameConstants.ENERGON_TO_FLUX_CONVERSION;

public class BuildingWoutPlayer extends WoutPlayer {
	
	public BuildingWoutPlayer(RobotController RC) {
		super(RC);
	}

	public void setGoals() {
		
		findEnemyGoal = new WoutFindEnemyGoal(this);

		movementGoals = new Goal [] {
			new SpotForArmyGoal(this,findEnemyGoal),
			new TransportEnergonGoal(this),
			new GoScoutingGoal(this),
			new SeekEnergonGoal(this)
		};

		broadcastGoals = new Goal [] {
			//new BroadcastScoutingFindGoal(this,findEnemyGoal),
			new GoodVisionBroadcastEnemyUnitLocationsGoal(this, findEnemyGoal)
		};

	}

}