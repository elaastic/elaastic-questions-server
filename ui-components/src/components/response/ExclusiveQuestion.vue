<script setup lang="ts">
import {ref, watch} from 'vue'
import type {PropType} from "vue";
import {useI18n} from "vue-i18n";

const props = defineProps({
  answers: {
    type: Array as PropType<number[]>,
    default: () => []
  },
  selected: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(["update:selected"])
const { t } = useI18n()

const selectedLocal = ref(props.selected);


watch(selectedLocal, (newVal) => {
  emit('update:selected', newVal);
});
</script>

<template>
  <div>
    <h5 class="answer">{{t('your-answer')}}</h5>
    <v-container fluid>
      <v-radio-group v-model="selectedLocal">
        <v-row>
          <v-radio
                   v-for="answer in answers"
                   :key="answer"
                   :value="answer"
          >
            <template v-slot:label>
              <div>{{ answer }}</div>
            </template>
          </v-radio>
        </v-row>
      </v-radio-group>
    </v-container>
  </div>
</template>

<style scoped>

.answer{
  white-space: nowrap;
  margin-bottom: 2.3%;
  font-weight: bold;
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
