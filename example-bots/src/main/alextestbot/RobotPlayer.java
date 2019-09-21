package alextestbot;

import battlecode.common.*;

/*
Strategy:
- Make some gardeners.
- Each gardener maintains 2 trees and one tank. The tank's only job is to long distance fire away at the enemies.
- TODO use scouts. Scouts find the enemies.
*/

public class RobotPlayer {
    public static void run(RobotController rc) throws Exception {
        Bot me;
        switch (rc.getType()) {
            case ARCHON:
                me = new Archon(rc);
                me.run();
                break;  

            case GARDENER:
                me = new Gardener(rc);
                me.run();
                break;

            case SCOUT:
                me = new Scout(rc);
                me.run();
                break;

            case LUMBERJACK:
                me = new Lumberjack(rc);
                me.run();
                break;

            case SOLDIER:
                me = new Soldier(rc);
                me.run();
                break;

            case TANK:
                me = new Tank(rc);
                me.run();
                break;
                
            default:
                throw new Exception("unexpected robot type " + rc.getType());
        }
    }
}
