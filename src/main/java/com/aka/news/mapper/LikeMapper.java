package com.aka.news.mapper;

import com.aka.news.pojo.entity.like;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeMapper extends BaseMapper<like> {

    @Select("SELECT COUNT(*) FROM user_like WHERE news_id = #{newsId}")
    Integer getLikeCountByNewsId(@Param("newsId") Integer newsId);

    @Select("SELECT COUNT(*) FROM user_like WHERE user_id = #{userId} AND news_id = #{newsId}")
    Integer checkUserLiked(@Param("userId") Integer userId, @Param("newsId") Integer newsId);
}
