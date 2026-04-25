package com.aka.news.controller;

import com.aka.news.pojo.Result;
import com.aka.news.pojo.entity.comment;
import com.aka.news.pojo.entity.news;
import com.aka.news.pojo.entity.user;
import com.aka.news.service.ArticalService;
import com.aka.news.service.CommentService;
import com.aka.news.service.UserService;
import com.aka.news.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticalService articalService;

    // 添加评论
    @PostMapping("/add")
    public Result addComment(@RequestBody Map<String, Object> commentData) {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if (u1 == null) {
                return Result.fail("用户不存在，请重新登录");
            }

            Integer newsId = (Integer) commentData.get("newsId");
            String content = (String) commentData.get("content");
            Integer parentId = commentData.get("parentId") != null ? (Integer) commentData.get("parentId") : null;

            if (newsId == null) {
                return Result.fail("新闻ID不能为空");
            }
            if (content == null || content.trim().isEmpty()) {
                return Result.fail("评论内容不能为空");
            }

            // 检查新闻是否存在且已审核通过
            news newsItem = articalService.getById(newsId);
            if (newsItem == null) {
                return Result.fail("新闻不存在");
            }
            if (newsItem.getStatus() != 1) {
                return Result.fail("该新闻暂未通过审核，无法评论");
            }

            boolean success = commentService.addComment(u1.getId(), newsId, content.trim(), parentId);
            if (success) {
                return Result.success("评论成功");
            } else {
                return Result.fail("评论失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("评论失败: " + e.getMessage());
        }
    }

    // 获取新闻的评论列表
    @GetMapping("/list/{newsId}")
    public Result getComments(@PathVariable Integer newsId) {
        try {
            // 检查新闻是否存在
            news newsItem = articalService.getById(newsId);
            if (newsItem == null) {
                return Result.fail("新闻不存在");
            }

            List<comment> comments = commentService.getCommentsByNewsId(newsId);
            return Result.success(comments);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取评论失败: " + e.getMessage());
        }
    }

    // 获取新闻评论数
    @GetMapping("/count/{newsId}")
    public Result getCommentCount(@PathVariable Integer newsId) {
        try {
            Integer count = commentService.getCommentCountByNewsId(newsId);
            return Result.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取评论数失败: " + e.getMessage());
        }
    }

    // 删除自己的评论
    @DeleteMapping("/delete/{id}")
    public Result deleteComment(@PathVariable Integer id) {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if (u1 == null) {
                return Result.fail("用户不存在，请重新登录");
            }

            comment commentItem = commentService.getById(id);
            if (commentItem == null) {
                return Result.fail("评论不存在");
            }

            // 只能删除自己的评论
            if (!commentItem.getUserId().equals(u1.getId())) {
                return Result.fail("您没有权限删除此评论");
            }

            boolean success = commentService.removeById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.fail("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("删除失败: " + e.getMessage());
        }
    }
}
