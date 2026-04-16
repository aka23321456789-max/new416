# MyBatis-Plus 使用指南

## 1. 依赖配置

在 `pom.xml` 文件中添加 MyBatis-Plus 依赖：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.4.3.4</version>
</dependency>

<!-- 同时需要以下依赖 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.2.2</version>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

## 2. 实体类配置

使用 `@TableName` 注解指定表名，使用 Lombok 注解简化代码：

```java
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@TableName("news") // 指定表名
@AllArgsConstructor
public class news {
    private int id;
    private String title;
    private String content;
    private int authorId;
    private int status; // 0:审核中 1:审核通过 2:审核未通过
    private String category;// 分类, 如:政法新闻\经济新闻\体育新闻\社会新闻\国际新闻
}
```

## 3. Mapper 接口

继承 `BaseMapper<T>` 接口，使用 `@Mapper` 注解标记：

```java
import com.aka.news.pojo.entity.news;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticalMapper extends BaseMapper<news> {

}
```

## 4. Service 层

继承 `ServiceImpl<M, T>` 类，使用 `@Service` 注解标记：

```java
import com.aka.news.mapper.ArticalMapper;
import com.aka.news.pojo.entity.news;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ArticalService extends ServiceImpl<ArticalMapper, news> {
}
```

## 5. 常用方法

### 5.1 基础 CRUD 方法

#### 插入操作

```java
// 插入一条记录
news news = new news();
news.setTitle("新闻标题");
news.setContent("新闻内容");
articalService.save(news);

// 批量插入
List<news> newsList = new ArrayList<>();
// 添加数据到集合
articalService.saveBatch(newsList);
```

#### 查询操作

```java
// 根据 ID 查询
news news = articalService.getById(1);

// 查询所有
List<news> newsList = articalService.list();

// 根据条件查询
QueryWrapper<news> wrapper = new QueryWrapper<>();
wrapper.eq("status", 1); // 条件：status = 1
List<news> newsList = articalService.list(wrapper);

// 使用 LambdaQueryWrapper（推荐）
LambdaQueryWrapper<news> lambdaWrapper = new LambdaQueryWrapper<>();
lambdaWrapper.eq(news::getStatus, 1)
             .like(news::getTitle, "关键词");
List<news> newsList = articalService.list(lambdaWrapper);
```

#### 更新操作

```java
// 根据 ID 更新
news news = new news();
news.setId(1);
news.setTitle("更新后的标题");
    articalService.updateById(news);

// 根据条件更新
QueryWrapper<news> wrapper = new QueryWrapper<>();
wrapper.eq("id", 1);
news updateNews = new news();
updateNews.setTitle("更新后的标题");
articalService.update(updateNews, wrapper);
```

#### 删除操作

```java
// 根据 ID 删除
articalService.removeById(1);

// 根据条件删除
QueryWrapper<news> wrapper = new QueryWrapper<>();
wrapper.eq("status", 2);
articalService.remove(wrapper);
```

### 5.2 分页查询

```java
// 配置分页插件（在配置类中）
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

// 分页查询
Page<news> page = new Page<>(1, 10); // 第1页，每页10条
IPage<news> result = articalService.page(page);

// 带条件的分页查询
LambdaQueryWrapper<news> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(news::getStatus, 1);
Page<news> page = new Page<>(1, 10);
IPage<news> result = articalService.page(page, wrapper);

// 获取分页信息
long total = result.getTotal(); // 总记录数
long current = result.getCurrent(); // 当前页码
long size = result.getSize(); // 每页大小
long pages = result.getPages(); // 总页数
List<news> records = result.getRecords(); // 当前页数据
```

### 5.3 条件构造器

MyBatis-Plus 提供了强大的条件构造器，支持各种查询条件：

```java
// 等于
wrapper.eq("column", value);

// 不等于
wrapper.ne("column", value);

// 大于
wrapper.gt("column", value);

// 大于等于
wrapper.ge("column", value);

// 小于
wrapper.lt("column", value);

// 小于等于
wrapper.le("column", value);

// 模糊查询
wrapper.like("column", value);

// 左模糊
wrapper.likeLeft("column", value);

// 右模糊
wrapper.likeRight("column", value);

// 范围查询
wrapper.between("column", value1, value2);

// 为空
wrapper.isNull("column");

// 不为空
wrapper.isNotNull("column");

// IN 查询
wrapper.in("column", values);

// NOT IN 查询
wrapper.notIn("column", values);

// 排序
wrapper.orderByAsc("column"); // 升序
wrapper.orderByDesc("column"); // 降序

// 多条件组合
wrapper.eq("status", 1)
       .like("title", "关键词")
       .orderByDesc("create_time");
```

## 6. 高级特性

### 6.1 自动填充

可以通过注解实现字段的自动填充，如创建时间、更新时间等：

```java
// 实体类中添加注解
public class news {
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

// 实现 MetaObjectHandler 接口
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
```

### 6.2 逻辑删除

MyBatis-Plus 支持逻辑删除，通过配置实现：

```yaml
# application.yml 配置
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: isDeleted # 逻辑删除字段名
      logic-delete-value: 1 # 逻辑删除值
      logic-not-delete-value: 0 # 逻辑未删除值

# 实体类中添加字段
public class news {
    @TableLogic
    private Integer isDeleted;
}

# 使用方法
articalService.removeById(1); // 执行逻辑删除，会将 isDeleted 字段设置为 1
```

## 7. 实际应用示例

### 7.1 新闻列表查询（带分页和条件）

```java
public IPage<news> getNewsList(int page, int pageSize, String keyword, String category) {
    Page<news> pageInfo = new Page<>(page, pageSize);
    LambdaQueryWrapper<news> wrapper = new LambdaQueryWrapper<>();
    
    // 添加查询条件
    if (StringUtils.isNotBlank(keyword)) {
        wrapper.like(news::getTitle, keyword).or().like(news::getContent, keyword);
    }
    
    if (StringUtils.isNotBlank(category)) {
        wrapper.eq(news::getCategory, category);
    }
    
    // 只查询审核通过的新闻
    wrapper.eq(news::getStatus, 1);
    
    // 按创建时间倒序
    wrapper.orderByDesc(news::getId);
    
    return articalService.page(pageInfo, wrapper);
}
```

### 7.2 新闻详情查询

```java
public news getNewsDetail(int id) {
    return articalService.getById(id);
}
```

### 7.3 新闻创建

```java
public boolean createNews(news news) {
    news.setStatus(0); // 初始状态为审核中
    return articalService.save(news);
}
```

### 7.4 新闻更新

```java
public boolean updateNews(news news) {
    return articalService.updateById(news);
}
```

### 7.5 新闻删除

```java
public boolean deleteNews(int id) {
    return articalService.removeById(id);
}
```

## 8. 总结

MyBatis-Plus 是一个功能强大的 MyBatis 增强工具，它简化了数据库操作代码，提供了丰富的 CRUD 方法和高级特性。通过本文的介绍，你应该能够快速上手 MyBatis-Plus，并在项目中灵活应用它的各种功能。

主要优势：
- 简化 CRUD 操作，减少代码量
- 提供强大的条件构造器
- 支持分页查询
- 支持自动填充
- 支持逻辑删除
- 与 Spring Boot 无缝集成

通过 MyBatis-Plus，你可以更专注于业务逻辑的实现，而不是繁琐的数据库操作代码。