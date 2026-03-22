package com.lld.im.service.user.model.req;

import com.lld.im.common.RequestBase;
import com.lld.im.service.user.dao.ImUserData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class  ImportUserReq extends RequestBase {
    private List<ImUserData> userData;
}
