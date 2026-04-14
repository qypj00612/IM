package com.lld.im.common.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GroupMessageContent extends MessageContent {

    private String groupId;

}
