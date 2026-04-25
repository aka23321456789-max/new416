package com.aka.news.service;

import com.aka.news.mapper.LikeMapper;
import com.aka.news.pojo.entity.like;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class LikeService extends ServiceImpl<LikeMapper, like> {

    public Integer getLikeCountByNewsId(Integer newsId) {
        return baseMapper.getLikeCountByNewsId(newsId);
    }

    public Boolean checkUserLiked(Integer userId, Integer newsId) {
        return baseMapper.checkUserLiked(userId, newsId) > 0;
    }

    public boolean like(Integer userId, Integer newsId) {
        like likeRecord = new like();
        likeRecord.setUserId(userId);
        likeRecord.setNewsId(newsId);
        return save(likeRecord);
    }

    public boolean unlike(Integer userId, Integer newsId) {
        QueryWrapper<like> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("news_id", newsId);
        return remove(wrapper);
    }
}
