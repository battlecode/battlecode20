package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generate a map.
 */
public class TheHighGround {

    // change this!!!
    public static final String mapName = "TheHighGround";

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

        String ds = "64	64	r	150	200	-4	/* Symbols: 'x' for symmetry-inferred, 'w' for infinite-depth water, 'W' for W-depth water, 's' and 'S' for soup, 'c' for cow, 'h' for HQ. Append a number to set elevation. 'Wsch10' is valid, but order must be w,s,c,h. */																																																									 "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	20	C20	20	20	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	20	S20	S20	20	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	20	S20	S20	20	3	3	3	3	3	3	3	3	3	3	25	25	25	-30	-30	-30	-30	25	25	25	25	25	c25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	20	20	C20	20	3	3	3	3	3	3	3	3	3	3	25	25	25	-30	S-30	S-30	-30	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	-30	S-30	S-30	-30	25	25	25	25	25	40	C40	40	40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	-30	S-30	S-30	-30	25	25	25	25	25	40	S20	S20	40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	-30	-30	-30	-30	25	25	25	25	25	40	S20	S20	40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	40	40	C40	40	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	5	5	3	3	3	3	3	3	3	3	3	25	25	c25	25	25	25	25	25	25	-30	-30	-30	-30	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	8	8	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	-30	S-30	S-30	-30	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	11	11	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	-30	S-30	S-30	-30	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	14	14	3	3	3	3	3	3	3	3	3	25	25	25	25	25	c25	25	25	25	-30	S-30	S-30	-30	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	17	17	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	-30	-30	-30	-30	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	20	20	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	23	23	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	26	26	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	29	29	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	W	W	w	w	w	w	25	240	240	240	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	32	32	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	W	w	w	w	w	w	260	260	260	260	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	35	35	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	W	w	w	w	w	w	280	280	280	S280	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	38	38	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	W	w	c200	c201	w	w	300	300	300	S300	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	41	41	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	W	w	c201	c202	w	w	320	320	320	S320	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	44	44	3	3	3	3	3	3	3	3	3	3	3	3	3	3	W	w	w	w	w	w	w	340	340	340	S340	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	47	47	3	3	3	3	3	3	3	3	3	3	3	3	3	W	w	w	w	w	w	w	w	360	360	360	S360	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	50	50	3	3	3	3	3	3	3	3	3	3	3	3	3	W	w	w	w	w	w	w	w	380	380	380	S380	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"W	W	W	W	W	50	50	W	W	W	W	W	W	W	W	W	W	W	W	W	W	w	400	400	400	400	400	400	400	400	400	400	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	W	W	W	W	50	50	W	W	W	W	W	W	W	W	W	W	W	W	W	W	w	400	S400	S400	S400	S400	400	400	400	400	400	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"w	W	W	W	W	50	50	W	W	W	W	W	W	W	W	W	W	W	W	W	W	w	400	S400	S400	S400	S400	400	400	400	400	400	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"W	W	W	W	W	50	50	W	W	W	W	W	W	W	W	W	W	W	W	W	W	w	400	400	400	400	400	400	400	400	400	400	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	50	50	3	3	3	3	3	3	3	3	3	3	3	3	3	W	w	w	w	w	w	w	w	380	380	380	S380	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	47	47	3	3	3	3	3	3	3	3	3	3	3	3	3	W	w	w	w	w	w	w	w	360	360	360	S360	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	44	44	3	3	-30	-30	-30	-30	3	3	3	3	3	3	3	3	W	w	w	w	w	w	w	340	340	340	S340	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	41	41	3	3	-30	S-30	S-30	-30	3	3	3	3	3	3	3	3	3	W	w	c200	c201	w	w	320	320	320	S320	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	38	38	3	3	-30	S-30	S-30	-30	3	3	3	3	3	3	3	3	3	W	w	c201	c202	w	w	300	300	300	S300	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	35	35	3	3	-30	S-30	S-30	-30	3	3	3	3	3	3	3	3	3	W	w	w	w	w	w	280	280	280	S280	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	32	32	3	3	-30	-30	-30	-30	3	3	3	3	3	3	3	3	3	W	w	w	w	w	w	260	260	260	260	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	29	29	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	W	W	w	w	w	w	240	240	240	240	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	26	26	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	220	220	220	220	w	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	23	23	3	3	-30	-30	-30	-30	3	3	3	3	3	25	25	25	25	25	25	25	25	200	200	200	200	w	w	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	20	20	3	3	-30	S-30	S-30	-30	3	3	3	3	25	25	25	25	25	25	25	25	180	180	180	180	W	W	W	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	17	17	3	3	-30	S-30	S-30	-30	3	3	3	25	25	25	25	25	25	25	25	160	160	160	160	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	14	14	3	3	-30	S-30	S-30	-30	3	3	30	30	30	30	30	30	30	30	140	140	140	140	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	11	11	3	3	-30	-30	-30	-30	3	3	30	30	30	30	30	30	30	120	120	120	120	30	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	8	8	3	3	3	3	3	3	3	3	30	30	40	40	40	40	100	100	100	100	30	30	25	25	25	25	S25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	5	5	3	3	3	3	3	3	3	3	30	30	40	40	40	80	80	80	80	40	30	30	25	25	25	S25	S25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	30	30	40	40	60	60	60	60	40	40	30	30	25	25	25	S25	S25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	30	30	40	40	40	40	40	40	40	40	30	30	25	25	25	25	S25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	30	30	40	40	40	40	40	40	40	40	30	30	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	30	30	30	30	30	30	30	30	30	30	30	30	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	s3	s3	s3	s3	s3	s3	s3	3	3	3	3	3	3	30	30	30	30	30	30	30	30	30	30	30	30	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	s7	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	4	3	3	3	3	s7	3	3	3	3	25	25	25	25	25	25	25	25	25	s25	25	25	c25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	4	h5	4	3	3	3	s7	3	3	3	3	25	25	25	25	25	25	25	25	s25	25	s25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	4	3	3	3	3	s7	3	3	3	3	25	25	25	25	25	25	25	25	25	s25	25	25	25	25	25	25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	s7	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	25	25	s25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	25	s25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	s25	25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x "+
"3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	3	25	25	25	25	25	25	25	25	25	25	25	25	s25	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x	x";

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
