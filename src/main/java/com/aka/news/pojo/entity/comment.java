package com.aka.news.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("comment")
@NoArgsConstructor
@AllArgsConstructor
public class comment {
    private Integer id;
    private Integer newsId;
    private Integer userId;
    private String content;
    private Integer parentId;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String username;
}
