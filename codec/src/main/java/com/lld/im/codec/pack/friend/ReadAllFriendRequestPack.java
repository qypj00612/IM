package com.lld.im.codec.pack.friend;

import lombok.Data;


@Data
public class ReadAllFriendRequestPack {

    private String fromId;

    private Long sequence;
}
