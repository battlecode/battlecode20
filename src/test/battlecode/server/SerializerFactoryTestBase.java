package battlecode.server;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.engine.signal.Signal;
import battlecode.serial.*;
import battlecode.serial.notification.PauseNotification;
import battlecode.serial.notification.ResumeNotification;
import battlecode.serial.notification.RunNotification;
import battlecode.serial.notification.StartNotification;
import battlecode.server.serializer.Serializer;
import battlecode.server.serializer.SerializerFactory;
import battlecode.world.GameMap;
import battlecode.world.ZombieSpawnSchedule;
import battlecode.world.signal.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by james on 7/28/15.
 */
public abstract class SerializerFactoryTestBase {
    static final Map<GameMap.MapProperties, Integer> properties = new HashMap<>();
    static {
        properties.put(GameMap.MapProperties.HEIGHT, 3);
        properties.put(GameMap.MapProperties.WIDTH, 3);
        properties.put(GameMap.MapProperties.ROUNDS, 2000);
        properties.put(GameMap.MapProperties.SEED, 12345);
    }

    static final ZombieSpawnSchedule zSchedule = new ZombieSpawnSchedule();
    static {
        zSchedule.add(5, RobotType.RANGEDZOMBIE, 10);
        zSchedule.add(10, RobotType.FASTZOMBIE, 4);
    }

    static final double[][] parts = new double[][] {
            new double[] {10, 11, 12},
            new double[] {13, 14, 15},
            new double[] {16, 17, 18},
    };

    static final double[][] rubble = new double[][] {
            new double[] {0, 1, 2},
            new double[] {3, 4, 5},
            new double[] {6, 7, 8},
    };

    static final Set<GameMap.InitialRobotInfo> initialRobots = new HashSet<>();
    static {
        initialRobots.add(new GameMap.InitialRobotInfo(10, 100, RobotType.ARCHON, Team.B));
    }

    static final GameMap gameMap = new GameMap(properties,
            rubble,
            parts,
            zSchedule,
            initialRobots,
            "Test Map");

    static final long[][] teamMemories = new long[][] {
            new long[] {1, 2, 3, 4, 5},
            new long[] {1, 2, 3, 4, 5},
    };

    // An array with a sample object from every type of thing we could ever want to serialize / deserialize.
    static final Object[] serializeableObjects = new Object[]{
            PauseNotification.INSTANCE,
            ResumeNotification.INSTANCE,
            RunNotification.forever(),
            StartNotification.INSTANCE,
            new MatchInfo("Team 1", "Team 2", new String[] {"Map 1", "Map 2"}),
            new MatchHeader(gameMap, teamMemories, 0, 3),
            new RoundDelta(new Signal[] {
                    new AttackSignal(57, new MapLocation(1,1)),
                    new BroadcastSignal(57, Team.B, new HashMap<>()),
                    new BuildSignal(57, new MapLocation(1,1), RobotType.GUARD, Team.A, 50),
                    new BytecodesUsedSignal(new int[] {5, 6}, new int[] {17, 32}),
                    new ClearRubbleSignal(57, new MapLocation(1, 1), 5),
                    new ControlBitsSignal(0, 0),
                    new DeathSignal(57),
                    new HealthChangeSignal(new int[] {5, 6}, new double[] {17.21, 32}),
                    new IndicatorDotSignal(57, Team.B, new MapLocation(0,0), 0, 0, 0),
                    new IndicatorLineSignal(57, Team.B, new MapLocation(0,0), new MapLocation(1,1), 0, 0, 0),
                    new IndicatorStringSignal(57, 0, "Test Indicator String"),
                    new InfectionSignal(new int[] {5, 6}, new int[] {1, 0},
                            new int[] {10, 5}),
                    new MatchObservationSignal(57, "test"),
                    new MovementOverrideSignal(0, new MapLocation(10000, 10000)),
                    new MovementSignal(57, new MapLocation(0, 0), 0),
                    new RubbleChangeSignal(new MapLocation(0, 0), 5),
                    new RobotDelaySignal(new int[] {5, 6}, new double[] {17, 32}, new double[] {10, 2.5}),
                    new SpawnSignal(120, 0, new MapLocation(5, 6), RobotType.ZOMBIEDEN, Team.ZOMBIE, 0),
                    new TeamResourceSignal(Team.A, 100),
                    new TypeChangeSignal(57, RobotType.TTM)
            }),
            new MatchFooter(Team.A, teamMemories),
            new RoundStats(100, 100),
            new GameStats(),
            DominationFactor.BARELY_BEAT,
            new ExtensibleMetadata()
    };

    /**
     * Runs all the objects we're going to serialize through a serializer-deserializer pair.
     *
     * @param serializerFactory The factory to create serializers with.
     * @throws IOException
     */
    public void testRoundTrip(final SerializerFactory serializerFactory) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final Serializer serializer = serializerFactory.createSerializer(output, null);
        for (int i = 0; i < serializeableObjects.length; i++) {
            try {
                serializer.serialize(serializeableObjects[i]);
            } catch (final IOException e) {
                throw new IOException("Couldn't serialize object of class: " +
                        serializeableObjects[i].getClass().getCanonicalName(), e);
            }
        }
        serializer.close();
        output.close();

        final ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        final Serializer deserializer = serializerFactory.createSerializer(null, input);

        for (int i = 0; i < serializeableObjects.length; i++) {
            final Object result;
            try {
                result = deserializer.deserialize();
            } catch (final IOException e) {
                throw new IOException("Couldn't deserialize object of class: " +
                        serializeableObjects[i].getClass().getCanonicalName(), e);
            }

            // TODO assertEquals(serializeableObjects[i], result);
            // For this to work, we'll need to override Object.equals on any class we want to serialize.
        }
    }
}
