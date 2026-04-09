package com.lld.im.common.enums.user;

import lombok.Getter;

@Getter
public enum UserForbiddenFlagEnum {

    /**
     * 0 正常；1 禁用。
     */
    NORMAL(0),

    FORBIBBEN(1),
    ;

    private final int code;

    UserForbiddenFlagEnum(int code){
        this.code=code;
    }

}
