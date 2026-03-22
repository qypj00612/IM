package com.lld.im.service.friendship.enums;

import lombok.Getter;

@Getter
public enum ApproveFriendRequestStatusEnum {

    /**
     * 1 同意；2 拒绝。
     */
    AGREE(1),

    REJECT(2),
    ;

    private final int code;

    ApproveFriendRequestStatusEnum(int code){
        this.code=code;
    }
}
