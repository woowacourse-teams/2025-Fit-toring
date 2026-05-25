package fittoring.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BatchSendResponseDto(List<FailedMessageDto> failedMessageList) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FailedMessageDto(
            String to,
            String statusCode,
            String statusMessage,
            Map<String, String> customFields
    ) {

    }
}
