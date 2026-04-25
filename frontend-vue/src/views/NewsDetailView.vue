<template>
  <div class="news-detail-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-logo">
          <h1>新闻系统</h1>
        </div>
        <div class="navbar-menu">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/news" class="nav-link">新闻列表</router-link>
          <template v-if="auth.isAuthenticated">
            <router-link v-if="auth.role === '2' || auth.role === '3'" to="/upload" class="nav-link">上传新闻</router-link>
            <router-link v-if="auth.role === '3'" to="/admin/news" class="nav-link">管理新闻</router-link>
            <router-link v-if="auth.role === '2' || auth.role === '3'" to="/my-news" class="nav-link">我的发布</router-link>
            <router-link to="/favorites" class="nav-link">我的收藏</router-link>
            <router-link to="/profile" class="nav-link">个人中心</router-link>
            <div class="user-info">
              <span class="username">{{ auth.username || '未登录' }}</span>
              <span class="role-badge" :class="{ 'admin': auth.role === '3' }">
                {{ auth.role === '1' ? '用户' : auth.role === '2' ? '发布者' : auth.role === '3' ? '管理员' : '未登录' }}
              </span>
            </div>
            <button class="logout-button" @click="handleLogout">退出登录</button>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-link">登录</router-link>
            <router-link to="/register" class="nav-link">注册</router-link>
            <span class="guest-badge">游客模式</span>
          </template>
        </div>
      </div>
    </nav>

    <!-- 主内容区 -->
    <main class="main-content">
      <div class="news-detail-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">
          <p>加载中...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error">
          <p>{{ error }}</p>
          <button class="btn btn-secondary" @click="fetchNewsDetail">重试</button>
        </div>
        
        <!-- 新闻详情 -->
        <div v-else-if="news" class="news-detail">
          <h3 class="news-title">{{ news.title }}</h3>
          <div class="news-meta">
            <span class="news-category">{{ news.category }}</span>
            <span class="news-author">作者: {{ news.authorName || news.authorId }}</span>
            <span class="news-time" v-if="news.createTime">{{ formatTime(news.createTime) }}</span>
            <span class="news-views">
              <i class="view-icon">👁</i> {{ news.viewCount || 0 }}
            </span>
          </div>
          <div class="news-content">
            {{ news.content }}
          </div>

          <!-- 互动按钮区 -->
          <div class="interaction-bar">
            <button 
              class="interaction-btn" 
              :class="{ 'active': news.isLiked }"
              @click="handleLike"
              :disabled="!auth.isAuthenticated"
            >
              <span class="icon">{{ news.isLiked ? '❤️' : '🤍' }}</span>
              <span class="count">{{ news.likeCount || 0 }}</span>
              <span class="text">{{ news.isLiked ? '已点赞' : '点赞' }}</span>
            </button>
            
            <button 
              class="interaction-btn" 
              :class="{ 'active': news.isFavorited }"
              @click="handleFavorite"
              :disabled="!auth.isAuthenticated"
            >
              <span class="icon">{{ news.isFavorited ? '⭐' : '☆' }}</span>
              <span class="count">{{ news.favoriteCount || 0 }}</span>
              <span class="text">{{ news.isFavorited ? '已收藏' : '收藏' }}</span>
            </button>
            
            <button class="interaction-btn" @click="scrollToComment">
              <span class="icon">💬</span>
              <span class="count">{{ news.commentCount || 0 }}</span>
              <span class="text">评论</span>
            </button>
          </div>

          <p v-if="!auth.isAuthenticated" class="login-tip">
            请 <router-link to="/login">登录</router-link> 后点赞、收藏和评论
          </p>
        </div>
        
        <!-- 空状态 -->
        <div v-else class="empty">
          <p>新闻不存在</p>
        </div>
      </div>

      <!-- 评论区 -->
      <div v-if="news" class="comments-section" id="comments">
        <h3 class="comments-title">
          评论 <span class="comment-count">({{ comments.length }})</span>
        </h3>
        
        <!-- 评论输入框 -->
        <div v-if="auth.isAuthenticated" class="comment-input-area">
          <textarea 
            v-model="newComment" 
            placeholder="写下你的评论..." 
            class="comment-textarea"
            rows="3"
          ></textarea>
          <button 
            class="submit-comment-btn" 
            @click="submitComment"
            :disabled="!newComment.trim() || commentSubmitting"
          >
            {{ commentSubmitting ? '提交中...' : '发表评论' }}
          </button>
        </div>
        <div v-else class="comment-login-tip">
          请 <router-link to="/login">登录</router-link> 后发表评论
        </div>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div v-if="commentsLoading" class="comments-loading">
            <p>加载评论中...</p>
          </div>
          <div v-else-if="comments.length === 0" class="no-comments">
            <p>暂无评论，快来抢沙发吧！</p>
          </div>
          <div v-else>
            <div 
              v-for="comment in comments" 
              :key="comment.id" 
              class="comment-item"
            >
              <div class="comment-header">
                <span class="comment-author">{{ comment.username || '匿名用户' }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-actions">
                <button 
                  v-if="auth.isAuthenticated && comment.userId === auth.userId"
                  class="delete-comment-btn"
                  @click="deleteComment(comment.id)"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { 
  getNewsDetailApi, 
  likeNewsApi, 
  unlikeNewsApi, 
  addFavoriteApi, 
  removeFavoriteApi,
  getCommentsApi,
  addCommentApi,
  deleteCommentApi
} from '../api/NewsApi'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

// 状态
const news = ref(null)
const loading = ref(true)
const error = ref(null)
const comments = ref([])
const commentsLoading = ref(false)
const newComment = ref('')
const commentSubmitting = ref(false)

// 获取新闻详情
const fetchNewsDetail = async () => {
  loading.value = true
  error.value = null
  try {
    const newsId = route.params.id
    const response = await getNewsDetailApi(newsId)
    news.value = response
    // 获取评论
    fetchComments()
  } catch (err) {
    error.value = err.message || '获取新闻详情失败'
  } finally {
    loading.value = false
  }
}

// 获取评论列表
const fetchComments = async () => {
  if (!news.value) return
  commentsLoading.value = true
  try {
    const response = await getCommentsApi(news.value.id)
    comments.value = response || []
  } catch (err) {
    console.error('获取评论失败:', err)
  } finally {
    commentsLoading.value = false
  }
}

// 处理点赞
const handleLike = async () => {
  if (!auth.isAuthenticated) {
    router.push('/login')
    return
  }
  
  try {
    if (news.value.isLiked) {
      await unlikeNewsApi(news.value.id)
      news.value.isLiked = false
      news.value.likeCount = (news.value.likeCount || 1) - 1
    } else {
      await likeNewsApi(news.value.id)
      news.value.isLiked = true
      news.value.likeCount = (news.value.likeCount || 0) + 1
    }
  } catch (err) {
    alert(err.message || '操作失败')
  }
}

// 处理收藏
const handleFavorite = async () => {
  if (!auth.isAuthenticated) {
    router.push('/login')
    return
  }
  
  try {
    if (news.value.isFavorited) {
      await removeFavoriteApi(news.value.id)
      news.value.isFavorited = false
      news.value.favoriteCount = (news.value.favoriteCount || 1) - 1
    } else {
      await addFavoriteApi(news.value.id)
      news.value.isFavorited = true
      news.value.favoriteCount = (news.value.favoriteCount || 0) + 1
    }
  } catch (err) {
    alert(err.message || '操作失败')
  }
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim()) return
  
  commentSubmitting.value = true
  try {
    await addCommentApi({
      newsId: news.value.id,
      content: newComment.value.trim()
    })
    newComment.value = ''
    news.value.commentCount = (news.value.commentCount || 0) + 1
    // 重新获取评论列表
    fetchComments()
  } catch (err) {
    alert(err.message || '评论失败')
  } finally {
    commentSubmitting.value = false
  }
}

