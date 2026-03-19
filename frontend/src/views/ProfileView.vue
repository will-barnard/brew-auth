<template>
  <AppLayout>
    <h2>Profile</h2>

    <div class="info-card">
      <h3>Account Info</h3>
      <div class="info-row">
        <span>Email</span>
        <span>{{ auth.user?.email }}</span>
      </div>
      <div class="info-row">
        <span>Role</span>
        <span><span class="role-badge" :class="auth.user?.role">{{ auth.user?.role }}</span></span>
      </div>
    </div>

    <div class="info-card" style="margin-top: 1rem;">
      <h3>Change Username</h3>
      <form @submit.prevent="handleUsernameChange">
        <div class="form-group" style="margin-top: 0.75rem;">
          <label for="username">Username</label>
          <input
            id="username"
            v-model="username"
            type="text"
            required
            minlength="2"
            maxlength="50"
          />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="success" class="success">{{ success }}</p>
        <button type="submit" class="btn-primary" style="width: auto;" :disabled="loading">
          {{ loading ? 'Saving...' : 'Save Username' }}
        </button>
      </form>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import AppLayout from '../components/AppLayout.vue'

const auth = useAuthStore()

const username = ref(auth.user?.username ?? '')
const error = ref('')
const success = ref('')
const loading = ref(false)

async function handleUsernameChange() {
  error.value = ''
  success.value = ''
  loading.value = true
  try {
    await auth.updateProfile(username.value)
    success.value = 'Username updated!'
    setTimeout(() => { success.value = '' }, 3000)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>
