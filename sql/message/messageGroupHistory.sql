CREATE TABLE `im_group_message_history` (
    `app_id` int DEFAULT NULL COMMENT '应用ID',
    `from_id` varchar(64) DEFAULT NULL COMMENT '发送者ID',
    `group_id` varchar(64) DEFAULT NULL COMMENT '群组ID',
    `message_key` bigint NOT NULL COMMENT '消息体ID(messageBodyId)',
    `sequence` bigint DEFAULT NULL COMMENT '序列号',
    `message_random` varchar(32) DEFAULT NULL COMMENT '随机码',
    `message_time` bigint DEFAULT NULL COMMENT '消息时间戳',
    `create_time` bigint DEFAULT NULL COMMENT '创建时间戳',
    PRIMARY KEY (`message_key`),
    KEY `idx_app_group` (`app_id`,`group_id`),
    KEY `idx_app_group_sequence` (`app_id`,`group_id`,`sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群消息历史表';