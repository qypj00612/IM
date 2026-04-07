package com.lld.im.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfo {

    private Integer appId;

    private Integer clientType;

    private String imei;

}
