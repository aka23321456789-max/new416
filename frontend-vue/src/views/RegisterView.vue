<template>
  <div class="register-container">
    <div class="register-form">
      <h1 class="register-title">用户注册</h1>
      <form @submit.prevent="handleRegister">
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
          <label for="confirmPassword">确认密码</label>
          <input 
            type="password" 
            id="confirmPassword" 
            v-model="form.confirmPassword" 
            placeholder="请确认密码" 
            required
          />
        </div>
        <div class="form-group">
          <label for="phone">手机号</label>
          <input 
            type="text" 
            id="phone" 
            v-model="form.phone" 
            placeholder="请输入手机号"
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
          class="register-button" 
          :disabled="auth.loading"
        >
          {{ auth.loading ? '注册中...' : '注册' }}
        </button>
        <div class="login-link">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { generateCaptcha, validateCaptcha } from '../utils/captcha'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  captcha: ''
})
const captcha = ref('')

const refreshCaptcha = () => {
  captcha.value = generateCaptcha()
}

const handleRegister = async () => {
  // 验证验证码
  if (!validateCaptcha(form.captcha, captcha.value)) {
    auth.setError('验证码错误')
    refreshCaptcha()
    return
  }
  
  // 验证密码一致性
  if (form.password !== form.confirmPassword) {
    auth.setError('两次输入的密码不一致')
    return
  }
  
  // 验证手机号格式（可选）
  if (form.phone && !/^1[3-9]\d{9}$/.test(form.phone)) {
    auth.setError('手机号格式不正确')
    return
  }
  
  try {
    await auth.register(form)
    // 注册成功后跳转到登录页
    router.push('/login')
  } catch (error) {
    console.error('Registration failed:', error)
    // 错误已在store中处理
    refreshCaptcha()
  }
}

// 初始化验证码
onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: url('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=high%20quality%20detailed%20breaking%20news%20background%20with%20clear%20headlines%20and%20modern%20news%20room%20atmosphere%20professional%20photography&image_size=landscape_16_9') no-repeat center center fixed;
  background-size: cover;
}

.register-form {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.register-title {
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

.register-button {
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

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.register-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}
</style>