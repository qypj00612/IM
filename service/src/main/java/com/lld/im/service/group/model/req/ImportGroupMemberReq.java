package com.lld.im.service.group.model.req;


import com.lld.im.common.RequestBase;
import com.lld.im.service.group.model.dto.GroupMemberDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImportGroupMemberReq extends RequestBase {
    @NotBlank(message = "群Id不能为空")
    private String groupId;

    private List<GroupMemberDTO> members;
}
