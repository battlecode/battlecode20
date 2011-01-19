package hardplayer.goal;

public interface Goal {

	public int maxPriority();

	public int priority();

	public void execute();

	static public final int FALLBACK = 1;
	static public final int WANDER = 50;
	static public final int ATTACK_DEBRIS = 60;
	static public final int BUILD_MINE = 70;
	static public final int CONSTRUCTOR_ATTACK = 95;
	static public final int BUILD_RECYCLER = 90;

}
