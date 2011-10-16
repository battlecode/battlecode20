package hardplayer.goal;

public interface Goal {

	public int maxPriority();

	public int priority();

	public void execute();

	// Archon goals
	static public final int FLEE = 100;
	static public final int MAKE_ARMY = 95;
	static public final int EXPLORE = 30;

	// Non-archon goals

}
