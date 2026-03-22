package com.lld.im.common.enums;

import lombok.Getter;

@Getter
public enum BaseCode implements ApplicationExceptionEnum{

    SUCCESS(200,"success"),
    SYSTEM_ERROR(90000,"服务器内部错误"),
    PARAMETER_ERROR(90001,"参数校验错误");

    final int code;
    final String desc;

    BaseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
