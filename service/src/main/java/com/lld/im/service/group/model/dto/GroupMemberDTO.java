package com.lld.im.service.group.model.dto;


import com.lld.im.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class GroupMemberDTO extends RequestBase {

    private String groupId;

    //成员id
    private String memberId;

    //群成员类型，0 普通成员, 1 管理员, 2 群主， 3 已经移除的成员，4 已经移除的成员禁言
    private Integer role;

    private Long speakDate;

    //群昵称
    private String alias;

    //加入时间
    private Long joinTime;

    //离开时间
    private Long leaveTime;

    private String joinType;

    private String extra;
}
