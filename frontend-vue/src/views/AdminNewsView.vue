<template>
  <div class="admin-news-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-logo">
          <h1>新闻系统</h1>
        </div>
        <div class="navbar-menu">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/admin/news" class="nav-link active">管理新闻</router-link>
          <router-link to="/my-news" class="nav-link">我的发布</router-link>
          <router-link to="/favorites" class="nav-link">我的收藏</router-link>
          <router-link to="/profile" class="nav-link">个人中心</router-link>
          <div class="user-info">
            <span class="username">{{ auth.username || '未登录' }}</span>
            <span class="role-badge admin">管理员</span>
          </div>
          <button class="logout-button" @click="handleLogout">退出登录</button>
        </div>
      </div>
    </nav>

    <!-- 主内容区 -->
    <main class="main-content">
      <div class="admin-news-container">
        <h2>新闻管理</h2>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">
          <p>加载中...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error">
          <p>{{ error }}</p>
          <button class="btn btn-secondary" @click="fetchPendingNews">重试</button>
        </div>
        
        <!-- 待审核新闻列表 -->
        <div v-else-if="pendingNews.length > 0" class="news-list">
          <h3>待审核新闻</h3>
          <div class="news-items">
            <div v-for="news in pendingNews" :key="news.id" class="news-item">
              <div class="news-header">
                <div class="news-info">
                  <h4 class="news-title">{{ news.title }}</h4>
                  <div class="news-meta">
                    <span class="news-id">ID: {{ news.id }}</span>
                    <span class="news-category">{{ news.category }}</span>
                    <span class="news-author">作者ID: {{ news.authorId }}</span>
                    <span class="news-time" v-if="news.createTime">{{ formatTime(news.createTime) }}</span>
                  </div>
                </div>
                <div class="news-actions">
                  <button 
                    class="btn btn-success" 
                    @click="reviewNews(news.id, 1)"
                    :disabled="reviewing === news.id"
                  >
                    {{ reviewing === news.id ? '处理中...' : '通过' }}
                  </button>
                  <button 
                    class="btn btn-danger" 
                    @click="reviewNews(news.id, 2)"
                    :disabled="reviewing === news.id"
                  >
                    {{ reviewing === news.id ? '处理中...' : '拒绝' }}
                  </button>
                </div>
              </div>
              <div class="news-content">
                <p>{{ news.content }}</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-else class="empty">
          <p>暂无待审核新闻</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { getPendingNewsApi, reviewNewsApi } from '../api/NewsApi'

const router = useRouter()
const auth = useAuthStore()

// 状态
const pendingNews = ref([])
const loading = ref(true)
const error = ref(null)
const reviewing = ref(null) // 当前正在审核的新闻ID

// 获取待审核新闻列表
const fetchPendingNews = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await getPendingNewsApi()
    pendingNews.value = response
  } catch (err) {
    error.value = err.message || '获取待审核新闻失败'
  } finally {
    loading.value = false
  }
}

// 审核新闻
const reviewNews = async (id, status) => {
  reviewing.value = id
  try {
    const response = await reviewNewsApi(id, status)
    alert(response.data)
    // 重新获取待审核列表
    fetchPendingNews()
  } catch (err) {
    alert('审核失败：' + (err.message || '请稍后重试'))
  } finally {
    reviewing.value = null
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

// 页面加载时获取待审核新闻
onMounted(() => {
  // 检查权限：只有管理员可以访问此页面
  if (auth.role !== '3') {
    alert('您没有权限访问此页面')
    router.push('/')
  } else {
    fetchPendingNews()
  }
})
</script>

<style scoped>
.admin-news-page {
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
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 2rem;
}

.admin-news-container {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.admin-news-container h2 {
  color: #333;
  margin-bottom: 2rem;
  text-align: center;
  font-size: 1.8rem;
}

.admin-news-container h3 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.3rem;
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

/* 新闻列表 */
.news-items {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.news-item {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1.5rem;
  background-color: #f8f9fa;
}

.news-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e9ecef;
}

.news-info {
  flex: 1;
}

.news-title {
  color: #333;
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
}

.news-meta {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  font-size: 0.85rem;
  color: #666;
}

.news-id {
  background-color: #e9ecef;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
}

.news-category {
  background-color: #667eea;
  color: white;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
}

.news-author {
  font-style: italic;
}

.news-time {
  color: #999;
}

.news-actions {
  display: flex;
  gap: 0.5rem;
}

.news-content {
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
  background-color: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.news-content p {
  margin: 0;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background-color: #218838;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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

  .admin-news-container {
    padding: 1.5rem;
  }

  .news-header {
    flex-direction: column;
    gap: 1rem;
  }

  .news-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .news-meta {
    gap: 0.5rem;
  }
}
</style>
