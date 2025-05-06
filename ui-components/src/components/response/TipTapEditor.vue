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
   * The content of the text-area
   */
  isReadonly: {
    type: Boolean,
    default: false
  },
  /**
   * The content of the text-area
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
