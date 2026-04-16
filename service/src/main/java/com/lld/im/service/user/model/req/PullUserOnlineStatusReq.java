package com.lld.im.service.user.model.req;

import com.lld.im.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 拉取用户在线状态的请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PullUserOnlineStatusReq extends RequestBase {

    private List<String> userIds;

}
