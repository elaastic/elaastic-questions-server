import vuetify from './vuetify'
import i18n from '@/plugins/i18n'
import { vuetifyProTipTap } from '@/tiptap'
import 'vuetify/styles'
import type { App } from 'vue';

export function registerPlugins (app: App) {
  app.use(vuetify)
  app.use(i18n)
  app.use(vuetifyProTipTap)
}

