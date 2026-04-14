package com.lld.im.common.model.message;

import lombok.Data;

@Data
public class ImMessageBodyDto {
    /**
     * 消息唯一ID(messageBodyId)
     */
    private Long messageKey;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 消息内容
     */
    private String messageBody;

    /**
     * 安全密钥
     */
    private String securityKey;

    /**
     * 消息发送时间戳
     */
    private Long messageTime;

    /**
     * 创建时间戳
     */
    private Long createTime;

    /**
     * 扩展字段
     */
    private String extra;

    /**
     * 删除标识 0正常 1删除
     */
    private Integer delFlag;

}
