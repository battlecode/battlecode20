package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Generate a map.
 */
public class GSF {

    // change this!!!
    public static final String mapName = "GSF";

    // don't change this!!
    public static final String outputDirectory = "engine/src/main/battlecode/world/resources/";

    private static int width;
    private static int height;

    /**
     * @param args unused
     */
    public static void main(String[] args) {
        try {
            makeSimple();
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Generated a map!");
    }

    public static ArrayList<ArrayList<Boolean>> makemap(Boolean contents,int w,int h) {
        ArrayList<ArrayList<Boolean>> arr = new ArrayList<>();
        for (int i=0; i<h; i++) {
            ArrayList<Boolean> b = new ArrayList<>();
            for (int j=0; j<w; j++) {
                b.add(contents);
            }
            arr.add(b);
        }
        return arr;
    }

    public static int loc2index(int x, int y) {
        return y*width + x;
    }

    public static void makeSimple() throws IOException {

        String ds = "56\t42\tr\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
                "1\t1\t1\t1\t1\ts\t1\ts\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\ts\ts\t1\ts\ts\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\ts\ts\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\ts\ts\ts\t1\t1\t1\tc1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\ts\t1\ts\t1\t1\ts\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\ts\t1\t1\ts\t1\ts\ts\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\ts\t1\t1\t1\t1\ts\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t1\ts\ts\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t0\t0\t1\t4\t4\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t0\t0\t4\t4\t4\t4\t4\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\ts\t0\t4\t4\t4\t4\t4\t4\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\ts\ts\t4\t4\t4\t4\t4\t4\t4\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "3\t3\t3\t3\t3\t3\t2\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\ts\t4\t4\th4\t4\t4\t4\t4\t4\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t3\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\ts\t4\t4\t4\t4\t4\t4\t4\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\ts\t4\t4\t4\t4\t4\t4\t0\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\t-3\tc-3\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\ts\ts\t4\t0\t0\t0\t0\t0\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\t-3\t-3\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t0\t0\t0\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\tS\t-3\t-3\t1\t1\t3\t2\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t-1\t-1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\tS\t-3\t1\t1\t3\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t-1\t-1\t-1\t-1\t-1\t-1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\t-3\t-3\t1\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t-1\t-1\t-1\t-1\t-1\t-1\tw\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\tS\t-3\t1\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\tS\t-3\t-3\t1\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t-1\t-1\t-1\t-1\t-1\t-1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "c-3\t-3\t-3\t-3\t1\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t-1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\tS\t-3\t-3\t1\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\tS\t-3\t1\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tc1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\t-3\t-3\t1\t1\t3\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\tS\t-3\t1\t1\t3\t2\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t2\t2\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\tS\t-3\t-3\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t3\t3\t2\t2\t3\t4\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\t-3\t-3\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t2\t3\t3\t4\t4\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\tS\t-3\t-3\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t2\t3\t4\t4\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\tS\t-3\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t2\t3\t4\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\t-3\t-3\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\tS\t-3\t-3\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "-3\t-3\t-3\tc-3\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t3\t2\t1\t1\t1\t1\t1\t1\t0\t0\t0\t0\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t3\t3\t3\t2\t1\t1\t1\t1\t1\t0\t0\t0\t1\t0\t0\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "3\t3\t3\t3\t3\t2\t2\t2\t1\t1\t1\t1\t1\t0\t0\t1\tw\t0\t0\t0\t0\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t1\t1\t0\t1\t1\t1\t0\t0\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t0\t0\t1\t1\t0\t0\t1\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t0\t0\t0\t0\t0\t0\t1\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t0\t0\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\n" +
                "1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t1\t0\t1\t1\t1\t1\t1\t1\t1\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx\tx";

        String[] splitDirt = ds.split("\\s+");

        width = Integer.parseInt(splitDirt[0]);
        height = Integer.parseInt(splitDirt[1]);
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 546);
        mapBuilder.setWaterLevel(0);
        String symmetry = splitDirt[2];
        switch (symmetry) {
            case "r":
                mapBuilder.setSymmetry(MapBuilder.MapSymmetry.rotational);
                break;
            case "h":
                mapBuilder.setSymmetry(MapBuilder.MapSymmetry.horizontal);
                break;
            case "v":
                mapBuilder.setSymmetry(MapBuilder.MapSymmetry.vertical);
                break;
            default:
                throw new RuntimeException("symmetry not specified in google sheets!!!");
        }

        String[] dirtGrid = Arrays.copyOfRange(splitDirt, 3, splitDirt.length);

        assert dirtGrid.length == width * height;


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int idx = loc2index(x,y);
                if (dirtGrid[idx].equals("x"))
                    continue;
                if (dirtGrid[idx].equals("w")) {
                    mapBuilder.setSymmetricWater(x,y,true);
                    mapBuilder.setSymmetricDirt(x,y,GameConstants.MIN_WATER_ELEVATION);
                    continue;
                }
                if (dirtGrid[idx].equals("s")) {
                    mapBuilder.setSymmetricDirt(x,y,1);
                    mapBuilder.setSymmetricSoup(x,y,100);
                    continue;
                }
                if (dirtGrid[idx].equals("S")) {
                    mapBuilder.setSymmetricDirt(x,y,-2);
                    mapBuilder.setSymmetricSoup(x,y,500);
                    continue;
                }
                if (dirtGrid[idx].startsWith("c")) {
                    mapBuilder.addSymmetricCow(x,y);
                    dirtGrid[idx] = dirtGrid[idx].substring(1);
                }
                if (dirtGrid[idx].startsWith("h")) {
                    mapBuilder.addSymmetricHQ(x,y);
                    dirtGrid[idx] = dirtGrid[idx].substring(1);
                }
                try {
                    int d = Integer.parseInt(dirtGrid[idx]);
                    mapBuilder.setSymmetricDirt(x,y,d);
                } catch (NumberFormatException e) {
                    System.out.println("INvalid: " + dirtGrid[idx]);
                    System.out.println("Invalid dirt at position (" + x + "," + y + "). Ignoring this.");
                }
            }
        }




        mapBuilder.saveMap(outputDirectory);

    }
    public static void setWaterInCyl(MapBuilder mapBuilder, float centerX, float centerY, float radius) {
        for (int[] point : pointsInCyl(centerX, centerY, radius)) {
            mapBuilder.setSymmetricWater(point[0],point[1],true);
        }
    }
    public static void setHeightInCyl(MapBuilder mapbuilder, float centerX, float centerY, float radius, int height) {
        for (int [] point : pointsInCyl(centerX, centerY, radius)) {
            mapbuilder.setSymmetricDirt(point[0], point[1], height);
        }
    }

    public static void addRectangleDirt(MapBuilder mapBuilder, int xl, int yb, int xr, int yt, int v) {
        for (int i = xl; i < xr+1; i++) {
            for (int j = yb; j < yt+1; j++) {
                mapBuilder.setSymmetricDirt(i, j, v);
                if (((i + j) % 10) == 0 && j >= 35)
                    try {
                        mapBuilder.addSymmetricCow(i, j);
                    } catch (RuntimeException e) {}
            }
        }
    }
    public static void addRectangleWater(MapBuilder mapBuilder, int xl, int yb, int xr, int yt, int v) {
        for (int i = xl; i < xr+1; i++) {
            for (int j = yb; j < yt+1; j++) {
                mapBuilder.setSymmetricDirt(i, j, v);
                mapBuilder.setSymmetricWater(i, j, true);
            }
        }
    }
    public static void setSoupInCyl(MapBuilder mapbuilder, float centerX, float centerY, float radius, int soup) {
        for (int [] point : pointsInCyl(centerX, centerY, radius)) {
            mapbuilder.setSymmetricSoup(point[0], point[1], soup);
        }
    }
    public static int[][] pointsInCyl(float centerX, float centerY, float radius) {
        ArrayList<int[]> points = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2)) <= radius)
                    points.add(new int[]{x, y});
            }
        }
        return points.toArray(new int[0][]);
    }


    /*
     * Add a nice circular lake centered at (x,y).
     */
    public static void addSoup(MapBuilder mapBuilder, int x, int y, int r2, int v) {
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int d = (xx-x)*(xx-x)/2 + (yy-y)*(yy-y);
                if (d <= r2) {
                    mapBuilder.setSymmetricSoup(xx, yy, v*(d+v));
                }
            }
        }
    }

    /*
     * Add a nice circular lake centered at (x,y).
     */
    public static void addLake(MapBuilder mapBuilder, int x, int y, int r2, int v) {
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int d = (xx-x)*(xx-x) + (yy-y)*(yy-y);
                if (d <= r2) {
                    mapBuilder.setSymmetricWater(xx, yy, true);
                    mapBuilder.setSymmetricDirt(xx, yy, v);
                }
            }
        }
        mapBuilder.setSymmetricDirt(x, y, GameConstants.MIN_WATER_ELEVATION);
    }
}
