package com.lld.im.common.enums.friendship;

import lombok.Getter;

@Getter
public enum AllowFriendTypeEnum {

    /**
     * 验证
     */
    NEED(2),

    /**
     * 不需要验证
     */
    NOT_NEED(1),

    ;


    private final int code;

    AllowFriendTypeEnum(int code){
        this.code=code;
    }

}
