@file:JvmName("RobotPlayer")

package kotlinplayer

import battlecode.common.RobotController
import battlecode.common.Direction
import battlecode.common.Clock

fun run(rc: RobotController) {
    while (true) {
        try {
            rc.move(Direction.NORTH)
        } catch (e: Exception) {}

        Clock.`yield`()
    }
}
