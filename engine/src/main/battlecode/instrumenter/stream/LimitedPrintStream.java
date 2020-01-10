package battlecode.instrumenter.stream;

import battlecode.common.GameConstants;
import battlecode.common.Team;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * LimitedPrintStream is a subclass of PrintStream that limits the total amount of output that a team
 * may produce. Any excess output exceeding these limits are ignored.
 *
 * @author j-mao
 */
@SuppressWarnings("unused")
public class LimitedPrintStream extends PrintStream {

    private static int[] limit = {GameConstants.MAX_OUTPUT_BYTES, GameConstants.MAX_OUTPUT_BYTES, GameConstants.MAX_OUTPUT_BYTES};
    private Team team;

    public LimitedPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    @Override
    public void write(byte[] b) {
        int printSize = java.lang.Math.min(b.length, getRemainingByteLimit());
        if (printSize <= 0) {
            return;
        }
        subtractBytesFromLimit(printSize);
        try {
            out.write(b, 0, printSize);
        } catch (IOException x) {
        }
    }

    @Override
    public void write(int b) {
        if (getRemainingByteLimit() <= 0) {
            return;
        }
        subtractBytesFromLimit(1);
        try {
            out.write(b);
        } catch (IOException x) {
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        int printSize = java.lang.Math.min(len, getRemainingByteLimit());
        if (printSize <= 0) {
            return;
        }
        subtractBytesFromLimit(printSize);
        try {
            out.write(b, off, printSize);
        } catch (IOException x) {
        }
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    private int getRemainingByteLimit() {
        switch (this.team) {
            case A:
                return limit[0];
            case B:
                return limit[1];
            default:
                return limit[2];
        }
    }

    private void subtractBytesFromLimit(int bytes) {
        switch (this.team) {
            case A:
                limit[0] -= bytes;
                break;
            case B:
                limit[1] -= bytes;
                break;
            default:
                limit[2] -= bytes;
                break;
        }
    }
}
