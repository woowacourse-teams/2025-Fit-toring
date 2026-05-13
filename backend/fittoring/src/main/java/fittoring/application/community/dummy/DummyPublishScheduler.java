package fittoring.application.community.dummy;

import fittoring.application.community.dummy.DummyPublishService.PublishResult;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "dummy.scheduler.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DummyPublishScheduler {

    private final DummyPublishService service;
    private final DummySchedulerProperties properties;

    @Scheduled(fixedDelayString = "${dummy.scheduler.poll-interval-ms}")
    public void publishDueRows() {
        publishPosts();
        publishComments();
    }

    private void publishPosts() {
        Set<Long> processedIds = new HashSet<>();
        for (int i = 0; i < properties.batchSize().post(); i++) {
            PublishResult result = service.publishNextPost(processedIds);
            if (!result.processed()) {
                return;
            }
            processedIds.add(result.pendingId());
        }
    }

    private void publishComments() {
        Set<Long> processedIds = new HashSet<>();
        for (int i = 0; i < properties.batchSize().comment(); i++) {
            PublishResult result = service.publishNextComment(processedIds);
            if (!result.processed()) {
                return;
            }
            processedIds.add(result.pendingId());
        }
    }
}
