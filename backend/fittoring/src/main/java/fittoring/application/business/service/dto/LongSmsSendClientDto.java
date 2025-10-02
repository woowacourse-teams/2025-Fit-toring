package fittoring.application.business.service.dto;

public record LongSmsSendClientDto(
    String to,
    String from,
    String text,
    String subject) {

}
