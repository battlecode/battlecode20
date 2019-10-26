package battlecode.common;

/**
 * Stores information about a Object/Body in the game world
 */
public interface BodyInfo {

    /**
     * Returns the ID of this body.
     *
     * @return the ID of this body.
     */
    int getID();

    /**
     * Returns the center location of this body.
     *
     * @return the center location of this body.
     */
    MapLocation getLocation();

    /**
     * Returns the radius of this body.
     *
     * @return the radius of this body.
     */
    float getRadius();

    /**
     * Returns whether this body is a robot.
     *
     * @return true if this body is a robot; false otherwise.
     */
    boolean isRobot();
}
