package com.aka.news.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("img")
@AllArgsConstructor
@NoArgsConstructor
public class img {
    private int id;
    private String url;
    private int newsId;

}
