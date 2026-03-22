package com.lld.im.service.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 好友分组成员表
 * @TableName im_friendship_group_member
 */
@TableName(value ="im_friendship_group_member",autoResultMap = true)
@Data
public class ImFriendshipGroupMember {
    /**
     * 分组ID
     */
    @TableId
    private Long groupId;

    /**
     * 群成员/好友ID
     */
    private String toId;

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
        ImFriendshipGroupMember other = (ImFriendshipGroupMember) that;
        return (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getToId() == null ? other.getToId() == null : this.getToId().equals(other.getToId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getToId() == null) ? 0 : getToId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", groupId=").append(groupId);
        sb.append(", toId=").append(toId);
        sb.append("]");
        return sb.toString();
    }
}