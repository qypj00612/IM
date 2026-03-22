package com.lld.im.service.group.model.resp;

import lombok.Data;

@Data
public class GetRoleResp {
    private Long groupMemberId;

    private String groupId;

    private Integer role;

    private Long speakDate;
}
