package com.lld.im.common.model.message;


import com.lld.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 消息已读
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageReadedContent extends ClientInfo {
    private Long messageSequence;

    private String fromId;

    private String toId;

    private String groupId;

    private Integer conversationType;

}
