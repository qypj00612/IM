package com.lld.im.service.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * IM好友关系表
 * @TableName im_friendship
 */
@TableName(value ="im_friendship",autoResultMap = true)
@Data
public class ImFriendship {
    /**
     * 应用id
     */
    @TableId
    private Integer appId;

    /**
     * 发起方用户ID
     */
    private String fromId;

    /**
     * 接收方用户ID
     */
    private String toId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态 1正常 2删除
     */
    private Integer status;

    /**
     * 状态 1正常 2拉黑
     */
    private Integer black;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 好友关系序列号
     */
    private Long friendSequence;

    /**
     * 黑名单关系序列号
     */
    private Long blackSequence;

    /**
     * 好友来源
     */
    private String addSource;

    /**
     * 扩展字段
     */
    private String extra;

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
        ImFriendship other = (ImFriendship) that;
        return (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getFromId() == null ? other.getFromId() == null : this.getFromId().equals(other.getFromId()))
            && (this.getToId() == null ? other.getToId() == null : this.getToId().equals(other.getToId()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getBlack() == null ? other.getBlack() == null : this.getBlack().equals(other.getBlack()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getFriendSequence() == null ? other.getFriendSequence() == null : this.getFriendSequence().equals(other.getFriendSequence()))
            && (this.getBlackSequence() == null ? other.getBlackSequence() == null : this.getBlackSequence().equals(other.getBlackSequence()))
            && (this.getAddSource() == null ? other.getAddSource() == null : this.getAddSource().equals(other.getAddSource()))
            && (this.getExtra() == null ? other.getExtra() == null : this.getExtra().equals(other.getExtra()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getFromId() == null) ? 0 : getFromId().hashCode());
        result = prime * result + ((getToId() == null) ? 0 : getToId().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getBlack() == null) ? 0 : getBlack().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getFriendSequence() == null) ? 0 : getFriendSequence().hashCode());
        result = prime * result + ((getBlackSequence() == null) ? 0 : getBlackSequence().hashCode());
        result = prime * result + ((getAddSource() == null) ? 0 : getAddSource().hashCode());
        result = prime * result + ((getExtra() == null) ? 0 : getExtra().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", appId=").append(appId);
        sb.append(", fromId=").append(fromId);
        sb.append(", toId=").append(toId);
        sb.append(", remark=").append(remark);
        sb.append(", status=").append(status);
        sb.append(", black=").append(black);
        sb.append(", createTime=").append(createTime);
        sb.append(", friendSequence=").append(friendSequence);
        sb.append(", blackSequence=").append(blackSequence);
        sb.append(", addSource=").append(addSource);
        sb.append(", extra=").append(extra);
        sb.append("]");
        return sb.toString();
    }
}