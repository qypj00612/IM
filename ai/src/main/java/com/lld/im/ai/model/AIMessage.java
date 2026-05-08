package com.lld.im.ai.model;

import lombok.Data;

@Data
public class AIMessage {

    /** messageBody*/
    private String messageBody;

    private Long messageTime;

    private String fromId;
}
