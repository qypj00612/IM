package com.lld.im.common.enums;

import lombok.Getter;

@Getter
public enum ImConnectStatusEnums {

    ON_LINE(1,"在线"),
    OFF_LINE(2,"离线")
    ;

    private final Integer code;
    private final String desc;

    ImConnectStatusEnums(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

