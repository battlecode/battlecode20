package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generate a map.
 */
public class Swirl {

    // change this!!!
    public static final String mapName = "Swirl";

    // don't change this!!
    public static final String outputDirectory = "engine/src/main/battlecode/world/resources/";

    private static int width;
    private static int height;

    private static boolean usesIndex;

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
        if (usesIndex) {
            return (height - y)*65 + x + 1;
        }
        return (height-1-y)*width + x;
    }

    public static void makeSimple() throws IOException {

        String ds = "52	41	r	100	900	-2																																														 "+
"h1	3	1	1	s300	s100	s100	s100	s200	s200	s200	s200	s200	80	77	70	55	20	15	35	35	35	35	35	25	20	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	1	1	s300	s7	s7	s7	s200	s200	10	s300	s100	74	69	54	32	20	15	35	50	50	50	35	25	20	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	6	1	1	s300	s200	4	s200	s200	s200	s200	s9	s300	67	53	42	31	20	30	14	50	S40	50	35	25	17	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	6	1	1	1	s200	1	7	1	1	1	1	8	65	52	40	30	20	30	14	50	50	50	35	25	16	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	6	1	1	1	1	1	7	1	s120	1	7	63	51	38	29	29	20	30	14	35	35	35	15	15	20	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	4	6	1	100	100	1	7	1	1	1	7	50	37	28	22	23	13	13	13	14	14	14	30	30	20	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	4	6	1	100	100	1	1	6	1	6	49	36	28	21	12	12	33	37	14	21	55	210	50	35	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	4	7	1	1	100	100	1	1	6	35	35	27	20	11	28	29	74	77	14	25	93	222	50	35	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"5	2	4	8	1	1	1	1	1	6	34	26	19	10	27	58	59	121	144	14	28	122	255	50	35	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"5	2	4	8	8	5	5	1	1	6	33	26	19	10	23	56	88	230	280	310	15	29	150	266	288	21	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"5	2	4	5	5	5	5	5	5	5	25	18	9	100	120	140	77	340	350	355	15	32	33	170	180	299	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"5	2	70	28	25	21	18	15	13	11	25	17	9	29	30	30	160	180	514	537	375	16	17	34	34	190	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"4	2	68	8	99	88	S400	77	24	40	25	17	9	19	19	20	31	32	190	721	576	388	399	412	489	512	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"7	4	2	66	66	39	32	28	42	24	16	8	200	9	9	9	21	22	33	210	733	588	601	623	659	500	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"7	4	2	64	62	58	54	50	48	24	14	8	500	250	260	280	10	10	23	34	222	755	777	798	800	820	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"13	7	4	2	2	9	9	1	1	1	14	8	721	522	563	583	320	330	11	24	34	223	840	850	888	11	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"16	12	7	3	3	2	2	9	8	8	1	8	892	743	748	781	592	623	377	12	25	35	226	902	905	11	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"20	15	11	8	8	14	14	29	28	44	7	1	930	898	905	917	919	642	379	12	26	36	229	1002	1291	10	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"20	20	9	16	15	31	30	50	47	52	7	1	300	940	954	958	977	922	693	384	13	c27	37	250	10	1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"S300	20	9	17	32	56	53	58	55	138	153	6	1	311	312	30	30	30	723	410	13	28	37	252	10	1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"20	20	10	33	57	68	63	121	133	152	168	6	1	410	313	30	Sc80	30	754	411	13	29	38	253	9	1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"20	10	16	34	60	83	120	132	149	164	189	6	1	413	319	30	30	30	800	785	412	29	39	256	9	1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"20	10	20	35	63	91	21	19	14	15	11	210	5	1	417	323	325	334	812	8	799	439	40	8	1	749	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"11	21	36	67	95	120	16	29	25	28	12	214	5	1	512	419	432	487	8	814	831	888	451	8	1	704	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"11	22	38	71	97	210	17	24	c30	25	19	239	5	1	601	534	555	7	512	515	523	7	7	1	694	785	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"12	23	40	75	98	222	18	27	23	25	14	253	5	1	700	604	612	7	6	6	6	1	1	684	765	240	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"12	24	43	79	99	231	14	16	12	18	14	264	5	1	5	5	5	5	1	1	1	684	632	745	512	220	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"24	43	82	100	231	18	18	18	15	0	0	0	0	c5	1	1	1	1	431	345	692	703	732	485	254	180	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"44	85	129	253	18	22	22	0	0	3	3	3	3	1	5	354	431	342	675	613	691	439	319	235	183	140	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"44	87	143	264	0	0	0	2	2	10	10	11	12	1	5	323	543	576	631	683	402	310	219	188	199	105	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"88	153	288	0	0	2	2	9	9	38	41	45	58	1	5	453	493	532	632	22	25	24	21	19	78	80	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"89	299	0	2	2	8	9	31	35	52	54	56	72	1	5	512	514	534	812	24	30	s40	30	21	77	75	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"90	311	0	2	8	21	19	14	15	11	83	75	93	1	5	683	673	721	829	21	40	S50	40	24	74	74	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"313	0	1	8	19	16	29	25	28	12	184	122	215	284	1	6	8	23	30	23	30	40	30	22	72	75	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"315	0	1	7	18	17	24	S30	25	19	242	218	293	321	1	6	10	25	32	25	25	23	24	23	71	73	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"317	0	1	7	17	18	27	23	25	21	19	14	15	11	1	1	6	12	27	29	41	43	54	58	68	71	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"319	0	1	7	16	s14	16	12	18	16	29	25	28	12	75	1	7	14	15	17	30	33	45	46	61	69	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-3	1	6	15	23	28	37	47	50	17	24	c30	25	19	70	84	1	7	7	7	18	19	35	38	49	67	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-2	1	6	14	22	26	33	43	49	18	27	23	25	14	69	81	94	1	1	1	7	7	20	21	39	52	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-1	1	6	13	20	30	S200	40	48	14	16	12	18	14	68	79	91	102	105	106	108	109	132	154	166	166	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	1	6	12	27	38	39	s47	50	52	56	58	61	64	78	88	100	104	106	108	109	132	154	166	178	190	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x";

        String[] splitDirt = ds.split("\\s+");

        width = Integer.parseInt(splitDirt[0]);
        height = Integer.parseInt(splitDirt[1]);
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 43223);
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
        int a = Integer.parseInt(splitDirt[3]);
        int b = Integer.parseInt(splitDirt[4]);
        int waterr = Integer.parseInt(splitDirt[5]);

        // check if there's a comment
        int startIndex = 6;
        while (splitDirt[startIndex].equals("/*")) {
            while (!splitDirt[startIndex].equals("*/")) {
                startIndex++;
            }
            startIndex++;
        }

        String[] dirtGrid = Arrays.copyOfRange(splitDirt, startIndex, splitDirt.length);

