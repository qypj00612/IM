package com.lld.im.service.conversation.model;

import com.lld.im.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateConversationReq extends RequestBase {

    private String conversationId;

    private String fromId;

    private Integer isMute;

    private Integer isTop;

}
