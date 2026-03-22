package com.lld.im.service.user.enums;

import com.lld.im.common.enums.ApplicationExceptionEnum;
import lombok.Getter;

@Getter
public enum UserErrorCode implements ApplicationExceptionEnum {

    IMPORT_SIZE_BEYOND(20000,"导入數量超出上限"),
    USER_IS_NOT_EXIST(20001,"用户不存在"),
    SERVER_GET_USER_ERROR(20002,"服务获取用户失败"),
    MODIFY_USER_ERROR(20003,"更新用户失败"),
    SERVER_NOT_AVAILABLE(71000, "没有可用的服务"),
    ;

    private final int code;
    private final String desc;

    UserErrorCode(int code, String error){
        this.code = code;
        this.desc = error;
    }

}
