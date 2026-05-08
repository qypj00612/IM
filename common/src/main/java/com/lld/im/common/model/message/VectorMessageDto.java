package com.lld.im.common.model.message;

import lombok.Data;

@Data
public class VectorMessageDto {

    private Integer appId;

    private Long messageKey;

    private String content;

    private String conversationId;

    private Long messageTime;

}
