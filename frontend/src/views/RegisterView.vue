<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1>Brew Auth</h1>
      <p class="subtitle">Create your account</p>

      <div v-if="!token" class="error">Invalid invitation link. Please check your email for the correct link.</div>

      <form v-else-if="!registered" @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">Username</label>
          <input id="username" v-model="username" type="text" required minlength="2" autocomplete="username" />
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input id="password" v-model="password" type="password" required minlength="8" autocomplete="new-password" />
        </div>
        <div class="form-group">
          <label for="confirm">Confirm Password</label>
          <input id="confirm" v-model="confirm" type="password" required autocomplete="new-password" />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" :disabled="loading">
          {{ loading ? 'Creating account...' : 'Create Account' }}
        </button>
      </form>

      <div v-else>
        <p class="success">Account created successfully!</p>
        <button class="btn-primary" style="margin-top: 1rem;" @click="$router.push('/')">
          Continue to Dashboard
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../api'

const route = useRoute()
const auth = useAuthStore()

const token = route.query.token
const username = ref('')
const password = ref('')
const confirm = ref('')
const error = ref('')
const loading = ref(false)
const registered = ref(false)

async function handleRegister() {
  error.value = ''
  if (password.value !== confirm.value) {
    error.value = 'Passwords do not match'
    return
  }
  if (password.value.length < 8) {
    error.value = 'Password must be at least 8 characters'
    return
  }
  loading.value = true
  try {
    const data = await api.post('/api/auth/register', {
      token,
      username: username.value,
      password: password.value
    })
    auth.user = data.user
    auth.token = data.token
    registered.value = true
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>
