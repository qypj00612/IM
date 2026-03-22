package com.lld.im.service.user.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;

/**
 * IM用户基础信息表
 * @TableName im_user_data
 */
@TableName(value ="im_user_data",autoResultMap = true)
@Data
public class ImUserData {
    /**
     * 用户id（主键1）
     */
    @TableId
    private String userId;

    /**
     * 应用id（主键2）
     */
    private Integer appId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 密码（建议加密存储）
     */
    private String password;

    /**
     * 头像URL
     */
    private String photo;

    /**
     * 性别 0女 1男
     */
    private Integer userSex;

    /**
     * 生日（格式建议：yyyy-MM-dd）
     */
    private String birthDay;

    /**
     * 个性签名
     */
    private String selfSignature;

    /**
     * 好友添加类型
     */
    private Integer friendAllowType;

    /**
     * 封禁状况 0 被封禁 1 未被封禁
     */
    private Integer forbiddenFlag;

    /**
     * 用户类型（预留字段）
     */
    private Integer type;

    /**
     * 禁言标识（预留字段）
     */
    private Integer silentFlag;

    /**
     * 删除标识 0未删除 1已删除
     */
    private Integer delFlag;

    /**
     * 扩展字段（JSON格式）
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
        ImUserData other = (ImUserData) that;
        return (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getNickname() == null ? other.getNickname() == null : this.getNickname().equals(other.getNickname()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getPhoto() == null ? other.getPhoto() == null : this.getPhoto().equals(other.getPhoto()))
            && (this.getUserSex() == null ? other.getUserSex() == null : this.getUserSex().equals(other.getUserSex()))
            && (this.getBirthDay() == null ? other.getBirthDay() == null : this.getBirthDay().equals(other.getBirthDay()))
            && (this.getSelfSignature() == null ? other.getSelfSignature() == null : this.getSelfSignature().equals(other.getSelfSignature()))
            && (this.getFriendAllowType() == null ? other.getFriendAllowType() == null : this.getFriendAllowType().equals(other.getFriendAllowType()))
            && (this.getForbiddenFlag() == null ? other.getForbiddenFlag() == null : this.getForbiddenFlag().equals(other.getForbiddenFlag()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getSilentFlag() == null ? other.getSilentFlag() == null : this.getSilentFlag().equals(other.getSilentFlag()))
            && (this.getDelFlag() == null ? other.getDelFlag() == null : this.getDelFlag().equals(other.getDelFlag()))
            && (this.getExtra() == null ? other.getExtra() == null : this.getExtra().equals(other.getExtra()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getNickname() == null) ? 0 : getNickname().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getPhoto() == null) ? 0 : getPhoto().hashCode());
        result = prime * result + ((getUserSex() == null) ? 0 : getUserSex().hashCode());
        result = prime * result + ((getBirthDay() == null) ? 0 : getBirthDay().hashCode());
        result = prime * result + ((getSelfSignature() == null) ? 0 : getSelfSignature().hashCode());
        result = prime * result + ((getFriendAllowType() == null) ? 0 : getFriendAllowType().hashCode());
        result = prime * result + ((getForbiddenFlag() == null) ? 0 : getForbiddenFlag().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getSilentFlag() == null) ? 0 : getSilentFlag().hashCode());
        result = prime * result + ((getDelFlag() == null) ? 0 : getDelFlag().hashCode());
        result = prime * result + ((getExtra() == null) ? 0 : getExtra().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", appId=").append(appId);
        sb.append(", nickname=").append(nickname);
        sb.append(", password=").append(password);
        sb.append(", photo=").append(photo);
        sb.append(", userSex=").append(userSex);
        sb.append(", birthDay=").append(birthDay);
        sb.append(", selfSignature=").append(selfSignature);
        sb.append(", friendAllowType=").append(friendAllowType);
        sb.append(", forbiddenFlag=").append(forbiddenFlag);
        sb.append(", type=").append(type);
        sb.append(", silentFlag=").append(silentFlag);
        sb.append(", delFlag=").append(delFlag);
        sb.append(", extra=").append(extra);
        sb.append("]");
        return sb.toString();
    }
}