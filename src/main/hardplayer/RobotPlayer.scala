package hardplayer

import battlecode.common.RobotController
import battlecode.common.Team

class RobotPlayer(myRC : RobotController) extends Runnable {

	def run() {
		println("hello")
		while(true) {
			Static.init(myRC)
			try {
				val ourStrat = myRC.getTeam() match {
					case Team.A => MainStrategy
					case Team.B => MainStrategy
				}
				ourStrat.execute(myRC)
			} catch {
				case e : Exception => { Static.debug_stackTrace(e) }
			}
		}
	}

}
