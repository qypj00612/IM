package com.lld.im.service.friendship.model.resp;

import lombok.Data;

import java.util.List;

@Data
public class DeleteFriendshipGroupMemberResp {
    List<String> success;
    List<String> fail;
}
