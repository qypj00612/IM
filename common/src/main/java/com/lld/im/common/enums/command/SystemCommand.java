package com.lld.im.common.enums.command;

import lombok.Getter;

@Getter
public enum SystemCommand implements Command {

    /**
     * 登录 9000 退出 9003 心跳 9999
     */
    LOGIN(0x2328),

    LOGOUT(0x232b),

    PING(0x270f),

    // 下线通知
    MULTIPLE_LOGIN(0x232a),

    LOGIN_ACK(0x2329),

    ;

    private final int command;
    private SystemCommand(int command) {
        this.command = command;
    }


}
