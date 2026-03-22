package com.lld.im.service.friendship.dao;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 好友申请表
 * @TableName im_friendship_request
 */
@TableName(value ="im_friendship_request")
@Data
public class ImFriendshipRequest {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 发起方ID
     */
    private String fromId;

    /**
     * 接收方ID
     */
    private String toId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 0 未读 1已读
     */
    private Integer readStatus;

    /**
     * 好友来源
     */
    private String addSource;

    /**
     * 添加说明
     */
    private String addWording;

    /**
     * 审批状态 1同意 2拒绝
     */
    private Integer approveStatus;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 序列号
     */
    private Long sequence;

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
        ImFriendshipRequest other = (ImFriendshipRequest) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getFromId() == null ? other.getFromId() == null : this.getFromId().equals(other.getFromId()))
            && (this.getToId() == null ? other.getToId() == null : this.getToId().equals(other.getToId()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getReadStatus() == null ? other.getReadStatus() == null : this.getReadStatus().equals(other.getReadStatus()))
            && (this.getAddSource() == null ? other.getAddSource() == null : this.getAddSource().equals(other.getAddSource()))
            && (this.getAddWording() == null ? other.getAddWording() == null : this.getAddWording().equals(other.getAddWording()))
            && (this.getApproveStatus() == null ? other.getApproveStatus() == null : this.getApproveStatus().equals(other.getApproveStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getSequence() == null ? other.getSequence() == null : this.getSequence().equals(other.getSequence()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getFromId() == null) ? 0 : getFromId().hashCode());
        result = prime * result + ((getToId() == null) ? 0 : getToId().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getReadStatus() == null) ? 0 : getReadStatus().hashCode());
        result = prime * result + ((getAddSource() == null) ? 0 : getAddSource().hashCode());
        result = prime * result + ((getAddWording() == null) ? 0 : getAddWording().hashCode());
        result = prime * result + ((getApproveStatus() == null) ? 0 : getApproveStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getSequence() == null) ? 0 : getSequence().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appId=").append(appId);
        sb.append(", fromId=").append(fromId);
        sb.append(", toId=").append(toId);
        sb.append(", remark=").append(remark);
        sb.append(", readStatus=").append(readStatus);
        sb.append(", addSource=").append(addSource);
        sb.append(", addWording=").append(addWording);
        sb.append(", approveStatus=").append(approveStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", sequence=").append(sequence);
        sb.append("]");
        return sb.toString();
    }
}