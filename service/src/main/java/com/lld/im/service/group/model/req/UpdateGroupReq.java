package com.lld.im.service.group.model.req;


import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateGroupReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    private String groupName;

    private Integer mute; //全体禁言 0 no 1 yes

    private Integer applyJoinType; // 0 所有人可加入 1 群成员拉人 2 群管理拉人

    private String groupIntroduction;

    private String notification;

    private String photo;

    private String extra;

    private Integer maxMemberCount;
}
