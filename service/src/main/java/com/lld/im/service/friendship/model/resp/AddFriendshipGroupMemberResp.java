package com.lld.im.service.friendship.model.resp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddFriendshipGroupMemberResp {
    List<String> success;
    List<String> fail;
}
