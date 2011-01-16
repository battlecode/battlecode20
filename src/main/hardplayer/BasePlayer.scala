package hardplayer

import battlecode.common._
import hardplayer.Static._
import hardplayer.goal.Goal

abstract class BasePlayer(myRC : RobotController) extends Static {

	init(myRC)

	var goals : Array[Goal]

	def runloop() {
		sensorAI.sense()
		goals.foreach { g => g.execute() }
	}

	def repurpose() : Boolean = false

	def run() {
		while(true) {
			try {
				runloop()
				if(repurpose())
					return
			} catch {
				case e : Exception => { debug_stackTrace(e) }
			}
		}
		myRC.`yield`()
	}

}
