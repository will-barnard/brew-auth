<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1>Change Password</h1>
      <p class="subtitle">You must change your password before continuing</p>
      <form @submit.prevent="handleChange">
        <div class="form-group">
          <label for="current">Current Password</label>
          <input id="current" v-model="currentPassword" type="password" required autocomplete="current-password" />
        </div>
        <div class="form-group">
          <label for="new-password">New Password</label>
          <input id="new-password" v-model="newPassword" type="password" required minlength="8" autocomplete="new-password" />
        </div>
        <div class="form-group">
          <label for="confirm">Confirm New Password</label>
          <input id="confirm" v-model="confirmPassword" type="password" required autocomplete="new-password" />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" :disabled="loading">
          {{ loading ? 'Changing...' : 'Change Password' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')
const loading = ref(false)

async function handleChange() {
  error.value = ''
  if (newPassword.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }
  if (newPassword.value.length < 8) {
    error.value = 'Password must be at least 8 characters'
    return
  }
  loading.value = true
  try {
    await auth.changePassword(currentPassword.value, newPassword.value)
    router.push('/')
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>
