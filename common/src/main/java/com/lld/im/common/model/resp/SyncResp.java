package com.lld.im.common.model.resp;

import lombok.Data;

import java.util.List;

@Data
public class  SyncResp<T> {
    private Long maxSeq;

    private boolean isCompleted;

    private List<T> dataList;
}
