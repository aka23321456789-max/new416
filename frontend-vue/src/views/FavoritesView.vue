<template>
  <div class="favorites-page">
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
            <router-link v-if="auth.role === '3'" to="/admin/news" class="nav-link">管理新闻</router-link>
            <router-link to="/my-news" class="nav-link">我的发布</router-link>
            <router-link to="/favorites" class="nav-link active">我的收藏</router-link>
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
      <div class="favorites-container">
        <h2>我的收藏</h2>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">
          <p>加载中...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error">
          <p>{{ error }}</p>
          <button class="btn btn-secondary" @click="fetchFavorites">重试</button>
        </div>
        
        <!-- 收藏列表 -->
        <div v-else-if="favorites.length > 0" class="favorites-list">
          <div 
            v-for="news in favorites" 
            :key="news.id" 
            class="favorite-item"
            @click="viewNewsDetail(news.id)"
          >
            <div class="favorite-content">
              <h3 class="favorite-title">{{ news.title }}</h3>
              <div class="favorite-meta">
                <span class="favorite-category">{{ news.category }}</span>
                <span class="favorite-author">作者: {{ news.authorName || news.authorId }}</span>
                <span class="favorite-time" v-if="news.createTime">{{ formatTime(news.createTime) }}</span>
              </div>
              <p class="favorite-summary">{{ news.content.substring(0, 100) }}...</p>
              <div class="favorite-stats">
                <span class="stat-item">
                  <i class="stat-icon">👁</i> {{ news.viewCount || 0 }}
                </span>
                <span class="stat-item">
                  <i class="stat-icon">❤️</i> {{ news.likeCount || 0 }}
                </span>
                <span class="stat-item">
                  <i class="stat-icon">💬</i> {{ news.commentCount || 0 }}
                </span>
              </div>
            </div>
            <button 
              class="remove-favorite-btn"
              @click.stop="removeFavorite(news.id)"
              title="取消收藏"
            >
              ✕
            </button>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-else class="empty">
          <p>您还没有收藏任何新闻</p>
          <router-link to="/news" class="btn btn-primary">去浏览新闻</router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { getMyFavoritesApi, removeFavoriteApi } from '../api/NewsApi'

const router = useRouter()
const auth = useAuthStore()

// 状态
const favorites = ref([])
const loading = ref(true)
const error = ref(null)

// 获取收藏列表
const fetchFavorites = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await getMyFavoritesApi()
    favorites.value = response || []
  } catch (err) {
    error.value = err.message || '获取收藏列表失败'
  } finally {
    loading.value = false
  }
}

// 查看新闻详情
const viewNewsDetail = (newsId) => {
  router.push(`/news/${newsId}`)
}

// 取消收藏
const removeFavorite = async (newsId) => {
  if (!confirm('确定要取消收藏这条新闻吗？')) return
  
  try {
    await removeFavoriteApi(newsId)
    // 从列表中移除
    favorites.value = favorites.value.filter(news => news.id !== newsId)
  } catch (err) {
    alert(err.message || '取消收藏失败')
  }
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
  router.push('/')
}

// 页面加载时获取收藏列表
onMounted(() => {
  if (!auth.isAuthenticated) {
    router.push('/login')
    return
  }
  fetchFavorites()
})
</script>

<style scoped>
.favorites-page {
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

.nav-link:hover,
.nav-link.active {
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
  max-width: 1000px;
  margin: 2rem auto;
  padding: 0 2rem;
}

.favorites-container {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.favorites-container h2 {
  color: #333;
  margin-bottom: 2rem;
  font-size: 1.8rem;
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

.empty .btn {
  margin-top: 1rem;
  display: inline-block;
  text-decoration: none;
}

/* 收藏列表 */
.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.favorite-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 1.5rem;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.favorite-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.favorite-content {
  flex: 1;
}

.favorite-title {
  color: #333;
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.favorite-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 0.75rem;
  font-size: 0.85rem;
  color: #666;
  flex-wrap: wrap;
}

.favorite-category {
  background-color: #667eea;
  color: white;
  padding: 0.15rem 0.5rem;
  border-radius: 10px;
  font-size: 0.7rem;
  font-weight: 500;
}

.favorite-summary {
  color: #666;
  font-size: 0.95rem;
  line-height: 1.5;
  margin-bottom: 0.75rem;
}

.favorite-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #999;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.stat-icon {
  font-size: 0.9rem;
}

.remove-favorite-btn {
  background: none;
  border: none;
  color: #e74c3c;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
  transition: all 0.3s ease;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-favorite-btn:hover {
  background-color: #ffebee;
}

/* 按钮样式 */
.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
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

  .favorites-container {
    padding: 1.5rem;
  }

  .favorite-item {
    padding: 1rem;
  }

  .favorite-title {
    font-size: 1.1rem;
  }

  .favorite-meta {
    font-size: 0.8rem;
  }
}
</style>
