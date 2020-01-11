package battlecode.instrumenter.profiler;

import java.util.ArrayList;
import java.util.List;

/**
 * A ProfilerRecord represents a node in the call tree created by the Profiler class.
 */
public class ProfilerRecord {
    private String name;

    private int enterCounter = 0;
    private int bytecodesUsed = 0;

    private List<ProfilerRecord> children = new ArrayList<>();

    public ProfilerRecord(String name) {
        this.name = name;
    }

    public void enter() {
        enterCounter++;
    }

    public void exit() {
        if (!isOpen()) {
            return;
        }

        enterCounter--;

        for (ProfilerRecord child : children) {
            child.exit();
        }
    }

    public boolean isOpen() {
        return enterCounter > 0;
    }

    public void incrementBytecodes(int amount) {
        if (!isOpen()) {
            return;
        }

        bytecodesUsed += amount;

        for (ProfilerRecord child : children) {
            child.incrementBytecodes(amount);
        }
    }

    public ProfilerRecord getLastOpenRecord() {
        for (ProfilerRecord child : children) {
            if (child.isOpen()) {
                return child.getLastOpenRecord();
            }
        }

        return this;
    }

    public int getBytecodesUsed() {
        return bytecodesUsed;
    }

    public String getName() {
        return name;
    }

    public List<ProfilerRecord> getChildren() {
        return children;
    }
}
