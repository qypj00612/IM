package com.lld.im.service.group.model.req;


import com.lld.im.common.RequestBase;
import com.lld.im.service.group.model.dto.GroupMemberDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddGroupMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    @NotEmpty(message = "群成员不能为空")
    private List<GroupMemberDTO> members;

}
