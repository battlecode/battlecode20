package integrationtestbot;

import battlecode.common.*;
import static battlecode.common.RobotType.*;
import java.util.*;

public class RobotPlayer {
    public static double epsilon = 1.0e-8;

    public static void fail(RobotController rc, String message) {
        System.out.println("FAILURE: " + message);
        try {
            rc.broadcast(999999, 999999);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rc.resign();
    }

    public static void assertTrue(boolean b, RobotController rc, String message) {
        if (!b) {
            fail(rc, message + " (not true)");
        }
    }

    public static void assertFalse(boolean b, RobotController rc, String message) {
        if (b) {
            fail(rc, message + " (not false)");
        }
    }

    public static void assertEquals(int a, int b, RobotController rc, String message) {
        if (a != b) {
            fail(rc, message + " ( " + a + " != " + b + " )");
        }
    }

    public static void assertEquals(double a, double b, RobotController rc, String message) {
        if (Math.abs(a - b) > epsilon) {
            fail(rc, message + " ( " + a + " != " + b + " )");
        }
    }

    public static void assertLess(int a, int b, RobotController rc, String message) {
        if (a >= b) {
            fail(rc, message + " ( " + a + " >= " + b + " )");
        }
    }

    public static void assertLessEq(int a, int b, RobotController rc, String message) {
        if (a > b) {
            fail(rc, message + " ( " + a + " > " + b + " )");
        }
    }

    public static void assertGreater(int a, int b, RobotController rc, String message) {
        if (a <= b) {
            fail(rc, message + " ( " + a + " <= " + b + " )");
        }
    }

    public static void assertGreaterEq(int a, int b, RobotController rc, String message) {
        if (a < b) {
            fail(rc, message + " ( " + a + " < " + b + " )");
        }
    }

    // inclusive
    public static void assertBetween(int a, int b, int c, RobotController rc, String message) {
        if (a < b || a > c) {
            fail(rc, message + " ( " + a + " is not between " + b + " and " + c + " )");
        }
    }

    public static void run(RobotController rc) {
        BaseBot myself;

        switch (rc.getType()) {
            case HQ: myself = new HQ(rc); break;
            case TOWER: myself = new Tower(rc); break;
            case SUPPLYDEPOT: myself = new SupplyDepot(rc); break;
            case TECHNOLOGYINSTITUTE: myself = new TechnologyInstitute(rc); break;
            case BARRACKS: myself = new Barracks(rc); break;
            case HELIPAD: myself = new Helipad(rc); break;
            case TRAININGFIELD: myself = new TrainingField(rc); break;
            case TANKFACTORY: myself = new TankFactory(rc); break;
            case MINERFACTORY: myself = new MinerFactory(rc); break;
            case HANDWASHSTATION: myself = new HandwashStation(rc); break;
            case AEROSPACELAB: myself = new AerospaceLab(rc); break;
            case BEAVER: myself = new Beaver(rc); break;
            case COMPUTER: myself = new Computer(rc); break;
            case SOLDIER: myself = new Soldier(rc); break;
            case BASHER: myself = new Basher(rc); break;
            case MINER: myself = new Miner(rc); break;
            case DRONE: myself = new Drone(rc); break;
            case TANK: myself = new Tank(rc); break;
            case COMMANDER: myself = new Commander(rc); break;
            case LAUNCHER: myself = new Launcher(rc); break;
            case MISSILE: myself = new Missile(rc); break;
            default: myself = new BaseBot(rc); fail(rc, "Unsupported robot type"); break;
        }

        while (true) {
            try {
                myself.go();
            } catch (Exception e) {
                e.printStackTrace();
                fail(rc, e.getMessage());
            }
        }
    }

    public static class Util {
        public static Direction[] dirs = {Direction.NORTH, Direction.NORTH_WEST, Direction.WEST, Direction.SOUTH_WEST, Direction.SOUTH, Direction.SOUTH_EAST, Direction.EAST, Direction.NORTH_EAST};
        public static Direction randomDir() {
            return dirs[(int) (Math.random() * 8)];
        }
    }

    public static class BaseBot {
        protected RobotController rc;
        protected MapLocation myHQ, theirHQ;
        protected Team myTeam, theirTeam;
        protected RobotType myType;

        public BaseBot(RobotController rc) {
            this.rc = rc;
            this.myHQ = rc.senseHQLocation();
            this.theirHQ = rc.senseEnemyHQLocation();
            this.myTeam = rc.getTeam();
            this.theirTeam = this.myTeam.opponent();
            this.myType = rc.getType();
        }

        public void beginningOfTurn() {
        }

        public void endOfTurn() {
        }

        public void go() throws GameActionException {
            beginningOfTurn();
            execute();
            endOfTurn();
        }

        public void execute() throws GameActionException {
            rc.yield();
        }
    }

    public static class BaseStructure extends BaseBot {
        protected ArrayList<RobotType> spawnable;
        public BaseStructure(RobotController rc) {
            super(rc);
            spawnable = new ArrayList<RobotType>();
            for (RobotType type : RobotType.values()) {
                if (type.spawnSource == myType) {
                    spawnable.add(type);
                }
            }
        }
        public void trySpawn(RobotType type) throws GameActionException {
            Direction dir = Util.randomDir();
            for (int i = 0; i < 8; ++i) {
                if (rc.canSpawn(dir, type)) {
                    rc.spawn(dir, type);
                    return;
                }
                dir = dir.rotateRight();
            }
        }
        public void execute() throws GameActionException {
            if (rc.isCoreReady() && spawnable.size() > 0) {
                trySpawn(spawnable.get((int) (Math.random() * spawnable.size())));
            }
            rc.yield();
        }
    }

    public static class HQ extends BaseStructure {
        public HQ(RobotController rc) {
            super(rc);
        }

        public void testGetRoundNum0() {
            assertEquals(0, Clock.getRoundNum(), rc, "Initial round number is not 0");
            System.out.println("Pass round 0 Clock test");
        }

        public void testMoreRoundNum() {
            int beginRoundNum = Clock.getRoundNum();
            rc.yield();
            assertEquals(beginRoundNum + 1, Clock.getRoundNum(), rc, "Round number +1 test");
            rc.yield();
            rc.yield();
            assertEquals(beginRoundNum + 3, Clock.getRoundNum(), rc, "Round number +3 test");
            System.out.println("Pass Clock increments test");
        }

        public void testBytecodeCounting() throws GameActionException {
            rc.yield();
            // methods not listed should be free
            String s = "abcdefghijklmnopqrstuvwxyz";
            int bc = Clock.getBytecodeNum();
            String a = s.substring(5, 15);
            String b = s.substring(5, 15);
            String c = s.substring(5, 15);
            String d = s.substring(5, 15);
            String e = s.substring(5, 15);
            String f = s.substring(5, 15);
            String g = s.substring(5, 15);
            String h = s.substring(5, 15);
            String i = s.substring(5, 15);
            String j = s.substring(5, 15);
            int ebc = Clock.getBytecodeNum();
            System.out.println("10 calls to free function cost " + (ebc - bc) + " bytecodes");
            assertLess((ebc - bc), 80, rc, "Free function bytecode count");

            // fixed cost for listed methods
            String y = "abcdefghijklmnopqrstuvwxyy";
            bc = Clock.getBytecodeNum();
            int aa = y.compareTo(s);
            int ab = y.compareTo(s);
            int ac = y.compareTo(s);
            int ad = y.compareTo(s);
            int ae = y.compareTo(s);
            int af = y.compareTo(s);
            int ag = y.compareTo(s);
            int ah = y.compareTo(s);
            int ai = y.compareTo(s);
            int aj = y.compareTo(s);
            ebc = Clock.getBytecodeNum();
            System.out.println("10 calls to fixed function cost " + (ebc - bc) + " bytecodes");
            assertBetween((ebc - bc), 150, 250, rc, "Fixed cost function bytecode count");

            // make sure we overflow after 10000 bytecodes
            rc.yield();
            int rnd = Clock.getRoundNum();
            for (int r = 0; r < 40; ++r) {
                rc.transferSupplies(0, rc.getLocation());
            }
            int ernd = Clock.getRoundNum();
            assertEquals(ernd - rnd, 2, rc, "Two turns to transfer supplies 40 times");

            System.out.println("Pass bytecode counting test");
        }

        // make sure everything works on round 0
        public void testRound0Stuff() {
            assertEquals(0, Clock.getRoundNum(), rc, "Just making sure this test runs on round 0");

            MapLocation[] towers = rc.senseTowerLocations();
            assertTrue(towers.length <= 6 && towers.length > 0, rc, "6 towers");
            MapLocation[] etowers = rc.senseEnemyTowerLocations();
            assertTrue(etowers.length <= 6 && etowers.length > 0, rc, "6 enemy towers");

            // sense terrain tile (my sight range = 35)
            // sense ore (my sight range = 35)
            MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 50);
            for (MapLocation loc : locs) {
                if (loc.x <= rc.getLocation().x && loc.y <= rc.getLocation().y) {
                    TerrainTile tile = rc.senseTerrainTile(loc);
                    double ore = rc.senseOre(loc);
                    if (loc.distanceSquaredTo(rc.getLocation()) <= 35) {
                        assertTrue(tile == TerrainTile.NORMAL || tile == TerrainTile.VOID || tile == TerrainTile.OFF_MAP, rc, "tile within sensor");
                        assertTrue(ore >= 0, rc, "ore within sensor");
                    } else {
                        assertTrue(tile == TerrainTile.UNKNOWN, rc, "tile not within sensor");
                        assertTrue(ore < 0, rc, "ore not within sensor");
                    }
                }
            }

            // assert that all your towers are sense-able
            for (int i = 0; i < towers.length; ++i) {
                try {
                    assertTrue(rc.canSenseLocation(towers[i]), rc, "canSenseLocation");
                    assertTrue(rc.canSenseLocation(towers[i].add(4, 2)), rc, "canSenseLocation");
                    RobotInfo r = rc.senseRobotAtLocation(towers[i]);
                    assertTrue(r.type == RobotType.TOWER, rc, "senseRobotAtLocation");
                    assertFalse(rc.canSenseLocation(etowers[i]), rc, "canSenseLocation for enemy");
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(rc, e.getMessage());
                }
            }

            System.out.println("Pass round 0 testing");
        }

        private class RandomObject implements Comparable<RandomObject> {
            private int x;
            public RandomObject(int x) {
                this.x = x;
            }
            public int getX() {
                return x;
            }
            public int compareTo(RandomObject other) {
                return x - other.x;
            }
            public int hashCode() {
                return x;
            }
        }

        public void testJavaUtil() throws GameActionException {
            // just try creating everything, and doing basic operations
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.add(5);
            hs.contains(5);
            hs.contains(4);
            for (Integer i : hs) {
                int y = i;
            }
            hs.hashCode();
            hs.remove(5);
            hs.size();
            hs.clear();
            ArrayList<RandomObject> list = new ArrayList<RandomObject>();
            list.add(new RandomObject(4));
            list.add(new RandomObject(6));
            list.contains(new RandomObject(4));
            list.get(1);
            list.indexOf(new RandomObject(5));
            list.isEmpty();
            for (RandomObject o : list) {
                o.getX();
            }
            list.set(0, new RandomObject(10));
            list.toArray(new RandomObject[]{});
            list.remove(new RandomObject(10));
            list.hashCode();
            TreeSet<RandomObject> ts = new TreeSet<RandomObject>();
            int[] x = {1, 2, 5, 7, 3, 2, 3, 6};
            RandomObject[] y = {new RandomObject(5), new RandomObject(8), new RandomObject(10)};
            TreeMap<RandomObject, Integer> tm = new TreeMap<RandomObject, Integer>();
            tm.put(new RandomObject(5), 1);
            tm.keySet();
            tm.entrySet();
            tm.isEmpty();
            tm.containsKey(new RandomObject(3));
            tm.hashCode();
            HashMap<RandomObject, RandomObject> hm = new HashMap<RandomObject, RandomObject>();
            hm.put(new RandomObject(5), new RandomObject(10));
            hm.keySet();
            hm.entrySet();
            hm.size();
            hm.containsKey(new RandomObject(5));
            hm.hashCode();
            PriorityQueue<RandomObject> pq = new PriorityQueue<RandomObject>();
            ArrayDeque<RandomObject> ad = new ArrayDeque<RandomObject>();
            //BitSet bs = new BitSet(1000); // TODO: BITSET DOESN'T WORK
            //Scanner scanner = new Scanner("asdf"); // TODO: doesn't work but isn't supposed to anyways
            EnumMap<RobotType, Integer> em = new EnumMap<RobotType, Integer>(RobotType.class);
            //EnumSet<RobotType> es = new EnumSet<RobotType>(RobotType.class); // TODO: doesn't seem to work...
            Hashtable<RandomObject, Integer> ht = new Hashtable<RandomObject, Integer>();
            IdentityHashMap<RandomObject, Integer> ihm = new IdentityHashMap<RandomObject, Integer>();
            LinkedHashMap<RandomObject, Integer> lhm = new LinkedHashMap<RandomObject, Integer>();
            LinkedHashSet<RandomObject> lhs = new LinkedHashSet<RandomObject>();
            LinkedList<RandomObject> ll = new LinkedList<RandomObject>();
            ll.offer(new RandomObject(5));
            ll.pop();
            ll.peek();
            ll.hashCode();
            Random r = new Random();
            r.nextDouble();
            r.nextInt(Integer.MAX_VALUE);
            StringTokenizer st = new StringTokenizer("helo hi hello hi    hello");
            st.nextToken();
            st.nextToken();
            st.hasMoreTokens();
            Vector<RandomObject> v = new Vector<RandomObject>();
            //WeakHashMap<RandomObject, Integer> whm = new WeakHashMap<RandomObject, Integer>(); // TODO: why isn't this allowed?

            // we'll pick one data structure to do lots of operations on
            for (int i = 0; i < 100; ++i) {
                list.add(new RandomObject(i));
                hm.put(new RandomObject(i), new RandomObject(i * i));
            }

            // let's try out Collections
            Collections.shuffle(list);
            Collections.sort(list);
            Collections.binarySearch(list, new RandomObject(5));
            Collections.max(list);
            Collections.reverse(list);
            Collections.fill(list, new RandomObject(7));

            // let's try out Arrays
            Arrays.sort(x);
            Arrays.binarySearch(x, 10);
            Arrays.equals(x, x);
            Arrays.fill(x, 0, 5, 2);
            Arrays.hashCode(x);
            Arrays.toString(x);

            // let's try out Objects
            Objects.deepEquals(new RandomObject(5), new RandomObject(8));
            Objects.hashCode(new RandomObject(8));

            System.out.println("Pass java util testing");
        }

        public void testBroadcasts() throws GameActionException {
            rc.yield();
            assertEquals(0, rc.readBroadcast(100), rc, "broadcast1");
            rc.broadcast(100, -1000);
            assertEquals(-1000, rc.readBroadcast(100), rc, "broadcast2");
            rc.yield();
            assertEquals(-1000, rc.readBroadcast(100), rc, "broadcast3");
            rc.broadcast(65535, 1);
            rc.broadcast(0, 1);
            System.out.println("Pass broadcast testing");
        }

        public void execute() throws GameActionException {
            if (rc.getTeam() == Team.A) {
                testGetRoundNum0();
                testRound0Stuff();
                testJavaUtil();
                testBytecodeCounting();
                testMoreRoundNum();
                testBroadcasts();
                System.out.println("PASSED ALL TESTS!");
            }
            while (true) {
                rc.yield();
            }
        }
    }

