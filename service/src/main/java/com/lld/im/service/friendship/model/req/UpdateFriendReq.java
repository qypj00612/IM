package com.lld.im.service.friendship.model.req;


import com.lld.im.common.RequestBase;
import com.lld.im.service.friendship.dto.FriendshipDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateFriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    private FriendshipDTO toItem;
}
