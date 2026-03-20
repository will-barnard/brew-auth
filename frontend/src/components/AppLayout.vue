<template>
  <div>
    <nav class="navbar">
      <div class="nav-left">
        <router-link to="/" class="nav-brand">Brew Auth</router-link>
        <router-link to="/" class="nav-link">Dashboard</router-link>
        <router-link v-if="isAdmin" to="/users" class="nav-link">Users</router-link>
        <router-link v-if="auth.user?.role === 'super_admin'" to="/workspaces" class="nav-link">Workspaces</router-link>
        <router-link to="/profile" class="nav-link">Profile</router-link>
        <router-link v-if="auth.user?.role === 'super_admin'" to="/settings" class="nav-link">Settings</router-link>
      </div>
      <div class="nav-right">
        <span class="nav-user">{{ auth.user?.email }}</span>
        <button class="btn-logout" @click="handleLogout">Logout</button>
      </div>
    </nav>
    <main class="content">
      <slot />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const isAdmin = computed(() =>
  ['super_admin', 'admin'].includes(auth.user?.role)
)

async function handleLogout() {
  await auth.logout()
  router.push('/login')
}
</script>
