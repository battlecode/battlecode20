package battlecode.instrumenter.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The Profiler class profiles bytecode usage in a sandboxed robot player.
 * It is called by the instrumenter through RobotMonitor and only profiles
 * down to methods created by the player (e.g. it won't show the amount of
 * bytecode an ArrayList.add() call costs).
 * <p>
 * Data is stored in such a way that it is easy to convert it to a file
 * compatible with speedscope (https://github.com/jlfwong/speedscope)
 * which is used in the client to show the profiling data. See
 * https://github.com/jlfwong/speedscope/wiki/Importing-from-custom-sources
 * for more information on speedscope's file format.
 */
public class Profiler {
    private String name;

    private int bytecodeCounter = 0;

    private List<ProfilerEvent> events = new ArrayList<>();
    private Function<String, Integer> frameIdProducer;

    public Profiler(String name, Function<String, Integer> frameIdProducer) {
        this.name = name;
        this.frameIdProducer = frameIdProducer;
    }

    public void incrementBytecodes(int amount) {
        try {
            bytecodeCounter = Math.addExact(bytecodeCounter, amount);
        } catch (ArithmeticException e) {
            bytecodeCounter = Integer.MAX_VALUE;
        }
    }

    public void enterMethod(String methodName) {
        if (methodName.startsWith("instrumented.")) {
            return;
        }

        events.add(new ProfilerEvent(ProfilerEventType.OPEN, bytecodeCounter, frameIdProducer.apply(methodName)));
    }

    public void exitMethod(String methodName) {
        if (methodName.startsWith("instrumented.")) {
            return;
        }

        events.add(new ProfilerEvent(ProfilerEventType.CLOSE, bytecodeCounter, frameIdProducer.apply(methodName)));
    }

    public void exitOpenMethods() {
        Map<Integer, Integer> methodCounter = new HashMap<>(events.size());

        for (ProfilerEvent event : events) {
            if (event.getType() == ProfilerEventType.OPEN) {
                if (!methodCounter.containsKey(event.getFrameId())) {
                    methodCounter.put(event.getFrameId(), 0);
                }

                methodCounter.put(event.getFrameId(), methodCounter.get(event.getFrameId()) + 1);
            } else {
                methodCounter.put(event.getFrameId(), methodCounter.get(event.getFrameId()) - 1);
            }
        }

        for (Map.Entry<Integer, Integer> entry : methodCounter.entrySet()) {
            for (int i = 0, iMax = entry.getValue(); i < iMax; i++) {
                events.add(new ProfilerEvent(ProfilerEventType.CLOSE, bytecodeCounter, entry.getKey()));
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<ProfilerEvent> getEvents() {
        return events;
    }
}
