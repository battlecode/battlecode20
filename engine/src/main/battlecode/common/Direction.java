package battlecode.common;

/**
 * This enumeration represents a direction from one MapLocation to another.
 * There is a direction for each of the cardinals (north, south, east, west),
 * and each of diagonals (northwest, southwest, northeast, southeast).
 * There is also NONE, representing no direction, and OMNI, representing
 * all directions.
 * <p>
 * Since Direction is a Java 1.5 enum, you can use it in <code>switch</code>
 * statements, it has all the standard enum methods (<code>valueOf</code>,
 * <code>values</code>, etc.), and you can safely use <code>==</code> for
 * equality tests.
 */
public enum Direction {
    /**
     * Direction that represents pointing north (up on screen).
     */
    NORTH(0, -1),
    /**
     * Direction that represents pointing east (right on screen).
     */
    EAST(1, 0),
    /**
     * Direction that represents pointing south (down on screen).
     */
    SOUTH(0, 1),
    /**
     * Direction that represents pointing west (left on screen).
     */
    WEST(-1, 0),
    /**
     * No direction.
     */
    NONE(0, 0),
    /**
     * All directions.
     */
    OMNI(0, 0);

    /**
     * Change in x, change in y.
     */
    public final int dx, dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Computes the direction opposite this one.
     *
     * @return the direction pointing in the opposite direction
     *         to this one, or NONE if it's NONE, or OMNI if it's OMNI
     *
     * @battlecode.doc.costlymethod
     */
    public Direction opposite() {
        if (ordinal() >= 4)
            return this;
        return Direction.values()[(ordinal() + 2) % 4];
    }

    /**
     * Computes the direction 90 degrees to the left (counter-clockwise)
     * of this one.
     *
     * @return the direction 90 degrees left of this one, or NONE if it's NONE,
     *         or OMNI if it's OMNI
     *
     * @battlecode.doc.costlymethod
     */
    public Direction rotateLeft() {
        if (ordinal() >= 4)
            return this;
        return Direction.values()[(ordinal() + 4 - 1) % 4];
    }

    /**
     * Computes the direction 90 degrees to the right (clockwise)
     * of this one.
     *
     * @return the direction 90 degrees right of this one, or NONE if it's NONE,
     *         or OMNI if it's OMNI
     *
     * @battlecode.doc.costlymethod
     */
    public Direction rotateRight() {
        if (ordinal() >= 4)
            return this;
        return Direction.values()[(ordinal() + 1) % 4];
    }
}