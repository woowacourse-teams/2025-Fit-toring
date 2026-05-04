package fittoring.application.community.dummy.scenario;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class ScenarioLoader {

    public ScenarioFile load(InputStream input) {
        Map<String, Object> root = new Yaml().load(input);
        List<Map<String, Object>> rawScenarios = asList(root.get("scenarios"));
        List<Scenario> scenarios = rawScenarios.stream()
                .map(this::toScenario)
                .toList();
        ScenarioFile file = new ScenarioFile(scenarios);
        ScenarioValidator.validate(file);
        return file;
    }

    private Scenario toScenario(Map<String, Object> raw) {
        ScenarioPost post = toPost(asMap(raw.get("post")));
        List<ScenarioComment> comments = asList(raw.get("comments")).stream()
                .map(this::toComment)
                .toList();
        return new Scenario(post, comments);
    }

    private ScenarioPost toPost(Map<String, Object> raw) {
        return new ScenarioPost(
                (String) raw.get("nickname"),
                OffsetDateTime.parse((String) raw.get("scheduled_at")),
                (String) raw.get("title"),
                (String) raw.get("content")
        );
    }

    private ScenarioComment toComment(Map<String, Object> raw) {
        List<ScenarioComment> replies = asList(raw.get("replies")).stream()
                .map(this::toComment)
                .toList();
        return new ScenarioComment(
                (String) raw.get("nickname"),
                OffsetDateTime.parse((String) raw.get("scheduled_at")),
                (String) raw.get("content"),
                replies
        );
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> asList(Object value) {
        if (value == null) {
            return List.of();
        }
        return (List<Map<String, Object>>) value;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        return (Map<String, Object>) value;
    }
}
