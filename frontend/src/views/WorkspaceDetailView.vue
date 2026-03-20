<template>
  <AppLayout>
    <div v-if="loading" style="color: var(--color-text-muted);">Loading...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <div class="page-header">
        <div>
          <h2>{{ workspace.name }}</h2>
          <p style="color: var(--color-text-muted); font-size: 0.85rem;">
            <code>{{ workspace.slug }}</code> &mdash; {{ workspace.url }}
          </p>
        </div>
        <button class="btn-primary" @click="showAdd = true">Add Member</button>
      </div>

      <!-- Add member modal -->
      <div v-if="showAdd" class="modal-overlay" @click.self="closeAdd">
        <div class="modal">
          <h3>Add Member</h3>
          <form @submit.prevent="handleAdd">
            <div class="form-group">
              <label for="member-user">User</label>
              <select id="member-user" v-model="addForm.userId" required>
                <option value="" disabled>Select a user</option>
                <option v-for="u in availableUsers" :key="u.id" :value="u.id">
                  {{ u.email }} ({{ u.username }})
                </option>
              </select>
            </div>
            <div class="form-group">
              <label for="member-role">Role in Workspace</label>
              <select id="member-role" v-model="addForm.role">
                <option value="member">Member</option>
                <option value="admin">Admin</option>
              </select>
            </div>
            <p v-if="addError" class="error">{{ addError }}</p>
            <div class="modal-actions">
              <button type="button" class="btn-secondary" @click="closeAdd">Cancel</button>
              <button type="submit" :disabled="addLoading">
                {{ addLoading ? 'Adding...' : 'Add' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Members table -->
      <div class="table-container">
        <h3 style="margin-bottom: 0.75rem;">Members</h3>
        <table>
          <thead>
            <tr>
              <th>Email</th>
              <th>Username</th>
              <th>Workspace Role</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="m in members" :key="m.id">
              <td>{{ m.email }}</td>
              <td>{{ m.username }}</td>
              <td><span class="role-badge" :class="m.role">{{ m.role }}</span></td>
              <td>
                <button class="btn-danger-sm" @click="removeMember(m.userId, m.email)">Remove</button>
              </td>
            </tr>
          </tbody>
        </table>
        <p v-if="members.length === 0" style="color: var(--color-text-muted); text-align: center; padding: 2rem;">
          No members yet. Add users to this workspace.
        </p>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '../components/AppLayout.vue'
import api from '../api'

const route = useRoute()
const workspaceId = route.params.id

const workspace = ref(null)
const members = ref([])
const allUsers = ref([])
const loading = ref(true)
const error = ref('')

const showAdd = ref(false)
const addForm = ref({ userId: '', role: 'member' })
const addError = ref('')
const addLoading = ref(false)

const availableUsers = computed(() => {
  const memberIds = new Set(members.value.map(m => m.userId))
  return allUsers.value.filter(u => !memberIds.has(u.id))
})

onMounted(load)

async function load() {
  try {
    const [workspaces, membersData, users] = await Promise.all([
      api.get('/api/workspaces'),
      api.get(`/api/workspaces/${workspaceId}/members`),
      api.get('/api/users')
    ])
    workspace.value = workspaces.find(w => w.id === workspaceId)
    if (!workspace.value) {
      error.value = 'Workspace not found'
      return
    }
    members.value = membersData
    allUsers.value = users
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

function closeAdd() {
  showAdd.value = false
  addForm.value = { userId: '', role: 'member' }
  addError.value = ''
}

async function handleAdd() {
  addError.value = ''
  addLoading.value = true
  try {
    await api.post(`/api/workspaces/${workspaceId}/members`, addForm.value)
    closeAdd()
    members.value = await api.get(`/api/workspaces/${workspaceId}/members`)
  } catch (e) {
    addError.value = e.message
  } finally {
    addLoading.value = false
  }
}

async function removeMember(userId, email) {
  if (!confirm(`Remove ${email} from this workspace?`)) return
  try {
    await api.delete(`/api/workspaces/${workspaceId}/members/${userId}`)
    members.value = await api.get(`/api/workspaces/${workspaceId}/members`)
  } catch (e) {
    alert(e.message)
  }
}
</script>
