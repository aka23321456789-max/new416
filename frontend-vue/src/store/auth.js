import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, registerApi } from '../api/NewsApi'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const username = ref(localStorage.getItem('username') || null)
  const role = ref(localStorage.getItem('role') || null)
  const userId = ref(localStorage.getItem('userId') || null)
  const loading = ref(false)
  const error = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  // 获取角色名称
  const roleName = computed(() => {
    if (role.value === '1') return '用户'
    if (role.value === '2') return '发布者'
    if (role.value === '3') return '管理员'
    return '未知'
  })

  const login = async (userData) => {
    loading.value = true
    error.value = null
    try {
      const response = await loginApi(userData)
      console.log('Login API response:', response)
      // 后端返回的 data 是包含 token、username、role 等信息的对象
      token.value = response.token
      username.value = response.username
      role.value = String(response.role)
      userId.value = String(response.userId || response.id)
      localStorage.setItem('token', response.token)
      localStorage.setItem('username', response.username)
      localStorage.setItem('role', String(response.role))
      localStorage.setItem('userId', String(response.userId || response.id))
      console.log('Token stored:', localStorage.getItem('token'))
      console.log('Username stored:', localStorage.getItem('username'))
      console.log('Role stored:', localStorage.getItem('role'))
      console.log('UserId stored:', localStorage.getItem('userId'))
      return response
    } catch (err) {
      console.log('Login error:', err)
      error.value = err.response?.data?.msg || err.message || '登录失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const register = async (userData) => {
    loading.value = true
    error.value = null
    try {
      console.log('Registering user:', userData)
      const response = await registerApi(userData)
      console.log('Register response:', response)
      return response
    } catch (err) {
      console.error('Register error:', err)
      error.value = err.response?.data?.msg || '注册失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    token.value = null
    username.value = null
    role.value = null
    userId.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
  }

  const setError = (message) => {
    error.value = message
  }

  return {
    token,
    username,
    role,
    userId,
    roleName,
    loading,
    error,
    isAuthenticated,
    login,
    register,
    logout,
    setError
  }
})
