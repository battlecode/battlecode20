package battlecode.instrumenter.profiler;

import battlecode.common.RobotType;
import battlecode.common.Team;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

/**
 * A ProfilerCollection is created by the Server and contains all Profiler instances for both
 * player teams for all matches that are being ran. It serves as a factory to create new
 * profilers and contains a writeToFile() method that converts all profiling data including the
 * metadata needed for the client to properly visualize the results into a single JSON object and
 * save it to a file.
 */
public class ProfilerCollection {
    /**
     * Per match, a map of team to map of robot id to Profiler instances.
     */
    private List<Map<Team, Map<Integer, Profiler>>> matches = new ArrayList<>(3);

    /**
     * A collection of method names and a map of method name to index in the frames list.
     * Each team has it's own frames list and frameIds map.
     */
    private Map<Team, List<String>> frames = new HashMap<>(2);
    private Map<Team, Map<String, Integer>> frameIds = new HashMap<>(2);

    public Profiler createProfiler(int matchId, Team team, int robotId, RobotType robotType) {
        if (matchId >= matches.size()) {
            matches.add(new HashMap<>(2));
        }

        Map<Team, Map<Integer, Profiler>> matchMap = matches.get(matchId);

        if (!matchMap.containsKey(team)) {
            matchMap.put(team, new HashMap<>());
        }

        Map<Integer, Profiler> profilerMap = matchMap.get(team);

        if (!frames.containsKey(team)) {
            frames.put(team, new ArrayList<>());
            frameIds.put(team, new HashMap<>());
        }

        // The name has to be display-friendly
        String name = String.format("#%s (%s)", robotId, robotType.toString());

        Profiler profiler = new Profiler(name, methodName -> getFrameIdForTeam(team, methodName));
        profilerMap.put(robotId, profiler);
        return profiler;
    }

    public void writeToFile(File outputFile) {
        byte[] outputBytes = toJSON().toString().getBytes();

        // Compress the data using gzip, taken from GameMaker.toBytes()
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            GZIPOutputStream zipper = new GZIPOutputStream(result);
            IOUtils.copy(new ByteArrayInputStream(outputBytes), zipper);
            zipper.close();
            zipper.flush();
            result.flush();
            outputBytes = result.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Gzipping failed?", e);
        }

        try {
            FileUtils.writeByteArrayToFile(outputFile, outputBytes);
        } catch (IOException e) {
            // GameMaker.writeGame() also throws a RuntimeException when an IOException occurs, this should be fine
            throw new RuntimeException(e);
        }
    }

    private int getFrameIdForTeam(Team team, String methodName) {
        if (!frameIds.containsKey(team)) {
            frameIds.put(team, new HashMap<>());
        }

        Map<String, Integer> frameIdsForTeam = frameIds.get(team);

        if (!frameIdsForTeam.containsKey(methodName)) {
            if (!frames.containsKey(team)) {
                frames.put(team, new ArrayList<>());
            }

            List<String> framesForTeam = frames.get(team);
            framesForTeam.add(methodName);
            frameIdsForTeam.put(methodName, framesForTeam.size());
        }

        return frameIdsForTeam.get(methodName);
    }

    /**
     * Returns a JSON object that can be used by the client to visualize the profiling results, structured like this:
     * <pre>
     * {@code
     * {
     *      // Matches in increasing order by match id
     *     "matches": [
     *          {
     *              "teamA": {
     *                  // Robot id to the index in data.profiles that contains the profiling results for that robot
     *                  "robots": {
     *                      "0": 0,
     *                      "10421": 1,
     *                      ...
     *                  },
     *                  "data": {
     *                      // All the profiling data in speedscope's file format
     *                      // See https://github.com/jlfwong/speedscope/wiki/Importing-from-custom-sources
     *                  }
     *              },
     *              "teamB": {
     *                  // Same as teamA
     *              }
     *          },
     *          ...
     *     ]
     * }
     * }
     * </pre>
     */
    private JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("matches", matches.stream().map(this::matchToJSON).collect(Collectors.toList()));
        return object;
    }

    private JSONObject matchToJSON(Map<Team, Map<Integer, Profiler>> match) {
        JSONObject object = new JSONObject();
        object.put("teamA", teamToJson(Team.A, match.get(Team.A)));
        object.put("teamB", teamToJson(Team.B, match.get(Team.B)));
        return object;
    }

    private JSONObject teamToJson(Team team, Map<Integer, Profiler> profilers) {
        Map<String, Integer> robotToProfileIndex = new HashMap<>(profilers.size());
        List<JSONObject> profileObjects = new ArrayList<>(profilers.size());

        for (Map.Entry<Integer, Profiler> entry : profilers.entrySet()) {
            robotToProfileIndex.put(entry.getKey().toString(), profileObjects.size());
            profileObjects.add(entry.getValue().toJSON());
        }

        JSONObject sharedObject = new JSONObject();
        sharedObject.put("frames", frames.get(team).stream().map(this::frameToJSON).collect(Collectors.toList()));

        JSONObject dataObject = new JSONObject();
        dataObject.put("shared", sharedObject);
        dataObject.put("profiles", profileObjects);

        JSONObject teamObject = new JSONObject();
        teamObject.put("robots", robotToProfileIndex);
        teamObject.put("data", dataObject);
        return teamObject;
    }

    private JSONObject frameToJSON(String methodName) {
        JSONObject object = new JSONObject();
        object.put("name", methodName);
        return object;
    }
}
