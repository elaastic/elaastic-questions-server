<script setup lang="ts">
import TextBar from "@/components/util/TextBar.vue";
import QCM from "@/components/response/QCM.vue";
import SelectorResponsive from "@/components/util/SelectorResponsive.vue";
import {type PropType, ref} from "vue";
import type {Selection} from "@/components/util/SelectorResponsive.vue";
import {useI18n} from "vue-i18n";
import TipTapEditor from "@/components/response/TipTapEditor.vue";

const props = defineProps({
  /**
   * The possibles answers at the question
   */
  providedAnswers: {
    type: Array as PropType<string[]>,
    default: () => []
  },
  /**
   * The answers selected by the user
   */
  selectedAnswers: {
    type: Array as PropType<string[]>,
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
   * The text which will be written on the editor when coming on this page
   */
  defaultText: {
    type: String,
    default: "Contenu par défaut"
  },
  /**
   * A boolean. True : It's a multiple choice question. False : It's not.
   */
  isMCQ: {
    type: Boolean,
    default: true,
  },
  /**
   * A boolean. True : The user has sent his answer to the question by click on the button. False : The user hasn't clicked yet.
   */
  isSend: {
    type: Boolean,
    default: false,
  }

});
const selectedLocalAnswers = ref([...props.selectedAnswers]);
const selectedLocalConfiance= ref(props.selectedConfiance);
const refIsValidate = ref(props.isSend);
const refIsMCQ = ref(props.isMCQ);

const text_ref = ref(props.defaultText);

const sendAnswer = () => {
  if(refIsMCQ.value){
    if(selectedLocalAnswers.value.length!==0){
      refIsValidate.value = true;
    }
  }
  if(!refIsMCQ.value){
    refIsValidate.value = true;
  }

}
const { t } = useI18n()
</script>

<template>
  <div v-if="!refIsValidate">
    <h1 class="h1Title">{{t('answer')}}</h1>
    <v-card>
      <div v-if="isMCQ">
        <TextBar v-if="selectedLocalAnswers.length===0" color="red" value="Veuillez soumettre une réponse"></TextBar>
        <QCM class="resize" :answers="providedAnswers" v-model:selected="selectedLocalAnswers" />
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
    </v-card>
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
  .h1Title{
    margin-bottom: 5%;
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
    "answer": "Answer  ",
    "textual-answer": "Textual answer",
    "trust-degree": "Trust degree",
    "save": "Save",
    "answer-sent": "Answer sent"
  },
  "fr": {
    "answer": "Réponse  ",
    "textual-answer": "Réponse textuelle",
    "trust-degree": "Votre degré de confiance",
    "save": "Enregistrer",
    "answer-sent": "Réponse Envoyée"
  }
}
</i18n>
