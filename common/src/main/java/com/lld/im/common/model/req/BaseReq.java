package com.lld.im.common.model.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BaseReq {
    @NotNull
    private Integer appId;

    private String operator;

}
