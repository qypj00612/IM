package com.lld.im.service.friendship.model.req;


import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReadFriendshipRequestReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String toId;
}
