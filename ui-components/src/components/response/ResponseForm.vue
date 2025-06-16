<script setup lang="ts">
import MultipleChoiceResponseInput from "@/components/response/MultipleChoiceResponseInput.vue";
import type {Selection} from "@/components/util/SelectorResponsive.vue";
import SelectorResponsive from "@/components/util/SelectorResponsive.vue";
import {type PropType, ref, watch} from "vue";
import {useI18n} from "vue-i18n";
import TipTapEditor from "@/components/inputs/TipTapEditor.vue";
import {
  type AnyResponse,
  ConfidenceDegree,
  type ExclusiveChoiceResponse,
  type MultipleChoiceResponse
} from "@/models/Response";
import ExclusiveChoiceResponseItem from "@/components/response/ExclusiveChoiceResponseItem.vue";

const props = defineProps({
  /**
   * The possibles answers at the question
   */
  providedAnswers : {
    type: Number,
  },
  /**
   * The answer proposed by the user. Composed of: an id, the questionType, an explanation and a degree of confidence for OpenEndedQuestion. Add choices for MultipleChoiceQuestion or choice for ExclusiveChoiceQuestion. All initialised empty but could also continue an old answer.
   */
  answer: {
    type: Object as PropType<AnyResponse>,
    default: () => ({}),
  },
  /**
   * The text of the alert (here it's an information)
   */
  textAlert: {
    type: String,
    default: "Veuillez soumettre une réponse"
  }

});

const emit = defineEmits<{
  (e: 'update:answer', value: AnyResponse): void;
}>();


const selectedMultipleAnswers = ref(
        props.answer.questionType === 'MultipleChoice'
                ? [...(props.answer as MultipleChoiceResponse).choices]
                : []
);

const selectedExclusiveAnswers = ref<number>(
        props.answer.questionType === 'ExclusiveChoice'
                ? (props.answer as ExclusiveChoiceResponse).choice
                : 0
);
const selectedLocalConfidence= ref(props.answer.confidence);
const refqTYpe = ref(props.answer.questionType);
const text_ref = ref(props.answer.explanation);

watch(selectedLocalConfidence, (newValue) => {
  const submitedAnswer={
    ...props.answer,
    confidence: newValue,
  };
  emit('update:answer', submitedAnswer);
})
watch(text_ref, (newValue) => {
  const submitedAnswer={
    ...props.answer,
    explanation: newValue,
  };
  emit('update:answer', submitedAnswer);
})
watch(selectedMultipleAnswers, (newValue) => {
  const submitedAnswer={
    ...props.answer,
    ...(refqTYpe.value === "MultipleChoice"
            ? { choices: newValue }
            : {})
  };
  emit('update:answer', submitedAnswer);
})
watch(selectedExclusiveAnswers, (newValue) => {
  const submitedAnswer={
    ...props.answer,
    ...(refqTYpe.value === "ExclusiveChoice"
            ? { choice: newValue }
            : {})
  };
  emit('update:answer', submitedAnswer);
})

const { t } = useI18n()

const handleSelected = (selected: Selection) => {
  if(selected.value === t('not-confident-at-all')){
    selectedLocalConfidence.value = ConfidenceDegree.NOT_CONFIDENT_AT_ALL
  }
  else if(selected.value === t('not-really-confident')){
    selectedLocalConfidence.value = ConfidenceDegree.NOT_REALLY_CONFIDENT
  }
  else if(selected.value === t('confident')){
    selectedLocalConfidence.value = ConfidenceDegree.CONFIDENT
  }
  else if(selected.value === 'Tout à fait confiant(e)'){
    selectedLocalConfidence.value = ConfidenceDegree.TOTALLY_CONFIDENT
  }
}
const confidenceByDefault =
        props.answer.confidence === ConfidenceDegree.TOTALLY_CONFIDENT ? ref(t('completely-confident'))
                : props.answer.confidence === ConfidenceDegree.NOT_REALLY_CONFIDENT ? ref(t('not-really-confident'))
                        : props.answer.confidence === ConfidenceDegree.NOT_CONFIDENT_AT_ALL ? ref(t('not-confident-at-all'))
                                :ref(t('confident'));

const confidenceSelections = [
  { label: t('not-confident-at-all'), value: t('not-confident-at-all') },
  { label: t('not-really-confident'), value: t('not-really-confident') },
  { label: t('confident'), value: t('confident') },
  { label: t('completely-confident'), value: t('completely-confident') }];

</script>

<template>
  <div v-if="refqTYpe === 'MultipleChoice'">
    <v-alert v-if="selectedMultipleAnswers.length===0 || props.textAlert !== t('please-submit-a-response')" :text="props.textAlert" type="info" variant="tonal" class="alert"></v-alert>
    <MultipleChoiceResponseInput class="resize" :nb-candidate-item="providedAnswers" v-model:selected="selectedMultipleAnswers" />
  </div>
  <div v-if="refqTYpe === 'ExclusiveChoice'">
    <ExclusiveChoiceResponseItem class="resize" :nb-candidate-item="providedAnswers" v-model:selected="selectedExclusiveAnswers" />
  </div>
  <div class="resize">
    <h5>{{refqTYpe !== 'OpenEnded' ? t('explanation')  :  t('your-answer')}}</h5>
    <TipTapEditor v-model="text_ref"></TipTapEditor>
  </div>
  <div class="resize">
    <h5 class="degreeTitle" >{{t('confidence-degree')}}</h5>
    <SelectorResponsive  class="selector" :selections="confidenceSelections" v-model:selected="confidenceByDefault" @change-selection="handleSelected"/>
  </div>
</template>

<style scoped>
.selector{
  margin-top: 5%;
  margin-bottom: 5%;
}
.resize{
  margin-left: 4%;
  margin-right: 4%;
}
.degreeTitle{
  margin-top: 3%;
}
.alert{
  margin-bottom: 2%;
}
</style>
<i18n>
  {
  "en": {
    "textual-answer": "Textual answer",
    "confidence-degree": "Confidence degree",
    "save": "Save",
    "please-submit-a-response": "Please submit a response",
    "explanation":  "Explanation",
    "your-answer": "Your answer  ",
    "not-confident-at-all": "Not confident at all",
    "not-really-confident": "Not really confident",
    "confident": "Confident",
    "completely-confident": "Completely confident"
  },
  "fr": {
    "textual-answer": "Réponse textuelle",
    "confidence-degree": "Votre degré de confiance",
    "save": "Enregistrer",
    "please-submit-a-response": "Veuillez soumettre une réponse",
    "explanation": "Explication",
    "your-answer": "Votre réponse  ",
    "not-confident-at-all": "Pas du tout confiant(e)",
    "not-really-confident": "Pas vraiment confiant(e)",
    "confident": "Confiant(e)",
    "completely-confident": "Tout à fait confiant(e)"
  }
}
</i18n>
