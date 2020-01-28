package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generate a map.
 */
public class DoesNotExist {

    // change this!!!
    public static final String mapName = "DoesNotExist";

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

        String ds = "64	54	h	133	250	-4	/* Symbols: 'x' for symmetry-inferred, 'w' for infinite-depth water, 'W' for W-depth water, 's' and 'S' for soup, 'c' for cow, 'h' for HQ. Append a number to set elevation. 'Order must be w,s,c,h; so Wsch13' is valid syntactically (but not logically: the HQ can't start in water or have a cow on it) */																																																										 "+
"indx	0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16	17	18	19	20	21	22	23	24	25	26	27	28	29	30	31	32	33	34	35	36	37	38	39	40	41	42	43	44	45	46	47	48	49	50	51	52	53	54	55	56	57	58	59	60	61	62	63 "+
"53	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"52	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"51	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"50	3	3	3	10	10	10	3	3	10	14	3	3	h3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	5	c4	c10	3	3	3	3	3	4	3	3	3	3	3	3	3	3	3	3	4	4	3	3	3	3	3	3	3 "+
"49	3	3	3	10	w	10	3	3	10	14	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	c8	4	3	3	3	3	3	3	S8	3	3	3	3	3	3	3	3	3	3	3	3	S5	5	3	3	3	3	3	3 "+
"48	3	3	3	3	14	10	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	6	10	3	3	3	3	3	3	S10	4	3	3	3	3	3	3	3	3	3	3	3	3	3	S8	4	3	3	3	3	3 "+
"47	3	3	3	3	14	10	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	c10	4	3	3	3	3	3	8	S10	3	3	3	3	3	3	3	3	3	3	3	3	3	3	5	S10	3	3	3	3	3 "+
"46	3	3	3	3	14	10	3	3	5	S14	5	3	6	14	6	4	14	14	4	8	6	14	3	3	3	3	3	3	3	3	3	3	3	10	14	3	3	3	3	3	3	S14	5	3	3	3	3	4	7	5	3	3	5	3	3	3	3	3	S14	4	3	3	3	3 "+
"45	3	3	3	3	14	10	3	3	S5	s14	5	3	6	14	14	6	4	14	14	6	4	6	14	4	3	3	3	3	3	3	4	6	8	8	10	8	6	3	3	3	4	S14	4	3	3	3	3	4	5	14	4	7	8	8	3	3	3	3	7	S7	3	3	3	3 "+
"44	3	3	3	3	14	10	3	3	5	s14	5	3	3	14	3	3	3	6	14	3	3	9	14	3	3	3	3	3	3	3	3	3	3	c10	6	3	3	3	3	3	5	S14	3	3	3	3	3	3	3	10	8	5	3	3	3	3	3	3	7	S10	3	3	3	3 "+
"43	3	3	3	3	14	10	3	3	5	s14	5	3	3	14	3	3	3	6	14	3	3	9	14	3	3	3	3	3	3	3	3	3	4	14	3	3	3	3	3	3	8	S14	3	3	3	3	3	3	3	3	10	3	3	3	3	3	3	3	7	S10	3	3	3	3 "+
"42	3	3	3	3	14	10	3	3	5	s14	5	3	3	14	3	3	3	6	14	3	3	9	14	3	3	3	3	3	3	3	3	3	6	10	3	3	3	3	3	3	8	S14	3	3	3	3	3	3	3	5	14	3	3	3	3	3	3	3	7	S10	3	3	3	3 "+
"41	3	3	3	3	14	10	3	3	5	s14	5	3	3	14	3	3	3	6	14	3	3	9	14	3	3	3	3	3	3	3	3	3	c10	3	3	3	3	3	3	3	5	S14	4	3	3	3	3	3	3	10	10	5	3	3	3	3	3	3	7	S7	3	3	3	3 "+
"40	3	3	3	3	14	10	3	3	5	s14	5	3	3	14	3	3	3	6	14	3	3	9	14	3	3	3	3	3	3	3	3	3	14	4	3	3	3	3	3	3	3	S14	5	3	3	3	5	5	14	6	8	8	4	4	3	3	3	3	S14	5	3	3	3	3 "+
"39	3	3	3	10	14	10	3	3	S5	S14	S5	3	6	14	14	4	3	9	14	6	3	6	14	14	4	3	3	3	3	3	3	5	10	3	3	3	3	3	3	3	3	S8	3	3	3	3	7	14	3	3	4	10	7	3	3	3	3	4	S14	3	3	3	3	3 "+
"38	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	c10	5	3	3	3	3	3	3	3	3	5	S10	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	S7	3	3	3	3	3 "+
"37	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	4	3	3	3	3	3	3	3	3	3	3	5	S4	3	3	3	3	3	3	3	3	3	3	3	3	4	S7	7	3	3	3	3	3 "+
"36	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	c10	c4	5	4	3	3	3	3	3	3	3	3	3	3	3	5	4	3	3	3	3	3	3	3	3	3	3	3	5	3	3	3	3	3	3	3 "+
"35	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	4	4	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"34	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"33	3	3	3	10	10	3	10	10	3	3	3	3	3	3	10	3	3	3	3	S1000	S1000	3	S1000	S1000	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"32	3	3	3	3	3	10	3	3	3	3	3	3	3	3	4	14	3	3	S1000	3	3	S1000	3	3	S1000	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"31	3	3	3	3	10	3	3	3	10	10	10	10	10	10	10	14	14	3	S1000	3	3	S1000	3	3	S1000	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"30	3	3	3	3	10	3	3	3	3	3	3	3	3	3	4	14	3	3	3	S1000	S1000	3	S1000	S1000	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"29	3	3	10	10	3	10	10	3	3	3	3	3	3	3	10	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"28	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"27	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3 "+
"26	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"24	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"23	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"22	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"21	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"20	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"19	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"18	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"17	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"16	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"15	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"14	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"13	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"12	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"11	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"10	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"9	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"8	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"6	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"5	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"4	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
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
"-10																																																																";

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
