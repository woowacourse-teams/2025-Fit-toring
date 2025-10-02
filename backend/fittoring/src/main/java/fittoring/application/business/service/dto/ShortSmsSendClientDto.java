package fittoring.application.business.service.dto;

public record ShortSmsSendClientDto(
    String to,
    String from,
    String text
) {

}
