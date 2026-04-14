package com.lld.im.common.model.message;

import lombok.Data;

@Data
public class DoStoreGroupMessage {
    private GroupMessageContent groupMessageContent;

    private ImMessageBodyDto imMessageBodyDto;
}
