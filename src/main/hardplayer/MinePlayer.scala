package hardplayer

import hardplayer.Static._
import hardplayer.goal.MineSpawnGoal
import hardplayer.goal.Goal

import battlecode.common.RobotController

class MinePlayer(myRC : RobotController) extends BasePlayer(myRC) {

	var goals = asArray(new MineSpawnGoal())

}
