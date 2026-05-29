package fittoring.infrastructure.dto;

import java.util.Map;

public record BatchSendRequestEntry(
        String to,
        String from,
        String text,
        String subject,
        Map<String, String> customFields
) {

}
