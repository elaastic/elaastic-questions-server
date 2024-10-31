import { fileURLToPath, URL } from 'node:url'
import { resolve } from 'path'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { version } from './package.json';

const libraryFileName = 'elaastic-vue-components';

// https://vite.dev/config/
export default defineConfig({
  define: {
    __APP_VERSION__: JSON.stringify(version),
  },
  build: {
    lib: {
      entry: resolve(__dirname, 'src/main.ts'),
      name: 'ElaasticVueComponents',
      fileName: libraryFileName,
      formats: ['umd']
    },
    rollupOptions: {
      external: ['vue'],
      output: {
        entryFileNames: `${libraryFileName}-v${version}.umd.min.js`,
        assetFileNames: `[name]-v${version}.[ext]`,
        globals: {
          vue: 'Vue',
        }
      }
    },
  },
  plugins: [
    vue(),
    vueDevTools()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
