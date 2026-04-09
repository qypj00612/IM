package com.lld.im.service.message.modul;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GroupMessageContent extends MessageContent {

    private String groupId;

}