    public static class Tower extends BaseBot {
        public Tower(RobotController rc) {
            super(rc);
        }
    }

    public static class SupplyDepot extends BaseStructure {
        public SupplyDepot(RobotController rc) {
            super(rc);
        }
    }

    public static class TechnologyInstitute extends BaseStructure {
        public TechnologyInstitute(RobotController rc) {
            super(rc);
        }
    }

    public static class Barracks extends BaseStructure {
        public Barracks(RobotController rc) {
            super(rc);
        }
    }

    public static class Helipad extends BaseStructure {
        public Helipad(RobotController rc) {
            super(rc);
        }
    }

    public static class TrainingField extends BaseStructure {
        public TrainingField(RobotController rc) {
            super(rc);
        }
    }

    public static class TankFactory extends BaseStructure {
        public TankFactory(RobotController rc) {
            super(rc);
        }
    }

    public static class MinerFactory extends BaseStructure {
        public MinerFactory(RobotController rc) {
            super(rc);
        }
    }

    public static class HandwashStation extends BaseStructure {
        public HandwashStation(RobotController rc) {
            super(rc);
        }
    }

    public static class AerospaceLab extends BaseStructure {
        public AerospaceLab(RobotController rc) {
            super(rc);
        }
    }

    public static class BaseUnit extends BaseBot {
        public BaseUnit(RobotController rc) {
            super(rc);
        }

