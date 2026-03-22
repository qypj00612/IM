package com.lld.im.common.enums;

import lombok.Getter;

@Getter
public enum DelFlagEnum {

    /**
     * 0 正常；1 删除。
     */
    NORMAL(0),

    DELETE(1),
    ;

    private final int code;

    DelFlagEnum(int code){
        this.code=code;
    }

}
