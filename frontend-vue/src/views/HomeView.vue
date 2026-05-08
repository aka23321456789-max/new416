<template>
  <div class="home-container">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-logo">
          <h1>新闻系统</h1>
        </div>
        <div class="navbar-menu">
          <router-link to="/" class="nav-link active">首页</router-link>
          <router-link to="/news" class="nav-link">新闻列表</router-link>
          <template v-if="auth.isAuthenticated">
            <!-- 管理员可以管理新闻 -->
            <router-link v-if="auth.role === '3'" to="/admin/news" class="nav-link">管理新闻</router-link>
            <router-link to="/my-news" class="nav-link">我的发布</router-link>
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

    <!-- 主内容区 - 新闻列表 -->
    <main class="main-content">
      <div class="news-list-container">
        <h2>新闻列表</h2>
        
        <!-- 搜索和筛选区域 -->
        <div class="filter-section">
          <!-- 搜索框 -->
          <div class="search-box">
            <input 
              v-model="searchKeyword" 
              type="text" 
              placeholder="搜索新闻标题或内容..."
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">🔍 搜索</button>
            <button v-if="isSearching" class="clear-btn" @click="clearSearch">清除</button>
          </div>
          
          <!-- 分类筛选 -->
          <div class="category-filter">
            <span class="filter-label">分类筛选：</span>
            <button 
              v-for="cat in categories" 
              :key="cat"
              :class="['category-btn', { 'active': selectedCategory === cat }]"
              @click="handleCategoryFilter(cat)"
            >
              {{ cat }}
            </button>
            <button 
              v-if="selectedCategory"
              class="clear-category-btn"
              @click="clearCategory"
            >
              全部
            </button>
          </div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">
          <p>加载中...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error">
          <p>{{ error }}</p>
          <button class="btn btn-secondary" @click="fetchNewsList">重试</button>
        </div>
        
        <!-- 新闻列表 -->
        <div v-else-if="newsList.length > 0" class="news-grid">
          <div v-for="news in newsList" :key="news.id" class="news-card" :style="getNewsCardStyle(news)">
            <div class="news-card-overlay"></div>
            <div class="news-card-content" @click="viewNewsDetail(news.id)">
              <div class="news-card-header">
                <h3 class="news-title">{{ news.title }}</h3>
                <span class="news-category">{{ news.category }}</span>
              </div>
              <div class="news-content">
                <p>{{ news.content.substring(0, 100) }}...</p>
              </div>
              <div class="news-card-footer">
                <div class="news-meta">
                  <span class="news-author">作者: {{ news.authorName || news.authorId }}</span>
                  <span class="news-time" v-if="news.createTime">{{ formatTime(news.createTime) }}</span>
                </div>
                <div class="news-stats">
                  <span class="stat-item" title="阅读量">
                    <i class="stat-icon">👁</i> {{ news.viewCount || 0 }}
                  </span>
                  <span class="stat-item" title="点赞数">
                    <i class="stat-icon">❤️</i> {{ news.likeCount || 0 }}
                  </span>
                  <span class="stat-item" title="收藏数">
                    <i class="stat-icon">⭐</i> {{ news.favoriteCount || 0 }}
                  </span>
                  <span class="stat-item" title="评论数">
                    <i class="stat-icon">💬</i> {{ news.commentCount || 0 }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-else class="empty">
          <p>{{ emptyMessage }}</p>
        </div>
        
        <!-- 分页组件 -->
        <div v-if="newsList.length > 0" class="pagination">
          <button 
            :disabled="currentPage === 1" 
            @click="changePage(currentPage - 1)"
            class="page-btn"
          >
            上一页
          </button>
          
          <span class="page-info">
            第 {{ currentPage }} / {{ totalPages }} 页 (共 {{ total }} 条)
          </span>
          
          <button 
            :disabled="currentPage === totalPages" 
            @click="changePage(currentPage + 1)"
            class="page-btn"
          >
            下一页
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { 
  getNewsListApi, 
  searchNewsApi, 
  getNewsByCategoryApi
} from '../api/NewsApi'

const router = useRouter()
const auth = useAuthStore()

// 状态
const newsList = ref([])
const loading = ref(true)
const error = ref(null)
const searchKeyword = ref('')
const selectedCategory = ref('')
const isSearching = ref(false)

// 分页状态
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)

// 分类列表
const categories = ['政法新闻', '经济新闻', '体育新闻', '社会新闻', '国际新闻']

// 空状态提示信息
const emptyMessage = computed(() => {
  if (isSearching.value) return '未找到匹配的新闻'
  if (selectedCategory.value) return `暂无"${selectedCategory.value}"分类的新闻`
  return '暂无新闻'
})

