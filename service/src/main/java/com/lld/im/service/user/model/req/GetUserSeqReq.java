package com.lld.im.service.user.model.req;

import com.lld.im.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserSeqReq extends RequestBase {

    private String userId;

}
