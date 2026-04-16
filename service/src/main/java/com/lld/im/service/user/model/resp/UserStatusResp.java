package com.lld.im.service.user.model.resp;

import com.lld.im.common.model.UserSession;
import lombok.Data;

import java.util.List;

@Data
public class UserStatusResp {

    private List<UserSession> userSessions;

    private Integer clientStatus;

    private String clientText;

}
