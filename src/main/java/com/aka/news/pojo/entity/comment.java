package com.aka.news.pojo.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("comment")
@NoArgsConstructor
@AllArgsConstructor
public class comment {//评论
    private int id;
    private int newsId;
    private int userId;
    private String content;



}
