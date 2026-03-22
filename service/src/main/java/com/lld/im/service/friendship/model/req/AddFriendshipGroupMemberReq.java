package com.lld.im.service.friendship.model.req;


import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddFriendshipGroupMemberReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    @NotEmpty(message = "请选择用户")
    private List<String> toIds;


}
