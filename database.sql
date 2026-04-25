-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS springboot_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE springboot_demo;

-- 创建 user 表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `role` INT DEFAULT 0 COMMENT '角色 0:普通用户 1:管理员',
  `phone` VARCHAR(20) COMMENT '手机号',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建 news 表
CREATE TABLE IF NOT EXISTS `news` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '新闻标题',
  `content` TEXT COMMENT '新闻内容',
  `authorId` INT NOT NULL COMMENT '作者ID',
  `status` INT DEFAULT 0 COMMENT '状态 0:审核中 1:审核通过 2:审核未通过',
  `category` VARCHAR(50) COMMENT '分类',
  `view_count` INT DEFAULT 0 COMMENT '阅读次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻表';

-- 创建 like 表（点赞）
CREATE TABLE IF NOT EXISTS `user_like` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `news_id` INT NOT NULL COMMENT '新闻ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_news` (`user_id`, `news_id`) COMMENT '用户只能点赞一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 创建 favorite 表（收藏）
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `news_id` INT NOT NULL COMMENT '新闻ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_news` (`user_id`, `news_id`) COMMENT '用户只能收藏一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 创建 comment 表（评论）
CREATE TABLE IF NOT EXISTS `comment` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `news_id` INT NOT NULL COMMENT '新闻ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` INT DEFAULT NULL COMMENT '父评论ID（回复功能）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_news_id` (`news_id`) COMMENT '新闻ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
