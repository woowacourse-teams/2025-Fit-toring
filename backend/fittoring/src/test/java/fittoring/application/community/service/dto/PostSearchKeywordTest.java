package fittoring.application.community.service.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.community.service.vo.PostSearchKeyword;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPostSearchKeywordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostSearchKeywordTest {

    @DisplayName("검색어가 null이면 값이 없다.")
    @Test
    void fromNullKeyword() {
        PostSearchKeyword actual = PostSearchKeyword.from(null);

        assertThat(actual.value()).isNull();
    }

    @DisplayName("검색어가 공백이면 값이 없다.")
    @Test
    void fromBlankKeyword() {
        PostSearchKeyword actual = PostSearchKeyword.from("   ");

        assertThat(actual.value()).isNull();
    }

    @DisplayName("검색어 앞뒤 공백을 제거한다.")
    @Test
    void trimKeyword() {
        PostSearchKeyword actual = PostSearchKeyword.from("  운동 루틴  ");

        assertThat(actual.value()).isEqualTo("운동 루틴");
    }

    @DisplayName("검색어가 정확히 50자이면 성공한다.")
    @Test
    void succeedWhenKeywordLengthIs50() {
        String keyword = "a".repeat(50);

        PostSearchKeyword actual = PostSearchKeyword.from(keyword);

        assertThat(actual.value()).isEqualTo(keyword);
    }

    @DisplayName("검색어가 50자를 초과하면 예외가 발생한다.")
    @Test
    void failWhenKeywordIsTooLong() {
        String keyword = "a".repeat(51);

        // when // then
        assertThatThrownBy(() -> PostSearchKeyword.from(keyword))
                .isInstanceOf(InvalidPostSearchKeywordException.class)
                .hasMessage(BusinessErrorMessage.POST_SEARCH_KEYWORD_TOO_LONG.getMessage());
    }
}
