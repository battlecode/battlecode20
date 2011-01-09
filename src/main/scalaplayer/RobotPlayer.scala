package scalaplayer

import battlecode.common._

class RobotPlayer(myRC : RobotController) extends Runnable {

	def run() {
		val components = myRC.newComponents()
		myRC.getChassis() match {
			case Chassis.BUILDING => runBuilder(components(0).asInstanceOf[MovementController],components(2).asInstanceOf[BuilderController])
			case _ => runMotor(components(0).asInstanceOf[MovementController])
		}
	}

	def testit(motor : MovementController) {
		motor.withinRange(myRC.getLocation());
	}

	def runBuilder(motor : MovementController, builder : BuilderController) {
		while(true) {
			try {
				myRC.`yield`()
				if(!motor.canMove(myRC.getDirection()))
					motor.setDirection(myRC.getDirection().rotateRight())
				else if(myRC.getTeamResources()>=2*Chassis.LIGHT.cost)
					builder.build(Chassis.LIGHT,myRC.getLocation().add(myRC.getDirection()))
			} catch {
				case e : Exception => {
					println("caught exception:")
					e.printStackTrace()
				}
			}
		}
	}

	def runMotor(motor : MovementController) {
		while(true) {
			try {
				while(motor.isActive()) {
					myRC.`yield`()
				}

				if (motor.canMove(myRC.getDirection())) {
					//println("about to move")
					motor.moveForward()
				}
				else {
					motor.setDirection(myRC.getDirection().rotateRight())
				}
			} catch {
				case e : Exception => {
					println("caught exception:")
					e.printStackTrace()
				}
			}
		}
	}

}
