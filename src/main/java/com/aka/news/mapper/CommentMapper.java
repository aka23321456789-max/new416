package com.aka.news.mapper;

import com.aka.news.pojo.entity.comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<comment> {

    @Select("SELECT COUNT(*) FROM comment WHERE news_id = #{newsId}")
    Integer getCommentCountByNewsId(@Param("newsId") Integer newsId);

    @Select("SELECT c.*, u.username FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.news_id = #{newsId} " +
            "ORDER BY c.create_time DESC")
    List<comment> getCommentsWithUserByNewsId(@Param("newsId") Integer newsId);
}
