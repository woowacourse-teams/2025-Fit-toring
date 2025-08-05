package fittoring.mentoring.business.service.dto;

public record LongSmsSendClientDto(
        String to,
        String from,
        String text,
        String subject
) {
}
