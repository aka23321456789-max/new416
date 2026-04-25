package com.aka.news.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@TableName("news")
@AllArgsConstructor
public class news {
    private Integer id;
    private String title;
    private String content;
    @TableField("authorId")
    private Integer authorId;
    @TableField(exist = false)
    private String authorName;
    private Integer status;
    private String category;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Integer likeCount;

    @TableField(exist = false)
    private Integer favoriteCount;

    @TableField(exist = false)
    private Integer commentCount;

    @TableField(exist = false)
    private Boolean isLiked;

    @TableField(exist = false)
    private Boolean isFavorited;
}
