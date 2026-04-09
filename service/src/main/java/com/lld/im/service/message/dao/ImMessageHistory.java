package com.lld.im.service.message.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 消息历史记录表
 * @TableName im_message_history
 */
@TableName(value ="im_message_history",autoResultMap = true)
@Data
public class ImMessageHistory {
    /**
     * 应用ID
     */
    @TableId
    private Integer appId;

    /**
     * 消息归属者ID（用户自己的历史记录）
     */
    private String ownerId;

    /**
     * 消息序列号（递增）
     */
    private Long sequence;

    /**
     * 发送方ID
     */
    private String fromId;

    /**
     * 接收方ID/群ID
     */
    private String toId;

    /**
     * 消息体ID，关联im_message_body表
     */
    private Long messageKey;

    /**
     * 随机码
     */
    private String messageRandom;

    /**
     * 消息发送时间戳
     */
    private Long messageTime;

    /**
     * 创建时间戳
     */
    private Long createTime;

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
        ImMessageHistory other = (ImMessageHistory) that;
        return (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getOwnerId() == null ? other.getOwnerId() == null : this.getOwnerId().equals(other.getOwnerId()))
            && (this.getSequence() == null ? other.getSequence() == null : this.getSequence().equals(other.getSequence()))
            && (this.getFromId() == null ? other.getFromId() == null : this.getFromId().equals(other.getFromId()))
            && (this.getToId() == null ? other.getToId() == null : this.getToId().equals(other.getToId()))
            && (this.getMessageKey() == null ? other.getMessageKey() == null : this.getMessageKey().equals(other.getMessageKey()))
            && (this.getMessageRandom() == null ? other.getMessageRandom() == null : this.getMessageRandom().equals(other.getMessageRandom()))
            && (this.getMessageTime() == null ? other.getMessageTime() == null : this.getMessageTime().equals(other.getMessageTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getOwnerId() == null) ? 0 : getOwnerId().hashCode());
        result = prime * result + ((getSequence() == null) ? 0 : getSequence().hashCode());
        result = prime * result + ((getFromId() == null) ? 0 : getFromId().hashCode());
        result = prime * result + ((getToId() == null) ? 0 : getToId().hashCode());
        result = prime * result + ((getMessageKey() == null) ? 0 : getMessageKey().hashCode());
        result = prime * result + ((getMessageRandom() == null) ? 0 : getMessageRandom().hashCode());
        result = prime * result + ((getMessageTime() == null) ? 0 : getMessageTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", appId=").append(appId);
        sb.append(", ownerId=").append(ownerId);
        sb.append(", sequence=").append(sequence);
        sb.append(", fromId=").append(fromId);
        sb.append(", toId=").append(toId);
        sb.append(", messageKey=").append(messageKey);
        sb.append(", messageRandom=").append(messageRandom);
        sb.append(", messageTime=").append(messageTime);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}