<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'
import {useI18n} from "vue-i18n";

const props = defineProps({
  /**
   * The number of possible answers
   */
  nbCandidateItem : {
    type: Number,
  },
  /**
   * The answers selected by the user
   */
  selected: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(["update:selected"])
const { t } = useI18n()

const selectedLocal = computed({
  get: () => props.selected,
  set: (val) => emit('update:selected', val)
});
onMounted(() => {
  if (!props.nbCandidateItem || props.nbCandidateItem === 0) {
    throw new Error("Prop 'nbCandidateItem' is required and cannot be 0.");
  }
});
</script>

<template>
  <div>
    <h5 class="answer">{{t('your-answer')}}</h5>
    <v-radio-group v-model="selectedLocal">
      <div class="radio">
        <v-radio
                v-for="answer in nbCandidateItem"
                :key="answer"
                :value="answer"
        >
          <template v-slot:label>
            <div>{{ answer }}</div>
          </template>
        </v-radio>
      </div>
    </v-radio-group>
  </div>
</template>

<style scoped>

.answer {
  white-space: nowrap;
  margin-bottom: 2.3%;
  font-weight: bold;
}

.radio {
  display: flex;
  flex-wrap: nowrap;

}

@media (max-width: 900px) {
  .radio {
    flex-wrap: wrap;
    gap: 1rem;
  }

  .radio :deep(.v-radio) {
    flex: 1 1 calc(33.33% - 1rem);
  }
}
@media (min-width: 900px){
  .radio{
    padding-right: 70%!important;
  }
}

</style>
<i18n>
{
  "en": {
    "your-answer": "Your answer  "
  },
  "fr": {
    "your-answer": "Votre réponse  "
  }
}
</i18n>
