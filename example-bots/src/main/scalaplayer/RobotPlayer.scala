package scalaplayer

import battlecode.common._

object RobotPlayer {

    def run(myRC : RobotController) {
        while(true) {
            try {
                Clock.`yield`()
            } catch {
                case e : Exception => {
                    println("caught exception:")
                    e.printStackTrace()
                }
            }
        }
    }

}
