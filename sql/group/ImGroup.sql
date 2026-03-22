CREATE TABLE `im_group` (
    `group_id` varchar(50) NOT NULL COMMENT '群id',
    `app_id` int(10) NOT NULL COMMENT '应用ID',
    `owner_id` varchar(50) DEFAULT NULL COMMENT '群主id',
    `group_type` tinyint(5) NOT NULL COMMENT '群类型 1私有群(类似微信) 2公开群(类似qq)',
    `group_name` varchar(50) NOT NULL COMMENT '群名称',
    `mute` tinyint(5) DEFAULT NULL COMMENT '是否开启群禁言 1禁言',
    `status` tinyint(5) DEFAULT NULL COMMENT '群状态 1正常 2解散',
    `apply_join_type` tinyint(5) DEFAULT NULL COMMENT '申请加群选项 0表示禁止任何人',
    `introduction` varchar(50) DEFAULT NULL COMMENT '群简介',
    `notification` varchar(1000) DEFAULT NULL COMMENT '群公告',
    `photo` varchar(500) DEFAULT NULL COMMENT '头像',
    `max_member_count` int(10) DEFAULT NULL COMMENT '最大的群成员人数',
    `sequence` bigint(20) DEFAULT NULL COMMENT 'seq',
    `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间戳',
    `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间戳',
    `extra` varchar(1000) DEFAULT NULL COMMENT '扩展字段',
    PRIMARY KEY (`group_id`,`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群信息表';