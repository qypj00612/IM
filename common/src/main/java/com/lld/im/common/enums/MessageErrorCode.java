package com.lld.im.common.enums;

import lombok.Getter;

@Getter
public enum MessageErrorCode implements ApplicationExceptionEnum {


    FROMER_IS_FORBIDDEN(50003, "发送方被禁用"),

    FROMER_IS_MUTE(50002, "发送方被禁言"),


    MESSAGEBODY_IS_NOT_EXIST(50003, "消息体不存在"),

    MESSAGE_IS_RECALLED(50005, "消息已被撤回"),

    MESSAGE_RECALL_TIME_OUT(50004, "消息已超过可撤回时间"),

    ;

    private final int code;
    private final String desc;

    MessageErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
