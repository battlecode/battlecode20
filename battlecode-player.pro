
-injars  battlecode-player.jar
-outjars proguard/battlecode-player.jar

-libraryjars <java.home>/lib/rt.jar
-libraryjars ../battlecode-server/build/classes

-dontoptimize
-overloadaggressively
-dontusemixedcaseclassnames

-keepclasseswithmembers public class *.RobotPlayer implements java.lang.Runnable {
    public RobotPlayer(battlecode.common.RobotController);

    public void run();
}
