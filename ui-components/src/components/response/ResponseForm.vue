<script setup lang="ts">
import MultipleChoiceResponseInput from "@/components/response/MultipleChoiceResponseInput.vue";
import SelectorResponsive from "@/components/util/SelectorResponsive.vue";
import {type PropType, ref, watch} from "vue";
import type {Selection} from "@/components/util/SelectorResponsive.vue";
import {useI18n} from "vue-i18n";
import TipTapEditor from "@/components/response/TipTapEditor.vue";
import type {AnyResponse, ExclusiveChoiceResponse, MultipleChoiceResponse} from "@/models/Response";
import ExclusiveChoiceResponseItem from "@/components/response/ExclusiveChoiceResponseItem.vue";

const props = defineProps({
  /**
   * The possibles answers at the question
   */
  providedAnswers : {
    type: Number,
  },
  /**
   * The possibles degrees of confidence
   */
  confidenceSelections: {
    type: Array as PropType<Selection[]>,
    default: () => []
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
const selectedLocalConfiance= ref(props.answer.confidence);
const refqTYpe = ref(props.answer.questionType);

const text_ref = ref(props.answer.explanation);

watch(selectedLocalConfiance, (newValue) => {
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
</script>

<template>
  <div v-if="refqTYpe === 'MultipleChoice'">
    <v-alert v-if="selectedMultipleAnswers.length===0 || props.textAlert !== 'Veuillez soumettre une réponse'" :text="props.textAlert" type="info" variant="tonal" class="alert"></v-alert>
    <MultipleChoiceResponseInput class="resize" :nb-candidate-item="providedAnswers" v-model:selected="selectedMultipleAnswers" />
  </div>
  <div v-if="refqTYpe === 'ExclusiveChoice'">
    <ExclusiveChoiceResponseItem class="resize" :nb-candidate-item="providedAnswers" v-model:selected="selectedExclusiveAnswers" />
  </div>
  <div class="resize">
    <h5>{{refqTYpe !== 'OpenEnded' ? t('explanation')  :  t('textual-answer')}}</h5>
    <TipTapEditor v-model="text_ref"></TipTapEditor>
  </div>
  <div class="resize">
    <h5 class="degreeTitle" >{{t('confidence-degree')}}</h5>
    <SelectorResponsive  class="selector" :selections="confidenceSelections" v-model:selected="selectedLocalConfiance" />
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
    "explanation":  "Explanation"
  },
  "fr": {
    "textual-answer": "Réponse textuelle",
    "confidence-degree": "Votre degré de confiance",
    "save": "Enregistrer",
    "please-submit-a-response": "Veuillez soumettre une réponse",
    "explanation": "Explication"
  }
}
</i18n>
