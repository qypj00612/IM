package com.lld.im.common.enums.command.conversation;

import com.lld.im.common.enums.command.Command;
import lombok.Getter;

@Getter
public enum ConversationEventCommand implements Command {

    //删除会话
    CONVERSATION_DELETE(5000),

    //删除会话
    CONVERSATION_UPDATE(5001),

    ;

    private final int command;

    ConversationEventCommand(int command){
        this.command=command;
    }

}
