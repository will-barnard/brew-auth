<template>
  <AppLayout>
    <div class="page-header">
      <h2>Workspaces</h2>
      <button class="btn-primary" @click="showCreate = true">Create Workspace</button>
    </div>

    <!-- Create modal -->
    <div v-if="showCreate" class="modal-overlay" @click.self="closeCreate">
      <div class="modal">
        <h3>Create Workspace</h3>
        <form @submit.prevent="handleCreate">
          <div class="form-group">
            <label for="ws-name">Name</label>
            <input id="ws-name" v-model="createForm.name" required placeholder="My Workspace" />
          </div>
          <div class="form-group">
            <label for="ws-slug">Slug</label>
            <input id="ws-slug" v-model="createForm.slug" required placeholder="my-workspace"
                   pattern="[a-z0-9\-]+" title="Lowercase letters, numbers, and hyphens only" />
          </div>
          <div class="form-group">
            <label for="ws-url">URL</label>
            <input id="ws-url" v-model="createForm.url" required placeholder="https://beachhead.example.com" />
          </div>
          <p v-if="createError" class="error">{{ createError }}</p>
          <div class="modal-actions">
            <button type="button" class="btn-secondary" @click="closeCreate">Cancel</button>
            <button type="submit" :disabled="createLoading">
              {{ createLoading ? 'Creating...' : 'Create' }}
            </button>
          </div>
        </form>
        <div v-if="createdResult" class="success-box">
          <p class="success">Workspace created!</p>
          <div class="info-row"><span>ID</span><code>{{ createdResult.workspaceId }}</code></div>
          <div class="info-row"><span>API Key</span><code>{{ createdResult.apiKey }}</code></div>
          <p style="font-size: 0.8rem; color: var(--color-text-muted); margin-top: 0.5rem;">
            Save this API key — it won't be shown again.
          </p>
        </div>
      </div>
    </div>

    <!-- Workspaces table -->
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Slug</th>
            <th>URL</th>
            <th>Created</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="ws in workspaces" :key="ws.id">
            <td>
              <router-link :to="`/workspaces/${ws.id}`">{{ ws.name }}</router-link>
            </td>
            <td><code>{{ ws.slug }}</code></td>
            <td>{{ ws.url }}</td>
            <td>{{ formatDate(ws.createdAt) }}</td>
            <td>
              <button class="btn-danger-sm" @click="deleteWorkspace(ws.id, ws.name)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-if="workspaces.length === 0" style="color: var(--color-text-muted); text-align: center; padding: 2rem;">
        No workspaces yet. Create one to connect a Beachhead instance.
      </p>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppLayout from '../components/AppLayout.vue'
import api from '../api'

const workspaces = ref([])
const showCreate = ref(false)
const createForm = ref({ name: '', slug: '', url: '' })
const createError = ref('')
const createLoading = ref(false)
const createdResult = ref(null)

onMounted(loadWorkspaces)

async function loadWorkspaces() {
  workspaces.value = await api.get('/api/workspaces')
}

function closeCreate() {
  showCreate.value = false
  createForm.value = { name: '', slug: '', url: '' }
  createError.value = ''
  createdResult.value = null
}

async function handleCreate() {
  createError.value = ''
  createLoading.value = true
  try {
    const result = await api.post('/api/workspaces', createForm.value)
    createdResult.value = result
    await loadWorkspaces()
  } catch (e) {
    createError.value = e.message
  } finally {
    createLoading.value = false
  }
}

async function deleteWorkspace(id, name) {
  if (!confirm(`Delete workspace "${name}"? This will remove all member associations.`)) return
  try {
    await api.delete(`/api/workspaces/${id}`)
    await loadWorkspaces()
  } catch (e) {
    alert(e.message)
  }
}

function formatDate(d) {
  return d ? new Date(d).toLocaleDateString() : ''
}
</script>
