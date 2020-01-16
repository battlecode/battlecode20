package battlecode.common;

/**
 * Struct that stores basic information that was 'sensed' of another Robot. This
 * info is ephemeral and there is no guarantee any of it will remain the same
 * between rounds.
 */
public class RobotInfo {

    /**
     * The unique ID of the robot.
     */
    public final int ID;

    /**
     * The Team that the robot is on.
     */
    public final Team team;

    /**
     * The type of the robot.
     */
    public final RobotType type;


    /**
     * The dirt carried by a landscaper, or the dirt on top of a building. 0 if
     * the robot is neither a landscaper or a building.
     */
    public final int dirtCarrying;

    /**
     * A boolean indicating whether the robot is currently holding a unit. Always false
     * for robots that are not delivery drones.
     */
    public final boolean currentlyHoldingUnit;

    /**
     * The ID of the unit that the robot is currently holding. -1 if currentlyHoldingUnit
     * is false.
     */
    public final int heldUnitID;

    /**
     * The amount of soup carried by the robot. Works for miners and refineries (HQs).
     */
    public final int soupCarrying;

    /**
     * The cooldown of the robot.
     */
    public final float cooldownTurns;

    /**
     * The current location of the robot.
     */
    public final MapLocation location;

    public RobotInfo(int ID, Team team, RobotType type, int dirtCarrying,
                     boolean currentlyHoldingUnit, int heldUnitID, int soupCarrying,
                     float cooldownTurns, MapLocation location) {
        super();
        this.ID = ID;
        this.team = team;
        this.type = type;
        this.dirtCarrying = dirtCarrying;
        this.currentlyHoldingUnit = currentlyHoldingUnit;
        this.heldUnitID = heldUnitID;
        this.soupCarrying = soupCarrying;
        this.cooldownTurns = cooldownTurns;
        this.location = location;
    }

    /**
     * Returns the ID of this robot.
     *
     * @return
     */
    public int getID() {
        return this.ID;
    }

    /**
     * Returns the team that this robot is on.
     *
     * @return the team that this robot is on.
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Returns the type of this robot.
     *
     * @return the type of this robot.
     */
    public RobotType getType() {
        return type;
    }

    /**
     * Returns the dirt carried by the robot.
     *
     * @return the dirt carried by the robot.
     */
    public int getDirtCarrying() {
        return dirtCarrying;
    }

    /**
     * Returns whether the robot is currently holding a unit.
     *
     * @return whether the robot is currently holding a unit.
     */
    public boolean isCurrentlyHoldingUnit() {
        return currentlyHoldingUnit;
    }

    /**
     * Returns the ID of the unit that the robot is holding, if it
     * is holding one. Returns -1 if isCurrentlyHoldingUnit() is false.
     *
     * @return the ID of the unit held.
     */
    public int getHeldUnitID() {
        return heldUnitID;
    }

    /**
     * Returns the soup carried by the robot (miner or refinery (or HQ), 0
     * for other robots).
     *
     * @return the soup amount.
     */
    public int getSoupCarrying() {
        return soupCarrying;
    }

    /**
     * Returns the cooldown turns of the robot.
     *
     * @return the cooldown.
     */
    public float getCooldownTurns() {
        return cooldownTurns;
    }

    /**
     * Returns the location of this robot.
     *
     * @return the location.
     */
    public MapLocation getLocation() {
        return this.location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RobotInfo robotInfo = (RobotInfo) o;

        if (ID != robotInfo.ID) return false;
        if (team != robotInfo.team) return false;
        if (type != robotInfo.type) return false;
        if (dirtCarrying != robotInfo.dirtCarrying) return false;
        if (currentlyHoldingUnit != robotInfo.currentlyHoldingUnit) return false;
        if (heldUnitID != robotInfo.heldUnitID) return false;
        if (soupCarrying != robotInfo.soupCarrying) return false;
        if (Math.abs(cooldownTurns - robotInfo.cooldownTurns) < 0.000001) return false;
        return location.equals(robotInfo.location);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = ID;
        result = 31 * result + team.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + dirtCarrying;
        result = 31 * result + (currentlyHoldingUnit ? 1 : 0);
        result = 31 * result + heldUnitID;
        result = 31 * result + soupCarrying;
        result = 31 * result + (int) Math.ceil(cooldownTurns);
        result = 31 * result + location.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RobotInfo{" +
                "ID=" + ID +
                ", team=" + team +
                ", type=" + type +
                ", dirtCarrying=" + dirtCarrying +
                ", currentlyHoldingUnit=" + currentlyHoldingUnit +
                ", heldUnitID=" + heldUnitID +
                ", soupCarrying=" + soupCarrying +
                ", cooldownTurns=" + cooldownTurns +
                ", location=" + location +
                '}';
    }
}