// 获取新闻卡片背景样式
const getNewsCardStyle = (news) => {
  if (news.imageUrl) {
    return {
      backgroundImage: `url(${news.imageUrl})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center'
    }
  }
  return {}
}

// 获取新闻列表（size由后端决定）
const fetchNewsList = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await getNewsListApi(currentPage.value)
    newsList.value = response.records
    total.value = response.total
    totalPages.value = response.pages
    // 使用后端返回的实际每页条数
    pageSize.value = response.size
  } catch (err) {
    error.value = err.message || '获取新闻列表失败'
  } finally {
    loading.value = false
  }
}

// 搜索新闻
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    fetchNewsList()
    return
  }
  
  loading.value = true
  error.value = null
  isSearching.value = true
  selectedCategory.value = ''
  
  try {
    const response = await searchNewsApi(searchKeyword.value.trim())
    newsList.value = response
  } catch (err) {
    error.value = err.message || '搜索失败'
  } finally {
    loading.value = false
  }
}

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = ''
  isSearching.value = false
  fetchNewsList()
}

// 分类筛选
const handleCategoryFilter = async (category) => {
  if (selectedCategory.value === category) return
  
  loading.value = true
  error.value = null
  selectedCategory.value = category
  searchKeyword.value = ''
  isSearching.value = false
  
  try {
    const response = await getNewsByCategoryApi(category)
    newsList.value = response
  } catch (err) {
    error.value = err.message || '获取分类新闻失败'
  } finally {
    loading.value = false
  }
}

// 清除分类筛选
const clearCategory = () => {
  selectedCategory.value = ''
  fetchNewsList()
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

// 查看新闻详情
const viewNewsDetail = (newsId) => {
  router.push(`/news/${newsId}`)
}

// 切换页面
const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  fetchNewsList()
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 页面加载时获取新闻列表
onMounted(() => {
  fetchNewsList()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

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

.main-content {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 2rem;
}

.news-list-container {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.news-list-container h2 {
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
  font-size: 1.8rem;
}

/* 搜索和筛选区域 */
.filter-section {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

/* 搜索框 */
.search-box {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.search-box input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 1px solid #e9ecef;
  border-radius: 5px;
  font-size: 1rem;
}

.search-box input:focus {
  outline: none;
  border-color: #667eea;
}

.search-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 500;
}

.clear-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 0.75rem 1rem;
  border-radius: 5px;
  cursor: pointer;
}

/* 分类筛选 */
.category-filter {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-label {
  color: #666;
  font-weight: 500;
}

.category-btn {
  padding: 0.4rem 0.8rem;
  border: 1px solid #e9ecef;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.category-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.category-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
}

.clear-category-btn {
  padding: 0.4rem 0.8rem;
  border: 1px solid #e74c3c;
  background: white;
  color: #e74c3c;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.clear-category-btn:hover {
  background: #e74c3c;
  color: white;
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

/* 新闻卡片 - 带背景图片 */
.news-card {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  min-height: 250px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.news-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

/* 背景图片遮罩层 */
.news-card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.4) 0%, rgba(0,0,0,0.7) 100%);
  z-index: 1;
}

/* 默认背景（无图片时） */
.news-card:not([style*="background-image"]) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.news-card:not([style*="background-image"]) .news-card-overlay {
  background: linear-gradient(to bottom, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.5) 100%);
}

.news-card-content {
  position: relative;
  z-index: 2;
  padding: 1.5rem;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: white;
}

.news-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.news-title {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
  flex: 1;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
}

.news-category {
  background-color: rgba(102, 126, 234, 0.9);
  color: white;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  backdrop-filter: blur(4px);
}

.news-content {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
  margin-bottom: 1rem;
  flex: 1;
}

.news-content p {
  margin: 0;
}

.news-card-footer {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.news-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: rgba(255, 255, 255, 0.8);
  flex-wrap: wrap;
}

.news-author {
  font-style: italic;
}

.news-time {
  color: rgba(255, 255, 255, 0.7);
}

.news-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.9);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.stat-icon {
  font-size: 0.9rem;
}

/* 分页组件 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #e9ecef;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #667eea;
  background: white;
  color: #667eea;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  border-color: #ccc;
  color: #999;
}

.page-info {
  color: #666;
  font-size: 0.9rem;
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

  .news-list-container {
    padding: 1.5rem;
  }

  .news-grid {
    grid-template-columns: 1fr;
  }

  .search-box {
    flex-direction: column;
  }

  .category-filter {
    justify-content: center;
  }
}
</style>
