package fittoring.application.community.dummy;

import fittoring.application.community.dummy.DummyPublishDao.CommentPendingRow;
import fittoring.application.community.dummy.DummyPublishDao.PostPendingRow;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DummyPublishService {

    private static final ZoneId SCHEDULE_ZONE = ZoneId.of("Asia/Seoul");

    private final DummyPublishDao dao;
    private final DummySchedulerProperties properties;

    @Transactional
    public PublishResult publishNextPost(Collection<Long> excludedIds) {
        Optional<PostPendingRow> row = dao.findNextPostForPublish(excludedIds, now());
        if (row.isEmpty()) {
            return PublishResult.empty();
        }
        PostPendingRow pending = row.get();
        long publishedPostId;
        try {
            publishedPostId = dao.insertPublishedPost(pending);
        } catch (RuntimeException e) {
            dao.markPostFailedAttempt(pending.id(), properties.maxAttempt());
            logFailure("post", pending.id(), pending.attemptCount(), e);
            return PublishResult.processed(pending.id());
        }
        dao.markPostPublished(pending.id(), publishedPostId);
        return PublishResult.processed(pending.id());
    }

    @Transactional
    public PublishResult publishNextComment(Collection<Long> excludedIds) {
        Optional<CommentPendingRow> row = dao.findNextCommentForPublish(excludedIds, now());
        if (row.isEmpty()) {
            return PublishResult.empty();
        }
        CommentPendingRow pending = row.get();
        long publishedCommentId;
        try {
            publishedCommentId = dao.insertPublishedComment(pending);
        } catch (RuntimeException e) {
            dao.markCommentFailedAttempt(pending.id(), properties.maxAttempt());
            logFailure("comment", pending.id(), pending.attemptCount(), e);
            return PublishResult.processed(pending.id());
        }
        dao.markCommentPublished(pending.id(), publishedCommentId);
        return PublishResult.processed(pending.id());
    }

    private void logFailure(String type, long pendingId, int currentAttemptCount, RuntimeException e) {
        int nextAttemptCount = currentAttemptCount + 1;
        if (nextAttemptCount >= properties.maxAttempt()) {
            log.error(
                    "Dummy {} publish failed and marked FAILED. pendingId={}, attemptCount={}",
                    type,
                    pendingId,
                    nextAttemptCount,
                    e
            );
            return;
        }
        log.warn(
                "Dummy {} publish failed. pendingId={}, attemptCount={}",
                type,
                pendingId,
                nextAttemptCount,
                e
        );
    }

    private LocalDateTime now() {
        return LocalDateTime.now(SCHEDULE_ZONE);
    }

    public record PublishResult(
            boolean processed,
            Long pendingId
    ) {

        public static PublishResult empty() {
            return new PublishResult(false, null);
        }

        public static PublishResult processed(long pendingId) {
            return new PublishResult(true, pendingId);
        }
    }
}
