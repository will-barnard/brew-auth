import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(null)
  const initialized = ref(false)
  const passwordChangeRequired = ref(false)

  const isAuthenticated = computed(() => !!user.value)

  async function init() {
    try {
      const data = await api.get('/api/auth/me')
      user.value = data
      passwordChangeRequired.value = false
    } catch {
      user.value = null
    }
    initialized.value = true
  }

  async function login(email, password) {
    const data = await api.post('/api/auth/login', { email, password })
    token.value = data.token
    user.value = data.user
    passwordChangeRequired.value = data.passwordChangeRequired
    return data
  }

  async function changePassword(currentPassword, newPassword) {
    const data = await api.post('/api/auth/change-password', { currentPassword, newPassword })
    token.value = data.token
    user.value = data.user
    passwordChangeRequired.value = false
    return data
  }

  async function updateProfile(username) {
    const data = await api.put('/api/auth/profile', { username })
    token.value = data.token
    user.value = data.user
    return data
  }

  async function logout() {
    try {
      await api.post('/api/auth/logout')
    } catch { /* ignore */ }
    user.value = null
    token.value = null
    passwordChangeRequired.value = false
  }

  return {
    user,
    token,
    initialized,
    passwordChangeRequired,
    isAuthenticated,
    init,
    login,
    changePassword,
    updateProfile,
    logout
  }
})
