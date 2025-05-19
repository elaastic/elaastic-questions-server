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
    <h5 class="answer">{{t('your-answer')}}</h5>
    <div class="Horizontal_container">
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
.answer{
  white-space: nowrap;
  margin-bottom: 2.3%;
  font-weight: bold;
  margin-left: 4%;
}
.Horizontal_container {
  display: flex;
  align-items: flex-start;
  margin-left: 1.2%;
  margin-top: -1%;
}

.checkBox {
  margin-right: 2%;
}

@media (max-width: 900px) {
  .checkBox {
    flex: 1 1 calc(33.33% - 1rem);
  }
  .Horizontal_container{
    gap: 1rem;
    flex-wrap: wrap;
  }
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
