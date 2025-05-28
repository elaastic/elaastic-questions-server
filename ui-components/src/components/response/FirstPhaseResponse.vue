<script setup lang="ts">

import ProgressBar from "@/components/progressBar/ProgressBar.vue";
import TextBar from "@/components/util/TextBar.vue";
import ContentBlock from "@/components/player/ContentBlock.vue";
import {type PropType, ref} from "vue";
import type {AnyResponse} from "@/models/Response";
import ResponseForm from "@/components/response/ResponseForm.vue";
import {useI18n} from "vue-i18n";
const props = defineProps({
  /**
   * The title of the question.
   */
  questionTitle: {
    type: String
  },
  /**
   * The content of the question.
   */
  questionContent: {
    type: String
  },
  /**
   * The number of possible answers at the question.
   */
  providedAnswers: {
    type: Number,
  },
  /**
   * The answer given by the user during the first phase.
   */
  firstAnswer: {
    type: Object as PropType<AnyResponse>
  },
  /**
   * A boolean. true if the panel of the question is opened. false if it's closed.
   */
  panelOpen: {
    type: Boolean,
    default: true
  },
  sequenceInProgress: {
    type: Boolean,
    default: false,
  }
})

const emits = defineEmits(['update:firstAnswer'])

const refpanelOpen = ref(props.panelOpen);
const firstAnswerLocal = ref(props.firstAnswer);
const handleFirstAnswer = (newAnswer: AnyResponse) => {
  firstAnswerLocal.value = newAnswer;
}
const sendAnswer = () => {
  emits('update:firstAnswer', firstAnswerLocal.value);
  console.log(firstAnswerLocal.value)
}

const { t } = useI18n()
</script>

<template>
  <ProgressBar :steps="[true, false, false]"/>
  <TextBar :value="props.sequenceInProgress ? t('the-sequence-is-in-progress') : t('sequence-is-closed')" :color="props.sequenceInProgress ? '#BBDEFB' : 'white'"/>
  <ContentBlock class="resize" :title="props.questionTitle" :subtitle="props.firstAnswer?.questionType" :collapsible="true" v-model:open="refpanelOpen">{{props.questionContent}}</ContentBlock>
  <div v-if="props.sequenceInProgress">
    <ContentBlock class="resize" title="Réponse" :is-subtitle-hidden="true">
      <ResponseForm
              :provided-answers="props.providedAnswers"
              :answer="firstAnswerLocal"
              @update:answer="handleFirstAnswer"
              :trust-selections="[
                { label: t('not-confident-at-all'), value: t('not-confident-at-all') },
                { label: t('not-really-confident'), value: t('not-really-confident') },
                { label: t('confident'), value: t('confident') },
                { label: t('completely-confident'), value: t('completely-confident') }]"
      ></ResponseForm>
      <v-btn style="margin-top: 3%; margin-left:4% " color="secondary" @click="sendAnswer">{{ t('save') }}</v-btn>
    </ContentBlock>
  </div>
</template>

<style scoped>
.resize{
  margin-top:4%
}
</style>

<i18n>
{
  "en": {
    "the-sequence-is-in-progress": "The sequence is in progress.",
    "save": "Save",
    "not-confident-at-all": "Not confident at all",
    "not-really-confident": "Not really confident",
    "confident": "Confident",
    "completely-confident": "Completely confident",
    "sequence-is-closed": "Sequence is closed"
  },
  "fr": {
    "the-sequence-is-in-progress": "La séquence est en cours.",
    "save": "Enregistrer",
    "not-confident-at-all": "Pas du tout confiant(e)",
    "not-really-confident": "Pas vraiment confiant(e)",
    "confident": "Confiant(e)",
    "completely-confident": "Tout à fait confiant(e)",
    "sequence-is-closed": "La séquence est close."
  }
}
</i18n>
