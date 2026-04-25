-- ============================================
-- 新闻系统数据库更新脚本
-- 用于添加点赞、收藏、评论功能所需的数据表
-- ============================================

USE springboot_demo;

-- ============================================
-- 1. 更新 news 表，添加新字段
-- ============================================
ALTER TABLE news 
ADD COLUMN IF NOT EXISTS view_count INT DEFAULT 0 COMMENT '阅读次数' AFTER category,
ADD COLUMN IF NOT EXISTS create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER view_count,
ADD COLUMN IF NOT EXISTS update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER create_time;

-- ============================================
-- 2. 创建点赞表 (user_like)
-- ============================================
CREATE TABLE IF NOT EXISTS `user_like` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `news_id` INT NOT NULL COMMENT '新闻ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_news` (`user_id`, `news_id`) COMMENT '用户只能点赞一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ============================================
-- 3. 创建收藏表 (favorite)
-- ============================================
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `news_id` INT NOT NULL COMMENT '新闻ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_news` (`user_id`, `news_id`) COMMENT '用户只能收藏一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ============================================
-- 4. 创建评论表 (comment)
-- ============================================
CREATE TABLE IF NOT EXISTS `comment` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `news_id` INT NOT NULL COMMENT '新闻ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` INT DEFAULT NULL COMMENT '父评论ID（回复功能）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_news_id` (`news_id`) COMMENT '新闻ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ============================================
-- 5. 更新现有新闻的阅读量为0（如果没有设置过）
-- ============================================
UPDATE news SET view_count = 0 WHERE view_count IS NULL;

-- ============================================
-- 完成！
-- ============================================
SELECT '数据库更新完成！' AS message;
