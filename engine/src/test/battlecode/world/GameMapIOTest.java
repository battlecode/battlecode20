package battlecode.world;

import battlecode.common.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class GameMapIOTest {

    final static ClassLoader loader = GameMapIOTest.class.getClassLoader();

    @Test
    public void testFindsDefaultMap() throws IOException {
        // will throw exception if default map can't be loaded
//        GameMapIO.loadMap("maptest", null);
    }

    // @Test
    // public void testFindsPackageMap() throws IOException {
    //     LiveMap readMap = GameMapIO.loadMapAsResource(loader,
    //             "battlecode/world/resources", "clearMap");
    //     assertEquals(readMap.getMapName(), "clearMap");
    //     assertEquals(readMap.getHeight(), 50.0, 0);
    //     assertEquals(readMap.getWidth(), 50.0, 0);
    //     assertEquals(readMap.getSeed(), 128);
    //     assertEquals(readMap.getOrigin().x, 0.0, 0);
    //     assertEquals(readMap.getOrigin().y, 0.0, 0);
    // }
}
