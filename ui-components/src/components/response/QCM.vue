<script setup lang="ts">
import {ref} from 'vue'
import type {PropType} from "vue";
import {useI18n} from "vue-i18n";

const props = defineProps({
  answers: {
    type: Array as PropType<string[]>,
    default: () => []
  },
  selected: {
    type: Array as PropType<string[]>,
    default: () => []
  }
})

const emit = defineEmits(['update:selected'])
const { t } = useI18n()

const selectedLocal = ref([...props.selected])


const updateSelected = (newSelected: string[]) => {
  emit('update:selected', newSelected)
  selectedLocal.value = newSelected
}
</script>

<template>
  <div class="Horizontal_container">
    <p class="answer">{{t('your-answer')}}</p>
    <v-container fluid class="Horizontal_container">
      <v-checkbox
              v-for="answer in answers"
              :key="answer"
              v-model="selectedLocal" :value="answer"
              @update:selected="updateSelected"
      >
        <template v-slot:label>
          <div>{{ answer }}</div>
        </template>
      </v-checkbox>
    </v-container>
  </div>
</template>

<style scoped>
.Horizontal_container{
  display: flex;
  align-items: center;
}
.answer{
  white-space: nowrap;
  margin-bottom: 2.3%;
  font-weight: bold;
}
</style>
<i18n>
{
  "en": {
    "your-answer": "Your Answer : "
  },
  "fr": {
    "your-answer": "Votre Réponse : "
  }
}
</i18n>
