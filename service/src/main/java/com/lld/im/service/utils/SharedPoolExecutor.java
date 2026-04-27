package com.lld.im.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class SharedPoolExecutor {

    private final ThreadPoolExecutor threadPoolExecutor;

     {
        AtomicInteger atomicLong = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(
                8,
                8,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        thread.setName("shared-poll-thread-" + atomicLong.getAndIncrement());
                        return thread;
                    }
                }
        );
    }

    public void submit(Runnable runnable) {
        threadPoolExecutor.submit(()->{
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("sharedPollError:{}",e.getMessage());
            }
        });
    }

}
