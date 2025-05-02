<script setup lang="ts">
  import {onMounted, ref} from 'vue';
  const props = defineProps({
    /**
     * The content of the text-area
     */
    modelValue: {
      type: String,
      default: ""
    }
  })
  const emit = defineEmits(["update:modelValue"])
  const editor = ref<HTMLTextAreaElement | null>(null);
  onMounted(() => {
    if (window.CKEDITOR && editor.value) {
      const instance = CKEDITOR.replace(editor.value);
      instance.setData(props.modelValue);

      instance.on('change', () => {
        const data = instance.getData();
        emit('update:modelValue', data);
      });
    }
  });

</script>

<template>
  <div>
    <textarea ref="editor"></textarea>
  </div>
</template>

<style scoped>
</style>
