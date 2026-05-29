package fittoring.application.community.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidLikeActorIdException;
import java.util.Optional;
import java.util.UUID;

public record LikeActorId(String value) {

    public LikeActorId {
        if (isInvalidUuid(value)) {
            throw new InvalidLikeActorIdException(BusinessErrorMessage.LIKE_ACTOR_ID_INVALID.getMessage());
        }
    }

    public static Optional<LikeActorId> from(String value) {
        if (isInvalidUuid(value)) {
            return Optional.empty();
        }
        return Optional.of(new LikeActorId(value));
    }

    public static LikeActorId create() {
        return new LikeActorId(UUID.randomUUID().toString());
    }

    private static boolean isInvalidUuid(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        try {
            return !UUID.fromString(value).toString().equals(value);
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}
