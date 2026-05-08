import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'
import NewsListView from '../views/NewsListView.vue'
import AdminNewsView from '../views/AdminNewsView.vue'
import NewsDetailView from '../views/NewsDetailView.vue'
import FavoritesView from '../views/FavoritesView.vue'
import MyNewsView from '../views/MyNewsView.vue'
import { useAuthStore } from '../store/auth'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/news',
    name: 'news',
    component: NewsListView
  },
  {
    path: '/admin/news',
    name: 'admin-news',
    component: AdminNewsView,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/news/:id',
    name: 'news-detail',
    component: NewsDetailView
  },
  {
    path: '/my-news',
    name: 'my-news',
    component: MyNewsView,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/favorites',
    name: 'favorites',
    component: FavoritesView,
    meta: {
      requiresAuth: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  if (requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router