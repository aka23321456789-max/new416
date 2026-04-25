<template>
  <div class="profile-page">
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
          <router-link to="/favorites" class="nav-link">我的收藏</router-link>
          <router-link to="/profile" class="nav-link active">个人中心</router-link>
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
      <div class="profile-container">
        <h2>个人信息修改</h2>
    <form @submit.prevent="updateProfile" class="profile-form">
      <div class="form-group">
        <label for="username">用户名</label>
        <input 
          type="text" 
          id="username" 
          v-model="formData.username" 
          placeholder="请输入用户名"
        >
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="formData.password" 
          placeholder="请输入密码（不修改请留空）"
        >
      </div>
      <div class="form-group">
        <label for="phone">手机号</label>
        <input 
          type="tel" 
          id="phone" 
          v-model="formData.phone" 
          placeholder="请输入手机号"
        >
      </div>
      <div class="form-actions">
        <button type="submit" class="btn btn-primary">保存修改</button>
        <button type="button" class="btn btn-secondary" @click="resetForm">重置</button>
      </div>
    </form>
      </div>
    </main>
  </div>
</template>

<script>
import { updateUserApi } from '../api/NewsApi'
import { useAuthStore } from '../store/auth'
import { useRouter } from 'vue-router'

export default {
  name: 'ProfileView',
  setup() {
    const auth = useAuthStore()
    const router = useRouter()

    const handleLogout = () => {
      auth.logout()
      router.push('/login')
    }

    return {
      auth,
      handleLogout
    }
  },
  data() {
    return {
      formData: {
        username: '',
        password: '',
        phone: ''
      }
    }
  },
  mounted() {
    // 从状态管理中获取当前用户信息
    const username = localStorage.getItem('username')
    if (username) {
      this.formData.username = username
    }
  },
  methods: {
    async updateProfile() {
      try {
        // 过滤掉空值，只发送有修改的字段
        const updateData = {}
        if (this.formData.username) {
          updateData.username = this.formData.username
        }
        if (this.formData.password) {
          updateData.password = this.formData.password
        }
        if (this.formData.phone) {
          updateData.phone = this.formData.phone
        }

        const response = await updateUserApi(updateData)
        if (response.code === '200') {
          alert('个人信息修改成功！')
          // 更新本地存储中的用户名
          if (this.formData.username) {
            localStorage.setItem('username', this.formData.username)
          }
        } else {
          alert('修改失败：' + response.msg)
        }
      } catch (error) {
        console.error('修改个人信息失败:', error)
        alert('修改失败，请稍后重试')
      }
    },
    resetForm() {
      this.formData = {
        username: localStorage.getItem('username') || '',
        password: '',
        phone: ''
      }
    }
  }
}
</script>

<style scoped>
.profile-page {
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

.profile-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

label {
  font-weight: 500;
  color: #555;
}

input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 10px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-secondary {
  background-color: #f1f1f1;
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}
</style>