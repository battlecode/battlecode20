package hardplayer

import battlecode.common._
import hardplayer.Static._
import hardplayer.goal.Goal

abstract class BasePlayer(myRC : RobotController) extends Static {

	init(myRC)

	var goals : Array[Goal] = null

	def setGoals

	def runloop() {
		sensorAI.sense()
		goals.foreach { g => g.execute() }
	}

	def run() {
		while(true) {
			try {
				runloop()	
			} catch {
				case e : Exception => { debug_stackTrace(e) }
			}
		}
	}

}
