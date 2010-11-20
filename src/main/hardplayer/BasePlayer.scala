package hardplayer

import battlecode.common._
import Static._

abstract class BasePlayer(myRC : RobotController) extends Static {

	init(myRC)

	def runloop() {
		sensorAI.sense()
		/*
		for(g <- goals) {
			g.execute()	
		}
		*/
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
