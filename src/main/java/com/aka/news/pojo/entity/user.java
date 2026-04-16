package com.aka.news.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@TableName("user")
@NoArgsConstructor
@AllArgsConstructor
public class user {

    @TableId(value = "id", type = IdType.AUTO)//主键自增
    private int id;

    @NonNull//非空
    @TableField(value = "username")
    private String username;

    @TableField(value = "password")
    @NonNull
    private String password;

    @TableField(value = "role")
    private int role;

    @TableField(value = "phone")
    private String phone;
}
