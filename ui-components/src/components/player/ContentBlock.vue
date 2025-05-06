<script setup lang="ts">

import {ref, watch} from "vue";
const props = defineProps({
  /**
   * The title of the block
   */
  title: {
    type: String,
    default: ""
  },
  /**
   * A boolean. true if the block is readonly (no collapsible). false if the block is not readonly (collapsible)
   */
  readonly: {
    type: Boolean,
    default: false,
  },
  /**
   * The state of the block. 0 if the block is open, 1 if the block is closed.
   */
  modelValue: {
    type: [Number, null],
    default: null
  }
})
const refModelValue = ref(props.modelValue);
const emits=defineEmits(['update:modelValue'])

watch(refModelValue, (newVal) => {
  emits('update:modelValue', newVal);
  refModelValue.value=newVal;
});
</script>

<template>
  <v-expansion-panels :readonly="readonly" v-model="refModelValue">
    <v-expansion-panel>
      <v-expansion-panel-title>
        {{title}}
      </v-expansion-panel-title>
      <v-expansion-panel-text>
        <slot />
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<style scoped>

</style>
