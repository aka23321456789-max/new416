package com.aka.news.mapper;

import com.aka.news.pojo.entity.user;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper extends BaseMapper<user> {
    //继承 BaseMapper 后，无需额外定义方法，即可使用 CRUD 功能
    
    //复杂查询示例：动态 SQL
    //List<user> selectUsersByCondition(Map<String, Object> params);
    

}
