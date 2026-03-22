package com.lld.im.service.group.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 群信息表
 * @TableName im_group
 */
@TableName(value ="im_group",autoResultMap = true)
@Data
public class ImGroup {
    /**
     * 群id
     */
    @TableId
    private String groupId;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 群主id
     */
    private String ownerId;

    /**
     * 群类型 1私有群(类似微信) 2公开群(类似qq)
     */
    private Integer groupType;

    /**
     * 群名称
     */
    private String groupName;

    /**
     * 是否开启群禁言 1禁言
     */
    private Integer mute;

    /**
     * 群状态 1正常 2解散
     */
    private Integer status;

    /**
     * 申请加群选项 0表示禁止任何人
     */
    private Integer applyJoinType;

    /**
     * 群简介
     */
    private String introduction;

    /**
     * 群公告
     */
    private String notification;

    /**
     * 头像
     */
    private String photo;

    /**
     * 最大的群成员人数
     */
    private Integer maxMemberCount;

    /**
     * seq
     */
    private Long sequence;

    /**
     * 创建时间戳
     */
    private Long createTime;

    /**
     * 更新时间戳
     */
    private Long updateTime;

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
        ImGroup other = (ImGroup) that;
        return (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getOwnerId() == null ? other.getOwnerId() == null : this.getOwnerId().equals(other.getOwnerId()))
            && (this.getGroupType() == null ? other.getGroupType() == null : this.getGroupType().equals(other.getGroupType()))
            && (this.getGroupName() == null ? other.getGroupName() == null : this.getGroupName().equals(other.getGroupName()))
            && (this.getMute() == null ? other.getMute() == null : this.getMute().equals(other.getMute()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getApplyJoinType() == null ? other.getApplyJoinType() == null : this.getApplyJoinType().equals(other.getApplyJoinType()))
            && (this.getIntroduction() == null ? other.getIntroduction() == null : this.getIntroduction().equals(other.getIntroduction()))
            && (this.getNotification() == null ? other.getNotification() == null : this.getNotification().equals(other.getNotification()))
            && (this.getPhoto() == null ? other.getPhoto() == null : this.getPhoto().equals(other.getPhoto()))
            && (this.getMaxMemberCount() == null ? other.getMaxMemberCount() == null : this.getMaxMemberCount().equals(other.getMaxMemberCount()))
            && (this.getSequence() == null ? other.getSequence() == null : this.getSequence().equals(other.getSequence()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getExtra() == null ? other.getExtra() == null : this.getExtra().equals(other.getExtra()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getOwnerId() == null) ? 0 : getOwnerId().hashCode());
        result = prime * result + ((getGroupType() == null) ? 0 : getGroupType().hashCode());
        result = prime * result + ((getGroupName() == null) ? 0 : getGroupName().hashCode());
        result = prime * result + ((getMute() == null) ? 0 : getMute().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getApplyJoinType() == null) ? 0 : getApplyJoinType().hashCode());
        result = prime * result + ((getIntroduction() == null) ? 0 : getIntroduction().hashCode());
        result = prime * result + ((getNotification() == null) ? 0 : getNotification().hashCode());
        result = prime * result + ((getPhoto() == null) ? 0 : getPhoto().hashCode());
        result = prime * result + ((getMaxMemberCount() == null) ? 0 : getMaxMemberCount().hashCode());
        result = prime * result + ((getSequence() == null) ? 0 : getSequence().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getExtra() == null) ? 0 : getExtra().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", groupId=").append(groupId);
        sb.append(", appId=").append(appId);
        sb.append(", ownerId=").append(ownerId);
        sb.append(", groupType=").append(groupType);
        sb.append(", groupName=").append(groupName);
        sb.append(", mute=").append(mute);
        sb.append(", status=").append(status);
        sb.append(", applyJoinType=").append(applyJoinType);
        sb.append(", introduction=").append(introduction);
        sb.append(", notification=").append(notification);
        sb.append(", photo=").append(photo);
        sb.append(", maxMemberCount=").append(maxMemberCount);
        sb.append(", sequence=").append(sequence);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", extra=").append(extra);
        sb.append("]");
        return sb.toString();
    }
}