        public void attackEverything() throws GameActionException {
            if (myTeam == Team.B) return; // only team A attacks
            RobotInfo[] nearby = rc.senseNearbyRobots(myType.attackRadiusSquared, Team.B);
            if (nearby.length > 0) {
                rc.attackLocation(nearby[0].location);
            }
        }

        public void execute() throws GameActionException {
        }
    }

    public static class Beaver extends BaseUnit {
        public Beaver(RobotController rc) {
            super(rc);
        }

        public void mineTests() throws GameActionException {
            for (int i = 0; i < 100; ++i) {
                double initialOre = rc.senseOre(rc.getLocation());
                while (!rc.isCoreReady()) {
                    rc.yield();
                }
                rc.mine();
                rc.yield();
                double oreAfter = rc.senseOre(rc.getLocation());
                double diff = initialOre - oreAfter;
                if (initialOre < 0.2) {
                    assertTrue(oreAfter == 0, rc, "mined to 0");
                } else if (initialOre < 0.2 * 20) {
                    assertEquals(diff, 0.2, rc, "min ore amount is 0.2");
                } else if (initialOre < 40) {
                    assertEquals(diff, initialOre / 20, rc, "min ore amount is div");
                } else {
                    assertEquals(diff, 2, rc, "mined max amt");
                }
            }

            System.out.println("pass beaver mine tests");
        }

