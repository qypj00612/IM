package com.lld.im.service.user.model.req;


import com.lld.im.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserInfoReq extends RequestBase {

    private List<String> userIds;

}
