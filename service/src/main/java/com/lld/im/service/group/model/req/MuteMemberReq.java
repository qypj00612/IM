package com.lld.im.service.group.model.req;


import com.lld.im.common.RequestBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MuteMemberReq extends RequestBase {
    @NotBlank(message = "群id不能为空")
    private String groupId;

    @NotBlank(message = "memberId不能为空")
    private String memberId;

    //禁言时间，单位毫秒
    @NotNull(message = "禁言时间不能为空")
    private Long speakDate;
}
