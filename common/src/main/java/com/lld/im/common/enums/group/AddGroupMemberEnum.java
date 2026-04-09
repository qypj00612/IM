package com.lld.im.common.enums.group;

import com.lld.im.common.enums.ApplicationExceptionEnum;
import lombok.Getter;

@Getter
public enum AddGroupMemberEnum implements ApplicationExceptionEnum {

    SUCCESS(0,"成功"),
    INSERT_ERROR(1,"失败"),
    HAVE_EXIST(2,"已是群成员");

    private final int code;
    private final String desc;

    AddGroupMemberEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
