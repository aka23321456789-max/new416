package com.aka.news.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("favorite")
@AllArgsConstructor
@NoArgsConstructor
public class favorite {
    private Integer id;
    private Integer userId;
    private Integer newsId;
    private LocalDateTime createTime;
}
