package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generate a map.
 */
public class Europe {

    // change this!!!
    public static final String mapName = "Europe";

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

        String ds = "63	32	v	250	350	-4	/* Symbols: 'x' for symmetry-inferred, 'w' for infinite-depth water, 'W' for W-depth water, 's' and 'S' for soup, 'c' for cow, 'h' for HQ. Append a number to set elevation. 'Order must be w,s,c,h; so Wsch1W' is valid syntactically (but not logically: the HQ can't start in water or have a cow on it) */																																																										 "+
"indx	0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16	17	18	19	20	21	22	23	24	25	26	27	28	29	30	31	32	33	34	35	36	37	38	39	40	41	42	43	44	45	46	47	48	49	50	51	52	53	54	55	56	57	58	59	60	61	62	63 "+
"31	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"30	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"29	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	1	2	1	1	W	W	W	W	W	W	W	W	W	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"28	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	W	2	3	3	3	3	3	3	2	1	1	W	W	W	W	W	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"27	W	w	W	W	W	W	W	W	W	W	W	W	W	W	1	4	4	4	4	3	4	4	4	4	4	4	3	2	W	W	2	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"26	W	W	W	W	W	W	W	W	W	W	W	W	W	1	4	4	4	4	4	3	4	4	c4	4	3	2	3	3	1	1	3	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"25	W	W	W	W	W	W	W	W	W	W	W	W	W	3	4	4	4	4	1	W	3	4	4	4	4	1	W	W	2	s4	4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"24	W	W	W	W	W	W	W	W	W	W	W	W	3	4	4	4	4	3	W	2	4	4	4	4	4	4	2	c3	4	4	s4	s7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"23	W	W	W	W	W	W	W	W	W	W	2	4	4	4	4	4	2	W	2	4	4	4	4	4	4	4	4	4	s4	4	4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"22	W	W	W	W	W	W	W	W	W	2	S4	S4	4	4	4	4	1	W	1	4	4	4	4	4	4	4	4	4	s4	4	4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"21	W	W	W	W	W	W	W	W	W	1	S4	4	4	4	h4	4	3	W	W	1	2	1	1	4	4	4	4	4	4	4	s4	s7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"20	W	W	W	W	W	W	W	W	W	W	3	3	1	4	4	4	2	W	W	W	2	4	3	4	4	4	4	4	4	s4	s4	s7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"19	W	W	W	3	3	W	W	W	W	W	W	W	W	2	4	4	W	W	W	1	1	4	4	4	4	4	4	s4	4	4	4	s7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"18	W	W	W	4	4	W	W	W	W	W	W	3	2	1	4	2	W	W	W	3	4	4	4	4	4	4	4	s4	s4	s4	4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"17	W	1	1	1	4	2	W	W	W	W	W	1	1	1	W	W	W	1	1	3	4	4	4	4	4	4	4	s4	4	4	4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"16	2	4	3	W	2	4	1	W	W	W	W	1	3	3	3	2	4	4	4	4	4	4	4	4	4	4	4	4	4	4	s4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"15	2	4	2	W	4	4	3	1	W	2	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	s4	4	4	4	s4	s7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"14	1	W	W	1	3	4	3	W	1	3	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	s4	s4	4	s4	s4	s7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"13	W	W	W	1	1	W	W	3	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	s4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"12	W	W	W	W	1	2	3	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"11	W	W	W	W	3	4	4	4	4	4	4	4	4	4	4	4	4	4	4	4	3	4	4	4	4	4	4	4	4	4	4	s7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"10	W	W	W	W	W	2	4	4	4	4	4	4	4	4	4	4	4	4	4	3	3	4	4	4	4	3	2	3	3	1	1	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"9	W	W	W	W	W	W	4	4	4	4	4	4	4	4	2	2	3	4	3	3	3	4	4	4	4	1	W	1	3	W	2	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"8	W	W	W	W	W	1	4	4	4	4	4	3	2	4	2	W	1	4	4	4	4	4	4	4	3	W	W	W	W	W	W	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"7	1	4	3	3	3	3	4	4	3	1	1	W	W	2	4	2	W	W	2	4	4	4	4	4	2	W	W	W	W	W	W	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"6	1	4	4	4	4	4	4	4	1	W	W	W	1	W	2	4	2	W	W	3	4	4	4	4	3	W	W	1	2	2	W	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"5	1	4	4	4	4	4	4	W	W	W	W	W	2	W	W	W	3	3	W	2	4	3	1	1	2	2	3	4	4	s4	4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"4	3	4	4	c4	4	4	1	W	W	W	W	W	2	W	W	W	W	2	W	W	4	2	W	1	4	s4	4	4	4	s4	s4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	2	4	4	4	4	4	1	W	W	W	W	W	W	W	W	1	2	2	W	W	1	3	W	W	4	s4	4	4	s4	s4	4	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	1	2	4	4	3	1	W	W	W	W	W	1	1	1	W	2	2	W	W	W	W	2	W	W	W	3	4	3	4	4	3	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	W	W	1	W	W	W	2	4	4	4	4	4	4	3	W	W	W	W	W	W	W	W	W	W	W	W	W	W	1	W	1	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	W	W	1	1	1	1	3	2	2	2	2	2	2	2	W	W	W	W	W	W	W	W	W	W	W	W	W	W	1	1	W	7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-4	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-5	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-6	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-7	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-8	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-9	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-10	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-11	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-12	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-13	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-14	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-15	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-16	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-17	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-18	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-19	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-20	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-21	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-22	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-23	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-24	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-26	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-27	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-28	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-29	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-30	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-31	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"-32	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x";

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
