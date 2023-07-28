/// <reference types="vitest" />
import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  test: {
    globals: true,
    environment:"jsdom",
    setupFiles:"src/setupTests.ts"
  },
  plugins: [
    vue(),
  ],
  server:{
    proxy: {
      '/api': {

        target: 'http://localhost:8888/',

        changeOrigin: true

      }
  }
  }
})
