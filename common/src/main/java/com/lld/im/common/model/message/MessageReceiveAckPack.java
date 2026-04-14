package com.lld.im.common.model.message;


import com.lld.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageReceiveAckPack extends ClientInfo {
    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

}
