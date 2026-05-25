package fittoring.infrastructure.dto;

import fittoring.domain.model.Phone;

public record SmsOutboxMessage(Long outboxId, Phone to, String text, String subject) {

}
