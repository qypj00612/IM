package com.lld.im.common.model.req;

import com.lld.im.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SyncReq extends RequestBase {
    private Long lastSeq;

    private Integer maxLimit;
}
