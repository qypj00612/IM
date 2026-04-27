package com.lld.im.common.model.message;

import com.lld.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息撤回
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReCallMessageContent extends ClientInfo {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageTime;

    private Long messageSequence;

    private Integer conversationType;

}
