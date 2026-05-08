package com.lld.im.codec.pack.ai;

import lombok.Data;

import java.util.List;

@Data
public class AIReplyPack {
    private Object reply;
    private String conversationId;
}
