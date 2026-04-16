package com.lld.im.common.model.user;


import com.lld.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserStatusModifyContent extends ClientInfo {
    private String userId;

    private Integer status;
}
