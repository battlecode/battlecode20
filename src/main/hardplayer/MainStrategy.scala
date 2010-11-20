package hardplayer

import battlecode.common.RobotController

import hardplayer.Static._
import hardplayer.sensor._

object MainStrategy extends Strategy {

	override def execute(myRC : RobotController) {
	
		val s1 = new LongRangeSensor()
		debug_startTiming()
		s1.sense()
		debug_stopTiming()
		myRC.suicide()
	}

}
