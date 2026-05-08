package com.lld.im.ai.model;

import com.lld.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AIReply extends ClientInfo {

    private String fromId;

    private String toId;

    private String conversationId;

    private Integer conversationType;

    private String requirement;

    private int recentMessageNum;

}
