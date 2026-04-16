package com.aka.news.service;

import com.aka.news.mapper.ArticalMapper;
import com.aka.news.pojo.entity.news;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ArticalService extends ServiceImpl<ArticalMapper, news> {
}
