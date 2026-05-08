package com.lld.im.common.enums.command.ai;

import com.lld.im.common.enums.command.Command;
import lombok.Getter;

@Getter
public enum AIEventCommand implements Command {

    // AI智能回复
    AI_INTELLIGENT_REPLY(8000),

    // AI智能总结
    AI_GROUP_CHAT_SUMMARY(8001),

    // AI智能查找
    AI_INTELLIGENT_SEARCH(8002)

    ;

    private final int command;

    AIEventCommand(int command) {
        this.command = command;
    }

}
