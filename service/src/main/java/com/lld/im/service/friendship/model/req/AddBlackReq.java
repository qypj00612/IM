package com.lld.im.service.friendship.model.req;


import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddBlackReq extends RequestBase {
    @NotBlank
    private String fromId;

    @NotBlank
    private String toId;
}
