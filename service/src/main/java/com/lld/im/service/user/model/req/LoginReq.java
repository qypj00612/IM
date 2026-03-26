package com.lld.im.service.user.model.req;


import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginReq extends RequestBase {

    @NotNull(message = "用户id不能位空")
    private String userId;

    private String userSign;


}
