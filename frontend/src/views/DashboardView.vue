<template>
  <AppLayout>
    <h2>Dashboard</h2>
    <div class="info-card">
      <h3>Your Account</h3>
      <div class="info-row">
        <span>Email</span>
        <span>{{ auth.user?.email }}</span>
      </div>
      <div class="info-row">
        <span>Username</span>
        <span>{{ auth.user?.username }}</span>
      </div>
      <div class="info-row">
        <span>Role</span>
        <span><span class="role-badge" :class="auth.user?.role">{{ auth.user?.role }}</span></span>
      </div>
    </div>

    <div class="info-card" style="margin-top: 1rem;">
      <h3>Integration</h3>
      <div class="info-row">
        <span>JWKS URL</span>
        <span><code>{{ origin }}/.well-known/jwks.json</code></span>
      </div>
      <div class="info-row">
        <span>Issuer</span>
        <span><code>{{ origin }}</code></span>
      </div>
      <p style="margin-top: 0.75rem; font-size: 0.8rem; color: var(--color-text-muted);">
        Configure other apps and Beachhead with these values to validate tokens issued by this auth service.
      </p>
    </div>
  </AppLayout>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import AppLayout from '../components/AppLayout.vue'

const auth = useAuthStore()
const origin = window.location.origin
</script>
