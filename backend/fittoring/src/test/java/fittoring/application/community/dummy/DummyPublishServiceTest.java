package fittoring.application.community.dummy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fittoring.application.community.dummy.DummyPublishDao.CommentPendingRow;
import fittoring.application.community.dummy.DummyPublishDao.PostPendingRow;
import fittoring.application.community.dummy.DummyPublishService.PublishResult;
import fittoring.application.community.dummy.DummySchedulerProperties.BatchSize;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DummyPublishServiceTest {

    private static final int MAX_ATTEMPT = 5;
    private static final LocalDateTime SCHEDULED_AT = LocalDateTime.of(2026, 5, 4, 15, 0);

    @Mock
    private DummyPublishDao dao;

    private DummyPublishService service;

    @BeforeEach
    void setUp() {
        DummySchedulerProperties properties = new DummySchedulerProperties(
                true,
                30000,
                new BatchSize(100, 200),
                MAX_ATTEMPT
        );
        service = new DummyPublishService(dao, properties);
    }

    @DisplayName("발행할 게시글 pending row가 없으면 false를 반환한다.")
    @Test
    void returnsFalseWhenPostIsEmpty() {
        // given
        when(dao.findNextPostForPublish(any(), any(LocalDateTime.class))).thenReturn(Optional.empty());

        // when
        PublishResult result = service.publishNextPost(List.of());

        // then
        assertThat(result.processed()).isFalse();
        verify(dao, never()).insertPublishedPost(any(), any(LocalDateTime.class));
    }

    @DisplayName("게시글 pending row를 운영 post로 발행하고 PUBLISHED로 표시한다.")
    @Test
    void publishesPost() {
        // given
        PostPendingRow row = postRow(1L, 0);
        when(dao.findNextPostForPublish(any(), any(LocalDateTime.class))).thenReturn(Optional.of(row));
        when(dao.insertPublishedPost(eq(row), any(LocalDateTime.class))).thenReturn(10L);

        // when
        PublishResult result = service.publishNextPost(List.of());

        // then
        assertThat(result.processed()).isTrue();
        assertThat(result.pendingId()).isEqualTo(1L);
        verify(dao).markPostPublished(1L, 10L);
        verify(dao, never()).markPostFailedAttempt(1L, MAX_ATTEMPT);
    }

    @DisplayName("게시글 발행 실패 시 attempt_count를 증가시킨다.")
    @Test
    void marksPostFailedAttemptWhenPublishFails() {
        // given
        PostPendingRow row = postRow(1L, 4);
        when(dao.findNextPostForPublish(any(), any(LocalDateTime.class))).thenReturn(Optional.of(row));
        doThrow(new RuntimeException("insert failed")).when(dao).insertPublishedPost(eq(row), any(LocalDateTime.class));

        // when
        PublishResult result = service.publishNextPost(List.of());

        // then
        assertThat(result.processed()).isTrue();
        assertThat(result.pendingId()).isEqualTo(1L);
        verify(dao).markPostFailedAttempt(1L, MAX_ATTEMPT);
        verify(dao, never()).markPostPublished(1L, 10L);
    }

    @DisplayName("게시글 운영 INSERT 후 PUBLISHED 갱신 실패는 예외를 전파해 트랜잭션 롤백 대상이 되게 한다.")
    @Test
    void propagatesWhenMarkingPostPublishedFails() {
        // given
        PostPendingRow row = postRow(1L, 0);
        when(dao.findNextPostForPublish(any(), any(LocalDateTime.class))).thenReturn(Optional.of(row));
        when(dao.insertPublishedPost(eq(row), any(LocalDateTime.class))).thenReturn(10L);
        doThrow(new RuntimeException("update failed")).when(dao).markPostPublished(1L, 10L);

        // when // then
        assertThatThrownBy(() -> service.publishNextPost(List.of()))
                .isInstanceOf(RuntimeException.class);
        verify(dao, never()).markPostFailedAttempt(1L, MAX_ATTEMPT);
    }

    @DisplayName("발행할 댓글 pending row가 없으면 false를 반환한다.")
    @Test
    void returnsFalseWhenCommentIsEmpty() {
        // given
        when(dao.findNextCommentForPublish(any(), any(LocalDateTime.class))).thenReturn(Optional.empty());

        // when
        PublishResult result = service.publishNextComment(List.of());

        // then
        assertThat(result.processed()).isFalse();
        verify(dao, never()).insertPublishedComment(any(), any(LocalDateTime.class));
    }

    @DisplayName("댓글 pending row를 운영 comment로 발행하고 PUBLISHED로 표시한다.")
    @Test
    void publishesComment() {
        // given
        CommentPendingRow row = commentRow(2L, 0);
        when(dao.findNextCommentForPublish(any(), any(LocalDateTime.class))).thenReturn(Optional.of(row));
        when(dao.insertPublishedComment(eq(row), any(LocalDateTime.class))).thenReturn(20L);

        // when
        PublishResult result = service.publishNextComment(List.of());

        // then
        assertThat(result.processed()).isTrue();
        assertThat(result.pendingId()).isEqualTo(2L);
        verify(dao).markCommentPublished(2L, 20L);
        verify(dao, never()).markCommentFailedAttempt(2L, MAX_ATTEMPT);
    }

    @DisplayName("댓글 발행 실패 시 attempt_count를 증가시킨다.")
    @Test
    void marksCommentFailedAttemptWhenPublishFails() {
        // given
        CommentPendingRow row = commentRow(2L, 4);
        when(dao.findNextCommentForPublish(any(), any(LocalDateTime.class))).thenReturn(Optional.of(row));
        doThrow(new RuntimeException("insert failed")).when(dao).insertPublishedComment(eq(row), any(LocalDateTime.class));

        // when
        PublishResult result = service.publishNextComment(List.of());

        // then
        assertThat(result.processed()).isTrue();
        assertThat(result.pendingId()).isEqualTo(2L);
        verify(dao).markCommentFailedAttempt(2L, MAX_ATTEMPT);
        verify(dao, never()).markCommentPublished(2L, 20L);
    }

    @DisplayName("댓글 운영 INSERT 후 PUBLISHED 갱신 실패는 예외를 전파해 트랜잭션 롤백 대상이 되게 한다.")
    @Test
    void propagatesWhenMarkingCommentPublishedFails() {
        // given
        CommentPendingRow row = commentRow(2L, 0);
        when(dao.findNextCommentForPublish(any(), any(LocalDateTime.class))).thenReturn(Optional.of(row));
        when(dao.insertPublishedComment(eq(row), any(LocalDateTime.class))).thenReturn(20L);
        doThrow(new RuntimeException("update failed")).when(dao).markCommentPublished(2L, 20L);

        // when // then
        assertThatThrownBy(() -> service.publishNextComment(List.of()))
                .isInstanceOf(RuntimeException.class);
        verify(dao, never()).markCommentFailedAttempt(2L, MAX_ATTEMPT);
    }

    private PostPendingRow postRow(long id, int attemptCount) {
        return new PostPendingRow(
                id,
                "title",
                "content",
                "nickname",
                "hash",
                SCHEDULED_AT,
                attemptCount
        );
    }

    private CommentPendingRow commentRow(long id, int attemptCount) {
        return new CommentPendingRow(
                id,
                "content",
                "nickname",
                "hash",
                SCHEDULED_AT,
                attemptCount,
                10L,
                null,
                null
        );
    }
}
