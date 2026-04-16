package com.lld.im.service.user.model.req;


import com.lld.im.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 临时订阅用户请求的实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SubscribeUserOnlineReq extends RequestBase {
    private List<String> subId;

    private Long subTime;
}