        public void buildTests() throws GameActionException {
            long what = rc.getControlBits();
            RobotType type = null;
            switch ((int) what) {
                case 0: type = null; break;
                case 1: type = RobotType.SUPPLYDEPOT; break;
                case 2: type = RobotType.TECHNOLOGYINSTITUTE; break;
                case 3: type = RobotType.BARRACKS; break;
                case 4: type = RobotType.HELIPAD; break;
                case 5: type = RobotType.TRAININGFIELD; break;
                case 6: type = RobotType.TANKFACTORY; break;
                case 7: type = RobotType.MINERFACTORY; break;
                case 8: type = RobotType.HANDWASHSTATION; break;
                case 9: type = RobotType.AEROSPACELAB; break;
            }
            if (type != null) {
                rc.build(Direction.NORTH, type);
            }
        }

        public void execute() throws GameActionException {
            if (myTeam == Team.A) {
                mineTests();
            } else {
                for (int i = 0; i < 5; ++i) {
                    rc.yield(); // gives me time to set control bits
                }
                buildTests();
            }

            System.out.println("PASSED ALL TESTS!");
            while (true) {
                rc.yield();
            }
        }
    }

    public static class Miner extends BaseUnit {
        public Miner(RobotController rc) {
            super(rc);
        }

        public void mineTests() throws GameActionException {
            for (int i = 0; i < 100; ++i) {
                double initialOre = rc.senseOre(rc.getLocation());
                while (!rc.isCoreReady()) {
                    rc.yield();
                }
                rc.mine();
                rc.yield();
                double oreAfter = rc.senseOre(rc.getLocation());
                double diff = initialOre - oreAfter;
                if (initialOre < 0.2) {
                    assertTrue(oreAfter == 0, rc, "mined to 0 (miner)");
                } else if (initialOre < 0.2 * 4) {
                    assertEquals(diff, 0.2, rc, "min ore amount is 0.2 (miner)");
                } else if (initialOre < 12) {
                    assertEquals(diff, initialOre / 4, rc, "min ore amount is div (miner)");
                } else {
                    assertEquals(diff, 3, rc, "mined max amt (miner)");
                }
            }

            System.out.println("pass beaver mine tests");
        }

