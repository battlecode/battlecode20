package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generate a map.
 */
public class Sheet4 {

    // change this!!!
    public static final String mapName = "Sheet4";

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

        String ds = "64	64	r	100	500	-4	/* Symbols: 'x' for symmetry-inferred, 'w' for infinite-depth water, 'W' for W-depth water, 's' and 'S' for soup, 'c' for cow, 'h' for HQ. Append a number to set elevation. 'Wsch10' is valid, but order must be w,s,c,h. */																																																									 "+
"1	1	2	2	2	2	2	1	1	1	w	w	1	1	1	1	1	1	1	1	1	w	w	5	5	5	5	5	5	5	5	5	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	S1	2	2	2	2	2	S1	1	1	2	W-3	1	0	0	0	0	0	0	0	1	W-3	W-3	5	4	3	3	2	2	2	2	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	2	2	2	2	2	2	2	W-3	2	1	0	-1	-1	-1	-1	-1	0	1	W-3	W-3	5	4	3	3	2	2	1	1	1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	3	3	3	2	2	2	2	W-3	W-3	1	0	-1	-2	-2	-2	-1	0	1	W-3	W-3	5	4	3	3	2	s2	1	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	3	h3	3	2	2	2	2	W-3	W-3	1	0	-1	-2	s-3	-2	-1	0	1	W-3	W-3	5	4	c3	3	2	s2	1	0	W0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	3	3	3	2	2	2	2	W-3	W-3	1	0	-1	-2	s-3	-2	-1	0	1	W-3	W-3	5	4	3	3	2	s2	1	0	W0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	2	2	2	2	2	2	2	W-3	W-3	1	0	-1	-2	-2	-2	-1	0	1	W-3	W-3	5	4	3	3	2	s2	1	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	S1	2	2	2	2	2	S1	1	1	W-3	W-3	1	0	-1	-1	-1	-1	-1	0	1	W-3	W-3	5	4	3	3	2	2	1	1	1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	1	2	2	2	2	2	1	1	1	W-3	W-3	1	0	0	0	0	0	0	0	1	W-3	W-3	5	4	3	3	2	2	2	2	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	1	2	2	2	2	2	1	1	1	W-3	W-3	1	1	1	1	1	1	1	1	c1	W-3	W-3	5	5	5	5	5	5	5	5	5	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	2	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	W-3	2	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	2	2	2	2	2	2	2	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	2	7	2	2	2	2	2	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	2	7	2	2	2	2	2	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	0	0	0	0	0	0	0	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	2	7	2	2	2	2	2	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	7	7	7	7	2	2	2	2	2	W-3	W-3	3	3	3	3	3	S3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	2	2	2	2	2	2	2	W-3	W-3	3	3	3	3	c3	3	3	3	3	W-3	W-3	0	0	0	0	0	0	0	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	7	7	7	7	2	2	2	W-3	W-3	3	3	3	S3	3	S3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	7	2	2	s2	s2	s2	2	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	7	2	s2	s2	s2	s2	s2	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	0	0	0	0	0	0	0	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	7	2	s2	s2	s2	s2	s2	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	7	2	s2	s2	s2	S2	S2	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"2	2	2	2	2	2	s2	s2	S2	2	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	1	1	2	1	1	1	2	1	1	W-3	W-3	40	40	40	40	40	40	40	s40	s40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	1	1	2	1	1	1	2	1	1	W-3	W-3	40	40	40	40	40	s40	s40	s40	s40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	2	2	2	2	2	2	2	2	2	W-3	W-3	40	40	40	40	s40	s40	s40	s40	s40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	1	1	2	1	1	1	2	1	1	W-3	W-3	40	40	40	s40	s40	s40	s40	100	100	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	1	1	2	1	1	1	2	1	1	W-3	W-3	40	40	s40	s40	s40	s40	100	100	c-10	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	c50	50	50	50	50	50	W-3	W-3	2	2	2	2	2	2	2	2	2	W-3	W-3	40	40	s40	s40	s40	s40	100	c-10	c-10	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	1	1	2	1	1	1	2	1	1	W-3	W-3	40	40	s40	s40	s40	s40	100	c-10	c-10	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	1	1	2	1	1	1	2	1	1	W-3	W-3	40	40	s40	s40	s40	s40	100	100	c-10	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	2	2	2	2	2	2	2	2	2	W-3	W-3	40	40	40	s40	s40	s40	s40	100	100	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	1	1	2	1	1	1	2	1	1	W-3	W-3	40	40	40	40	s40	s40	s40	s40	s40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	1	1	2	1	1	1	2	1	1	W-3	W-3	40	40	40	40	40	s40	s40	s40	s40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"50	50	50	50	50	50	50	50	50	50	W-3	W-3	1	1	2	1	1	1	2	1	1	W-3	W-3	40	40	40	40	40	40	40	s40	s40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	1	1	1	1	1	1	1	1	1	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	3	3	3	3	3	3	3	1	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	5	5	5	5	5	5	3	1	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	5	7	7	7	7	5	3	1	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	0	0	0	0	0	0	0	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	5	7	9	9	7	5	3	1	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	5	7	9	9	7	5	3	1	W-3	W-3	3	3	3	S3	3	S3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	5	7	9	9	7	5	3	1	W-3	W-3	3	3	3	3	Sc3	3	3	3	3	W-3	W-3	0	0	0	0	0	0	0	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	5	7	9	9	7	5	3	1	W-3	W-3	3	3	3	S3	3	S3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	5	7	7	7	7	5	3	1	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	5	5	5	5	5	5	3	1	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	0	0	0	0	0	0	0	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	3	3	3	3	3	3	3	3	1	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"1	1	1	1	1	1	1	1	1	1	W-3	W-3	3	3	3	3	3	3	3	3	3	W-3	W-3	2	2	2	0	2	2	2	0	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	W-3	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	0	0	0	0	0	0	0	W-3	W-3	1	1	1	1	1	1	1	1	1	W-3	W-3	5	5	5	5	5	5	5	5	5	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	0	0	0	0	0	0	0	W-3	W-3	1	0	0	0	0	0	0	0	1	W-3	W-3	5	4	3	3	2	2	2	2	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	0	100	100	100	0	0	0	W-3	W-3	1	0	-1	-1	-1	-1	-1	0	1	W-3	W-3	5	4	3	3	2	2	1	1	1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	100	100	c-10	100	100	0	0	W-3	W-3	1	0	c-1	-2	-2	-2	-1	0	1	W-3	W-3	5	4	3	3	2	s2	1	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	100	c-10	c-10	c-10	100	0	0	W-3	W-3	1	0	-1	-2	s-3	-2	-1	0	1	W-3	W-3	5	4	c3	3	2	s2	1	0	W0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	100	100	c-10	100	100	0	0	W-3	W-3	1	0	-1	-2	s-3	-2	-1	0	1	W-3	W-3	5	4	3	3	2	s2	1	0	W0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	0	100	100	100	0	0	0	W-3	W-3	1	0	-1	-2	-2	-2	-1	0	1	W-3	W-3	5	4	3	3	2	s2	1	0	0	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	0	0	0	0	0	0	0	W-3	W-3	1	0	-1	-1	-1	-1	-1	0	1	W-3	W-3	5	4	3	3	2	2	1	1	1	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	0	0	0	0	0	0	0	W-3	W-3	1	0	0	0	0	0	0	0	1	W-3	W-3	5	4	3	3	2	2	2	2	2	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"0	0	0	0	0	0	0	0	0	0	W-3	W-3	1	1	1	1	1	1	1	1	1	W-3	W-3	5	5	5	5	5	5	5	5	5	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x";

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
