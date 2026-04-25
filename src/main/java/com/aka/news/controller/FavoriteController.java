package com.aka.news.controller;

import com.aka.news.pojo.Result;
import com.aka.news.pojo.entity.favorite;
import com.aka.news.pojo.entity.news;
import com.aka.news.pojo.entity.user;
import com.aka.news.service.ArticalService;
import com.aka.news.service.FavoriteService;
import com.aka.news.service.UserService;
import com.aka.news.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticalService articalService;

    // 添加收藏
    @PostMapping("/add/{newsId}")
    public Result addFavorite(@PathVariable Integer newsId) {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if (u1 == null) {
                return Result.fail("用户不存在，请重新登录");
            }

            // 检查新闻是否存在
            news newsItem = articalService.getById(newsId);
            if (newsItem == null) {
                return Result.fail("新闻不存在");
            }

            // 检查是否已经收藏
            if (favoriteService.checkUserFavorited(u1.getId(), newsId)) {
                return Result.fail("您已经收藏过了");
            }

            boolean success = favoriteService.addFavorite(u1.getId(), newsId);
            if (success) {
                return Result.success("收藏成功");
            } else {
                return Result.fail("收藏失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("收藏失败: " + e.getMessage());
        }
    }

    // 取消收藏
    @DeleteMapping("/cancel/{newsId}")
    public Result removeFavorite(@PathVariable Integer newsId) {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if (u1 == null) {
                return Result.fail("用户不存在，请重新登录");
            }

            boolean success = favoriteService.removeFavorite(u1.getId(), newsId);
            if (success) {
                return Result.success("取消收藏成功");
            } else {
                return Result.fail("取消收藏失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("取消收藏失败: " + e.getMessage());
        }
    }

    // 获取新闻收藏数
    @GetMapping("/count/{newsId}")
    public Result getFavoriteCount(@PathVariable Integer newsId) {
        try {
            Integer count = favoriteService.getFavoriteCountByNewsId(newsId);
            return Result.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取收藏数失败: " + e.getMessage());
        }
    }

    // 检查用户是否已收藏
    @GetMapping("/check/{newsId}")
    public Result checkFavorited(@PathVariable Integer newsId) {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if (u1 == null) {
                return Result.fail("用户不存在，请重新登录");
            }

            Boolean isFavorited = favoriteService.checkUserFavorited(u1.getId(), newsId);
            return Result.success(isFavorited);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("检查失败: " + e.getMessage());
        }
    }

    // 获取用户的收藏列表
    @GetMapping("/my-favorites")
    public Result getMyFavorites() {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if (u1 == null) {
                return Result.fail("用户不存在，请重新登录");
            }

            List<favorite> favorites = favoriteService.getUserFavorites(u1.getId());
            List<news> newsList = new ArrayList<>();

            for (favorite fav : favorites) {
                news newsItem = articalService.getById(fav.getNewsId());
                if (newsItem != null && newsItem.getStatus() == 1) {
                    user author = userService.getById(newsItem.getAuthorId());
                    if (author != null) {
                        newsItem.setAuthorName(author.getUsername());
                    }
                    newsList.add(newsItem);
                }
            }

            return Result.success(newsList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取收藏列表失败: " + e.getMessage());
        }
    }
}
