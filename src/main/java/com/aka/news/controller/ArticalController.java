package com.aka.news.controller;

import com.aka.news.pojo.Result;
import com.aka.news.pojo.entity.news;
import com.aka.news.pojo.entity.user;
import com.aka.news.service.ArticalService;
import com.aka.news.service.CommentService;
import com.aka.news.service.FavoriteService;
import com.aka.news.service.LikeService;
import com.aka.news.service.UserService;
import com.aka.news.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/artical")
public class ArticalController {

    @Autowired
    private ArticalService articalService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private CommentService commentService;

    //获取新闻列表（支持分页）
    @GetMapping("/list")
    public Result getNewsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "6") Integer size){
        try{
            // 创建分页对象
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<news> pageParam = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
            
            // 分页查询
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<news> newsPage = 
                articalService.page(pageParam, new QueryWrapper<news>()
                    .eq("status", 1)
                    .orderByDesc("create_time"));

            // 替换作者ID为作者名称，并添加互动数据
            for (news newsItem : newsPage.getRecords()) {
                user author = userService.getById(newsItem.getAuthorId());
                if (author != null) {
                    newsItem.setAuthorName(author.getUsername());
                }
                // 添加点赞数、收藏数、评论数
                newsItem.setLikeCount(likeService.getLikeCountByNewsId(newsItem.getId()));

                newsItem.setFavoriteCount(favoriteService.getFavoriteCountByNewsId(newsItem.getId()));
                newsItem.setCommentCount(commentService.getCommentCountByNewsId(newsItem.getId()));
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("records", newsPage.getRecords());
            result.put("total", newsPage.getTotal());
            result.put("pages", newsPage.getPages());
            result.put("current", newsPage.getCurrent());
            result.put("size", newsPage.getSize());
            
            return Result.success(result);
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.fail("获取新闻列表失败: " + e.getMessage());
        }
    }

    //审核新闻（管理员）
    @PutMapping("/review/{id}")
    public Result reviewNews(@PathVariable int id, @RequestBody Map<String, Integer> reviewData){
        try{
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if(u1 == null){
                return Result.fail("用户不存在，请重新登录");
            }

            // 检查权限：只有管理员可以审核
            if (u1.getRole() != 3) {
                return Result.fail("您没有权限审核新闻");
            }

            news newsItem = articalService.getById(id);
            if (newsItem == null) {
                return Result.fail("新闻不存在");
            }

            Integer status = reviewData.get("status");
            if (status == null || (status != 1 && status != 2)) {
                return Result.fail("审核状态无效");
            }

            newsItem.setStatus(status);
            articalService.updateById(newsItem);

            String statusText = status == 1 ? "通过" : "不通过";
            return Result.success("审核" + statusText);
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.fail("审核失败: " + e.getMessage());
        }
    }

    //获取待审核新闻列表（管理员）
    @GetMapping("/pending")
    public Result getPendingNews(){
        try{
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if(u1 == null){
                return Result.fail("用户不存在，请重新登录");
            }

            // 检查权限：只有管理员可以查看待审核列表
            if (u1.getRole() != 3) {
                return Result.fail("您没有权限查看待审核新闻");
            }

            List<news> pendingNews = articalService.list(new QueryWrapper<news>()
                    .eq("status", 0));

            return Result.success(pendingNews);
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.fail("获取待审核新闻失败: " + e.getMessage());
        }
    }

    // 测试接口
    @GetMapping("/test")
    public Result test(){
        return Result.success("测试成功");
    }

    // 获取新闻详情
    @GetMapping("/detail/{id}")
    public Result getNewsDetail(@PathVariable int id){
        try{
            news newsItem = articalService.getById(id);
            if (newsItem == null) {
                return Result.fail("新闻不存在");
            }

            // 只返回审核通过的新闻
            if (newsItem.getStatus() != 1) {
                return Result.fail("新闻不存在");
            }

            // 增加阅读量
            newsItem.setViewCount(newsItem.getViewCount() + 1);
            articalService.updateById(newsItem);

            // 替换作者ID为作者名称
            user author = userService.getById(newsItem.getAuthorId());
            if (author != null) {
                newsItem.setAuthorName(author.getUsername());
            }

            // 添加互动数据
            newsItem.setLikeCount(likeService.getLikeCountByNewsId(newsItem.getId()));
            newsItem.setFavoriteCount(favoriteService.getFavoriteCountByNewsId(newsItem.getId()));
            newsItem.setCommentCount(commentService.getCommentCountByNewsId(newsItem.getId()));

            // 检查当前用户是否点赞/收藏
            Map<String, Object> map = ThreadLocalUtil.get();
            if (map != null && map.get("username") != null) {
                String username = (String) map.get("username");
                user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
                if (u1 != null) {
                    newsItem.setIsLiked(likeService.checkUserLiked(u1.getId(), newsItem.getId()));
                    newsItem.setIsFavorited(favoriteService.checkUserFavorited(u1.getId(), newsItem.getId()));
                }
            }

            return Result.success(newsItem);
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.fail("获取新闻详情失败: " + e.getMessage());
        }
    }

    // 获取发布者自己的发布记录（包括所有状态）
    @GetMapping("/my-news")
    public Result getMyNews(){
        try{
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if(u1 == null){
                return Result.fail("用户不存在，请重新登录");
            }

            // 获取当前用户发布的所有新闻（包括所有状态）
            List<news> newsList = articalService.list(new QueryWrapper<news>()
                    .eq("authorId", u1.getId())
                    .orderByDesc("create_time"));

            // 替换作者ID为作者名称，并添加互动数据
            for (news newsItem : newsList) {
                user author = userService.getById(newsItem.getAuthorId());
                if (author != null) {
                    newsItem.setAuthorName(author.getUsername());
                }
                newsItem.setLikeCount(likeService.getLikeCountByNewsId(newsItem.getId()));
                newsItem.setFavoriteCount(favoriteService.getFavoriteCountByNewsId(newsItem.getId()));
                newsItem.setCommentCount(commentService.getCommentCountByNewsId(newsItem.getId()));
            }

            return Result.success(newsList);
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.fail("获取发布记录失败: " + e.getMessage());
        }
    }

    // ==================== 搜索功能 ====================
    @GetMapping("/search")
    public Result searchNews(@RequestParam String keyword){
        try{
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.fail("搜索关键词不能为空");
            }

            // 搜索标题或内容包含关键词的新闻（只搜索已审核通过的）
            List<news> newsList = articalService.list(new QueryWrapper<news>()
                    .eq("status", 1)
                    .and(wrapper -> wrapper
                            .like("title", keyword)
                            .or()
                            .like("content", keyword))
                    .orderByDesc("create_time"));

            // 添加作者名称和互动数据
            for (news newsItem : newsList) {
                user author = userService.getById(newsItem.getAuthorId());
                if (author != null) {
                    newsItem.setAuthorName(author.getUsername());
                }
                newsItem.setLikeCount(likeService.getLikeCountByNewsId(newsItem.getId()));
                newsItem.setFavoriteCount(favoriteService.getFavoriteCountByNewsId(newsItem.getId()));
                newsItem.setCommentCount(commentService.getCommentCountByNewsId(newsItem.getId()));
            }

            return Result.success(newsList);
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.fail("搜索失败: " + e.getMessage());
        }
    }

    // ==================== 分类筛选功能 ====================
    @GetMapping("/category/{category}")
    public Result getNewsByCategory(@PathVariable String category){
        try{
            if (category == null || category.trim().isEmpty()) {
                return Result.fail("分类不能为空");
            }

            // 根据分类筛选已审核通过的新闻
            List<news> newsList = articalService.list(new QueryWrapper<news>()
                    .eq("status", 1)
                    .eq("category", category)
                    .orderByDesc("create_time"));

            // 添加作者名称和互动数据
            for (news newsItem : newsList) {
                user author = userService.getById(newsItem.getAuthorId());
                if (author != null) {
                    newsItem.setAuthorName(author.getUsername());
                }
                newsItem.setLikeCount(likeService.getLikeCountByNewsId(newsItem.getId()));
                newsItem.setFavoriteCount(favoriteService.getFavoriteCountByNewsId(newsItem.getId()));
                newsItem.setCommentCount(commentService.getCommentCountByNewsId(newsItem.getId()));
            }

            return Result.success(newsList);
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.fail("获取分类新闻失败: " + e.getMessage());
        }
    }

    // ==================== 新闻删除功能 ====================
    @DeleteMapping("/delete/{id}")
    public Result deleteNews(@PathVariable int id){
        try{
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if(u1 == null){
                return Result.fail("用户不存在，请重新登录");
            }

            news newsItem = articalService.getById(id);
            if (newsItem == null) {
                return Result.fail("新闻不存在");
            }

            // 权限判断：只能删除自己发布的新闻，管理员可以删除任何新闻
            if (newsItem.getAuthorId() != u1.getId() && u1.getRole() != 3) {
                return Result.fail("您没有权限删除此新闻");
            }

            // 删除新闻（同时删除相关的点赞、收藏、评论）
            articalService.removeById(id);

            // 删除相关的点赞记录
            likeService.remove(new QueryWrapper<com.aka.news.pojo.entity.like>().eq("news_id", id));

            // 删除相关的收藏记录
            favoriteService.remove(new QueryWrapper<com.aka.news.pojo.entity.favorite>().eq("news_id", id));

            // 删除相关的评论
            commentService.remove(new QueryWrapper<com.aka.news.pojo.entity.comment>().eq("news_id", id));

            return Result.success("删除成功");
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.fail("删除失败: " + e.getMessage());
        }
    }

    // ==================== 新闻编辑功能 ====================
    @PutMapping("/update/{id}")
    public Result updateNews(@PathVariable int id, @RequestBody news newsData){
        try{
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String) map.get("username");
            user u1 = userService.getOne(new QueryWrapper<user>().eq("username", username));
            if(u1 == null){
                return Result.fail("用户不存在，请重新登录");
            }

            news newsItem = articalService.getById(id);
            if (newsItem == null) {
                return Result.fail("新闻不存在");
            }

            // 权限判断：只能编辑自己发布的新闻
            if (newsItem.getAuthorId() != u1.getId()) {
                return Result.fail("您没有权限编辑此新闻");
            }

            // 更新新闻内容
            if (newsData.getTitle() != null && !newsData.getTitle().trim().isEmpty()) {
                newsItem.setTitle(newsData.getTitle().trim());
            }
            if (newsData.getContent() != null && !newsData.getContent().trim().isEmpty()) {
                newsItem.setContent(newsData.getContent().trim());
            }
            if (newsData.getCategory() != null && !newsData.getCategory().trim().isEmpty()) {
                newsItem.setCategory(newsData.getCategory().trim());
            }

            // 编辑后重置为待审核状态
            newsItem.setStatus(0);

            articalService.updateById(newsItem);

            return Result.success("新闻编辑成功，请等待管理员审核");
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.fail("编辑失败: " + e.getMessage());
        }
    }

}
