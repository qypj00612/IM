package com.lld.im.vector.service;

import com.lld.im.common.model.message.VectorMessageDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class VectorStoreService {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreService.class);
    private final ThreadPoolExecutor threadPoolExecutor;

    private final VectorStore vectorStore;

    {
        AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NotNull Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        thread.setName("vector-store-thread-" + num.getAndIncrement());
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

    }

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void vectorMessage(VectorMessageDto dto) {
        threadPoolExecutor.execute(()->{

            try {
                // 1. 构建元数据（只存索引和过滤字段，不存原文）
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("appId", dto.getAppId());
                metadata.put("messageKey", dto.getMessageKey()); // 唯一索引
                metadata.put("conversationId", dto.getConversationId());
                metadata.put("messageTime", dto.getMessageTime());

                // 2. 创建 document（用 content 生成向量，但是不存储原文）
                Document document = new Document(
                        dto.getContent(),  // 只用来生成向量
                        metadata           // 真正存入向量库的是这个
                );

                // 3. 向量化并入库
                vectorStore.add(Collections.singletonList(document));

            } catch (Exception e) {
                e.printStackTrace();
                log.info("消息向量异常");
            }

        });
    }
}
