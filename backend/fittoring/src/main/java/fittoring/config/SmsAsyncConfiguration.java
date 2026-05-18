package fittoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class SmsAsyncConfiguration {

    public static final String SMS_EXECUTOR_BEAN = "smsExecutor";

    @Bean(name = SMS_EXECUTOR_BEAN)
    public TaskExecutor smsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("sms-");
        executor.setWaitForTasksToCompleteOnShutdown(true); // Graceful Shutdown 설정 : 서버 종료할 때, 이미 큐에 들어가 있거나 현재 처리 중인 SMS 발송 작업이 있다면
        executor.setAwaitTerminationSeconds(10);    // 최대 10초간 기다림
        executor.initialize();
        return executor;
    }
}
