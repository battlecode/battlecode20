package battlecode.instrumenter.profiler;

import battlecode.common.RobotType;
import battlecode.common.Team;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilerCollection {
    /**
     * Match id to map of team to a list of all profilers of the team.
     */
    private Map<Integer, Map<Team, List<Profiler>>> profilers = new HashMap<>(3);

    public Profiler createProfiler(int matchId, Team team, int robotId, RobotType robotType) {
        if (!profilers.containsKey(matchId)) {
            profilers.put(matchId, new HashMap<>(2));
        }

        Map<Team, List<Profiler>> matchProfilers = profilers.get(matchId);

        if (!matchProfilers.containsKey(team)) {
            matchProfilers.put(team, new ArrayList<>());
        }

        // The name has to be display-friendly
        Profiler profiler = new Profiler(String.format("#%s (%s)", robotId, robotType.toString()));
        matchProfilers.get(team).add(profiler);

        return profiler;
    }

    public void writeToFile(File outputFile) {
        // TODO(jmerle): Implement the actual convert-to-json logic

        StringBuilder output = new StringBuilder();

        for (Map.Entry<Integer, Map<Team, List<Profiler>>> matchEntry : profilers.entrySet()) {
            int matchId = matchEntry.getKey();
            Map<Team, List<Profiler>> profilerMap = matchEntry.getValue();

            output.append("Match: ").append(matchId).append("\n");

            for (Map.Entry<Team, List<Profiler>> teamEntry : profilerMap.entrySet()) {
                Team team = teamEntry.getKey();
                List<Profiler> profilerList = teamEntry.getValue();

                output.append("Team: ").append(team.toString()).append("\n");

                for (Profiler profiler : profilerList) {
                    output.append(profiler.getName()).append("\n");
                }
            }
        }

        try {
            Files.write(outputFile.toPath(), output.toString().getBytes());
        } catch (IOException e) {
            // GameMaker.writeGame() also throws a RuntimeException when an IOException occurs, this should be fine
            throw new RuntimeException(e);
        }
    }
}
