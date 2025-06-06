<script setup lang="ts">

import ContentBlock from "@/components/player/ContentBlock.vue";
import ConfrontingViewpoint from "@/components/evaluation/ConfrontingViewpoint.vue";
import ResponseForm from "@/components/response/ResponseForm.vue";
import {onMounted, type PropType, ref} from "vue";
import type {AnyResponse} from "@/models/Response";
import type {LikertValue} from "@/components/evaluation/Likert";
import ProgressBar from "@/components/progressBar/ProgressBar.vue";
import {useI18n} from "vue-i18n";
const props = defineProps({
  /**
   * The title of the question.
   */
  questionTitle: {
    type: String,
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
  /**
   * A boolean. True if the sequence is in progress. False if not.
   */
  sequenceInProgress: {
    type: Boolean,
    default: false
  }

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
const { t } = useI18n();
</script>

<template>
  <ProgressBar
          responseSubmissionState="COMPLETED"
          evaluationState="ACTIVE"
          readState="DISABLED"/>
  <v-alert :title="props.sequenceInProgress ? t('the-sequence-is-in-progress') : t('sequence-is-closed')" :color="props.sequenceInProgress ? '#BBDEFB' : 'white'" style="color: #1976D2; margin-bottom: 4%" elevation="1"></v-alert>
  <ContentBlock class="contentBlock"
                :title="questionTitle"
                :collapsible="true"
                :subtitle="props.updatedAnswer?.questionType"
                v-model:open="refpanelOpen">
    <p>{{ questionContent }}</p>
  </ContentBlock>
  <div v-if="props.sequenceInProgress">
    <ConfrontingViewpoint
            :responses="props.responsesToEvaluate"
            @evaluation-changed="handleGrades">
    </ConfrontingViewpoint>
    <br/>
    <br/>
    <ResponseForm
            :provided-answers="props.providedAnswers"
            :confidence-selections="[
              { label: t('not-confident-at-all'), value: t('not-confident-at-all') },
              { label: t('not-really-confident'), value: t('not-really-confident') },
              { label: t('confident'), value: t('confident') },
              { label: t('completely-confident'), value: t('completely-confident') }]"
            :answer="updatedAnswerLocal"
            @update:answer="handleAnswer"
            :textAlert="t('second-chance')">
    </ResponseForm>
    <v-btn class="bouton" color="secondary" @click="sendAnswer()" style="margin-top: 5%; margin-bottom: 5%; margin-left: 2%;">{{t('save')}}</v-btn>
  </div>
</template>

<style scoped>
.contentBlock {
  margin-bottom: 4%;
}

</style>
<i18n>
{
  "en": {
    "the-sequence-is-in-progress": "The sequence is in progress.",
    "second-chance": "You have a second chance to change your answer and your confident degree.",
    "save": "Save",
    "not-confident-at-all": "Not confident at all",
    "not-really-confident": "Not really confident",
    "confident": "Confident",
    "completely-confident": "Completely confident",
    "sequence-is-closed": "The sequence is closed."
  },
  "fr": {
    "the-sequence-is-in-progress": "La séquence est en cours.",
    "second-chance": "Vous disposez d'une deuxième chance pour changer votre réponse et votre degré de confiance.",
    "save": "Enregistrer",
    "not-confident-at-all": "Pas du tout confiant(e)",
    "not-really-confident": "Pas vraiment confiant(e)",
    "confident": "Confiant(e)",
    "completely-confident": "Tout à fait confiant(e)",
    "sequence-is-closed": "La séquence est close."
  }
}
</i18n>
