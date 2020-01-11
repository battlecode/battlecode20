package battlecode.instrumenter.profiler;

import java.util.ArrayList;
import java.util.List;

/**
 * The Profiler class profiles bytecode usage in a sandboxed robot player.
 * It is called by the instrumenter through RobotMonitor and only profiles
 * down to methods created by the player (e.g. it won't show the amount of
 * bytecode an ArrayList.add() call costs).
 */
public class Profiler {
    private String name;

    private List<ProfilerRecord> rootRecords = new ArrayList<>();

    public Profiler(String name) {
        this.name = name;
    }

    public void incrementBytecodes(int amount) {
        if (rootRecords.isEmpty()) {
            return;
        }

        rootRecords.get(rootRecords.size() - 1).incrementBytecodes(amount);
    }

    public void enterMethod(String methodName) {
        if (methodName.startsWith("instrumented.")) {
            return;
        }

        ProfilerRecord newRecord = new ProfilerRecord(methodName);
        newRecord.enter();

        ProfilerRecord lastOpenRecord = getLastOpenRecord();

        // If there is no open record a new root has been entered
        if (lastOpenRecord == null) {
            rootRecords.add(newRecord);
            return;
        }

        List<ProfilerRecord> children = lastOpenRecord.getChildren();

        // Find a child with the same name and enter the already created child if it exists
        for (ProfilerRecord child : children) {
            if (child.getName().equals(methodName)) {
                child.enter();
                return;
            }
        }

        // No child with same name found, create a new record
        children.add(newRecord);
    }

    public void exitMethod(String methodName) {
        if (methodName.startsWith("instrumented.")) {
            return;
        }

        ProfilerRecord lastOpenRecord = getLastOpenRecord();
        if (lastOpenRecord != null) {
            lastOpenRecord.exit();
        }
    }

    public void exitAllOpenMethods() {
        while (true) {
            ProfilerRecord lastOpenRecord = getLastOpenRecord();

            if (lastOpenRecord == null) {
                break;
            }

            lastOpenRecord.exit();
        }
    }

    public String getName() {
        return name;
    }

    private ProfilerRecord getLastOpenRecord() {
        if (rootRecords.isEmpty()) {
            return null;
        }

        ProfilerRecord lastRootRecord = rootRecords.get(rootRecords.size() - 1);

        if (!lastRootRecord.isOpen()) {
            return null;
        }

        return lastRootRecord.getLastOpenRecord();
    }
}
