CREATE TABLE `im_conversation_set` (
   `conversation_id` varchar(128) NOT NULL COMMENT '会话id 0_fromId_toId',
   `conversation_type` int NOT NULL COMMENT '会话类型 1单聊 2群聊',
   `from_id` varchar(64) NOT NULL COMMENT '发送方ID',
   `to_id` varchar(64) NOT NULL COMMENT '接收方ID/群ID',
   `is_mute` int NOT NULL DEFAULT '0' COMMENT '是否免打扰 0否 1是',
   `is_top` int NOT NULL DEFAULT '0' COMMENT '是否置顶 0否 1是',
   `sequence` bigint NOT NULL DEFAULT '0' COMMENT '消息最大序列号',
   `read_sequence` bigint NOT NULL DEFAULT '0' COMMENT '已读消息序列号',
   `app_id` int NOT NULL COMMENT '应用ID',
   PRIMARY KEY (`conversation_id`),
   KEY `idx_from_to` (`from_id`,`to_id`),
   KEY `idx_conversation_app` (`conversation_id`,`app_id`),
   KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话设置表';