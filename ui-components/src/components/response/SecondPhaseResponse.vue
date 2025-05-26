<script setup lang="ts">

import ContentBlock from "@/components/player/ContentBlock.vue";
import ConfrontingViewpoint from "@/components/evaluation/ConfrontingViewpoint.vue";
import ResponseForm from "@/components/response/ResponseForm.vue";
import {onMounted, type PropType, ref} from "vue";
import type {AnyResponse, QuestionType} from "@/models/Response";
import type {LikertValue} from "@/components/evaluation/Likert";
import ProgressBar from "@/components/progressBar/ProgressBar.vue";
import TextBar from "@/components/util/TextBar.vue";
const props = defineProps({
  /**
   * The title of the question.
   */
  questionTitle: {
    type: String,
  },
  /**
   * The type of the question. It could be 'OpenEnded' | 'ExclusiveChoice' | 'MultipleChoice'.
   */
  questionType: {
    type: Object as PropType<QuestionType>
  },
  /**
   * The content of the question.
   */
  questionContent: {
    type: String,
  },
  /**
   * A boolean. true if the panel is opened. false if it's closed.
   */
  panelOpen: {
    type: Boolean,
    default: true
  },
  /**
   * The number of possible answers at the question.
   */
  providedAnswers: {
    default: Number,
  },
  /**
   * The responses given by other students.
   */
  responsesToEvaluate: {
    type: Array as PropType<AnyResponse[]>,
    default: () => []
  },
  /**
   * The previous answer given by the user (during first phase).
   */
  updatedAnswer: {
    type: Object as PropType<AnyResponse>
  },

})
const grades : Array<{id: number, value: LikertValue }> = []
const emit = defineEmits(["update:updatedAnswer", "update:grades"])
const refpanelOpen = ref(props.panelOpen);
const updatedAnswerLocal = ref(props.updatedAnswer)
const handleGrades = (id: number, value: LikertValue) => {
  for (const grade of grades) {
    if(grade.id === id){
      grade.value = value
    }
  }
}
const handleAnswer = (val : AnyResponse) => {
  updatedAnswerLocal.value = val;
}
const sendAnswer = () => {
  emit("update:updatedAnswer", updatedAnswerLocal.value);
  emit("update:grades", grades);
  console.log(updatedAnswerLocal.value)
  console.log(grades)
}
onMounted(() => {
  for (const response of props.responsesToEvaluate) {
    grades.push({id: response.id, value: null})
  }
})
</script>

<template>
  <ProgressBar :steps="[false, true, false]"></ProgressBar>
  <TextBar value="La séquence est en cours." color="#BBDEFB" style="color: #1976D2"></TextBar>
  <ContentBlock class="cb"
          :title="questionTitle"
          :collapsible="true"
          :subtitle="questionType"
          v-model:open="refpanelOpen">
    <p>{{ questionContent }}</p>
  </ContentBlock>
  <ConfrontingViewpoint
          :responses="props.responsesToEvaluate"
          @evaluation-changed="handleGrades">
  </ConfrontingViewpoint>
  <br/>
  <br/>
  <ResponseForm
          :provided-answers="props.providedAnswers"
          :trust-selections="[
            { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
            { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
            { label: 'Confiant(e)', value: 'Confiant(e)' },
            { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' }]"
          :answer="updatedAnswerLocal"
          @update:answer="handleAnswer"
          :textAlert="'Vous disposez d\'une deuxième chance pour changer votre réponse et votre degré de confiance.'">
  </ResponseForm>
  <v-btn class="bouton" color="secondary" @click="sendAnswer()" style="margin-top: 5%; margin-bottom: 5%; margin-left: 4%;">Enregistrer</v-btn>
</template>

<style scoped>
.cb {
  margin-bottom: 4%;
}

</style>
