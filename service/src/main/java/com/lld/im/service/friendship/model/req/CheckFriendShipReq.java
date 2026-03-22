package com.lld.im.service.friendship.model.req;




import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CheckFriendShipReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotEmpty(message = "toIds不能为空")
    private List<String> toIds;

    @NotNull(message = "checkType不能为空")
    private Integer checkType;
}
