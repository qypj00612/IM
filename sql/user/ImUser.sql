-- IM用户数据表
CREATE TABLE `im_user_data` (
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户id（主键1）',
    `app_id` INT NOT NULL COMMENT '应用id（主键2）',
    `nickname` VARCHAR(128) DEFAULT '' COMMENT '昵称',
    `password` VARCHAR(256) DEFAULT '' COMMENT '密码（建议加密存储）',
    `photo` VARCHAR(512) DEFAULT '' COMMENT '头像URL',
    `user_sex` TINYINT DEFAULT 0 COMMENT '性别 0女 1男',
    `birth_day` VARCHAR(32) DEFAULT '' COMMENT '生日（格式建议：yyyy-MM-dd）',
    `self_signature` VARCHAR(512) DEFAULT '' COMMENT '个性签名',
    `friend_allow_type` TINYINT DEFAULT 0 COMMENT '好友添加类型',
    `forbidden_flag` TINYINT DEFAULT 1 COMMENT '封禁状况 0 被封禁 1 未被封禁',
    `type` INT DEFAULT 0 COMMENT '用户类型（预留字段）',
    `silent_flag` TINYINT DEFAULT 0 COMMENT '禁言标识（预留字段）',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识 0未删除 1已删除',
    `extra` VARCHAR(1024) DEFAULT '' COMMENT '扩展字段（JSON格式）',
    PRIMARY KEY (`user_id`, `app_id`)
    -- 复合主键（userId + appId 唯一标识一个用户）
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IM用户基础信息表';