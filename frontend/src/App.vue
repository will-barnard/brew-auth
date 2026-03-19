<template>
  <router-view />
</template>

<style>
:root {
  --color-bg: #0f1117;
  --color-surface: #1a1d27;
  --color-border: #2a2d3a;
  --color-primary: #6366f1;
  --color-primary-hover: #818cf8;
  --color-text: #e2e8f0;
  --color-text-muted: #94a3b8;
  --color-error: #ef4444;
  --color-success: #22c55e;
  --color-danger: #dc2626;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: var(--color-bg);
  color: var(--color-text);
  min-height: 100vh;
}

a {
  color: var(--color-primary);
  text-decoration: none;
}

a:hover {
  color: var(--color-primary-hover);
}

/* Auth pages */
.auth-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 1rem;
}

.auth-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 2rem;
  width: 100%;
  max-width: 400px;
}

.auth-card h1 {
  font-size: 1.5rem;
  margin-bottom: 0.25rem;
}

.auth-card .subtitle {
  color: var(--color-text-muted);
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
}

/* Forms */
.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-size: 0.875rem;
  color: var(--color-text-muted);
  margin-bottom: 0.375rem;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.625rem 0.75rem;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  color: var(--color-text);
  font-size: 0.875rem;
  transition: border-color 0.15s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--color-primary);
}

button, .btn {
  padding: 0.625rem 1rem;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
}

.btn-primary, button[type="submit"] {
  width: 100%;
  background: var(--color-primary);
  color: white;
  margin-top: 0.5rem;
}

.btn-primary:hover, button[type="submit"]:hover {
  background: var(--color-primary-hover);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: var(--color-border);
  color: var(--color-text);
}

.btn-secondary:hover {
  background: #3a3d4a;
}

.btn-danger-sm {
  background: transparent;
  color: var(--color-danger);
  border: 1px solid var(--color-danger);
  padding: 0.25rem 0.625rem;
  font-size: 0.75rem;
}

.btn-danger-sm:hover {
  background: var(--color-danger);
  color: white;
}

.error {
  color: var(--color-error);
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
}

.success {
  color: var(--color-success);
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
}

/* Layout */
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1.5rem;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nav-brand {
  font-weight: 700;
  font-size: 1.1rem;
  color: var(--color-text) !important;
}

.nav-link {
  color: var(--color-text-muted) !important;
  font-size: 0.875rem;
}

.nav-link:hover, .nav-link.router-link-active {
  color: var(--color-text) !important;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.nav-user {
  color: var(--color-text-muted);
  font-size: 0.875rem;
}

.btn-logout {
  background: transparent;
  color: var(--color-text-muted);
  border: 1px solid var(--color-border);
  padding: 0.375rem 0.75rem;
  font-size: 0.8rem;
}

.btn-logout:hover {
  color: var(--color-text);
  border-color: var(--color-text-muted);
}

.content {
  max-width: 960px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

/* Info cards */
.info-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1.5rem;
  margin-top: 1rem;
}

.info-card h3 {
  font-size: 1rem;
  margin-bottom: 1rem;
  color: var(--color-text-muted);
}

.info-row {
  display: flex;
  gap: 0.5rem;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.9rem;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row span:first-child {
  color: var(--color-text-muted);
  min-width: 100px;
}

/* Tables */
.table-container {
  margin-top: 1rem;
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
}

thead th {
  text-align: left;
  padding: 0.75rem 1rem;
  font-size: 0.8rem;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid var(--color-border);
}

tbody td {
  padding: 0.75rem 1rem;
  font-size: 0.875rem;
  border-bottom: 1px solid var(--color-border);
}

tbody tr:last-child td {
  border-bottom: none;
}

.role-badge {
  display: inline-block;
  padding: 0.125rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.role-badge.super_admin {
  background: #7c3aed22;
  color: #a78bfa;
}

.role-badge.admin {
  background: #2563eb22;
  color: #60a5fa;
}

.role-badge.user {
  background: #16a34a22;
  color: #4ade80;
}

/* Page header */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-header .btn-primary {
  width: auto;
  margin-top: 0;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
}

.modal {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 2rem;
  width: 100%;
  max-width: 420px;
}

.modal h3 {
  margin-bottom: 1rem;
}

.modal-actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 1rem;
}

.modal-actions button {
  flex: 1;
}
</style>
