package com.lld.im.service.friendship.model.req;

import com.lld.im.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetFriendshipRequestReq extends RequestBase {
    private String toId;
}
