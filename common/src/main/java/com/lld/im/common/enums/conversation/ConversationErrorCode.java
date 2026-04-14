package com.lld.im.common.enums.conversation;


import com.lld.im.common.enums.ApplicationExceptionEnum;
import lombok.Getter;

@Getter
public enum ConversationErrorCode implements ApplicationExceptionEnum {

    CONVERSATION_UPDATE_PARAM_ERROR(50000,"会话修改参数错误"),


    ;

    private final int code;
    private final String desc;

    ConversationErrorCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

}
