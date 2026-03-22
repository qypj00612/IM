package com.lld.im.service.friendship.model.req;


import com.lld.im.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApproveFriendRequestReq extends RequestBase {

    private Long id;

    //1同意 2拒绝
    private Integer status;
}
