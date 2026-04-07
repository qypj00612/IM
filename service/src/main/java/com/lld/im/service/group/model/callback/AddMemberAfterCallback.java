package com.lld.im.service.group.model.callback;


import com.lld.im.service.group.model.resp.AddGroupMemberResp;
import lombok.Data;

import java.util.List;

@Data
public class AddMemberAfterCallback {
    private String groupId;
    private Integer groupType;
    private String operator;
    private List<AddGroupMemberResp> memberId;
}
