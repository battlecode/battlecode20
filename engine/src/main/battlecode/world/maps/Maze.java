package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generate a map.
 */
public class Maze {

    // change this!!!
    public static final String mapName = "Maze";

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

        String ds = "64	64	r	150	300	-10	/* Symbols: 'x' for symmetry-inferred, 'w' for infinite-depth water, 'W' for W-depth water, 's' and 'S' for soup, 'c' for cow, 'h' for HQ. Append a number to set elevation. 'Order must be w,s,c,h; so Wsch10' is valid syntactically (but not logically: the HQ can't start in water or have a cow on it) */																																																										 "+
"indx	0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16	17	18	19	20	21	22	23	24	25	26	27	28	29	30	31	32	33	34	35	36	37	38	39	40	41	42	43	44	45	46	47	48	49	50	51	52	53	54	55	56	57	58	59	60	61	62	63 "+
"63	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"62	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	s3	s3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"61	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	100	100	100	100	100	3	3	3	3	3	3	s3	s3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"60	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	100	s3	s3	100	3	3	3	3	3	3	3	s3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"59	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	100	s3	s3	100	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"58	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	100	s3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"57	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"56	3	3	3	3	3	h3	3	3	3	100	100	100	100	100	100	100	100	100	100	100	100	100	100	100	100	100	100	100	100	100	100	100	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"55	3	3	s3	s3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"54	3	s3	s3	s3	3	3	3	3	3	100	3	3	3	s3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"53	3	s3	s3	3	s3	3	3	3	3	100	3	3	s3	s3	s3	3	3	3	3	3	3	3	3	3	3	3	3	s3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"52	3	3	3	3	3	3	3	3	3	100	3	3	3	s3	3	3	3	3	3	3	3	2	3	3	3	3	3	s3	s3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"51	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	2	100	3	3	3	3	3	s3	s3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	3	3	3	3	3	3	3	3	3	100	3	3	3	2	2	2	2	2	2	2	3	100	3	3	3	3	3	s3	s3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"49	3	3	3	3	3	3	3	3	3	100	3	3	2	3	3	3	3	3	3	3	3	100	3	3	3	3	s3	s3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"48	3	3	3	3	3	3	3	3	3	100	3	2	3	3	3	3	3	3	3	3	3	100	3	3	3	3	s3	s3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"47	3	3	3	3	3	3	3	3	3	100	2	3	100	100	100	100	100	100	100	100	100	100	3	3	3	3	s3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"46	3	3	3	3	3	3	3	3	3	100	2	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"45	3	3	3	3	3	3	3	3	3	100	3	2	2	2	2	2	2	2	2	2	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"44	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	2	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"43	3	3	3	3	3	3	3	3	3	100	100	100	100	100	100	100	100	100	100	2	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"42	3	3	3	3	3	3	3	3	3	100	3	2	2	2	2	2	3	3	3	3	2	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"41	3	3	3	3	3	3	3	3	3	100	2	3	3	3	3	3	2	2	2	2	3	100	100	100	100	100	100	100	100	100	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"40	3	3	3	3	3	3	3	3	3	100	3	2	100	100	100	100	100	100	100	100	100	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"39	3	3	3	3	3	3	3	3	3	100	2	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"38	3	3	3	3	3	3	3	3	3	100	3	2	2	2	2	2	2	2	2	w	3	100	3	3	3	3	3	3	3	c3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"37	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	1	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"36	3	3	3	3	3	3	3	3	3	100	100	100	100	100	100	100	100	100	100	3	1	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"35	3	3	3	3	3	3	3	3	3	100	3	3	1	1	3	3	1	1	3	3	1	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"34	3	3	3	3	3	3	3	3	3	100	3	1	3	3	1	1	3	3	1	1	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"33	3	3	3	3	3	3	3	3	3	100	1	3	100	100	100	100	100	100	100	100	100	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"32	3	3	3	3	3	3	3	3	3	100	3	1	3	3	1	1	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"31	3	3	3	3	3	3	3	3	3	100	3	3	1	1	3	3	1	3	100	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"30	3	3	3	3	3	3	3	3	3	100	3	100	100	100	100	100	1	3	100	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"29	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	100	3	1	100	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"28	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	100	1	3	100	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"27	3	3	3	3	3	3	3	3	3	100	100	100	100	100	100	100	100	100	100	100	3	100	3	3	3	3	3	3	c3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"26	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	100	W	W	W	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"25	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	100	W	W	W	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"24	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	100	W	W	W	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"23	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	100	W	W	W	100	3	3	3	3	3	s3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"22	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	s3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"21	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	s3	s3	s3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"20	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	s3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"19	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"18	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"17	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"16	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"15	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"14	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	c3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"13	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"12	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"11	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"10	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"9	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"8	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"7	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"6	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	100	100	100	100	100	100	100	100	100	100	100	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"5	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"4	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	3	3	3	3	3	3	3	3	3	100	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x";

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
