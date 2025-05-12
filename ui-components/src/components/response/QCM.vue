<script setup lang="ts">
import {ref, watch} from 'vue'
import type {PropType} from "vue";
import {useI18n} from "vue-i18n";

const props = defineProps({
  /**
   * The possibles answers
   */
  answers: {
    type: Array as PropType<number[]>,
    default: () => []
  },
  /**
   * The answers selected by the user
   */
  selected: {
    type: Array as PropType<number[]>,
    default: () => []
  }
})

const emit = defineEmits(["update:selected"])
const { t } = useI18n()

const selectedLocal = ref([...props.selected])


watch(selectedLocal, (newVal) => {
  emit('update:selected', newVal);
});
</script>

<template>
  <div class="Horizontal_container">
    <h5 class="answer">{{t('your-answer')}}</h5>
    <v-container fluid class="Horizontal_container">
      <v-checkbox class="checkBox"
              v-for="answer in answers"
              :key="answer"
              v-model="selectedLocal"
              :value="answer"
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
.checkBox{
  margin-right: 2%;
}
</style>
<i18n>
{
  "en": {
    "your-answer": "Your answer : "
  },
  "fr": {
    "your-answer": "Votre réponse : "
  }
}
</i18n>
