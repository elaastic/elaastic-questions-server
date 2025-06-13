<script setup lang="ts">

import ContentBlock from "@/components/player/ContentBlock.vue";
import ChoiceFrame from "@/components/results/ChoiceFrame.vue";
import ElExplanation from "@/components/results/ElExplanation.vue";
import {onMounted, type PropType, ref} from "vue";
import type {AnyResponse} from "@/models/Response";
import {useI18n} from "vue-i18n";

const props = defineProps({
  /**
   * All possible answers.
   */
  answers: {
    type: Array<{itemIndex: number, isCorrect: boolean}>,
    default: () => []
  },
  /**
   * Student's explanation at his first attempt.
   */
  explanationFirstAttempt: {
    type: Object as PropType<{answer: AnyResponse, grade: number, nbPeer: number, isTeacher: boolean}>,
    default: () => []
  },
  /**
   * Student's explanation at his second attempt.
   */
  explanationSecondAttempt: {
    type: Object as PropType<{answer: AnyResponse, grade: number, nbPeer: number, isTeacher: boolean}>,
    default: () => []
  },
  /**
   * A boolean. True if the panel is open. False if not.
   */
  open: {
    type: Boolean,
    default: true
  },

})
const openLocal = ref(props.open);
const correctAnswer = props.answers.filter(answer => answer.isCorrect);
const wrongAnswer = props.answers.filter(answer => !answer.isCorrect)
const answersCheckedFirstAttempt: number[] = props.explanationFirstAttempt.answer.questionType === "MultipleChoice" ? props.explanationFirstAttempt.answer.choices : props.explanationFirstAttempt.answer.questionType === "ExclusiveChoice" ? [props.explanationFirstAttempt.answer.choice] : [];
const answersCheckedSecondAttempt: number[] = props.explanationSecondAttempt.answer.questionType === "MultipleChoice" ? props.explanationSecondAttempt.answer.choices : props.explanationSecondAttempt.answer.questionType === "ExclusiveChoice"? [props.explanationSecondAttempt.answer.choice] : [];
const scoreFirstAttempt = ref(0);
const scoreSecondAttempt = ref(0);

onMounted(() => {
  for (const answer of correctAnswer) {
    scoreFirstAttempt.value = answersCheckedFirstAttempt.includes(answer.itemIndex) ? scoreFirstAttempt.value + 1 : scoreFirstAttempt.value;
    scoreSecondAttempt.value = answersCheckedSecondAttempt.includes(answer.itemIndex) ? scoreSecondAttempt.value + 1 : scoreSecondAttempt.value;
  }
  for (const answer of wrongAnswer) {
    scoreFirstAttempt.value = answersCheckedFirstAttempt.includes(answer.itemIndex) ? scoreFirstAttempt.value - 1 : scoreFirstAttempt.value;
    scoreSecondAttempt.value = answersCheckedSecondAttempt.includes(answer.itemIndex) ? scoreSecondAttempt.value - 1 : scoreSecondAttempt.value;
  }
  scoreFirstAttempt.value = (scoreFirstAttempt.value / correctAnswer.length) * 100;
  scoreSecondAttempt.value = (scoreSecondAttempt.value / correctAnswer.length) * 100;
  if(scoreFirstAttempt.value < 0) {
    scoreFirstAttempt.value = 0;
  }
  if(scoreSecondAttempt.value < 0){
    scoreSecondAttempt.value = 0;
  }
})

const { t } = useI18n()
</script>

<template>
  <ContentBlock :title="t('my-results')" :collapsible="true" v-model:open="openLocal" :is-subtitle-hidden="true">
    <h2>{{t('step')}} 1</h2>
    <div v-if="props.explanationFirstAttempt.answer.questionType !== 'OpenEnded'">
      <h4>{{t('choice')}}</h4>
      <div class="groupChoiceFrame">
        <ChoiceFrame v-for="a in answers" :value="a.itemIndex" :is-correct="a.isCorrect" :is-checked="answersCheckedFirstAttempt.includes(a.itemIndex)"/>
      </div>
      <h4>Score</h4>
      <v-card style="width: 100px; margin-bottom: 2%" :color="scoreFirstAttempt === 100 ? '#2E7D32' : '#AD1457'">
        <v-card-title>
          {{scoreFirstAttempt}}%
        </v-card-title>
      </v-card>
    </div>
    <ElExplanation :response="props.explanationFirstAttempt.answer" :number-of-peer-review="props.explanationFirstAttempt.nbPeer" :grade="explanationFirstAttempt.grade"/>
    <h2 style="margin-top: 2%">{{t('step')}} 2</h2>
    <div v-if="props.explanationFirstAttempt.answer.questionType !== 'OpenEnded'">
      <h4>{{t('choice')}}</h4>
      <div class="groupChoiceFrame">
        <ChoiceFrame v-for="a in answers" :value="a.itemIndex" :is-correct="a.isCorrect" :is-checked="answersCheckedSecondAttempt.includes(a.itemIndex)"/>
      </div>
      <h4>Score</h4>
      <v-card style="width: 100px; margin-bottom: 2%; color: white" :color="scoreSecondAttempt === 100 ? '#2E7D32' : scoreSecondAttempt > scoreFirstAttempt ? '#FF8A65' :'#AD1457'">
        <v-card-title>
          {{scoreSecondAttempt}}%
        </v-card-title>
      </v-card>
    </div>
    <ElExplanation :response="props.explanationSecondAttempt.answer" :number-of-peer-review="props.explanationSecondAttempt.nbPeer" :grade="props.explanationSecondAttempt.grade"/>
  </ContentBlock>
</template>

<style scoped>
.groupChoiceFrame{
  display: flex;
  flex: 1 1 calc(33.33% - 1rem);
  gap: 1rem;
  flex-wrap: wrap;
}
</style>
<i18n>
{
  "en": {
    "my-results": "My results",
    "step": "Step",
    "choice": "Choice"
  },
  "fr": {
    "my-results": "Mes résultats",
    "step": "Phase",
    "choice": "Choix"
  }
}
</i18n>
