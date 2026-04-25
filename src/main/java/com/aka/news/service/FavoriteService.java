package com.aka.news.service;

import com.aka.news.mapper.FavoriteMapper;
import com.aka.news.pojo.entity.favorite;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService extends ServiceImpl<FavoriteMapper, favorite> {

    public Integer getFavoriteCountByNewsId(Integer newsId) {
        return baseMapper.getFavoriteCountByNewsId(newsId);
    }

    public Boolean checkUserFavorited(Integer userId, Integer newsId) {
        return baseMapper.checkUserFavorited(userId, newsId) > 0;
    }

    public boolean addFavorite(Integer userId, Integer newsId) {
        favorite fav = new favorite();
        fav.setUserId(userId);
        fav.setNewsId(newsId);
        return save(fav);
    }

    public boolean removeFavorite(Integer userId, Integer newsId) {
        QueryWrapper<favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("news_id", newsId);
        return remove(wrapper);
    }

    public List<favorite> getUserFavorites(Integer userId) {
        QueryWrapper<favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        return list(wrapper);
    }
}
