
-injars  battlecode-player.jar
-outjars proguard/battlecode-player.jar

-libraryjars <java.home>/lib/rt.jar
-libraryjars ../battlecode-server/build/classes

-dontoptimize
-overloadaggressively
-dontusemixedcaseclassnames

-keepclasseswithmembers public class *.RobotPlayer {

    public static void run(battlecode.common.RobotController);
}
