<template>
  <AppLayout>
    <h2>Settings</h2>

    <div class="info-card">
      <h3>Appearance</h3>
      <form @submit.prevent="saveLoginTitle">
        <div class="form-group" style="margin-top: 0.75rem;">
          <label for="login-title">Login Page Title</label>
          <input
            id="login-title"
            v-model="loginTitle"
            type="text"
            required
            maxlength="100"
            placeholder="Brew Auth"
          />
          <p style="margin-top: 0.375rem; font-size: 0.8rem; color: var(--color-text-muted);">
            The heading shown on the login page.
          </p>
        </div>
        <p v-if="titleError" class="error">{{ titleError }}</p>
        <p v-if="titleSuccess" class="success">{{ titleSuccess }}</p>
        <button type="submit" class="btn-primary" style="width: auto;" :disabled="titleLoading">
          {{ titleLoading ? 'Saving...' : 'Save' }}
        </button>
      </form>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppLayout from '../components/AppLayout.vue'
import api from '../api'

const loginTitle = ref('')
const titleError = ref('')
const titleSuccess = ref('')
const titleLoading = ref(false)

onMounted(async () => {
  try {
    const settings = await api.get('/api/settings')
    loginTitle.value = settings.login_title ?? 'Brew Auth'
  } catch (e) {
    titleError.value = e.message
  }
})

async function saveLoginTitle() {
  titleError.value = ''
  titleSuccess.value = ''
  titleLoading.value = true
  try {
    await api.put('/api/settings/login_title', { value: loginTitle.value })
    titleSuccess.value = 'Saved!'
    setTimeout(() => { titleSuccess.value = '' }, 3000)
  } catch (e) {
    titleError.value = e.message
  } finally {
    titleLoading.value = false
  }
}
</script>
