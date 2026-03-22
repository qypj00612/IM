package com.lld.im.service.group.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 群成员表
 * @TableName im_group_member
 */
@TableName(value ="im_group_member")
@Data
public class ImGroupMember {
    /**
     * 群成员ID
     */
    @TableId
    private Long groupMemberId;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 群ID
     */
    private String groupId;

    /**
     * 成员ID
     */
    private String memberId;

    /**
     * 禁言到期时间戳
     */
    private Long speakDate;

    /**
     * 群成员类型: 0普通成员, 1管理员, 2群主, 3已退出
     */
    private Integer role;

    /**
     * 群昵称
     */
    private String alias;

    /**
     * 加入时间戳
     */
    private Long joinTime;

    /**
     * 离开时间戳
     */
    private Long leaveTime;

    /**
     * 加入方式
     */
    private String joinType;

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
        ImGroupMember other = (ImGroupMember) that;
        return (this.getGroupMemberId() == null ? other.getGroupMemberId() == null : this.getGroupMemberId().equals(other.getGroupMemberId()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getMemberId() == null ? other.getMemberId() == null : this.getMemberId().equals(other.getMemberId()))
            && (this.getSpeakDate() == null ? other.getSpeakDate() == null : this.getSpeakDate().equals(other.getSpeakDate()))
            && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()))
            && (this.getAlias() == null ? other.getAlias() == null : this.getAlias().equals(other.getAlias()))
            && (this.getJoinTime() == null ? other.getJoinTime() == null : this.getJoinTime().equals(other.getJoinTime()))
            && (this.getLeaveTime() == null ? other.getLeaveTime() == null : this.getLeaveTime().equals(other.getLeaveTime()))
            && (this.getJoinType() == null ? other.getJoinType() == null : this.getJoinType().equals(other.getJoinType()))
            && (this.getExtra() == null ? other.getExtra() == null : this.getExtra().equals(other.getExtra()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getGroupMemberId() == null) ? 0 : getGroupMemberId().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getMemberId() == null) ? 0 : getMemberId().hashCode());
        result = prime * result + ((getSpeakDate() == null) ? 0 : getSpeakDate().hashCode());
        result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
        result = prime * result + ((getAlias() == null) ? 0 : getAlias().hashCode());
        result = prime * result + ((getJoinTime() == null) ? 0 : getJoinTime().hashCode());
        result = prime * result + ((getLeaveTime() == null) ? 0 : getLeaveTime().hashCode());
        result = prime * result + ((getJoinType() == null) ? 0 : getJoinType().hashCode());
        result = prime * result + ((getExtra() == null) ? 0 : getExtra().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", groupMemberId=").append(groupMemberId);
        sb.append(", appId=").append(appId);
        sb.append(", groupId=").append(groupId);
        sb.append(", memberId=").append(memberId);
        sb.append(", speakDate=").append(speakDate);
        sb.append(", role=").append(role);
        sb.append(", alias=").append(alias);
        sb.append(", joinTime=").append(joinTime);
        sb.append(", leaveTime=").append(leaveTime);
        sb.append(", joinType=").append(joinType);
        sb.append(", extra=").append(extra);
        sb.append("]");
        return sb.toString();
    }
}