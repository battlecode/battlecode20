package battlecode.instrumenter.inject;

import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// This class allows us to instrument certain string operations.
// The instrumenter replaces calls to java.lang.String methods,
// which are not instrumented, with these methods, which are.

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
            rnd = new Random(RobotMonitor.getRandomSeed());
        return rnd;
    }

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

    // Instrumented String indexOf methods
    static public int indexOf(String str, int ch) {
        Matcher matcher = Pattern.compile(String.valueOf((char) ch)).matcher(str);
        return matcher.find() ? matcher.start() : NOT_FOUND;
    }

    static public int indexOf(String str, int ch, int fromIndex) {
        Matcher matcher = Pattern.compile(String.valueOf((char) ch)).matcher(str.substring(fromIndex));
        return matcher.find() ? fromIndex + matcher.start() : NOT_FOUND;
    }

    static public int indexOf(String str, String query) {
        Matcher matcher = Pattern.compile(query).matcher(str);
        return matcher.find() ? matcher.start() : NOT_FOUND;
    }

    static public int indexOf(String str, String query, int fromIndex) {
        Matcher matcher = Pattern.compile(query).matcher(str.substring(fromIndex));
        return matcher.find() ? fromIndex + matcher.start() : NOT_FOUND;
    }

    // Instrumented String lastIndexOf methods
    static public int lastIndexOf(String str, int ch) {
        Matcher matcher = Pattern.compile(String.valueOf((char) ch)).matcher(str);
        int lastIndex = NOT_FOUND;
        while (matcher.find()) {
            lastIndex = matcher.start();
        }
        return lastIndex;
    }

    static public int lastIndexOf(String str, int ch, int fromIndex) {
        Matcher matcher = Pattern.compile(String.valueOf((char) ch)).matcher(str);
        int lastIndex = NOT_FOUND;
        while (matcher.find() && lastIndex <= fromIndex) {
            lastIndex = matcher.start();
        }
        return lastIndex;
    }

    static public int lastIndexOf(String str, String query) {
        Matcher matcher = Pattern.compile(query).matcher(str);
        int lastIndex = NOT_FOUND;
        while (matcher.find()) {
            lastIndex = matcher.start();
        }
        return lastIndex;
    }

    static public int lastIndexOf(String str, String query, int fromIndex) {
        Matcher matcher = Pattern.compile(query).matcher(str);
        int lastIndex = NOT_FOUND;
        while (matcher.find() && lastIndex <= fromIndex) {
            lastIndex = matcher.start();
        }
        return lastIndex;
    }

    // Instrumented StringBuffer indexOf methods
    static public int indexOf(StringBuffer str, String query) {
        return indexOf(str.toString(), query);
    }

    static public int indexOf(StringBuffer str, String query, int fromIndex) {
        return indexOf(str.toString(), query, fromIndex);
    }

    // Instrumented StringBuffer lastIndexOf methods
    static public int lastIndexOf(StringBuffer str, String query) {
        return lastIndexOf(str.toString(), query);
    }

    static public int lastIndexOf(StringBuffer str, String query, int fromIndex) {
        return lastIndexOf(str.toString(), query, fromIndex);
    }

    // Instrumented StringBuilder indexOf methods
    static public int indexOf(StringBuilder str, String query) {
        return indexOf(str.toString(), query);
    }

    static public int indexOf(StringBuilder str, String query, int fromIndex) {
        return indexOf(str.toString(), query, fromIndex);
    }

    // Instrumented StringBuilder lastIndexOf methods
    static public int lastIndexOf(StringBuilder str, String query) {
        return lastIndexOf(str.toString(), query);
    }

    static public int lastIndexOf(StringBuilder str, String query, int fromIndex) {
        return lastIndexOf(str.toString(), query, fromIndex);
    }

}
