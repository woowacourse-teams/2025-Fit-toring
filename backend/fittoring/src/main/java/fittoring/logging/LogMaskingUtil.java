package fittoring.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LogMaskingUtil {

    private final ObjectMapper objectMapper;

    private final Set<String> MASKING_KEYS = Set.of(
            "phoneNumber",
            "phone",
            "password",
            "accessToken",
            "refreshToken"
    );

    /**
     * 문자열을 JsonNode로 파싱 (실패 시 null)
     */
    public JsonNode toJsonNodeOrNull(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * JsonNode 마스킹 (원본 보존: deepCopy 후 수정)
     */
    public JsonNode maskNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return NullNode.getInstance();
        }

        if (node.isObject()) {
            ObjectNode obj = node.deepCopy();
            List<String> keys = new ArrayList<>();
            obj.fieldNames().forEachRemaining(keys::add);
            for (String key : keys) {
                JsonNode val = obj.get(key);
                if (val == null) {
                    continue;
                }
                if (val.isValueNode()) {
                    String replaced = maskValueByKey(key, val.asText());
                    if (replaced != null) {
                        obj.put(key, replaced);
                    }
                } else {
                    obj.set(key, maskNode(val));
                }
            }
            return obj;
        }
        if (node.isArray()) {
            ArrayNode arr = objectMapper.createArrayNode();
            node.forEach(el -> arr.add(maskNode(el)));
            return arr;
        }
        return node;
    }

    private String maskValueByKey(String key, String original) {
        if (!MASKING_KEYS.contains(key)) {
            return null;
        }

        if ("phoneNumber".equals(key) || "phone".equals(key)) {
            return maskPhoneMid4(original);
        }
        // password / accessToken / refreshToken
        return maskSecret(original);
    }

    /**
     * 가운데 4자리 마스킹: 010-****-1234
     */
    public static String maskPhoneMid4(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("(?<=^\\d{2,3}-)\\d{3,4}(?=-\\d{4}$)", "****");
    }

    /**
     * 끝 2자리만 남기고 마스킹: 123456 -> ****56
     */
    private static String maskSecret(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        if (len <= 2) {
            return "**";
        }
        return "*".repeat(len - 2) + s.substring(len - 2);
    }
}
