<template>
  <div class="login-container">
    <div class="login-form">
      <h1 class="login-title">用户登录</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="form.username" 
            placeholder="请输入用户名" 
            required
          />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="form.password" 
            placeholder="请输入密码" 
            required
          />
        </div>
        <div class="form-group">
          <label for="captcha">验证码</label>
          <div class="captcha-container">
            <input 
              type="text" 
              id="captcha" 
              v-model="form.captcha" 
              placeholder="请输入验证码" 
              required
              maxlength="5"
            />
            <div class="captcha-code" @click="refreshCaptcha">
              {{ captcha }}
            </div>
          </div>
        </div>
        <div v-if="auth.error" class="error-message">
          {{ auth.error }}
        </div>
        <button 
          type="submit" 
          class="login-button" 
          :disabled="auth.loading"
        >
          {{ auth.loading ? '登录中...' : '登录' }}
        </button>
        <div class="register-link">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { loginApi } from '../api/NewsApi'
import { generateCaptcha, validateCaptcha } from '../utils/captcha'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({
  username: '',
  password: '',
  captcha: ''
})
const captcha = ref('')

const refreshCaptcha = () => {
  captcha.value = generateCaptcha()
}

const handleLogin = async () => {
  console.log('开始登录流程')
  console.log('表单数据:', form)
  
  // 验证验证码
  if (!validateCaptcha(form.captcha, captcha.value)) {
    auth.setError('验证码错误')
    refreshCaptcha()
    return
  }
  
  try {
    console.log('调用auth.login方法')
    // 使用auth store的login方法
    await auth.login({
      username: form.username,
      password: form.password
    })
    console.log('登录成功，准备跳转到首页')
    router.push('/')
  } catch (error) {
    console.log('登录失败，错误:', error)
    refreshCaptcha()
  }
}

// 初始化验证码
onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: url('https://images.unsplash.com/photo-1504711434969-e33886168f5c?q=80&w=2070&auto=format&fit=crop') no-repeat center center fixed;
  background-size: cover;
}

.login-container::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  z-index: 0;
}

.login-form {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 400px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.captcha-container {
  display: flex;
  gap: 10px;
}

.captcha-code {
  min-width: 110px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 1px solid #667eea;
  border-radius: 8px;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 2px;
  color: white;
  cursor: pointer;
  user-select: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.captcha-code:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  border-color: #764ba2;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 20px;
  font-size: 14px;
}

.login-button {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}
</style>