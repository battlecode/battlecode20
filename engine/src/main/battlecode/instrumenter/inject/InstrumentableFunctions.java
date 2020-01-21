package battlecode.instrumenter.inject;

import main.battlecode.instrumenter.inject.InstrumentedString;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * This class allows us to instrument certain string operations.
 * The instrumenter replaces calls to java.lang.String methods, which are
 * not instrumented, with these methods, which are.
 */
@SuppressWarnings("unused")
public class InstrumentableFunctions {
    static public final int NOT_FOUND = -1;

    private InstrumentableFunctions() {
    }

    static private Random rnd;

    static public double random() {
        return getRandom().nextDouble();
    }

    static private Random getRandom() {
        if (rnd == null)
            rnd = new Random(battlecode.instrumenter.inject.RobotMonitor.getRandomSeed());
        return rnd;
    }

    /*
     * Instrumented String methods
     *   - indexOf
     *   - lastIndexOf
     *   - contains
     *
     *   Previously, we used calls to java.util.regex.Pattern and
     *   java.util.regex.Matcher to instrument these functions. However, those
     *   calls had a baseline bytecode cost (e.g. a cost with trivially small
     *   inputs) of around 1500, which could be problematic for competitors.
     *
     *   Instead, these methods now use calls to a dummy InstrumentedString class
     *   which contains a slightly modified copy of the standard java.lang.String
     *   class. This dummy class contains only the methods that are to be
     *   instrumented, reducing the baseline bytecode cost to ~100.
     *
     *   These specific methods were instrumented because their standard
     *   implementations in java contain nested for loops, which led to an exploit
     *   where competitors could overwhelm the engine using moderately sized
     *   outputs.
     *
     *   In addition to the above instrumented methods, there are also several
     *   comparison methods in the String class that we have left untouched at
     *   this time, as they do not cause too much issue with inputs of <1e8 and
     *   instrumenting them would likely decrease competitor QoL. The same thing
     *   can be said for StringBuffer and StringBuilder methods.
     *
     *   However, these may all be instrumented in the future should problems arise.
     *
     */
    static public int indexOf(String str, int ch) {
        return new InstrumentedString(str).indexOf(ch);
    }

    static public int indexOf(String str, int ch, int fromIndex) {
        return new InstrumentedString(str).indexOf(ch, fromIndex);
    }

    static public int indexOf(String str, String query) {
        return new InstrumentedString(str).indexOf(query);
    }

    static public int indexOf(String str, String query, int fromIndex) {
        return new InstrumentedString(str).indexOf(query, fromIndex);
    }

    static public int lastIndexOf(String str, int ch) {
        return new InstrumentedString(str).lastIndexOf(ch);
    }

    static public int lastIndexOf(String str, int ch, int fromIndex) {
        return new InstrumentedString(str).lastIndexOf(ch, fromIndex);
    }

    static public int lastIndexOf(String str, String query) {
        return new InstrumentedString(str).lastIndexOf(query);
    }

    static public int lastIndexOf(String str, String query, int fromIndex) {
        return new InstrumentedString(str).lastIndexOf(query, fromIndex);
    }

    static public boolean contains(String str, CharSequence s) {
        return new InstrumentedString(str).contains(s);
    }

    // Instrumented StringBuffer indexOf and lastIndexOf methods
    static public int indexOf(StringBuffer str, String query) {
        return indexOf(str.toString(), query);
    }

    static public int indexOf(StringBuffer str, String query, int fromIndex) {
        return indexOf(str.toString(), query, fromIndex);
    }

    static public int lastIndexOf(StringBuffer str, String query) {
        return lastIndexOf(str.toString(), query);
    }

    static public int lastIndexOf(StringBuffer str, String query, int fromIndex) {
        return lastIndexOf(str.toString(), query, fromIndex);
    }

    // Instrumented StringBuilder indexOf and lastIndexOf methods
    static public int indexOf(StringBuilder str, String query) {
        return indexOf(str.toString(), query);
    }

    static public int indexOf(StringBuilder str, String query, int fromIndex) {
        return indexOf(str.toString(), query, fromIndex);
    }

    static public int lastIndexOf(StringBuilder str, String query) {
        return lastIndexOf(str.toString(), query);
    }

    static public int lastIndexOf(StringBuilder str, String query, int fromIndex) {
        return lastIndexOf(str.toString(), query, fromIndex);
    }


    /* The methods below are not part of InstrumentedString because they
       are already implemented in java.lang.String using java.util.regex.Pattern,
       so it felt more efficient to just skip the steps in between */
    static public boolean matches(String str, String regex) {
        return Pattern.matches(regex, str);
    }

    static public String replaceAll(String str, String regex, String replacement) {
        return Pattern.compile(regex).matcher(str).replaceAll(replacement);
    }

    static public String replaceFirst(String str, String regex, String replacement) {
        return Pattern.compile(regex).matcher(str).replaceFirst(replacement);
    }

    static public String[] split(String str, String regex) {
        return split(str, regex, 0);
    }

    static public String[] split(String str, String regex, int limit) {
        return Pattern.compile(regex).split(str, limit);
    }
}
