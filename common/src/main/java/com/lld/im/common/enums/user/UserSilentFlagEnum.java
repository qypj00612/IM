package com.lld.im.common.enums.user;

import lombok.Getter;

@Getter
public enum UserSilentFlagEnum {

    /**
     * 0 正常；1 禁言。
     */
    NORMAL(0),

    MUTE(1),
    ;

    private final int code;

    UserSilentFlagEnum(int code){
        this.code=code;
    }

}
