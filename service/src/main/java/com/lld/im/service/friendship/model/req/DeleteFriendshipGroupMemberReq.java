package com.lld.im.service.friendship.model.req;

import com.lld.im.common.RequestBase;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: Chackylee
 * @description:
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteFriendshipGroupMemberReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    @NotEmpty(message = "请选择用户")
    private List<String> toIds;


}
