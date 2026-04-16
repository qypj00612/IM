package com.lld.im.common.enums.command.user;

import com.lld.im.common.enums.command.Command;
import lombok.Getter;

@Getter
public enum UserEventCommand implements Command {

    //用户修改command 4000
    USER_MODIFY(4000),

    //4001 tcp 发给 逻辑层
    USER_ONLINE_STATUS_CHANGE(4001),

    //4004 用户在线状态通知报文 逻辑层 发给 tcp
    USER_ONLINE_STATUS_CHANGE_NOTIFY(4004),

    //4005 用户在线状态通知同步报文 逻辑层 发给 tcp
    USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC(4005),


    ;

    private final int command;

    UserEventCommand(int command){
        this.command=command;
    }

}
