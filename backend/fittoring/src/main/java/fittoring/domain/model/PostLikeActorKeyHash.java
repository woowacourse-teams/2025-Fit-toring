package fittoring.domain.model;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPostLikeActorKeyHashException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class PostLikeActorKeyHash {

    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-f]{64}$");

    @Column(name = "actor_key_hash", nullable = false, length = 64)
    private String value;

    protected PostLikeActorKeyHash() {
    }

    public PostLikeActorKeyHash(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (isInvalidHash(value)) {
            throw new InvalidPostLikeActorKeyHashException(
                    BusinessErrorMessage.POST_LIKE_ACTOR_KEY_HASH_INVALID.getMessage());
        }
    }

    private boolean isInvalidHash(String value) {
        return value == null || !HEX_PATTERN.matcher(value).matches();
    }
}
