import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    console.log('API Response:', response.data)
    return response.data
  },
  error => {
    console.error('API Error:', error)
    if (error.response) {
      console.error('Error Response Data:', error.response.data)
      console.error('Error Status:', error.response.status)
    }
    return Promise.reject(error)
  }
)

// 登录API
export const loginApi = (userData) => {
  return api.post('/user/login', userData)
}

// 注册API
export const registerApi = (userData) => {
  return api.post('/user/add', userData)
}

export default api