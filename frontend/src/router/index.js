import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue'),
    meta: { public: true }
  },
  {
    path: '/change-password',
    name: 'change-password',
    component: () => import('../views/ChangePasswordView.vue')
  },
  {
    path: '/',
    name: 'dashboard',
    component: () => import('../views/DashboardView.vue')
  },
  {
    path: '/users',
    name: 'users',
    component: () => import('../views/UsersView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (!auth.initialized) {
    await auth.init()
  }

  if (to.meta.public) return true

  if (!auth.isAuthenticated) {
    return { name: 'login' }
  }

  if (auth.passwordChangeRequired && to.name !== 'change-password') {
    return { name: 'change-password' }
  }

  return true
})

export default router
