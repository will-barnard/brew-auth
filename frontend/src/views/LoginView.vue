<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1>{{ title }}</h1>
      <p class="subtitle">Sign in to your account</p>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">Email</label>
          <input id="email" v-model="email" type="email" required autocomplete="email" />
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input id="password" v-model="password" type="password" required autocomplete="current-password" />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" :disabled="loading">
          {{ loading ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../api'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const title = ref('Brew Auth')
const email = ref('')

onMounted(async () => {
  try {
    const settings = await api.get('/api/settings')
    if (settings.login_title) title.value = settings.login_title
  } catch { /* use default */ }
})
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    const data = await auth.login(email.value, password.value)
    if (data.passwordChangeRequired) {
      router.push('/change-password')
    } else {
      const returnUrl = route.query.return_url
      if (returnUrl && returnUrl.startsWith('https://')) {
        window.location.href = returnUrl
      } else {
        router.push('/')
      }
    }
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>
