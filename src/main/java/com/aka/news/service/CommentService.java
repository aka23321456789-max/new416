package com.aka.news.service;

import com.aka.news.mapper.CommentMapper;
import com.aka.news.pojo.entity.comment;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService extends ServiceImpl<CommentMapper, comment> {

    public Integer getCommentCountByNewsId(Integer newsId) {
        return baseMapper.getCommentCountByNewsId(newsId);
    }

    public List<comment> getCommentsByNewsId(Integer newsId) {
        return baseMapper.getCommentsWithUserByNewsId(newsId);
    }

    public boolean addComment(Integer userId, Integer newsId, String content, Integer parentId) {
        comment commentRecord = new comment();
        commentRecord.setUserId(userId);
        commentRecord.setNewsId(newsId);
        commentRecord.setContent(content);
        commentRecord.setParentId(parentId);
        return save(commentRecord);
    }
}
