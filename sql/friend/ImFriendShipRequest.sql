CREATE TABLE `im_friendship_request` (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
     `app_id` int DEFAULT NULL COMMENT '应用ID',
     `from_id` varchar(64) DEFAULT NULL COMMENT '发起方ID',
     `to_id` varchar(64) DEFAULT NULL COMMENT '接收方ID',
     `remark` varchar(255) DEFAULT NULL COMMENT '备注',
     `read_status` int DEFAULT NULL COMMENT '0 未读 1已读',
     `add_source` varchar(64) DEFAULT NULL COMMENT '好友来源',
     `add_wording` varchar(255) DEFAULT NULL COMMENT '添加说明',
     `approve_status` int DEFAULT NULL COMMENT '审批状态 1同意 2拒绝',
     `create_time` bigint DEFAULT NULL COMMENT '创建时间',
     `update_time` bigint DEFAULT NULL COMMENT '更新时间',
     `sequence` bigint DEFAULT NULL COMMENT '序列号',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友申请表';