package com.lld.im.service.conversation.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 会话设置表
 * @TableName im_conversation_set
 */
@TableName(value ="im_conversation_set")
@Data
public class ImConversationSet {
    /**
     * 会话id 0_fromId_toId
     */
    @TableId
    private String conversationId;

    /**
     * 会话类型 1单聊 2群聊
     */
    private Integer conversationType;

    /**
     * 发送方ID
     */
    private String fromId;

    /**
     * 接收方ID/群ID
     */
    private String toId;

    /**
     * 是否免打扰 0否 1是
     */
    private Integer isMute;

    /**
     * 是否置顶 0否 1是
     */
    private Integer isTop;

    /**
     * 消息最大序列号
     */
    private Long sequence;

    /**
     * 已读消息序列号
     */
    private Long readSequence;

    /**
     * 应用ID
     */
    private Integer appId;

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
        ImConversationSet other = (ImConversationSet) that;
        return (this.getConversationId() == null ? other.getConversationId() == null : this.getConversationId().equals(other.getConversationId()))
            && (this.getConversationType() == null ? other.getConversationType() == null : this.getConversationType().equals(other.getConversationType()))
            && (this.getFromId() == null ? other.getFromId() == null : this.getFromId().equals(other.getFromId()))
            && (this.getToId() == null ? other.getToId() == null : this.getToId().equals(other.getToId()))
            && (this.getIsMute() == null ? other.getIsMute() == null : this.getIsMute().equals(other.getIsMute()))
            && (this.getIsTop() == null ? other.getIsTop() == null : this.getIsTop().equals(other.getIsTop()))
            && (this.getSequence() == null ? other.getSequence() == null : this.getSequence().equals(other.getSequence()))
            && (this.getReadSequence() == null ? other.getReadSequence() == null : this.getReadSequence().equals(other.getReadSequence()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getConversationId() == null) ? 0 : getConversationId().hashCode());
        result = prime * result + ((getConversationType() == null) ? 0 : getConversationType().hashCode());
        result = prime * result + ((getFromId() == null) ? 0 : getFromId().hashCode());
        result = prime * result + ((getToId() == null) ? 0 : getToId().hashCode());
        result = prime * result + ((getIsMute() == null) ? 0 : getIsMute().hashCode());
        result = prime * result + ((getIsTop() == null) ? 0 : getIsTop().hashCode());
        result = prime * result + ((getSequence() == null) ? 0 : getSequence().hashCode());
        result = prime * result + ((getReadSequence() == null) ? 0 : getReadSequence().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", conversationId=").append(conversationId);
        sb.append(", conversationType=").append(conversationType);
        sb.append(", fromId=").append(fromId);
        sb.append(", toId=").append(toId);
        sb.append(", isMute=").append(isMute);
        sb.append(", isTop=").append(isTop);
        sb.append(", sequence=").append(sequence);
        sb.append(", readSequence=").append(readSequence);
        sb.append(", appId=").append(appId);
        sb.append("]");
        return sb.toString();
    }
}