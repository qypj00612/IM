package com.lld.im.codec.pack.user;

import lombok.Data;


/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class UserClientStatusChangeNotifyPack {

    private String clientText;

    private Integer clientStatus;

    private String userId;

}
