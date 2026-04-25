<template>
  <div class="add-news-container">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-logo">
          <h1>新闻系统</h1>
        </div>
        <div class="navbar-menu">
          <router-link to="/" class="nav-link">首页</router-link>
          <!-- 发布者和管理员可以上传新闻 -->
          <router-link v-if="auth.role === '2' || auth.role === '3'" to="/upload" class="nav-link active">上传新闻</router-link>
          <!-- 管理员可以管理新闻 -->
          <router-link v-if="auth.role === '3'" to="/admin/news" class="nav-link">管理新闻</router-link>
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
      <div class="form-container">
        <h2>发布新闻</h2>
        <form @submit.prevent="submitNews" class="news-form">
          <!-- 新闻标题 -->
          <div class="form-group">
            <label for="title">新闻标题 <span class="required">*</span></label>
            <input
              type="text"
              id="title"
              v-model="formData.title"
              placeholder="请输入新闻标题"
              required
            />
          </div>

          <!-- 新闻分类 -->
          <div class="form-group">
            <label for="category">新闻分类 <span class="required">*</span></label>
            <select id="category" v-model="formData.category" required>
              <option value="">请选择分类</option>
              <option value="政法新闻">政法新闻</option>
              <option value="经济新闻">经济新闻</option>
              <option value="体育新闻">体育新闻</option>
              <option value="社会新闻">社会新闻</option>
              <option value="国际新闻">国际新闻</option>
            </select>
          </div>

          <!-- 新闻内容 -->
          <div class="form-group">
            <label for="content">新闻内容 <span class="required">*</span></label>
            <textarea
              id="content"
              v-model="formData.content"
              placeholder="请输入新闻内容"
              rows="10"
              required
            ></textarea>
          </div>

          <!-- 按钮组 -->
          <div class="form-actions">
            <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
              {{ isSubmitting ? '发布中...' : '发布新闻' }}
            </button>
            <button type="button" class="btn btn-secondary" @click="resetForm">
              重置
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { addNewsApi } from '../api/NewsApi'

const router = useRouter()
const auth = useAuthStore()

// 表单数据
const formData = ref({
  title: '',
  content: '',
  category: ''
})

// 提交状态
const isSubmitting = ref(false)

// 提交新闻
const submitNews = async () => {
  // 表单验证
  if (!formData.value.title.trim()) {
    alert('请输入新闻标题')
    return
  }
  if (!formData.value.category) {
    alert('请选择新闻分类')
    return
  }
  if (!formData.value.content.trim()) {
    alert('请输入新闻内容')
    return
  }

  isSubmitting.value = true

  try {
    const newsData = {
      title: formData.value.title.trim(),
      content: formData.value.content.trim(),
      category: formData.value.category
      // authorId 由后端从登录用户中获取
    }

    const response = await addNewsApi(newsData)
    if (response.code === '200') {
      alert('新闻发布成功！')
      resetForm()
      // 可选：发布成功后跳转到新闻列表页
      // router.push('/news')
    } else {
      alert('发布失败：' + response.msg)
    }
  } catch (error) {
    console.error('发布新闻失败:', error)
    alert('发布失败：' + (error.message || '请稍后重试'))
  } finally {
    isSubmitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  formData.value = {
    title: '',
    content: '',
    category: ''
  }
}

// 退出登录
const handleLogout = () => {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.add-news-container {
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

/* 主内容区样式 */
.main-content {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 2rem;
}

.form-container {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-container h2 {
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
  font-size: 1.5rem;
}

.news-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 500;
  color: #333;
  font-size: 0.95rem;
}

.required {
  color: #e74c3c;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
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
  min-height: 200px;
  font-family: inherit;
  line-height: 1.6;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1rem;
}

.btn {
  padding: 0.75rem 2rem;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f1f1f1;
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background: #e0e0e0;
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

  .form-container {
    padding: 1.5rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>
