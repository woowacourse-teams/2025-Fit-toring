package fittoring.infrastructure.dto;

public record LongSmsSendClientDto(
    String to,
    String from,
    String text,
    String subject) {

}
