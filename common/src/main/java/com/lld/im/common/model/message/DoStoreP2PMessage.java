package com.lld.im.common.model.message;

import lombok.Data;

@Data
public class DoStoreP2PMessage {

    private MessageContent messageContent;

    private ImMessageBodyDto imMessageBodyDto;

}
