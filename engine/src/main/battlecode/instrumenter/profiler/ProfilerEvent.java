package battlecode.instrumenter.profiler;

import org.json.JSONObject;

public class ProfilerEvent {
    private ProfilerEventType type;
    private int at;
    private int frameId;

    public ProfilerEvent(ProfilerEventType type, int at, int frameId) {
        this.type = type;
        this.at = at;
        this.frameId = frameId;
    }

    public ProfilerEventType getType() {
        return type;
    }

    public int getAt() {
        return at;
    }

    public int getFrameId() {
        return frameId;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("type", type.getValue());
        object.put("at", at);
        object.put("frame", frameId);
        return object;
    }
}
