package com.lld.im.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestBase {
    @NotNull(message = "appId不能为空")
    private Integer appId;

    @NotNull(message = "operator不能为空")
    // 操作者的id
    private String operator;
}
