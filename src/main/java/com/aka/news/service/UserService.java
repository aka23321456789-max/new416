package com.aka.news.service;

import com.aka.news.mapper.UserMapper;
import com.aka.news.pojo.entity.user;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, user> {
    //继承 ServiceImpl 后，可以直接使用 save、update、delete、select 等方法
}
