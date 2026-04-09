package com.lld.im.service.message.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 群消息历史表
 * @TableName im_group_message_history
 */
@TableName(value ="im_group_message_history")
@Data
public class ImGroupMessageHistory {
    /**
     * 消息体ID(messageBodyId)
     */
    @TableId
    private Long messageKey;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 发送者ID
     */
    private String fromId;

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 序列号
     */
    private Long sequence;

    /**
     * 随机码
     */
    private String messageRandom;

    /**
     * 消息时间戳
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
        ImGroupMessageHistory other = (ImGroupMessageHistory) that;
        return (this.getMessageKey() == null ? other.getMessageKey() == null : this.getMessageKey().equals(other.getMessageKey()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getFromId() == null ? other.getFromId() == null : this.getFromId().equals(other.getFromId()))
            && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getSequence() == null ? other.getSequence() == null : this.getSequence().equals(other.getSequence()))
            && (this.getMessageRandom() == null ? other.getMessageRandom() == null : this.getMessageRandom().equals(other.getMessageRandom()))
            && (this.getMessageTime() == null ? other.getMessageTime() == null : this.getMessageTime().equals(other.getMessageTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMessageKey() == null) ? 0 : getMessageKey().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getFromId() == null) ? 0 : getFromId().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getSequence() == null) ? 0 : getSequence().hashCode());
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
        sb.append(", messageKey=").append(messageKey);
        sb.append(", appId=").append(appId);
        sb.append(", fromId=").append(fromId);
        sb.append(", groupId=").append(groupId);
        sb.append(", sequence=").append(sequence);
        sb.append(", messageRandom=").append(messageRandom);
        sb.append(", messageTime=").append(messageTime);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}