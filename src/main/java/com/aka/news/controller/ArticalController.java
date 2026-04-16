package com.aka.news.controller;


import com.aka.news.pojo.Result;
import com.aka.news.pojo.entity.news;
import com.aka.news.pojo.entity.user;
import com.aka.news.service.ArticalService;

import com.aka.news.service.UserService;
import com.aka.news.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/artical")
public class ArticalController {

    @Autowired
    private ArticalService articalService;
    @Autowired
    private UserService userService;
    //添加文章
    @PostMapping("/add")
    public Result addArtical(@RequestBody news news1){
        try{
            //添加文章
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));//查询用户
            news1.setAuthorId(u1.getId());//设置作者ID

            articalService.save(news1);

            return Result.success();
        }
        catch (Exception e){
            return Result.fail(e.getMessage());
        }




    }

}
