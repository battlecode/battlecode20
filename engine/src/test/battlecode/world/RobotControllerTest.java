package battlecode.world;

import battlecode.common.*;

import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for RobotController. These are where the gameplay tests are.
 *
 * Using TestGame and TestMapBuilder as helpers.
 */
public class RobotControllerTest {
    public final double EPSILON = 1.0e-5; // Smaller epsilon requred, possibly due to strictfp? Used to be 1.0e-9

    /**
     * Tests the most basic methods of RobotController. This test has extra
     * comments to serve as an example of how to use TestMapBuilder and
     * TestGame.
     *
     * @throws GameActionException shouldn't happen
     */
    @Test
    public void testBasic() throws GameActionException {
        // Prepares a map with the following properties:
        // origin = [0,0], width = 10, height = 10, num rounds = 100
        // random seed = 1337
        // The map doesn't have to meet specs.
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 10, 10, 1337, 100)
            .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);

        // Let's spawn a robot for each team. The integers represent IDs.
        float oX = game.getOriginX();
        float oY = game.getOriginY();
        final int archonA = game.spawn(oX + 3, oY + 3, RobotType.ARCHON, Team.A);
        final int soldierB = game.spawn(oX + 1, oY + 1, RobotType.SOLDIER, Team
                .B);
        InternalRobot archonABot = game.getBot(archonA);

        assertEquals(new MapLocation(oX + 3, oY + 3), archonABot.getLocation());

        // The following specifies the code to be executed in the next round.
        // Bytecodes are not counted, and yields are automatic at the end.
        game.round((id, rc) -> {
            if (id == archonA) {
                rc.move(Direction.getEast());
            } else if (id == soldierB) {
                // do nothing
            }
        });

        // Let's assert that things happened properly.
        assertEquals(new MapLocation(
                oX + 3 + RobotType.ARCHON.strideRadius,
                oY + 3
        ), archonABot.getLocation());

        // Lets wait for 10 rounds go by.
        game.waitRounds(10);

        // hooray!
    }

    /**
     * Ensure that actions take place immediately.
     */
    @Test
    public void testImmediateActions() throws GameActionException {
        LiveMap map= new TestMapBuilder("test", 0, 0, 100, 100, 1337, 1000).build();
        TestGame game = new TestGame(map);

        final int a = game.spawn(1, 1, RobotType.SOLDIER, Team.A);

        game.round((id, rc) -> {
            if (id != a) return;

            final MapLocation start = rc.getLocation();
            assertEquals(new MapLocation(1, 1), start);

            rc.move(Direction.getEast());

            final MapLocation newLocation = rc.getLocation();
            assertEquals(new MapLocation(1 + RobotType.SOLDIER.strideRadius, 1), newLocation);
        });

        // Let delays go away
        game.waitRounds(10);
    }

    @Test
    public void testSpawns() throws GameActionException {
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 10, 10, 1337, 100)
            .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);

        // Let's spawn a robot for each team. The integers represent IDs.
        final int archonA = game.spawn(3, 3, RobotType.ARCHON, Team.A);

        // The following specifies the code to be executed in the next round.
        // Bytecodes are not counted, and yields are automatic at the end.
        game.round((id, rc) -> {
            assertTrue("Can't build robot", rc.canBuildRobot(RobotType.GARDENER, Direction.getEast()));
            rc.buildRobot(RobotType.GARDENER, Direction.getEast());
        });

        for (InternalRobot robot : game.getWorld().getObjectInfo().robots()) {
            if (robot.getID() != archonA) {
                assertEquals(RobotType.GARDENER, robot.getType());
            }
        }

        // Lets wait for 10 rounds go by.
        game.waitRounds(10);

        // hooray!

    }
    
    /**
     * Checks attacks of bullets in various ways
     * 
     * @throws GameActionException
     */
    @Test
    public void testBulletAttack() throws GameActionException {
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 10, 10, 1337, 100)
            .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);
        
        // Create some units
        final int soldierA = game.spawn(5, 5, RobotType.SOLDIER, Team.A);
        final int soldierA2 = game.spawn(9, 5, RobotType.SOLDIER, Team.A);
        final int soldierB = game.spawn(1, 5, RobotType.SOLDIER, Team.B);
        game.waitRounds(20); // Let soldiers mature
        
        // soldierA fires a shot at soldierA2
        game.round((id, rc) -> {
            if (id != soldierA) return;
            RobotInfo[] nearbyRobots = rc.senseNearbyRobots(-1, Team.A);
            assertEquals(nearbyRobots.length,1);
            
            // Ensure bullet exists and spawns at proper location
            InternalBullet[] bullets = game.getWorld().getObjectInfo().bulletsArray();
            assertEquals(
                    bullets[0].getLocation().distanceTo(rc.getLocation()),
                    rc.getType().bodyRadius + GameConstants.BULLET_SPAWN_OFFSET,
                    EPSILON
            );
        });
        
        // soldierA fires a shot at soldierB
        game.round((id, rc) -> {
            if (id != soldierA) return;

            // Original bullet should be gone
            InternalBullet[] bullets = game.getWorld().getObjectInfo().bulletsArray();
            assertEquals(bullets.length,0);
            
            RobotInfo[] nearbyRobots = rc.senseNearbyRobots(-1, Team.B);
            assertEquals(nearbyRobots.length,1);
            
            // Ensure new bullet exists
            bullets = game.getWorld().getObjectInfo().bulletsArray();
        });
        
        // Let bullets propagate to targets
        game.waitRounds(1);
        
        // No more bullets in flight
        InternalBullet[] bulletIDs = game.getWorld().getObjectInfo().bulletsArray();
        assertEquals(bulletIDs.length,0);
        
        // Two targets are damaged
    }

    @Test // Bullet collision works continuously and not at discrete intervals
    public void continuousBulletCollisionTest() throws GameActionException {
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 12, 10, 1337, 100)
                .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);

        // Create some units
        final int soldierA = game.spawn(3, 5.01f +(RobotType.SOLDIER.bodyRadius-RobotType.SOLDIER.strideRadius), RobotType.SOLDIER, Team.A);
        final int soldierB = game.spawn(9, 5, RobotType.SOLDIER, Team.B);
        final int soldierB2 = game.spawn(10f,6.8f,RobotType.SOLDIER, Team.B);
        game.waitRounds(20);    // Wait for bots to mature to full health

        MapLocation soldierBLocation = game.getBot(soldierB).getLocation();
        // topOfSoldierB is a location just near the top edge of soldierB.
        // if discrete bullet position checking is used, the bullet will clip though some of the tests.
        MapLocation topOfSoldierB = soldierBLocation.add(Direction.getNorth(),RobotType.SOLDIER.bodyRadius - 0.01f);

        final float testInterval = 0.01f;
        for(float i=0; i<1; i+=testInterval){
            // soldierA fires a shot at soldierB, and moves a small amount closer.
            // Move before firing so it doesn't step into it's own bullet
            game.round((id, rc) -> {
                if (id != soldierA) return;
                rc.move(Direction.EAST,testInterval);
            });
            game.waitRounds(5); // Bullet propagation

            // SoldierB should get hit every time (bullet never clips through)
            game.getBot(soldierB).repairRobot(10); // Repair back to full health so it doesn't die

            // SoldierB2 should never get hit
            assertEquals(game.getBot(soldierB).getHealth(), RobotType.SOLDIER.maxHealth, EPSILON);
        }

        // Now check cases where it shouldn't hit soldierB
        game.round((id, rc) -> {
            if (id != soldierA) return;
            rc.move(Direction.getNorth(),RobotType.SOLDIER.strideRadius);
        });
        game.waitRounds(5); // Bullet propagation

        // Bullet goes over soldierB
        assertEquals(game.getBot(soldierB).getHealth(), RobotType.SOLDIER.maxHealth, EPSILON);
        // ...and hits soldier B2

        // Test shooting off the map
        game.round((id, rc) -> {
            if (id == soldierB2)
                rc.move(Direction.getNorth());  // Move out of way so soldierA can shoot off the map
        });

        assertEquals(game.getBot(soldierB).getHealth(), RobotType.SOLDIER.maxHealth, EPSILON);
        // Bullet should still be in game
        game.waitRounds(1);
        // Bullet should hit wall and die
        assertEquals(game.getWorld().getObjectInfo().bullets().size(),0);
    }

    @Test // Buying victory points
    public void victoryPointTest() throws GameActionException {
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 10, 10, 1337, 100)
                .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);
        final int archonA = game.spawn(8, 5, RobotType.GARDENER, Team.A);
        final int archonB = game.spawn(2, 5, RobotType.GARDENER, Team.B);

        game.round((id, rc) -> {
            if (id != archonA) return;
            rc.donate(rc.getVictoryPointCost()*10);
            assertEquals(rc.getTeamBullets(),GameConstants.BULLETS_INITIAL_AMOUNT-rc.getVictoryPointCost()*10,EPSILON);
            assertEquals(rc.getTeamVictoryPoints(),10);
            rc.donate(rc.getVictoryPointCost()-0.1f);
            rc.donate(rc.getVictoryPointCost()-0.1f);
            assertEquals(rc.getTeamBullets(),GameConstants.BULLETS_INITIAL_AMOUNT-rc.getVictoryPointCost()*12+0.2f,1E-4);
            assertEquals(rc.getTeamVictoryPoints(),10);

            // Try to donate negative bullets, should fail.
            boolean exception = false;
            try {
                rc.donate(-1);
            } catch (GameActionException e) {
                exception = true;
            }
            assertTrue(exception);

            // Try to donate more than you have, should fail.
            exception = false;
            try {
                rc.donate(rc.getTeamBullets()+0.1f);
            } catch (GameActionException e) {
                exception = true;
            }
            assertTrue(exception);
        });

        // No winner yet
        assertEquals(game.getWorld().getWinner(),null);

        game.round((id, rc) -> {
            if(id != archonA) return;

            // Give TeamA lots of bullets
            game.getWorld().getTeamInfo().adjustBulletSupply(Team.A,GameConstants.VICTORY_POINTS_TO_WIN*rc.getVictoryPointCost());

            rc.donate(rc.getTeamBullets());
        });

        // Team A should win
        assertEquals(game.getWorld().getWinner(),Team.A);
        // ...by victory point threshold
        assertEquals(game.getWorld().getGameStats().getDominationFactor(), DominationFactor.PHILANTROPIED);

    }

    @Test
    public void testNullSense() throws GameActionException {
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 10, 10, 1337, 100)
                .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);

        final int soldierA = game.spawn(3, 5, RobotType.SOLDIER, Team.A);
        final int soldierB = game.spawn(7, 5, RobotType.SOLDIER, Team.B);

        game.round((id, rc) -> {
            if(id != soldierA) return;

            RobotInfo actualBot = rc.senseRobotAtLocation(new MapLocation(3,5));
            RobotInfo nullBot = rc.senseRobotAtLocation(new MapLocation(5,7));

            assertNotEquals(actualBot,null);
            assertEquals(nullBot,null);
        });
    }

    @Test
    public void testNullIsCircleOccupied() throws GameActionException {

        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 10, 10, 1337, 100)
                .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);

        final int gardener = game.spawn(5, 5, RobotType.GARDENER, Team.A);


        game.round((id, rc) -> {
            if(id != gardener) return;

            boolean exception = false;
            try {
                assertFalse(rc.isCircleOccupiedExceptByThisRobot(rc.getLocation(), 3));
            } catch(Exception e) {
                exception = true;
            }
            assertFalse(exception);
        });
    }

    // Check to ensure execution order is equal to spawn order
    @Test
    public void executionOrderTest() throws GameActionException {
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 50, 10, 1337, 100)
                .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);

        final int TEST_UNITS = 10;

        int[] testIDs = new int[TEST_UNITS];

        for(int i=0; i<TEST_UNITS; i++) {
            testIDs[i] = game.spawn(2+i*3,5,RobotType.SOLDIER,Team.A);
        }
        final int archonA = game.spawn(40,5,RobotType.ARCHON,Team.A);
        final int gardenerA = game.spawn(46,5,RobotType.GARDENER,Team.A);

        TIntArrayList executionOrder = new TIntArrayList();

        game.round((id, rc) -> {
            if(rc.getType() == RobotType.SOLDIER) {
                executionOrder.add(id);
            } else if (id == archonA) {
                assertTrue(rc.canHireGardener(Direction.getEast()));
                rc.hireGardener(Direction.getEast());
            } else if (id == gardenerA) {
                assertTrue(rc.canBuildRobot(RobotType.LUMBERJACK,Direction.getEast()));
            } else {
                // If either the spawned gardener or the lumberjack run code in the first round, this will fail.
                assertTrue(false);
            }
        });

        // Assert IDs aren't in order (random change, but very unlikely unless something is wrong)
        boolean sorted = true;
        for(int i=0; i<TEST_UNITS-1; i++) {
            if (testIDs[i] < testIDs[i+1])
                sorted = false;
        }
        assertFalse(sorted);


        // Assert execution IS in order
        for(int i=0; i<TEST_UNITS; i++) {
            assertEquals(testIDs[i],executionOrder.get(i));
        }
    }

    @Test
    public void noHealing() throws GameActionException {

        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 10, 10, 1337, 100)
                .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);

        final int gardener = game.spawn(5,5,RobotType.GARDENER,Team.A);
        final int archon = game.spawn(5,2,RobotType.ARCHON,Team.A);
        final int soldier = game.spawn(5,8,RobotType.SOLDIER,Team.A);

        assertEquals(game.getBot(gardener).getHealth(),RobotType.GARDENER.maxHealth,EPSILON);
        assertEquals(game.getBot(archon).getHealth(),RobotType.ARCHON.maxHealth,EPSILON);
        assertEquals(game.getBot(soldier).getHealth(),RobotType.SOLDIER.getStartingHealth(),EPSILON);

        game.getBot(gardener).damageRobot(10);
        game.getBot(archon).damageRobot(10);

        game.waitRounds(20);

        // Gardener and Archon should not heal in first 20 turns
        assertEquals(game.getBot(gardener).getHealth(),RobotType.GARDENER.maxHealth-10,EPSILON);
        assertEquals(game.getBot(archon).getHealth(),RobotType.ARCHON.maxHealth-10,EPSILON);
        assertEquals(game.getBot(soldier).getHealth(),RobotType.SOLDIER.maxHealth,EPSILON);
    }

    @Test
    public void sensingEachOtherTest() throws GameActionException {
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 50, 50, 1337, 100)
                .build();

        TestGame game = new TestGame(map);

        final int tankA = game.spawn(10, 10, RobotType.TANK, Team.A);
        final int soldierB = game.spawn(10, (float) 18.4534432, RobotType.SOLDIER, Team.B);

        game.waitRounds(50);


        // Soldier can see tank
        game.round((id, rc) -> {
            if (id == soldierB) {
                RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
                assertEquals(robots.length, 1);
            }
        });

        // Tank can't see soldier, but can see its bullet
        game.round((id, rc) -> {
            if (id == tankA) {
                RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            }
        });
    }

    @Test
    public void turnOrderTest() throws GameActionException {
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 50, 50, 1337, 100)
                .build();

        TestGame game = new TestGame(map);

        // Spawn two tanks close enough such that a bullet fired from one
        // at the other will hit after updating once.
        final int tankA = game.spawn(10, 10, RobotType.TANK, Team.A);
        final int tankB = game.spawn(15, 10, RobotType.TANK, Team.B);

        game.waitRounds(50);

        game.round((id, rc) -> {
            if (id == tankA) {
            } else if (id == tankB) {
            }
        });

        game.round((id, rc) -> {
            if (id == tankA) {
                // The bullet fired by this tank last round should
                // now have hit the other tank.
            } else if (id == tankB) {
                // Both bullets should now have updated.
            }
        });
    }

    @Test
    public void testImmediateCollisionDetection() throws GameActionException {
        LiveMap map = new TestMapBuilder("test", new MapLocation(0,0), 10, 10, 1337, 100)
                .build();

        // This creates the actual game.
        TestGame game = new TestGame(map);

        final int soldierA = game.spawn(2.99f,5,RobotType.SOLDIER,Team.A);
        final int soldierB = game.spawn(5,5,RobotType.SOLDIER,Team.B);

        game.waitRounds(20); // Let units mature

        game.round((id, rc) -> {
            if (id == soldierA) {
                RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
                assertEquals(nearbyRobots.length,1);
                // Damage is done immediately
            }
        });

        game.getBot(soldierB).damageRobot(RobotType.SOLDIER.maxHealth-RobotType.SOLDIER.attackPower-1);

        game.round((id, rc) -> {
            if (id == soldierA) {
                RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
                // Damage is done immediately and robot is dead
            }
        });
    }
}
