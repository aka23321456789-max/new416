package com.aka.news.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("favorite")
@AllArgsConstructor
@NoArgsConstructor
public class favorite {//收藏
    private int id;
    private int userid;
    private int newsid;
    private String content;
}
