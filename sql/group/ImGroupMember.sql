CREATE TABLE `im_group_member` (
    `group_member_id` bigint(20) NOT NULL COMMENT '群成员ID',
    `app_id` int(11) NOT NULL COMMENT '应用ID',
    `group_id` varchar(50) NOT NULL COMMENT '群ID',
    `member_id` varchar(50) NOT NULL COMMENT '成员ID',
    `speak_date` bigint(20) DEFAULT NULL COMMENT '禁言到期时间戳',
    `role` tinyint(5) NOT NULL COMMENT '群成员类型: 0普通成员, 1管理员, 2群主, 3已退出',
    `alias` varchar(50) DEFAULT NULL COMMENT '群昵称',
    `join_time` bigint(20) DEFAULT NULL COMMENT '加入时间戳',
    `leave_time` bigint(20) DEFAULT NULL COMMENT '离开时间戳',
    `join_type` varchar(50) DEFAULT NULL COMMENT '加入方式',
    `extra` varchar(1000) DEFAULT NULL COMMENT '扩展字段',
    PRIMARY KEY (`group_member_id`),
    UNIQUE KEY `uk_group_member` (`app_id`,`group_id`,`member_id`),
    KEY `idx_group_id` (`group_id`),
    KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群成员表';