// 删除评论
const deleteComment = async (commentId) => {
  if (!confirm('确定要删除这条评论吗？')) return
  
  try {
    await deleteCommentApi(commentId)
    news.value.commentCount = (news.value.commentCount || 1) - 1
    // 重新获取评论列表
    fetchComments()
  } catch (err) {
    alert(err.message || '删除失败')
  }
}

// 滚动到评论区
const scrollToComment = () => {
  document.getElementById('comments')?.scrollIntoView({ behavior: 'smooth' })
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 退出登录
const handleLogout = () => {
  auth.logout()
  router.push('/login')
}

// 页面加载时获取新闻详情
onMounted(() => {
  fetchNewsDetail()
})
</script>

<style scoped>
.news-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 导航栏样式 */
.navbar {
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
}

.navbar-logo h1 {
  color: #667eea;
  font-size: 1.5rem;
  font-weight: 700;
}

.navbar-menu {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.nav-link {
  color: #333;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
  padding: 0.5rem 1rem;
  border-radius: 5px;
}

.nav-link:hover {
  color: #667eea;
  background-color: #f0f0f0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #f8f9fa;
  border-radius: 20px;
  border: 1px solid #e9ecef;
}

.username {
  font-weight: 500;
  color: #333;
}

.role-badge {
  padding: 0.2rem 0.6rem;
  background-color: #6c757d;
  color: white;
  font-size: 0.75rem;
  border-radius: 12px;
  font-weight: 500;
}

.role-badge.admin {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.guest-badge {
  padding: 0.2rem 0.6rem;
  background-color: #6c757d;
  color: white;
  font-size: 0.75rem;
  border-radius: 12px;
  font-weight: 500;
}

.logout-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

/* 主内容区样式 */
.main-content {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 2rem;
}

.news-detail-container {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 加载状态 */
.loading {
  text-align: center;
  padding: 4rem;
  color: #666;
}

/* 错误状态 */
.error {
  text-align: center;
  padding: 4rem;
  color: #e74c3c;
}

.error .btn {
  margin-top: 1rem;
}

/* 空状态 */
.empty {
  text-align: center;
  padding: 4rem;
  color: #666;
}

/* 新闻详情 */
.news-detail {
  line-height: 1.6;
}

.news-title {
  color: #333;
  font-size: 1.5rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

.news-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e9ecef;
  font-size: 0.9rem;
  color: #666;
  flex-wrap: wrap;
}

.news-category {
  background-color: #667eea;
  color: white;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.news-views {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.view-icon {
  font-size: 0.8rem;
}

.news-content {
  color: #333;
  font-size: 1.1rem;
  white-space: pre-wrap;
  margin-bottom: 2rem;
  line-height: 1.8;
}

/* 互动按钮区 */
.interaction-bar {
  display: flex;
  gap: 1rem;
  padding: 1.5rem 0;
  border-top: 1px solid #e9ecef;
  border-bottom: 1px solid #e9ecef;
  margin-bottom: 1rem;
}

.interaction-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid #e9ecef;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.interaction-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
  transform: translateY(-2px);
}

.interaction-btn.active {
  background-color: #667eea;
  color: white;
  border-color: #667eea;
}

.interaction-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.interaction-btn .icon {
  font-size: 1.2rem;
}

.interaction-btn .count {
  font-weight: 600;
}

.login-tip {
  text-align: center;
  color: #666;
  font-size: 0.9rem;
}

.login-tip a {
  color: #667eea;
  text-decoration: none;
}

/* 评论区 */
.comments-section {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-top: 2rem;
}

.comments-title {
  color: #333;
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #667eea;
}

.comment-count {
  color: #666;
  font-size: 0.9rem;
  font-weight: normal;
}

/* 评论输入框 */
.comment-input-area {
  margin-bottom: 2rem;
}

.comment-textarea {
  width: 100%;
  padding: 1rem;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  resize: vertical;
  font-family: inherit;
  font-size: 1rem;
  margin-bottom: 1rem;
}

.comment-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.submit-comment-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-comment-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.submit-comment-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.comment-login-tip {
  text-align: center;
  padding: 2rem;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 2rem;
  color: #666;
}

.comment-login-tip a {
  color: #667eea;
  text-decoration: none;
}

/* 评论列表 */
.comments-list {
  min-height: 100px;
}

.comments-loading,
.no-comments {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.comment-item {
  padding: 1.5rem 0;
  border-bottom: 1px solid #e9ecef;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.comment-author {
  font-weight: 600;
  color: #667eea;
}

.comment-time {
  font-size: 0.85rem;
  color: #999;
}

.comment-content {
  color: #333;
  line-height: 1.6;
  margin-bottom: 0.5rem;
}

.comment-actions {
  display: flex;
  gap: 1rem;
}

.delete-comment-btn {
  background: none;
  border: none;
  color: #e74c3c;
  cursor: pointer;
  font-size: 0.85rem;
  padding: 0;
}

.delete-comment-btn:hover {
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-container {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .navbar-menu {
    width: 100%;
    justify-content: center;
    flex-wrap: wrap;
  }

  .main-content {
    padding: 0 1rem;
  }

  .news-detail-container,
  .comments-section {
    padding: 1.5rem;
  }

  .news-meta {
    font-size: 0.8rem;
  }

  .interaction-bar {
    flex-wrap: wrap;
  }

  .interaction-btn {
    padding: 0.4rem 0.8rem;
    font-size: 0.85rem;
  }
}
</style>