//        assert dirtGrid.length == width * height;

        if (dirtGrid[0].equals("indx")) {
            usesIndex = true;
        } else {
            usesIndex = false;
        }

        if (usesIndex) {
            assert dirtGrid.length == 65 * 65;
        } else {
            assert dirtGrid.length == width*height;
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int idx = loc2index(x,y);
                if (dirtGrid[idx].equals("x"))
                    continue;
                if (dirtGrid[idx].startsWith("w")) {
                    mapBuilder.setSymmetricWater(x,y,true);
                    mapBuilder.setSymmetricDirt(x,y,GameConstants.MIN_WATER_ELEVATION);
                    dirtGrid[idx] = dirtGrid[idx].substring(1);
                }
                if (dirtGrid[idx].startsWith("W")) {
                    mapBuilder.setSymmetricWater(x,y,true);
                    mapBuilder.setSymmetricDirt(x,y,waterr);
                    dirtGrid[idx] = dirtGrid[idx].substring(1);
                }
                if (dirtGrid[idx].startsWith("s")) {
                    mapBuilder.setSymmetricSoup(x,y,a);
                    dirtGrid[idx] = dirtGrid[idx].substring(1);
                }
                if (dirtGrid[idx].startsWith("S")) {
                    mapBuilder.setSymmetricSoup(x,y,b);
                    dirtGrid[idx] = dirtGrid[idx].substring(1);
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
