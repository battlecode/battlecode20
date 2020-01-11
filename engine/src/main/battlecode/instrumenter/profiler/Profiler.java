package battlecode.instrumenter.profiler;

/**
 * TODO(jmerle): Add documentation to explain how this thing works
 */
public class Profiler {
    private String name;

    public Profiler(String name) {
        this.name = name;
    }

    public void incrementBytecodes(int amount) {
        // TODO(jmerle): Find active nodes and add bytecode to it
    }

    public void enterMethod(String name) {
        // TODO(jmerle): Implement
        if (name.endsWith("run"))
            System.out.println("Entering " + name);
    }

    public void exitMethod(String name) {
        // TODO(jmerle): Implement
        if (name.endsWith("run"))
            System.out.println("Exiting " + name);
    }

    public void exitOpenMethods() {
        // TODO(jmerle): Implement
        System.out.println("Exiting open methods");
    }

    public String getName() {
        return name;
    }
}
