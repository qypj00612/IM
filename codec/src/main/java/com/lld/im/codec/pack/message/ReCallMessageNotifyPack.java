package com.lld.im.codec.pack.message;

import lombok.Data;

@Data
public class ReCallMessageNotifyPack {

    private String fromId;

    private String toId;

    private Long messageKey;

    private Long messageSequence;

}
