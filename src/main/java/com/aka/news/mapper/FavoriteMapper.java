package com.aka.news.mapper;

import com.aka.news.pojo.entity.favorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FavoriteMapper extends BaseMapper<favorite> {

    @Select("SELECT COUNT(*) FROM favorite WHERE news_id = #{newsId}")
    Integer getFavoriteCountByNewsId(@Param("newsId") Integer newsId);

    @Select("SELECT COUNT(*) FROM favorite WHERE user_id = #{userId} AND news_id = #{newsId}")
    Integer checkUserFavorited(@Param("userId") Integer userId, @Param("newsId") Integer newsId);
}
