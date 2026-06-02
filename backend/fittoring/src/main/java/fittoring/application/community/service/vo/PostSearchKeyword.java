package fittoring.application.community.service.vo;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPostSearchKeywordException;

public record PostSearchKeyword(String value) {

    private static final int MAX_LENGTH = 50;

    public static PostSearchKeyword from(String rawKeyword) {
        if (rawKeyword == null || rawKeyword.isBlank()) {
            return new PostSearchKeyword(null);
        }

        String trimmedKeyword = rawKeyword.trim();
        if (trimmedKeyword.length() > MAX_LENGTH) {
            throw new InvalidPostSearchKeywordException(
                    BusinessErrorMessage.POST_SEARCH_KEYWORD_TOO_LONG.getMessage());
        }
        return new PostSearchKeyword(trimmedKeyword);
    }
}
