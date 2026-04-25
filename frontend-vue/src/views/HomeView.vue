<template>
  <div class="home-container">
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
            <!-- 发布者和管理员可以上传新闻 -->
            <router-link v-if="auth.role === '2' || auth.role === '3'" to="/upload" class="nav-link">上传新闻</router-link>
            <!-- 管理员可以管理新闻 -->
            <router-link v-if="auth.role === '3'" to="/admin/news" class="nav-link">管理新闻</router-link>
            <!-- 发布者和管理员可以查看自己的发布记录 -->
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
      <div class="welcome-section">
        <h2>欢迎使用新闻系统</h2>
        <p v-if="auth.isAuthenticated">您已成功登录，可以开始使用系统的各项功能。</p>
        <p v-else>您当前处于游客模式，可以浏览新闻内容。</p>
      </div>

      <div class="features-section">
        <!-- 发布者和管理员显示新闻管理卡片 -->
        <div v-if="auth.isAuthenticated && (auth.role === '2' || auth.role === '3')" class="feature-card">
          <h3>新闻管理</h3>
          <p>上传、编辑和管理新闻内容</p>
          <router-link to="/upload" class="feature-button">上传新闻</router-link>
        </div>
        <!-- 管理员显示新闻审核卡片 -->
        <div v-if="auth.isAuthenticated && auth.role === '3'" class="feature-card">
          <h3>新闻审核</h3>
          <p>审核和管理所有新闻内容</p>
          <router-link to="/admin/news" class="feature-button">管理新闻</router-link>
        </div>
        <!-- 登录用户显示个人中心卡片 -->
        <div v-if="auth.isAuthenticated" class="feature-card">
          <h3>个人中心</h3>
          <p>修改个人信息和密码</p>
          <router-link to="/profile" class="feature-button">个人设置</router-link>
        </div>
        <!-- 所有人显示新闻列表卡片 -->
        <div class="feature-card">
          <h3>新闻列表</h3>
          <p>查看所有新闻内容</p>
          <router-link to="/news" class="feature-button">浏览新闻</router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const router = useRouter()
const auth = useAuthStore()

const handleLogout = () => {
  auth.logout()
  router.push('/login')
}
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

.main-content {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 2rem;
}

.welcome-section {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
  text-align: center;
}

.welcome-section h2 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.8rem;
}

.welcome-section p {
  color: #666;
  font-size: 1.1rem;
}

.features-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.feature-card h3 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.3rem;
}

.feature-card p {
  color: #666;
  margin-bottom: 1.5rem;
}

.feature-button {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 5px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.feature-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .navbar-container {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .navbar-menu {
    width: 100%;
    justify-content: center;
  }

  .features-section {
    grid-template-columns: 1fr;
  }
}
</style>