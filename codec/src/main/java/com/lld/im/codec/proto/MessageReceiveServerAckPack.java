package com.lld.im.codec.proto;

import com.lld.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageReceiveServerAckPack extends ClientInfo {
    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

    private Boolean serverSend;
}
