package com.lld.im.ai.service;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lld.im.ai.dao.ImGroupMessageHistory;
import com.lld.im.ai.dao.ImMessageBody;
import com.lld.im.ai.dao.ImMessageHistory;
import com.lld.im.ai.dao.tool.AITools;
import com.lld.im.ai.model.AIMessage;
import com.lld.im.ai.model.AIReply;
import com.lld.im.ai.model.SearchedMessage;
import com.lld.im.ai.model.TimeRangeResult;
import com.lld.im.ai.utils.MessageProducer;
import com.lld.im.codec.pack.ai.AIReplyPack;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.ai.AIEventCommand;
import com.lld.im.common.enums.conversation.ConversationTypeEnum;
import com.lld.im.common.model.ClientInfo;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class AIService {

    private final ChatClient chatClient;

    private final VectorStore vectorStore;

    private final AITools aiTools;

    private final MessageProducer messageProducer;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final ImMessageBodyService imMessageBodyService;

    private final ImGroupMessageHistoryService imGroupMessageHistoryService;

    private final ImMessageHistoryService imMessageHistoryService;

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
                        thread.setName("AI-Thread-" + num.incrementAndGet());
                        thread.setDaemon(true);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public AIService(ChatClient.Builder clientBuilder, VectorStore vectorStore, RocketMQTemplate rocketMQTemplate, AITools aiTools, MessageProducer messageProducer, ImMessageBodyService imMessageBodyService, ImGroupMessageHistoryService imGroupMessageHistoryService, ImMessageHistoryService imMessageHistoryService) {
        chatClient = clientBuilder.build();
        this.vectorStore = vectorStore;
        this.aiTools = aiTools;
        this.messageProducer = messageProducer;
        this.imMessageBodyService = imMessageBodyService;
        this.imGroupMessageHistoryService = imGroupMessageHistoryService;
        this.imMessageHistoryService = imMessageHistoryService;
    }

    public void intelligenceReply(AIReply aiReply) {

        threadPoolExecutor.execute(() -> {

            AIReplyPack aiReplyPack = new AIReplyPack();

            try {
                List<AIMessage> message = getRecentMessage(aiReply);
                if (message.isEmpty()) {
                    aiReplyPack.setConversationId(aiReply.getConversationId());
                    aiReplyPack.setReply(List.of("抱歉，您与对方暂无聊天记录"));
                    return;
                }
                String s = buildConversationContext(message);
                String mpt = buildReplyPrompt(aiReply.getRequirement(), s);

                // 调用ai大模型
                String content = chatClient.prompt(mpt).call().content();
                log.info("ai生成回复：{}", content);

                List<String> list = JSON.parseObject(content, new TypeReference<List<String>>() {
                });

                aiReplyPack.setReply(list);
                aiReplyPack.setConversationId(aiReply.getConversationId());

//                messageProducer.sendToUser(
//                        aiReply.getFromId(),
//                        AIEventCommand.AI_INTELLIGENT_REPLY,
//                        aiReplyPack,
//                        new ClientInfo(aiReply.getAppId(), aiReply.getClientType(), aiReply.getImei()),
//                        Constants.RocketConstants.AI2Im
//                );
            } catch (Exception e) {
                e.printStackTrace();
                log.info("ai调用异常");
                aiReplyPack.setConversationId(aiReply.getConversationId());
                aiReplyPack.setReply(List.of("AI服务繁忙"));
            } finally {
                messageProducer.sendToUser(
                        aiReply.getFromId(),
                        AIEventCommand.AI_INTELLIGENT_REPLY,
                        aiReplyPack,
                        new ClientInfo(aiReply.getAppId(), aiReply.getClientType(), aiReply.getImei()),
                        Constants.RocketConstants.AI2Im
                );
            }
        });

    }

    private List<AIMessage> getRecentMessage(AIReply aiReply) {
        if (ObjectUtil.isNull(aiReply.getRecentMessageNum()) || aiReply.getRecentMessageNum() > 200) {
            aiReply.setRecentMessageNum(20);
        }

        List<AIMessage> message = new ArrayList<>();

        if (aiReply.getConversationType() == ConversationTypeEnum.P2P.getCode()) {

            List<ImMessageHistory> imHistorys = imMessageHistoryService.getRecentMessage(aiReply);

            List<Long> messageKeyList = imHistorys.stream()
                    .map(ImMessageHistory::getMessageKey)
                    .toList();

            List<ImMessageBody> imBody = imMessageBodyService.getRecentMessage(messageKeyList);

            for (int i = 0; i < imBody.size(); i++) {
                AIMessage aiMessage = new AIMessage();
                aiMessage.setMessageBody(imBody.get(i).getMessageBody());
                aiMessage.setMessageTime(imBody.get(i).getMessageTime());
                aiMessage.setFromId(imHistorys.get(i).getFromId());

                message.add(aiMessage);
            }

        } else {
            List<ImGroupMessageHistory> imHistorys = imGroupMessageHistoryService.getRecentMessage(aiReply);

            List<Long> messageKeyList = imHistorys.stream()
                    .map(ImGroupMessageHistory::getMessageKey)
                    .toList();

            List<ImMessageBody> imBody = imMessageBodyService.getRecentMessage(messageKeyList);

            for (int i = 0; i < imBody.size(); i++) {
                AIMessage aiMessage = new AIMessage();
                aiMessage.setMessageBody(imBody.get(i).getMessageBody());
                aiMessage.setMessageTime(imBody.get(i).getMessageTime());
                aiMessage.setFromId(imHistorys.get(i).getFromId());

                message.add(aiMessage);
            }
        }

//        List<OfflineMessageContent> message = new ArrayList<>();
//
//        ZSetOperations<String, String> zSet = stringRedisTemplate.opsForZSet();
//        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
//
//        String key = aiReply.getAppId()+ Constants.RedisConstants.OfflineConstantConversationIndex+aiReply.getConversationId();
//        String hashKey = aiReply.getAppId()+Constants.RedisConstants.OfflineConstant+aiReply.getFromId();
//
//        Set<String> messageKeys = zSet.range(key, 0, aiReply.getRecentMessageNum() - 1);
//        List<Object> objects = hash.multiGet(hashKey, new ArrayList<>(messageKeys));
//
//        for (Object object : objects) {
//            OfflineMessageContent offlineMessageContent = JSONObject.parseObject(object.toString(), OfflineMessageContent.class);
//            message.add(offlineMessageContent);
//        }

        log.info("获取到最近的消息：{}", message);

        return message;
    }

    // ==================== 拼接对话上下文 ====================
    private String buildConversationContext(List<AIMessage> messageList) {
        if (messageList.isEmpty()) {
            return "暂无对话上下文";
        }

        StringBuilder sb = new StringBuilder();
        for (AIMessage msg : messageList) {

            // 时间戳转成标准时间格式
            String time = new java.text.SimpleDateFormat("MM-dd HH:mm")
                    .format(new java.util.Date(msg.getMessageTime()));

            sb.append("【").append(time).append("】")
                    .append(msg.getFromId()).append("：")
                    .append(msg.getMessageBody())
                    .append("\n");
        }
        return sb.toString().trim();
    }

    // ==================== 构建 AI 智能回复 Prompt ====================
    private String buildReplyPrompt(String require, String context) {

        if (StrUtil.isBlank(require)) {
            require = "正常";
        }

        return """
                你是智能聊天助手，请根据以下对话上下文，生成 3 条自然、符合语境的回复。
                回复要求：%s
                严格只返回 JSON 数组，不要任何多余文字，例如：["好的","收到啦","我知道了"]
                                
                对话上下文：
                %s
                """.formatted(require, context);
    }

    // ==================== 构建 总结 专用 Prompt ====================
    private String buildSummaryPrompt(String require, String context) {
        if (StrUtil.isBlank(require)) {
            require = "详细、清晰、重点突出";
        }

        return """
                你是一个专业的会话总结助手，请根据以下聊天记录，进行简洁、通顺、重点突出的总结。
                总结要求：%s
                不要使用列表，不要使用格式，只用一段通顺文字总结即可。
                            
                聊天记录：
                %s
                """.formatted(require, context);
    }

    public void IntelligenceSummary(AIReply aiReply) {

        threadPoolExecutor.execute(() -> {

            AIReplyPack aiReplyPack = new AIReplyPack();
            aiReplyPack.setConversationId(aiReply.getConversationId());
            try {
                List<AIMessage> message = getRecentMessage(aiReply);
                if (message.isEmpty()) {
                    aiReplyPack.setReply(List.of("抱歉，此会话暂无聊天记录"));
                    return;
                }
                String s = buildConversationContext(message);
                String prompt = buildSummaryPrompt(aiReply.getRequirement(), s);

                String content = chatClient.prompt(prompt).call().content();
                log.info("ai总结完毕");

                aiReplyPack.setReply(List.of(content));

            } catch (Exception e) {
                e.printStackTrace();
                log.info("ai调用异常");
                aiReplyPack.setConversationId(aiReply.getConversationId());
                aiReplyPack.setReply(List.of("AI服务繁忙"));
            } finally {
                messageProducer.sendToUser(
                        aiReply.getFromId(),
                        AIEventCommand.AI_INTELLIGENT_REPLY,
                        aiReplyPack,
                        new ClientInfo(aiReply.getAppId(), aiReply.getClientType(), aiReply.getImei()),
                        Constants.RocketConstants.AI2Im
                );
            }

        });
    }

    public void IntelligenceSearch(AIReply aiReply) {
        threadPoolExecutor.execute(() -> {

            AIReplyPack aiReplyPack = new AIReplyPack();
            aiReplyPack.setConversationId(aiReply.getConversationId());

            try {

                if (StrUtil.isBlank(aiReply.getRequirement())) {
                    aiReplyPack.setReply("请输入要搜索内容的描述");
                    return;
                }

                TimeRangeResult timeRangeResult = AIIdentifyTime(aiReply.getRequirement());
                log.info("消息中时间检索判断：{}", timeRangeResult);

                List<Document> documents;

                if (timeRangeResult.isNeedTimeRange()) {

                    String filter = "conversationId == '" + aiReply.getConversationId() + "' " +
                            "&& messageTime >= '" + timeRangeResult.getStartTime() + "'";

                    documents = vectorStore.similaritySearch(
                            SearchRequest.builder().
                                    query(aiReply.getRequirement()).
                                    filterExpression(filter).
                                    topK(10).
                                    build()
                    );

                } else {
                    documents = vectorStore.similaritySearch(
                            SearchRequest.builder().
                                    query(aiReply.getRequirement()).
                                    filterExpression("conversationId == '" + aiReply.getConversationId() + "'").
                                    topK(10).
                                    build()
                    );
                }

                if (!documents.isEmpty()) {
                    List<Long> messageKey = documents.stream().map(document -> (Long) document.getMetadata().get("messageKey")).toList();
                    List<ImMessageBody> bodies = imMessageBodyService.getRecentMessage(messageKey);
                    List<SearchedMessage> list = bodies.stream().
                            map(
                                    (imMessageBody) -> {
                                        SearchedMessage searchedMessage = new SearchedMessage();
                                        searchedMessage.setTime(imMessageBody.getMessageTime());
                                        searchedMessage.setMessage(imMessageBody.getMessageBody());
                                        return searchedMessage;
                                    }
                            ).
                            toList();

                    aiReplyPack.setReply(list);
                } else {
                    aiReplyPack.setReply("抱歉，没有找到相似的记录");
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.info("ai查找异常");
                aiReplyPack.setReply(List.of("ai服务繁忙"));
            } finally {
                messageProducer.sendToUser(
                        aiReply.getFromId(),
                        AIEventCommand.AI_INTELLIGENT_REPLY,
                        aiReplyPack,
                        new ClientInfo(aiReply.getAppId(), aiReply.getClientType(), aiReply.getImei()),
                        Constants.RocketConstants.AI2Im
                );
            }

        });
    }

    private TimeRangeResult AIIdentifyTime(String requirement) {
        String systemPrompt = """
                你是聊天记录智能检索助手，严格遵守以下规则：
                1. 分析用户问题，判断是否需要按时间范围检索历史聊天记录；
                2. 如果需要时间范围，必须调用工具 getCurrentDateTime 获取当前标准时间；
                3. 根据当前时间和用户语义，推算出要检索的起始时间戳，时间戳级别为毫秒
                4. 只允许返回 JSON，不能返回任何解释、多余文字、markdown；
                5. 需要时间范围时固定返回：{"needTimeRange":true,"startTime":"起始时间"}
                6. 不需要时间范围时固定返回：{"needTimeRange":false}
                """;

        // 调用 AI，并注入 Tool
        return chatClient.prompt()
                .system(systemPrompt)
                .user(requirement)
                .tools(aiTools)  // 你的 Tool
                .call()
                .entity(TimeRangeResult.class);
    }

}