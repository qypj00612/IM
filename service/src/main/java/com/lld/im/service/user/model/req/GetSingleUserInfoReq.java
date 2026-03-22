package com.lld.im.service.user.model.req;

import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetSingleUserInfoReq extends RequestBase {
    @NotBlank
    private String userId;
}
