package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generate a map.
 */
public class Climb {

    // change this!!!
    public static final String mapName = "Climb";

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

        String ds = "40	40	r	117	300	-5	/* Symbols: 'x' for symmetry-inferred, 'w' for infinite-depth water, 'W' for W-depth water, 's' and 'S' for soup, 'c' for cow, 'h' for HQ. Append a number to set elevation. 'Order must be w,s,c,h; so Wsch10' is valid syntactically (but not logically: the HQ can't start in water or have a cow on it) */																																																										 "+
"indx	0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16	17	18	19	20	21	22	23	24	25	26	27	28	29	30	31	32	33	34	35	36	37	38	39	40	41	42	43	44	45	46	47	48	49	50	51	52	53	54	55	56	57	58	59	60	61	62	63 "+
"39	w	s1	2	s2	4	7	10	13	16	19	22	25	28	31	34	37	s40	43	46	49	52	55	58	61	64	67	70	73	76	79	82	85	88	91	94	97	100	S100	100	S100	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"38	s1	s1	2	s2	4	7	10	13	16	19	22	25	28	31	34	37	s40	43	46	49	52	55	58	61	64	67	70	73	76	79	82	85	88	91	94	97	100	S100	100	100	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"37	2	2	2	s2	h4	7	10	13	16	19	22	25	28	31	34	37	s40	43	46	49	52	55	58	61	64	67	70	73	76	79	82	85	88	91	94	97	100	S100	S100	S100	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"36	s2	s2	s2	s2	4	7	10	13	16	19	22	25	28	31	34	37	s40	43	46	49	52	55	58	61	64	67	70	73	76	79	82	85	88	91	94	97	s100	100	100	100	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"35	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	103	103	103	103	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"34	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	106	106	106	106	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"33	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	109	109	109	109	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"32	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	112	112	112	112	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"31	x	x	x	x	337	337	337	337	340	343	346	349	352	355	358	361	S364	367	370	373	376	379	382	385	388	391	394	397	400	400	400	400	x	x	x	x	115	115	115	115	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"30	x	x	x	x	337	337	337	337	340	343	346	349	352	355	358	361	S364	367	370	373	376	379	382	385	388	391	394	397	400	400	400	400	x	x	x	x	118	118	118	118	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"29	x	x	x	x	337	337	337	337	340	343	346	349	352	355	358	361	S364	367	370	373	376	379	382	385	388	391	394	397	400	400	400	400	x	x	x	x	121	121	121	121	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"28	x	x	x	x	337	337	337	337	340	343	346	349	352	355	358	361	S364	367	370	373	376	379	382	385	388	391	394	397	400	400	400	400	x	x	x	x	124	124	124	124	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"27	x	x	x	x	334	334	334	334	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	403	403	403	403	x	x	x	x	127	127	127	127	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"26	x	x	x	x	331	331	331	331	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	s406	s406	s406	s406	x	x	x	x	s130	s130	s130	s130	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"25	x	x	x	x	328	328	328	328	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	409	409	409	409	x	x	x	x	133	133	133	133	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"24	x	x	x	x	325	325	325	325	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	412	412	412	412	x	x	x	x	136	136	136	136	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"23	x	x	x	x	322	322	322	322	x	x	x	x	493	493	493	493	s496	s499	500	500	500	500	499	s496	x	x	x	x	415	415	415	415	x	x	x	x	139	139	139	139	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"22	x	x	x	x	319	319	319	319	x	x	x	x	493	493	493	493	s496	499	500	500	500	500	s499	496	x	x	x	x	418	418	418	418	x	x	x	x	142	142	142	142	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"21	x	x	x	x	316	316	316	316	x	x	x	x	493	493	493	493	496	499	S500	S500	S500	S500	499	496	x	x	x	x	421	421	421	421	x	x	x	x	145	145	145	145	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"20	x	x	x	x	313	313	313	313	x	x	x	x	493	493	493	493	496	499	S500	WS	WS	S500	499	496	x	x	x	x	424	424	424	424	x	x	x	x	148	148	148	148	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"19	x	x	x	x	s310	s310	s310	s310	x	x	x	x	490	490	490	490	x	x	x	x	x	x	x	x	x	x	x	x	427	427	427	427	x	x	x	x	151	151	151	151	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"18	x	x	x	x	307	307	307	307	x	x	x	x	487	487	487	487	x	x	x	x	x	x	x	x	x	x	x	x	430	430	430	430	x	x	x	x	154	154	154	154	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"17	x	x	x	x	304	304	304	304	x	x	x	x	484	484	484	484	x	x	x	x	x	x	x	x	x	x	x	x	433	433	433	433	x	x	x	x	157	157	157	157	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"16	x	x	x	x	301	301	301	301	x	x	x	x	481	481	481	481	x	x	x	x	x	x	x	x	x	x	x	x	436	436	436	436	x	x	x	x	s160	s160	s160	s160	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"15	x	x	x	x	298	298	298	298	x	x	x	x	478	478	478	478	475	472	469	466	463	460	457	454	451	448	445	442	439	439	439	439	x	x	x	x	163	163	163	163	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"14	x	x	x	x	295	295	295	295	x	x	x	x	478	478	478	478	475	472	469	466	463	460	457	454	451	448	445	442	439	439	439	439	x	x	x	x	166	166	166	166	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"13	x	x	x	x	292	292	292	292	x	x	x	x	478	478	478	478	475	472	469	466	463	460	457	454	451	448	445	442	439	439	439	439	x	x	x	x	169	169	169	169	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"12	x	x	x	x	289	289	289	289	x	x	x	x	478	478	478	478	475	472	469	466	463	460	457	454	451	448	445	442	439	439	439	439	x	x	x	x	172	172	172	172	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"11	x	x	x	x	286	286	286	286	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	175	175	175	175	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"10	x	x	x	x	283	283	283	283	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	178	178	178	178	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"9	x	x	x	x	280	280	280	280	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	181	181	181	181	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"8	x	x	x	x	277	277	277	277	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	184	184	184	184	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"7	x	x	x	x	274	274	274	274	271	268	265	262	259	256	253	s250	247	244	241	238	235	232	229	226	223	s220	217	214	211	208	205	202	199	196	193	190	s187	s187	s187	187	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"6	x	x	x	x	274	274	274	274	271	268	265	262	259	256	253	s250	247	244	241	238	235	232	229	226	223	s220	217	214	211	208	205	202	199	196	193	190	s187	S187	S187	187	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"5	x	x	x	x	s274	s274	274	274	271	268	265	262	259	256	253	s250	247	244	241	238	235	232	229	226	223	s220	217	214	211	208	205	202	199	196	193	190	s187	S187	S187	187	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"4	x	x	x	x	274	s274	274	274	271	268	265	262	259	256	253	s250	247	244	241	238	235	232	229	226	223	s220	217	214	211	208	205	202	199	196	193	190	187	187	187	187	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-1																																																																 "+
"-2																																																																 "+
"-3																																																																 "+
"-4																																																																 "+
"-5																																																																 "+
"-6																																																																 "+
"-7																																																																 "+
"-8																																																																 "+
"-9																																																																 "+
"-10																																																																 "+
"-11																																																																 "+
"-12																																																																 "+
"-13																																																																 "+
"-14																																																																 "+
"-15																																																																 "+
"-16																																																																 "+
"-17																																																																 "+
"-18																																																																 "+
"-19																																																																 "+
"-20																																																																 "+
"-21																																																																 "+
"-22																																																																 "+
"-23																																																																 "+
"-24																																																																";

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
