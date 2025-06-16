<script setup lang="ts">

import { computed } from 'vue'

const props = defineProps({
  /**
   * The title of the block.
   */
  title: {
    type: String,
    required: true
  },
  /**
   * A boolean. true if the block is collapsible. false if not.
   */
  collapsible: {
    type: Boolean,
    default: true
  },
  /**
   * The state of the block. true if the block is open, false if the block is closed.
   */
  open: {
    type: Boolean,
    default: true
  },
  /**
   * The side of the block, which is next to the title but has a smaller size than it.
   */
  subtitle: {
    type: String
  },
  /**
   * A boolean. true if the subtitle is shown. false if not.
   */
  showSubtitle: {
    type: Boolean,
    default: true
  }
})
const emit = defineEmits(['update:open', 'update:isSubtitleHidden'])
const openPanel = computed({
  get: () => props.open ? 0 : null,
  set: (val: number | null) => {
    const isOpen = val === 0
    emit('update:open', isOpen)
  }
})
</script>

<template>
  <v-expansion-panels
          :readonly="!collapsible"
          v-model="openPanel"
  >
    <v-expansion-panel :elevation="2">
      <v-expansion-panel-title>
        <template v-slot:actions="{ readonly, expanded }">
          <v-icon :icon="readonly ? '' : expanded ? 'mdi-chevron-up' : 'mdi-chevron-down'"></v-icon>
        </template>
        <div>
          <span class="title">{{ title }}</span>
          <span v-if="props.showSubtitle && subtitle" class="subtitle"><strong>&nbsp;[</strong>{{ subtitle
            }}<strong>]</strong></span>
        </div>
      </v-expansion-panel-title>
      <v-expansion-panel-text>
        <slot />
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<style scoped>
.title {
  font-weight: bold;
  font-size: 1.6em;
}

.subtitle {
  font-size: small;
  color: #666;
}


</style>
