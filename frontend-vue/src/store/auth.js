import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, registerApi } from '../api/NewsApi'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const username = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  const login = async (userData) => {
    loading.value = true
    error.value = null
    try {
      const response = await loginApi(userData)
      token.value = response.data
      localStorage.setItem('token', response.data)
      return response
    } catch (err) {
      error.value = err.response?.data?.msg || '登录失败'
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
    localStorage.removeItem('token')
  }

  const setError = (message) => {
    error.value = message
  }

  return {
    token,
    username,
    loading,
    error,
    isAuthenticated,
    login,
    register,
    logout,
    setError
  }
})