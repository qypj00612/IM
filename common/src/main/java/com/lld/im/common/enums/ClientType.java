package com.lld.im.common.enums;

import lombok.Getter;

@Getter
public enum ClientType {

    WEBAPI(0,"webApi"),
    WEB(1,"web"),
    IOS(2,"ios"),
    ANDROID(3,"android"),
    WINDOWS(4,"windows"),
    MAC(5,"mac"),
    ;

    private final int code;
    private final String error;

    ClientType(int code, String error){
        this.code = code;
        this.error = error;
    }

}
