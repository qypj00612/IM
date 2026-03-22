package com.lld.im.service.friendship.model.req;


import com.lld.im.common.RequestBase;
import com.lld.im.service.friendship.dto.ImportFriendDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImportFriendshipReq extends RequestBase {

    @NotBlank
    private String fromId;

    private List<ImportFriendDTO> friendItem;

}
