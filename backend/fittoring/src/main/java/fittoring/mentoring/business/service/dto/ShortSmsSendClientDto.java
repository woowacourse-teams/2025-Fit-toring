package fittoring.mentoring.business.service.dto;

public record ShortSmsSendClientDto(
        String to,
        String from,
        String text
) {
}
