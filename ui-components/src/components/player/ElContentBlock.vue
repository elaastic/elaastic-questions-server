<script setup lang="ts">

import {computed, onMounted} from "vue";

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
    default: false,
  },
  /**
   * The state of the block. true if the block is open, false if the block is closed.
   */
  open: {
    type: Boolean,
    default: true,
  },
  /**
   * The side of the block, which is next to the title but has a smaller size than it.
   */
  subtitle: {
    type: String,
  },
  /**
   * A boolean. true if the subtitle is hidden. false if not.
   */
  isSubtitleHidden: {
    type: Boolean,
    default: false
  }
});
const emit = defineEmits(["update:open", "update:isSubtitleHidden"]);
const openPanel = computed({
  get: () => props.open ? [0] : [],
  set: (val: number[] | number | null) => {
    const isOpen = Array.isArray(val) ? val.includes(0) : val === 0;
    emit("update:open", isOpen);
  },
});
onMounted(() => {
  if (!props.title || props.title.trim() === "") {
    throw new Error("Prop 'title' is required and cannot be empty.");
  }
});
</script>

<template>
  <v-expansion-panels
          :readonly="!collapsible"
          v-model="openPanel"
  >
    <v-expansion-panel :elevation="2">
      <v-expansion-panel-title class="title-container">
        <div class="title-side">
          <span class="title">{{ title }}</span>
          <span v-if="!props.isSubtitleHidden" class="side"><strong>[</strong>{{ subtitle }}<strong>]</strong></span>
        </div>
      </v-expansion-panel-title>
      <v-expansion-panel-text>
        <slot />
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<style scoped>
.title-container {
  display: flex;
}

.title-side {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  width: 100%;
  text-align: left;
}

.title {
  font-weight: bold;
  font-size: 1.6em;
}

.side {
  font-size: small;
  color: #666;
}


</style>
