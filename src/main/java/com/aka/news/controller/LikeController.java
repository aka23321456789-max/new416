package com.aka.news.controller;

import com.aka.news.pojo.Result;
import com.aka.news.pojo.entity.user;
import com.aka.news.service.LikeService;
import com.aka.news.service.UserService;
import com.aka.news.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    // 点赞
    @PostMapping("/add/{newsId}")
    public Result like(@PathVariable Integer newsId) {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if (u1 == null) {
                return Result.fail("用户不存在，请重新登录");
            }

            // 检查是否已经点赞
            if (likeService.checkUserLiked(u1.getId(), newsId)) {
                return Result.fail("您已经点赞过了");
            }

            boolean success = likeService.like(u1.getId(), newsId);
            if (success) {
                return Result.success("点赞成功");
            } else {
                return Result.fail("点赞失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("点赞失败: " + e.getMessage());
        }
    }

    // 取消点赞
    @DeleteMapping("/cancel/{newsId}")
    public Result unlike(@PathVariable Integer newsId) {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if (u1 == null) {
                return Result.fail("用户不存在，请重新登录");
            }

            boolean success = likeService.unlike(u1.getId(), newsId);
            if (success) {
                return Result.success("取消点赞成功");
            } else {
                return Result.fail("取消点赞失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("取消点赞失败: " + e.getMessage());
        }
    }

    // 获取新闻点赞数
    @GetMapping("/count/{newsId}")
    public Result getLikeCount(@PathVariable Integer newsId) {
        try {
            Integer count = likeService.getLikeCountByNewsId(newsId);
            return Result.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取点赞数失败: " + e.getMessage());
        }
    }

    // 检查用户是否已点赞
    @GetMapping("/check/{newsId}")
    public Result checkLiked(@PathVariable Integer newsId) {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if (u1 == null) {
                return Result.fail("用户不存在，请重新登录");
            }

            Boolean isLiked = likeService.checkUserLiked(u1.getId(), newsId);
            return Result.success(isLiked);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("检查失败: " + e.getMessage());
        }
    }
}
