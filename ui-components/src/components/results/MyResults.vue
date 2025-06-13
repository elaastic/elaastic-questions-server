<script setup lang="ts">

import ContentBlock from "@/components/player/ContentBlock.vue";
import ChoiceFrame from "@/components/results/ChoiceFrame.vue";
import ElExplanation from "@/components/results/ElExplanation.vue";
import {type PropType, ref} from "vue";
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
  },
  /**
   * Student's explanation at his second attempt.
   */
  explanationSecondAttempt: {
    type: Object as PropType<{answer: AnyResponse, grade: number, nbPeer: number, isTeacher: boolean}>,
  },
  /**
   * A boolean. True if the panel is open. False if not.
   */
  open: {
    type: Boolean,
    default: true
  },
  /**
   * The student's score at the first attempt.
   */
  scoreFirstAttempt: {
    type: Number,
  },
  /**
   * The student's score at the second attempt.
   */
  scoreSecondAttempt: {
    type: Number,
  }

})
const openLocal = ref(props.open);
const answersCheckedFirstAttempt: number[] = props.explanationFirstAttempt?.answer.questionType === "MultipleChoice" ? props.explanationFirstAttempt.answer.choices : props.explanationFirstAttempt?.answer.questionType === "ExclusiveChoice" ? [props.explanationFirstAttempt.answer.choice] : [];
const answersCheckedSecondAttempt: number[] = props.explanationSecondAttempt?.answer.questionType === "MultipleChoice" ? props.explanationSecondAttempt.answer.choices : props.explanationSecondAttempt?.answer.questionType === "ExclusiveChoice"? [props.explanationSecondAttempt.answer.choice] : [];

const { t } = useI18n()

const defineColor =  () => {
    if(props.scoreSecondAttempt === 100){
      return '#2E7D32';
    }
    else{
      if(props.scoreFirstAttempt !== undefined && props.scoreSecondAttempt !== undefined && props.scoreSecondAttempt > props.scoreFirstAttempt){
        return '#FF8A65';
      }
      else{
        return '#AD1457';
      }
    }
}
</script>

<template>
  <ContentBlock :title="t('my-results')" :collapsible="true" v-model:open="openLocal" :is-subtitle-hidden="true">
    <div v-if="props.explanationFirstAttempt != null && props.explanationSecondAttempt != null">
      <h2>{{t('step')}} 1</h2>
      <div v-if="props.explanationFirstAttempt.answer.questionType !== 'OpenEnded'">
        <h4>{{t('choice')}}</h4>
        <div class="groupChoiceFrame">
          <ChoiceFrame v-for="a in answers" :value="a.itemIndex" :is-correct="a.isCorrect" :is-checked="answersCheckedFirstAttempt.includes(a.itemIndex)"/>
        </div>
        <h4>Score</h4>
        <v-card style="width: 100px; margin-bottom: 2%" :color="props.scoreFirstAttempt === 100 ? '#2E7D32' : '#AD1457'">
          <v-card-title>
            {{props.scoreFirstAttempt}}%
          </v-card-title>
        </v-card>
      </div>
      <ElExplanation v-if="props.explanationFirstAttempt.answer.explanation !== ''" :response="props.explanationFirstAttempt.answer" :number-of-peer-review="props.explanationFirstAttempt.nbPeer" :grade="explanationFirstAttempt?.grade"/>
      <h2 style="margin-top: 2%">{{t('step')}} 2</h2>
      <div v-if="props.explanationFirstAttempt.answer.questionType !== 'OpenEnded'">
        <h4>{{t('choice')}}</h4>
        <div class="groupChoiceFrame">
          <ChoiceFrame v-for="a in answers" :value="a.itemIndex" :is-correct="a.isCorrect" :is-checked="answersCheckedSecondAttempt.includes(a.itemIndex)"/>
        </div>
        <h4>Score</h4>
        <v-card style="width: 100px; margin-bottom: 2%; color: white" :color="defineColor()">
          <v-card-title>
            {{props.scoreSecondAttempt}}%
          </v-card-title>
        </v-card>
      </div>
      <ElExplanation v-if="props.explanationSecondAttempt?.answer.explanation !== ''" :response="props.explanationSecondAttempt?.answer" :number-of-peer-review="props.explanationSecondAttempt?.nbPeer" :grade="props.explanationSecondAttempt?.grade"/>
    </div>
    <div v-else>
      <v-alert :title="t('no-answer')" :text="t('v-alert-title')" :type="'warning'"></v-alert>
    </div>
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
    "choice": "Choice",
    "no-answer": "No answer",
    "v-alert-title": "You didn't provide any answer for this question"
  },
  "fr": {
    "my-results": "Mes résultats",
    "step": "Phase",
    "choice": "Choix",
    "no-answer" :"Pas de réponse",
    "v-alert-title": "Vous n'avez pas fournie de réponse pour cette question."
  }
}
</i18n>
