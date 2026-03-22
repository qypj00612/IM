package com.lld.im.service.friendship.enums;

import lombok.Getter;

@Getter
public enum CheckFriendShipTypeEnum {

    /**
     * 1 单方校验；2双方校验。
     */
    SINGLE(1),

    BOTH(2),
    ;

    private final int type;

    CheckFriendShipTypeEnum(int type){
        this.type=type;
    }

}
