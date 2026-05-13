package fittoring.application.auth.repository;

public record PhoneVerificationData(
        String phoneNumber,
        String code,
        boolean verified
) {
}
