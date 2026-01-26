package fittoring.admin.presentation.dto;

public record AdminDeviceResponse(
    Long id,
    String memberName,
    Long memberId,
    String pushToken
) {
}
