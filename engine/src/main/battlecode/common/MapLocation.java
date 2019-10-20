package battlecode.common;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * This class is an immutable representation of two-dimensional coordinates
 * in the battlecode world.
 */
public final strictfp class MapLocation implements Serializable, Comparable<MapLocation> {

    private static final long serialVersionUID = -8945913587066072824L;
    /**
     * The x-coordinate.
     */
    public final int x;
    /**
     * The y-coordinate.
     */
    public final int y;

    /**
     * Creates a new MapLocation representing the location
     * with the given coordinates.
     *
     * @param x the x-coordinate of the location
     * @param y the y-coordinate of the location
     *
     * @battlecode.doc.costlymethod
     */
    public MapLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * A comparison function for MapLocations. Smaller x values go first, with ties broken by smaller y values.
     *
     * @param other the MapLocation to compare to.
     * @return whether this MapLocation goes before the other one.
     *
     * @battlecode.doc.costlymethod
     */
    public int compareTo(MapLocation other) {
        if (x != other.x)
            return x - other.x;
        return y - other.y;
    }

    /**
     * Two MapLocations are regarded as equal iff
     * their coordinates are the same.
     * {@inheritDoc}
     *
     * @battlecode.doc.costlymethod
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapLocation))
            return false;
        return (((MapLocation) obj).x == this.x) && (((MapLocation) obj).y == this.y);
    }

    /**
     * {@inheritDoc}
     *
     * @battlecode.doc.costlymethod
     */
    // @Override
    // public int hashCode() {
    //     return Float.floatToIntBits(this.x) * 13 + Float.floatToIntBits(this.y) * 23;
    // }

    public static MapLocation valueOf(String s) {
        String[] coord = StringUtils.replaceChars(s, "[](){}", null).split(",");
        if (coord.length != 2)
            throw new IllegalArgumentException("Invalid map location string");
        int x = Integer.valueOf(coord[0].trim());
        int y = Integer.valueOf(coord[1].trim());
        return new MapLocation(x, y);
    }

    /**
     * {@inheritDoc}
     *
     * @battlecode.doc.costlymethod
     */
    public String toString() {
        return String.format("[%d, %d]", this.x, this.y);
    }

    /**
     * Computes the squared distance from this location to the specified
     * location.
     *
     * @param location the location to compute the squared distance to
     * @return the squared distance to the given location
     *
     * @battlecode.doc.costlymethod
     */
    public final int distanceSquaredTo(MapLocation location) {
        int dx = this.x - location.x;
        int dy = this.y - location.y;
        return dx * dx + dy * dy;
    }

    /**
     * Determines whether this location is within a specified distance
     * from target location.
     *
     * @param location the location to test
     * @param dist the distance for the location to be within
     * @return true if the given location is within dist to this one; false otherwise
     *
     * @battlecode.doc.costlymethod
     */
    public final boolean isWithinDistance(MapLocation location, float dist) {
        return this.distanceSquaredTo(location) <= dist * dist;
    }

    /**
     * Determines whether this location is within the sensor radius of the
     * given robot.
     *
     * @param robot the robot to test
     * @return true if this location is within the robot's sensor radius,
     *         false otherwise
     *
     * @battlecode.doc.costlymethod
     */
    public final boolean isWithinSensorRadius(RobotInfo robot){
        return isWithinDistance(robot.location, robot.type.sensorRadius);
    }

    /**
     * Returns a new MapLocation object translated from this location
     * by a fixed amount.
     *
     * @param dx the amount to translate in the x direction
     * @param dy the amount to translate in the y direction
     * @return the new MapLocation that is the translated version of the original.
     *
     * @battlecode.doc.costlymethod
     */
    public final MapLocation translate(int dx, int dy) {
        return new MapLocation(x + dx, y + dy);
    }

    /**
     * For use by serializers.
     *
     * @battlecode.doc.costlymethod
     */
    private MapLocation() {
        this(0,0);
    }
}
