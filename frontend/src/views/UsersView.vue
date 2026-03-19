<template>
  <AppLayout>
    <div class="page-header">
      <h2>Users</h2>
      <button class="btn-primary" @click="showInvite = true">Invite User</button>
    </div>

    <!-- Invite modal -->
    <div v-if="showInvite" class="modal-overlay" @click.self="closeInvite">
      <div class="modal">
        <h3>Invite User</h3>
        <form @submit.prevent="handleInvite">
          <div class="form-group">
            <label for="invite-email">Email</label>
            <input id="invite-email" v-model="inviteEmail" type="email" required />
          </div>
          <div class="form-group">
            <label for="invite-role">Role</label>
            <select id="invite-role" v-model="inviteRole">
              <option value="user">User</option>
              <option v-if="auth.user?.role === 'super_admin'" value="admin">Admin</option>
            </select>
          </div>
          <p v-if="inviteError" class="error">{{ inviteError }}</p>
          <p v-if="inviteSuccess" class="success">{{ inviteSuccess }}</p>
          <div class="modal-actions">
            <button type="button" class="btn-secondary" @click="closeInvite">Cancel</button>
            <button type="submit" :disabled="inviteLoading">
              {{ inviteLoading ? 'Sending...' : 'Send Invite' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Users table -->
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>Email</th>
            <th>Username</th>
            <th>Role</th>
            <th>Created</th>
            <th v-if="auth.user?.role === 'super_admin'"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td>{{ u.email }}</td>
            <td>{{ u.username }}</td>
            <td><span class="role-badge" :class="u.role">{{ u.role }}</span></td>
            <td>{{ formatDate(u.createdAt) }}</td>
            <td v-if="auth.user?.role === 'super_admin'">
              <button
                v-if="u.id !== auth.user?.id"
                class="btn-danger-sm"
                @click="deleteUser(u.id)"
              >Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import AppLayout from '../components/AppLayout.vue'
import api from '../api'

const auth = useAuthStore()
const users = ref([])

const showInvite = ref(false)
const inviteEmail = ref('')
const inviteRole = ref('user')
const inviteError = ref('')
const inviteSuccess = ref('')
const inviteLoading = ref(false)

onMounted(loadUsers)

async function loadUsers() {
  users.value = await api.get('/api/users')
}

function closeInvite() {
  showInvite.value = false
  inviteEmail.value = ''
  inviteRole.value = 'user'
  inviteError.value = ''
  inviteSuccess.value = ''
}

async function handleInvite() {
  inviteError.value = ''
  inviteSuccess.value = ''
  inviteLoading.value = true
  try {
    await api.post('/api/users/invite', {
      email: inviteEmail.value,
      role: inviteRole.value
    })
    inviteSuccess.value = 'Invitation sent!'
    inviteEmail.value = ''
    inviteRole.value = 'user'
  } catch (e) {
    inviteError.value = e.message
  } finally {
    inviteLoading.value = false
  }
}

async function deleteUser(id) {
  if (!confirm('Are you sure you want to delete this user?')) return
  try {
    await api.delete(`/api/users/${id}`)
    await loadUsers()
  } catch (e) {
    alert(e.message)
  }
}

function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleDateString()
}
</script>
