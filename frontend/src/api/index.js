class ApiClient {
  async request(method, url, body) {
    const options = {
      method,
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include'
    }
    if (body) {
      options.body = JSON.stringify(body)
    }

    const response = await fetch(url, options)

    if (!response.ok) {
      const error = await response.json().catch(() => ({ error: 'Request failed' }))
      throw new Error(error.error || error.message || 'Request failed')
    }

    if (response.status === 204 || response.headers.get('content-length') === '0') {
      return null
    }

    return response.json()
  }

  get(url) { return this.request('GET', url) }
  post(url, body) { return this.request('POST', url, body) }
  put(url, body) { return this.request('PUT', url, body) }
  delete(url) { return this.request('DELETE', url) }
}

export default new ApiClient()
