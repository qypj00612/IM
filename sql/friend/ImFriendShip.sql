CREATE TABLE `im_friendship` (
     `app_id` int NOT NULL COMMENT '应用id',
     `from_id` varchar(64) NOT NULL COMMENT '发起方用户ID',
     `to_id` varchar(64) NOT NULL COMMENT '接收方用户ID',
     `remark` varchar(255) DEFAULT NULL COMMENT '备注',
     `status` int DEFAULT NULL COMMENT '状态 1正常 2删除',
     `black` int DEFAULT NULL COMMENT '状态 1正常 2拉黑',
     `create_time` bigint DEFAULT NULL COMMENT '创建时间',
     `friend_sequence` bigint DEFAULT NULL COMMENT '好友关系序列号',
     `black_sequence` bigint DEFAULT NULL COMMENT '黑名单关系序列号',
     `add_source` varchar(64) DEFAULT NULL COMMENT '好友来源',
     `extra` varchar(1024) DEFAULT NULL COMMENT '扩展字段',
     PRIMARY KEY (`app_id`,`from_id`,`to_id`) USING BTREE COMMENT '联合主键：app_id + from_id + to_id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IM好友关系表';