<template>
  <div class="my-news-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-logo">
          <h1>新闻系统</h1>
        </div>
        <div class="navbar-menu">
          <router-link to="/" class="nav-link">首页</router-link>
          <!-- 发布者和管理员可以上传新闻 -->
          <router-link v-if="auth.role === '2' || auth.role === '3'" to="/upload" class="nav-link">上传新闻</router-link>
          <!-- 管理员可以管理新闻 -->
          <router-link v-if="auth.role === '3'" to="/admin/news" class="nav-link">管理新闻</router-link>
          <router-link to="/news" class="nav-link">新闻列表</router-link>
          <!-- 发布者和管理员可以查看自己的发布记录 -->
          <router-link v-if="auth.role === '2' || auth.role === '3'" to="/my-news" class="nav-link active">我的发布</router-link>
          <router-link to="/favorites" class="nav-link">我的收藏</router-link>
          <router-link to="/profile" class="nav-link">个人中心</router-link>
          <div class="user-info">
            <span class="username">{{ auth.username || '未登录' }}</span>
            <span class="role-badge" :class="{ 'admin': auth.role === '3' }">
              {{ auth.role === '1' ? '用户' : auth.role === '2' ? '发布者' : auth.role === '3' ? '管理员' : '未登录' }}
            </span>
          </div>
          <button class="logout-button" @click="handleLogout">退出登录</button>
        </div>
      </div>
    </nav>

    <!-- 主内容区 -->
    <main class="main-content">
      <div class="my-news-container">
        <h2>我的发布记录</h2>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">
          <p>加载中...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error">
          <p>{{ error }}</p>
          <button class="btn btn-secondary" @click="fetchMyNews">重试</button>
        </div>
        
        <!-- 新闻列表 -->
        <div v-else-if="newsList.length > 0" class="news-grid">
          <div v-for="news in newsList" :key="news.id" class="news-card" @click="viewNewsDetail(news.id)">
            <div class="news-card-header">
              <h3 class="news-title">{{ news.title }}</h3>
              <span class="news-category">{{ news.category }}</span>
            </div>
            <div class="news-content">
              <p>{{ news.content.substring(0, 100) }}...</p>
            </div>
            <div class="news-card-footer">
              <span class="news-status" :class="getStatusClass(news.status)">
                {{ getStatusText(news.status) }}
              </span>
              <span class="news-author">作者: {{ news.authorName || news.authorId }}</span>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-else class="empty">
          <p>暂无发布记录</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { getMyNewsApi } from '../api/NewsApi'

const router = useRouter()
const auth = useAuthStore()

// 状态
const newsList = ref([])
const loading = ref(true)
const error = ref(null)

// 获取我的发布记录
const fetchMyNews = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await getMyNewsApi()
    newsList.value = response
  } catch (err) {
    error.value = err.message || '获取发布记录失败'
  } finally {
    loading.value = false
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0: return '审核中'
    case 1: return '已通过'
    case 2: return '已拒绝'
    default: return '未知'
  }
}

// 获取状态样式
const getStatusClass = (status) => {
  switch (status) {
    case 0: return 'status-pending'
    case 1: return 'status-approved'
    case 2: return 'status-rejected'
    default: return ''
  }
}

// 查看新闻详情
const viewNewsDetail = (newsId) => {
  router.push(`/news/${newsId}`)
}

// 退出登录
const handleLogout = () => {
  auth.logout()
  router.push('/login')
}

// 页面加载时获取我的发布记录
onMounted(() => {
  // 检查权限：只有发布者和管理员可以访问此页面
  if (auth.role !== '2' && auth.role !== '3') {
    alert('您没有权限访问此页面')
    router.push('/')
  } else {
    fetchMyNews()
  }
})
</script>

<style scoped>
.my-news-page {
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

.my-news-container {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.my-news-container h2 {
  color: #333;
  margin-bottom: 2rem;
  text-align: center;
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

/* 新闻网格 */
.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

/* 新闻卡片 */
.news-card {
  background: #f9f9f9;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1.5rem;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.news-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.news-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.news-title {
  color: #333;
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
  flex: 1;
}

.news-category {
  background-color: #667eea;
  color: white;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.news-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.news-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.875rem;
  color: #666;
}

.news-status {
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-weight: 500;
}

.status-pending {
  background-color: #ffc107;
  color: #333;
}

.status-approved {
  background-color: #28a745;
  color: white;
}

.status-rejected {
  background-color: #dc3545;
  color: white;
}

.news-author {
  font-style: italic;
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

  .my-news-container {
    padding: 1.5rem;
  }

  .news-grid {
    grid-template-columns: 1fr;
  }
}
</style>