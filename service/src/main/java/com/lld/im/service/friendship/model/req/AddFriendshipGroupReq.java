package com.lld.im.service.friendship.model.req;


import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddFriendshipGroupReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    public String fromId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    private List<String> toIds;

}
