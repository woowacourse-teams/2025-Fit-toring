package fittoring.application.service.dto;

public record ShortSmsSendClientDto(
    String to,
    String from,
    String text
) {

}
