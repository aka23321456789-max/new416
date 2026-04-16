package com.aka.news.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("like")
@AllArgsConstructor
@NoArgsConstructor
public class like {//点赞
    private int id;
    private String username;//点赞者
    private String math;//数量
}
