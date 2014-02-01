package yourmom;

import battlecode.common.GameActionException;
import battlecode.common.Robot;

public class RadarSystem {
	BaseRobot br;
	public int numAllyRobots;
	public int numAllySoldiers;

	public int numNearbyAllySoldiers;

	public Robot[] nearbyEnemySoldiers;
	public int numNearbyEnemySoldiers;

	public RadarSystem(BaseRobot br) {
		this.br = br;
	}

	public void scan() throws GameActionException {
		final Robot[] allies = br.rc.senseNearbyGameObjects(
			Robot.class,
			100000,
			br.myTeam
		);
		numAllyRobots = allies.length;
		// XXX crude estimate
		numAllySoldiers = numAllyRobots - 1;

		numNearbyAllySoldiers = br.rc.senseNearbyGameObjects(Robot.class, 14, br.myTeam).length;

		nearbyEnemySoldiers = br.rc.senseNearbyGameObjects(
			Robot.class,
			br.myType.attackRadiusMaxSquared,
			br.enemyTeam
		);
		numNearbyEnemySoldiers = nearbyEnemySoldiers.length;
	}
}
