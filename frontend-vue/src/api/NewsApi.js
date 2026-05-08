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
      config.headers['Authorization'] = token
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
    // 检查响应code
    if (response.data.code !== 200 && response.data.code !== '200') {
      const error = new Error(response.data.msg || '请求失败')
      error.response = response
      return Promise.reject(error)
    }
    // 检查是否有data字段，如果没有，直接返回响应数据
    return response.data.data || response.data
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

// 更新用户信息API
export const updateUserApi = (userData) => {
  return api.put('/user/update', userData)
}

// 获取新闻列表API（支持分页）
// 注意：size由后端决定，前端只传page
export const getNewsListApi = (page = 1) => {
  return api.get('/artical/list', { params: { page } })
}

// 获取待审核新闻列表API（管理员）
export const getPendingNewsApi = () => {
  return api.get('/artical/pending')
}

// 审核新闻API（管理员）
export const reviewNewsApi = (id, status) => {
  return api.put(`/artical/review/${id}`, { status })
}

// 获取新闻详情API
export const getNewsDetailApi = (id) => {
  return api.get(`/artical/detail/${id}`)
}

// 获取发布者自己的发布记录API
export const getMyNewsApi = () => {
  return api.get('/artical/my-news')
}

// ==================== 点赞功能API ====================
// 点赞
export const likeNewsApi = (newsId) => {
  return api.post(`/like/add/${newsId}`)
}

// 取消点赞
export const unlikeNewsApi = (newsId) => {
  return api.delete(`/like/cancel/${newsId}`)
}

// 获取新闻点赞数
export const getLikeCountApi = (newsId) => {
  return api.get(`/like/count/${newsId}`)
}

// 检查用户是否已点赞
export const checkLikedApi = (newsId) => {
  return api.get(`/like/check/${newsId}`)
}

// ==================== 收藏功能API ====================
// 添加收藏
export const addFavoriteApi = (newsId) => {
  return api.post(`/favorite/add/${newsId}`)
}

// 取消收藏
export const removeFavoriteApi = (newsId) => {
  return api.delete(`/favorite/cancel/${newsId}`)
}

// 获取新闻收藏数
export const getFavoriteCountApi = (newsId) => {
  return api.get(`/favorite/count/${newsId}`)
}

// 检查用户是否已收藏
export const checkFavoritedApi = (newsId) => {
  return api.get(`/favorite/check/${newsId}`)
}

// 获取我的收藏列表
export const getMyFavoritesApi = () => {
  return api.get('/favorite/my-favorites')
}

// ==================== 评论功能API ====================
// 添加评论
export const addCommentApi = (commentData) => {
  return api.post('/comment/add', commentData)
}

// 获取新闻评论列表
export const getCommentsApi = (newsId) => {
  return api.get(`/comment/list/${newsId}`)
}

// 获取新闻评论数
export const getCommentCountApi = (newsId) => {
  return api.get(`/comment/count/${newsId}`)
}

// 删除评论
export const deleteCommentApi = (commentId) => {
  return api.delete(`/comment/delete/${commentId}`)
}

// ==================== 搜索功能API ====================
// 搜索新闻
export const searchNewsApi = (keyword) => {
  return api.get('/artical/search', { params: { keyword } })
}

// ==================== 分类筛选API ====================
// 根据分类获取新闻
export const getNewsByCategoryApi = (category) => {
  return api.get(`/artical/category/${category}`)
}

// ==================== 新闻删除API ====================
// 删除新闻
export const deleteNewsApi = (newsId) => {
  return api.delete(`/artical/delete/${newsId}`)
}

// ==================== 新闻编辑API ====================
// 编辑新闻
export const updateNewsApi = (newsId, newsData) => {
  return api.put(`/artical/update/${newsId}`, newsData)
}

export default api
