<script setup lang="ts">
import TextBar from "@/components/util/TextBar.vue";
import QCM from "@/components/response/QCM.vue";
import SelectorResponsive from "@/components/util/SelectorResponsive.vue";
import {type PropType, ref} from "vue";
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
  selectionsConfiance: {
    type: Array as PropType<Selection[]>,
    default: () => []
  },
  /**
   * The selected degree of trust by the user
   */
  selectedConfiance: {
    type: String,
    default:"Confiant(e)"
  },
  /**
   * A boolean. True : The user has sent his answer to the question by click on the button. False : The user hasn't clicked yet.
   */
  isSend: {
    type: Boolean,
    default: false,
  },
  /**
   * The answer proposed by the user. Composed of: an id, the questionType and an explanation for OpenEndedQuestion. Add choices for MultipleChoiceQuestion or choice for ExclusiveChoiceQuestion. All initialised empty but could continue an old answer.
   */
  answer: {
    type: Object as PropType<AnyResponse>,
    default: () => ({}),
  }

});
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
const selectedLocalConfiance= ref(props.selectedConfiance);
const refIsValidate = ref(props.isSend);
const refqTYpe = ref(props.answer.questionType);

const text_ref = ref(props.answer.explanation);

const sendAnswer = () => {
  if(refqTYpe.value === "MultipleChoice"){
    if(selectedMultipleAnswers.value.length!==0){
      refIsValidate.value = true;
    }
  }
  else{
    refIsValidate.value = true;
  }

}
const { t } = useI18n()
</script>

<template>
  <div v-if="!refIsValidate">
      <div v-if="refqTYpe === 'MultipleChoice'">
        <TextBar v-if="selectedMultipleAnswers.length===0" color="red" value="Veuillez soumettre une réponse"></TextBar>
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
        <SelectorResponsive  class="selector" :selections="selectionsConfiance" v-model:selected="selectedLocalConfiance" />
      </div>
      <v-btn class="bouton" color="secondary" @click="sendAnswer()">{{t('save')}}</v-btn>
  </div>
  <div v-if="refIsValidate">
    <h1 class="main-title">{{t('answer-sent')}}</h1>
  </div>
</template>

<style scoped>
  .selector{
    margin-top: 5%;
    margin-bottom: 5%;
  }
  .bouton{
    margin-top: 5%;
    margin-bottom: 5%;
    margin-left: 4%;
  }
  .resize{
    margin-left: 4%;
    margin-right: 4%;
  }
  .degreeTitle{
    margin-top: 3%;
  }
  .main-title {
    text-align: center;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    font-size: 50px;
    color: #333;
    margin-top: 50px;
    margin-bottom: 30px;
    font-weight: 600;
    letter-spacing: 1px;
    position: relative;
  }

  .main-title::after {
    content: "✔️";
    display: block;
    font-size: 50px;
    color: green;
    margin: 10px auto 0 auto;
  }
</style>
<i18n>
  {
  "en": {
    "textual-answer": "Textual answer",
    "trust-degree": "Trust degree",
    "save": "Save",
    "answer-sent": "Answer sent"
  },
  "fr": {
    "textual-answer": "Réponse textuelle",
    "trust-degree": "Votre degré de confiance",
    "save": "Enregistrer",
    "answer-sent": "Réponse Envoyée"
  }
}
</i18n>
