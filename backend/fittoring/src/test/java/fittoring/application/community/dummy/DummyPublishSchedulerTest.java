package fittoring.application.community.dummy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fittoring.application.community.dummy.DummyPublishService.PublishResult;
import fittoring.application.community.dummy.DummySchedulerProperties.BatchSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DummyPublishSchedulerTest {

    @Mock
    private DummyPublishService service;

    private DummyPublishScheduler scheduler;

    @BeforeEach
    void setUp() {
        DummySchedulerProperties properties = new DummySchedulerProperties(
                true,
                30000,
                new BatchSize(5, 5),
                5
        );
        scheduler = new DummyPublishScheduler(service, properties);
    }

    @DisplayName("게시글과 댓글을 배치 크기 안에서 처리하고, 더 이상 없으면 조기 종료한다.")
    @Test
    void publishesUntilBatchLimitOrEmpty() {
        // given
        when(service.publishNextPost(any()))
                .thenReturn(PublishResult.processed(1L), PublishResult.processed(2L), PublishResult.empty());
        when(service.publishNextComment(any()))
                .thenReturn(PublishResult.processed(10L), PublishResult.empty());

        // when
        scheduler.publishDueRows();

        // then
        verify(service, times(3)).publishNextPost(any());
        verify(service, times(2)).publishNextComment(any());
    }
}
