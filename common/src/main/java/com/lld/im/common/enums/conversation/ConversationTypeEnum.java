package com.lld.im.common.enums.conversation;

import lombok.Getter;

@Getter
public enum ConversationTypeEnum {

    /**
     * 0 单聊 1群聊 2机器人 3公众号
     */
    P2P(0),

    GROUP(1),

    ROBOT(2),
    ;

    private final int code;

    ConversationTypeEnum(int code){
        this.code=code;
    }

}
