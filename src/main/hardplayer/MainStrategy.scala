package hardplayer

import battlecode.common.Chassis
import battlecode.common.ComponentType
import battlecode.common.RobotController

import hardplayer.Static._
import hardplayer.sensor._

object MainStrategy extends Strategy {

	override def execute(myRC : RobotController) {

		while(true) {
			try {
				var comp = myRC.components()
				myRC.getChassis() match {
					case Chassis.BUILDING => {
						for(c <- comp) {
							if(c==ComponentType.RECYCLER) {
								new MinePlayer(myRC).run()
							}
						}
					}
					case Chassis.LIGHT => {
						for(c <- comp) {
							if(c==ComponentType.CONSTRUCTOR) {
								new ConstructorPlayer(myRC).run()
							}
							else {
								new LarvaPlayer(myRC).run() 
							}
						}
					}
				}
			} catch {
				case e : Exception => { debug_stackTrace(e) }
			}
		}
	
	}

}
