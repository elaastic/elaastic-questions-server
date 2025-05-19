<script setup lang="ts">

import {ref, watch} from "vue";

const props = defineProps({
  /**
   * The title of the block.
   */
  title: {
    type: String,
    default: "",
  },
  /**
   * A boolean. true if the block is readonly (no collapsible). false if the block is not readonly (collapsible).
   */
  readonly: {
    type: Boolean,
    default: false,
  },
  /**
   * The state of the block. 0 if the block is open, 1 if the block is closed.
   */
  state: {
    type: [Number, null],
    default: null,
  },
  /**
   * The side of the block, which is next to the title but has a smaller size than it.
   */
  side: {
    type: String,
    default: "",
  },
});
const refModelValue = ref(props.state)
const emit = defineEmits(["update:state"]);
watch(() => props.state, (newVal) => {
  refModelValue.value = newVal;
});
</script>

<template>
  <v-expansion-panels :readonly="readonly" v-model="refModelValue" @update:modelValue="emit('update:state', $event)">
    <v-expansion-panel>
      <v-expansion-panel-title class="title-container">
        <div class="title-side">
          <span class="title">{{ title }}</span>
          <span class="side">{{ side }}</span>
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
  flex-direction: column;
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
  font-size: 1.1em;
}

.side {
  font-size: small;
  color: #666;
}


</style>
