package com.lld.im.service.friendship.model.callback;

import com.lld.im.service.friendship.dto.FriendshipDTO;
import lombok.Data;

@Data
public class DeleteFriendCallBackDTO {
    private String fromId;
    private String toId;
}
