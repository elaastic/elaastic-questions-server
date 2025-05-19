<script setup lang="ts">
import QCM from "@/components/response/QCM.vue";
import SelectorResponsive from "@/components/util/SelectorResponsive.vue";
import {type PropType, ref, watch} from "vue";
import type {Selection} from "@/components/util/SelectorResponsive.vue";
import {useI18n} from "vue-i18n";
import TipTapEditor from "@/components/response/TipTapEditor.vue";
import type {AnyResponse, ExclusiveChoiceResponse, MultipleChoiceResponse} from "@/models/Response";
import ExclusiveQuestion from "@/components/response/ExclusiveQuestion.vue";

const props = defineProps({
  /**
   * The possibles answers at the question
   */
  providedAnswers: {
    type: Array as PropType<number[]>,
    default: () => []
  },
  /**
   * The possibles degrees of trust
   */
  trustSelections: {
    type: Array as PropType<Selection[]>,
    default: () => []
  },
  /**
   * The answer proposed by the user. Composed of: an id, the questionType, an explanation and a degree of trust for OpenEndedQuestion. Add choices for MultipleChoiceQuestion or choice for ExclusiveChoiceQuestion. All initialised empty but could also continue an old answer.
   */
  answer: {
    type: Object as PropType<AnyResponse>,
    default: () => ({}),
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
const selectedLocalConfiance= ref(props.answer.trust);
const refqTYpe = ref(props.answer.questionType);

const text_ref = ref(props.answer.explanation);

watch(selectedLocalConfiance, (newValue) => {
  const submitedAnswer={
    ...props.answer,
    trust: newValue,
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
      <v-alert v-if="selectedMultipleAnswers.length===0" :text="t('please-submit-a-response')" type="info"></v-alert>
      <QCM class="resize" :answers="providedAnswers" v-model:selected="selectedMultipleAnswers" />
    </div>
    <div v-if="refqTYpe === 'ExclusiveChoice'">
      <ExclusiveQuestion class="resize" :answers="providedAnswers" v-model:selected="selectedExclusiveAnswers" />
    </div>
    <div class="resize">
      <h5>{{t('textual-answer')}}</h5>
      <TipTapEditor v-model="text_ref"></TipTapEditor>
    </div>
    <div class="resize">
      <h5 class="degreeTitle" >{{t('trust-degree')}}</h5>
      <SelectorResponsive  class="selector" :selections="trustSelections" v-model:selected="selectedLocalConfiance" />
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
</style>
<i18n>
  {
  "en": {
    "textual-answer": "Textual answer",
    "trust-degree": "Trust degree",
    "save": "Save",
    "please-submit-a-response": "Please submit a response"
  },
  "fr": {
    "textual-answer": "Réponse textuelle",
    "trust-degree": "Votre degré de confiance",
    "save": "Enregistrer",
    "please-submit-a-response": "Veuillez soumettre une réponse"
  }
}
</i18n>
