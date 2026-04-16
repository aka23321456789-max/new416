package com.aka.news.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@TableName("news")
@AllArgsConstructor
public class news {
    private int id;
    private String title;
    private String content;
    private int authorId;
    private int status; // 0:审核中 1:审核通过 2:审核未通过
    private String category;// 分类, 如:政法新闻\经济新闻\体育新闻\社会新闻\国际新闻
}
