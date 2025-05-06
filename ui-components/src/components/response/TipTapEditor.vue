<script setup lang="ts">
import {ref, watch} from "vue";

const props = defineProps({
  /**
   * The content of the text-area
   */
  modelValue: {
    type: String,
    default: ""
  },
  /**
   * A boolean. true if you can't write in the text-area. false if you can.
   */
  isReadonly: {
    type: Boolean,
    default: false
  },
  /**
   * A boolean. true if the editor is in dark-mode (backgroundcolor=black). false if the editor is in light-mode (backgroundcolor=white)
   */
  isDark: {
    type: Boolean,
    default: false
  },

})
const modelContent = ref(props.modelValue)
const emit = defineEmits(["update:modelValue"])
watch(modelContent, (newVal) => {
  emit('update:modelValue', newVal);
});

</script>

<template>
  <vuetify-tiptap v-model="modelContent" output="text" :disabled="isReadonly" :dark="isDark"></vuetify-tiptap>
</template>

<style scoped>

</style>
