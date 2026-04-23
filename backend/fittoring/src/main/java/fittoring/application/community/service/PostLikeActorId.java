package fittoring.application.community.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPostLikeActorIdException;
import java.util.Optional;
import java.util.UUID;

public record PostLikeActorId(String value) {

    public PostLikeActorId {
        if (isInvalidUuid(value)) {
            throw new InvalidPostLikeActorIdException(BusinessErrorMessage.POST_LIKE_ACTOR_ID_INVALID.getMessage());
        }
    }

    public static Optional<PostLikeActorId> from(String value) {
        if (isInvalidUuid(value)) {
            return Optional.empty();
        }
        return Optional.of(new PostLikeActorId(value));
    }

    public static PostLikeActorId create() {
        return new PostLikeActorId(UUID.randomUUID().toString());
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
