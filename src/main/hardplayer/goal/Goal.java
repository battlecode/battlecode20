package hardplayer.goal;

public interface Goal {

	public int maxPriority();

	public int priority();

	public void execute();

	static public final int BUILD_MINE = 70;

}
