package com.lld.im.service.group.model.req;


import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DestroyGroupReq extends RequestBase {

    @NotNull(message = "群id不能为空")
    private String groupId;

}
