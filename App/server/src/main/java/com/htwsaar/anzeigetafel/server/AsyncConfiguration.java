package com.htwsaar.anzeigetafel.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@EnableAsync
public class AsyncConfiguration {

    /**
     * Executor bean for async tasks in the application
     * the method is annotated with @Bean to make it async task executor
     * @return Executor
     */
    @Bean("asyncTaskExecutor")
    public Executor executor()
    {
        // create a new thread pool task executor
        // and set the properties of the executor
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        taskExecutor.setQueueCapacity(150);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setThreadNamePrefix("AsyncTaskThread-");
        taskExecutor.initialize();

        return taskExecutor;
    }
}
