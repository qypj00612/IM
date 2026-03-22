package com.lld.im.service.friendship.model.resp;

import lombok.Data;

import java.util.List;

@Data
public class AddFriendshipGroupResp {
    List<String> success;
    List<String> fail;
}
