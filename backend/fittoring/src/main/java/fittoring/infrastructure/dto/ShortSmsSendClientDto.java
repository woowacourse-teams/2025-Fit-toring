package fittoring.infrastructure.dto;

public record ShortSmsSendClientDto(
    String to,
    String from,
    String text
) {

}
