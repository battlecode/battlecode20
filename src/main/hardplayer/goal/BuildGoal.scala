package hardplayer.goal

import hardplayer.Static._

import battlecode.common.Chassis
import battlecode.common.ComponentType

class BuildGoal extends Goal {

	var avg100 = 0.;

	def priority() = 0
	def maxPriority() = 0

	def executeChassis() {
		avg100*=.99
		var resources = myRC.getTeamResources()
		avg100 += resources
		if(avg100/100. - resources > Chassis.HEAVY.upkeep) {
		}
	}

	def executeComponent() {
	}

	def execute() {
	}

}
