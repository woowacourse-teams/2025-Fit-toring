package fittoring.application.community.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPostSearchKeywordException;

public class PostSearchKeyword {

    private static final int MAX_LENGTH = 50;

    private final String value;

    private PostSearchKeyword(String value) {
        this.value = value;
    }

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

    public String value() {
        return value;
    }
}
