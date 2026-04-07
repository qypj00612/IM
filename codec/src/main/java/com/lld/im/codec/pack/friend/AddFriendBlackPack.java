package com.lld.im.codec.pack.friend;

import lombok.Data;


@Data
public class AddFriendBlackPack {
    private String fromId;

    private String toId;

    private Long sequence;
}
