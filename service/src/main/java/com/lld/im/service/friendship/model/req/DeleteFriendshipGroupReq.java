package com.lld.im.service.friendship.model.req;


import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: Chackylee
 * @description: 删除分组，同时删除分组下的成员
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteFriendshipGroupReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotEmpty(message = "分组名称不能为空")
    private List<String> groupName;

}
