package com.lld.im.service.message.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 消息内容表
 * @TableName im_message_body
 */
@TableName(value ="im_message_body")
@Data
public class ImMessageBody {
    /**
     * 消息唯一ID(messageBodyId)
     */
    @TableId
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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ImMessageBody other = (ImMessageBody) that;
        return (this.getMessageKey() == null ? other.getMessageKey() == null : this.getMessageKey().equals(other.getMessageKey()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getMessageBody() == null ? other.getMessageBody() == null : this.getMessageBody().equals(other.getMessageBody()))
            && (this.getSecurityKey() == null ? other.getSecurityKey() == null : this.getSecurityKey().equals(other.getSecurityKey()))
            && (this.getMessageTime() == null ? other.getMessageTime() == null : this.getMessageTime().equals(other.getMessageTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getExtra() == null ? other.getExtra() == null : this.getExtra().equals(other.getExtra()))
            && (this.getDelFlag() == null ? other.getDelFlag() == null : this.getDelFlag().equals(other.getDelFlag()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMessageKey() == null) ? 0 : getMessageKey().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getMessageBody() == null) ? 0 : getMessageBody().hashCode());
        result = prime * result + ((getSecurityKey() == null) ? 0 : getSecurityKey().hashCode());
        result = prime * result + ((getMessageTime() == null) ? 0 : getMessageTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getExtra() == null) ? 0 : getExtra().hashCode());
        result = prime * result + ((getDelFlag() == null) ? 0 : getDelFlag().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", messageKey=").append(messageKey);
        sb.append(", appId=").append(appId);
        sb.append(", messageBody=").append(messageBody);
        sb.append(", securityKey=").append(securityKey);
        sb.append(", messageTime=").append(messageTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", extra=").append(extra);
        sb.append(", delFlag=").append(delFlag);
        sb.append("]");
        return sb.toString();
    }
}