<script setup lang="ts">

import {type PropType, ref} from "vue";
import SequenceQuestion, {type Question} from "@/components/sequence/SequenceQuestion.vue";
import type {SequenceState} from "@/components/sequence/LogoSVG.vue";

const props = defineProps({
  /**
   * All the questions of a subject. It's an array of Object composed of 2 attributes : the state of the sequence related to the question so it can be 'NOT_STARTED' | 'RESPONSE_PHASE' | 'CONFRONTING_VIEWPOINT' | 'RESULTS_PHASE' | 'CLOSED' | 'DISTANT' | 'BLENDED'. The second attribute is a 3-uplet : the title, the statement, and the number of the question.,
   */
  questions: {
    type: Array as PropType<{ state: SequenceState, question: Question }[]>,
  },
  /**
   * A boolean. True if statements are hidden. False if they are displayed.
   */
  hideStatements: {
    type: Boolean,
    default: false
  }
})

const emits = defineEmits(["changeSelectedQuestion"])

const selectedQuestion = ref<number>(1)
const handleSelectedQuestion = (newValue : number) => {
  selectedQuestion.value = newValue;
  emits("changeSelectedQuestion", newValue)
}

</script>

<template>
  <SequenceQuestion
          v-for="q in questions"
          :sequence-state="q.state"
          :question="q.question"
          :is-selected="q.question.questionNumber === selectedQuestion"
          :hide-statement="hideStatements"
          @click="handleSelectedQuestion(q.question.questionNumber)"
  />
</template>

<style scoped></style>
