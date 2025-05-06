<script setup lang="ts">
import {ref} from "vue";

const props = defineProps({
  modelValue: {
    type: String,
    default: ""
  }
})
const modelContent = ref(props.modelValue)
</script>

<template>
  <vuetify-tiptap v-model="modelContent"></vuetify-tiptap>
</template>

<style scoped>

</style>