        public void execute() throws GameActionException {
            mineTests();

            System.out.println("PASSED ALL TESTS!");
            while (true) {
                rc.yield();
            }
        }
    }

    public static class Computer extends BaseUnit {
        public Computer(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
        }
    }

    public static class Soldier extends BaseUnit {
        public Soldier(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            while (true) {
                if (rc.isWeaponReady()) {
                    attackEverything();
                }
            }
        }
    }

    public static class Basher extends BaseUnit {
        public Basher(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
        }
    }

    public static class Drone extends BaseUnit {
        public Drone(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
        }
    }

    public static class Tank extends BaseUnit {
        public Tank(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            while (true) {
                if (rc.isWeaponReady()) {
                    attackEverything();
                }
            }
        }
    }

    public static class Commander extends BaseUnit {
        public Commander(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            while (true) {
                if (rc.isWeaponReady()) {
                    attackEverything();
                }
            }
        }
    }

    public static class Launcher extends BaseUnit {
        public Launcher(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
        }
    }

    public static class Missile extends BaseUnit {
        public Missile(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            boolean exception = false;
            try {
                rc.broadcast(5, 5);
            } catch (Exception e) {
                exception = true;
            }
            assertTrue(exception, rc, "missile broadcast");
            System.out.println("Pass missile broadcast test!");

            while (true) {
                rc.yield();
            }
        }
    }
}
