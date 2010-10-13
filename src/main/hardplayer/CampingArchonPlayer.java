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

public class CampingArchonPlayer extends ArchonPlayer {

	boolean [] woutSpawnDirs = new boolean [] { true, true, true, true, true, true, true, true };

	public CampingArchonPlayer(RobotController RC) {
		super(RC);
	}

	public boolean canSpawn(Direction d, RobotType t) throws battlecode.common.GameActionException {
		if(t==RobotType.WOUT) return woutSpawnDirs[d.ordinal()]&&canSpawnGround(d);
		else return canSpawnGround(d);
	}

	public void checkSpawnDirs() throws battlecode.common.GameActionException {
		int i;
		for(i=7;i>=0;i--) {
			if(myRC.senseAirRobotAtLocation(myLoc.add(directions[i]))!=null)
				woutSpawnDirs[i]=false;
		}
	}

	public void setGoals() {

		findEnemyGoal = new ArchonFindEnemyGoal(this);

		movementGoals = new Goal [] {
			new StayTogetherGoal(this)
		};

		broadcastGoals = new Goal [] {
			new GoodVisionBroadcastEnemyUnitLocationsGoal(this, findEnemyGoal)
		};

		try {
			Direction d;
			myLoc = myRC.getLocation();
			
			checkSpawnDirs();
			
			if(myRC.senseAirRobotAtLocation(myLoc.add(Direction.EAST))!=null&&
			   myRC.senseAirRobotAtLocation(myLoc.add(Direction.WEST))!=null) {
				if(myRC.canMove(Direction.SOUTH))
					myRC.setDirection(Direction.SOUTH);
				else if(myRC.canMove(Direction.NORTH))
					myRC.setDirection(Direction.NORTH);
				else
					return;
				myRC.yield();
				myRC.moveForward();
				myRC.yield();
			}
			else if(myRC.senseAirRobotAtLocation(myLoc.add(Direction.NORTH))!=null&&
					myRC.senseAirRobotAtLocation(myLoc.add(Direction.SOUTH))!=null) {
				if(myRC.canMove(Direction.EAST))
					myRC.setDirection(Direction.EAST);
				else if(myRC.canMove(Direction.WEST))
					myRC.setDirection(Direction.WEST);
				else
					return;
				myRC.yield();
				myRC.moveForward();
				myRC.yield();
			}
			else {
				myRC.yield();
				myRC.yield();
			}
			checkSpawnDirs();
			/*
			int dx, dy;
			if(myRC.senseAirRobotAtLocation(myLoc.add(Direction.SOUTH))==null)
				dy = 0;
			else
				dy = -1;
			if(myRC.senseAirRobotAtLocation(myLoc.add(Direction.EAST))==null)
				dx = 1;
			else if(myRC.senseAirRobotAtLocation(myLoc.add(Direction.WEST))==null)
				dx = -1;
			else
				dx = 0;
			d = myLoc.directionTo(new MapLocation(myLoc.getX()+dx, myLoc.getY()+dy));
			if(myRC.canMove(d)) {		
				myRC.setDirection(d);
				myRC.yield();
				myRC.moveForward();
				myRC.yield();
			}
			*/
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

	//boolean camping=true;

	public void runLoop() {
		super.runLoop();

		/*
		if(enemyArchons.size+enemyTurrets.size+enemyChainers.size+enemySoldiers.size>0) {
			ArchonPlayer ap = new ArchonPlayer(myRC);
			ap.run();
		}
		*/

		/*
		if(camping&&enemyArchons.size+enemyTurrets.size+enemyChainers.size+enemySoldiers.size>0) {
			super.setGoals();
			camping=false;
		}
		*/
	}

	public RobotType chooseTypeToSpawn() {
		//if(!camping) return super.chooseTypeToSpawn();
		if(myRC.getFlux()>=RobotType.COMM.spawnFluxCost()&&alliedComms.size==0) {
			return RobotType.COMM;
		}
		if(myRC.getFlux()>=RobotType.AURA.spawnFluxCost()&&alliedAuras.size<2) {
			return RobotType.AURA;
		}
		else if(alliedWouts.size==0) {
			return RobotType.WOUT;
		}
		else
			return RobotType.TURRET;
	}

	public void makeArmy() {
		RobotType typeToSpawn=chooseTypeToSpawn();
		if(typeToSpawn==null||myRC.getEnergonLevel()<MIN_ENERGON+typeToSpawn.spawnCost()||myRC.getFlux()<typeToSpawn.spawnFluxCost())
			return;
		if((!typeToSpawn.isBuilding())&&!canSupportAnother(typeToSpawn))
			return;
		try {
			/* We want to spawn closer to the fighting.  But we do try to be
			   facing the fighting already so maybe this isn't needed.
			if(typeToSpawn==RobotType.AURA_OFF||typeToSpawn==RobotType.AURA_DEF) {
				Direction [] enemyDirs = findEnemyGoal.getEnemyDirs();
				if(canSpawnGround(enemyDirs[0])) {
					if(myRC.getDirection()==enemyDirs[0])
						myRC.spawn(typeToSpawn);
					else if(!myRC.isMovementActive())
						myRC.setDirection(enemyDirs[0]);
				}
			}
			else {
			*/
			if(canSpawn(myRC.getDirection(),typeToSpawn)) {
				myRC.spawn(typeToSpawn);
			}
			else if(!myRC.isMovementActive()) {
				int i;
				for(i=7;i>=0;i--) {
					if(canSpawn(directions[i],typeToSpawn)) {
						myRC.setDirection(directions[i]);
						return;
					}
				}
			}
			//}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public boolean flee() {
		//if(!camping) return super.flee();
		return false;
	}

	public static double turretUpkeep = RobotType.TURRET.energonUpkeep()-TURRET_DEPLOY_UPKEEP_REDUCTION;

	public boolean canSupportAnother(RobotType type) {
		return .7*(alliedArchons.size+1)>=
			RobotType.WOUT.energonUpkeep()*alliedWouts.size+RobotType.CHAINER.energonUpkeep()*alliedChainers.size+RobotType.SOLDIER.energonUpkeep()*alliedSoldiers.size+turretUpkeep*alliedTurrets.size+type.energonUpkeep();
	}

}