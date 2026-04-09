package com.lld.im.common.enums.friendship;

import com.lld.im.common.enums.ApplicationExceptionEnum;
import lombok.Getter;

@Getter
public enum FriendShipGroupErrorCode implements ApplicationExceptionEnum {

    CREAT_ERROR(40000,"群组创建失败"),
    GROUP_EXIST(40001,"群组已存在");


    private final int code;
    private final String desc;
    FriendShipGroupErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
