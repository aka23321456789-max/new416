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
          <router-link v-if="auth.role === '3'" to="/admin/news" class="nav-link">管理新闻</router-link>
          <router-link to="/news" class="nav-link">新闻列表</router-link>
          <router-link to="/my-news" class="nav-link active">我的发布</router-link>
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
          <div v-for="news in newsList" :key="news.id" class="news-card">
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
            <div class="news-actions">
              <button class="btn btn-primary" @click="openEditModal(news)">编辑</button>
              <button class="btn btn-danger" @click="deleteNews(news.id)">删除</button>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-else class="empty">
          <p>暂无发布记录</p>
        </div>
      </div>
    </main>

    <!-- 编辑弹窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
      <div class="modal-content">
        <h3>编辑新闻</h3>
        <form @submit.prevent="submitEdit">
          <div class="form-group">
            <label for="edit-title">标题</label>
            <input 
              id="edit-title"
              v-model="editForm.title" 
              type="text" 
              placeholder="请输入新闻标题"
              required
            />
          </div>
          <div class="form-group">
            <label for="edit-category">分类</label>
            <select id="edit-category" v-model="editForm.category" required>
              <option value="">请选择分类</option>
              <option value="政法新闻">政法新闻</option>
              <option value="经济新闻">经济新闻</option>
              <option value="体育新闻">体育新闻</option>
              <option value="社会新闻">社会新闻</option>
              <option value="国际新闻">国际新闻</option>
            </select>
          </div>
          <div class="form-group">
            <label for="edit-content">内容</label>
            <textarea 
              id="edit-content"
              v-model="editForm.content" 
              rows="6" 
              placeholder="请输入新闻内容"
              required
            ></textarea>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
              {{ isSubmitting ? '保存中...' : '保存' }}
            </button>
            <button type="button" class="btn btn-secondary" @click="closeEditModal">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { getMyNewsApi, updateNewsApi, deleteNewsApi } from '../api/NewsApi'

const router = useRouter()
const auth = useAuthStore()

// 状态
const newsList = ref([])
const loading = ref(true)
const error = ref(null)
const showEditModal = ref(false)
const isSubmitting = ref(false)
const editForm = ref({
  id: null,
  title: '',
  content: '',
  category: ''
})

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

// 打开编辑弹窗
const openEditModal = (news) => {
  editForm.value = {
    id: news.id,
    title: news.title,
    content: news.content,
    category: news.category
  }
  showEditModal.value = true
}

// 关闭编辑弹窗
const closeEditModal = () => {
  showEditModal.value = false
  editForm.value = {
    id: null,
    title: '',
    content: '',
    category: ''
  }
}

// 提交编辑
const submitEdit = async () => {
  if (!editForm.value.title.trim() || !editForm.value.content.trim() || !editForm.value.category) {
    alert('请填写完整信息')
    return
  }

  isSubmitting.value = true
  try {
    const response = await updateNewsApi(editForm.value.id, {
      title: editForm.value.title.trim(),
      content: editForm.value.content.trim(),
      category: editForm.value.category
    })
    alert(response.data || '编辑成功，请等待管理员审核')
    closeEditModal()
    fetchMyNews()
  } catch (err) {
    alert(err.message || '编辑失败')
  } finally {
    isSubmitting.value = false
  }
}

// 删除新闻
const deleteNews = async (newsId) => {
  if (!confirm('确定要删除这条新闻吗？\n删除后将同时删除相关的点赞、收藏和评论。')) {
    return
  }
  
  try {
    await deleteNewsApi(newsId)
    newsList.value = newsList.value.filter(news => news.id !== newsId)
    alert('删除成功')
  } catch (err) {
    alert(err.message || '删除失败')
  }
}

// 退出登录
const handleLogout = () => {
  auth.logout()
  router.push('/')
}

// 页面加载时获取我的发布记录
onMounted(() => {
  fetchMyNews()
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
  margin-bottom: 1rem;
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

.news-actions {
  display: flex;
  gap: 0.5rem;
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

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #5a6fd6;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #5a6268;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
  font-family: inherit;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 150px;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1.5rem;
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
