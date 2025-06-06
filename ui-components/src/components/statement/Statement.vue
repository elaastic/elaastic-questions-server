<script setup lang="ts">

import ContentBlock from "@/components/player/ContentBlock.vue";
import {type PropType, ref} from "vue";
import {useI18n} from "vue-i18n";
import type {QuestionType} from "@/models/Response";

const props = defineProps({
  /**
   * The title of the statement.
   */
  title: {
    type: String,
    default: "The title of this test statement"
  },
  /**
   * The content of the statement, given as a string but actually is HTML code.
   */
  contentStatement: {
    type: String,
    default: "<p>The content <strong>of</strong> this test statement</p>"
  },
  /**
   * The type of the question.
   */
  questionType: {
    type: Object as PropType<QuestionType>,
  },
  /**
   * The state of the collapsible block. 0 if the block is open, 1 if the block is closed.
   */
  panelOpen: {
    type: Boolean,
    default: true
  },
  /**
   * The state of the block's content. false if the content is shown, true if the content is hidden.
   */
  hideStatement: {
    type: Boolean,
    default: false
  },
  /**
   * The state of the first checkbox. false if the checkbox is checked, true if not.
   */
  check1: {
    type: Boolean,
    default: false
  },
  /**
   * The state of the second checkbox. false if the checkbox is checked, true if not.
   */
  check2: {
    type: Boolean,
    default: false
  },
  /**
   * The state of the third checkbox. false if the checkbox is checked, true if not.
   */
  check3: {
    type: Boolean,
    default: false
  },
  /**
   * A boolean. true if the type of the question is hidden. false if not.
   */
  HideQuestionType: {
    type: Boolean,
    default: false
  }
})
const refpanelOpen = ref(props.panelOpen);
const refQuestionType = ref(props.questionType);
const refHideQuestionType = ref(props.HideQuestionType);
const refhideStatement = ref(props.hideStatement);
const refcheck1 = ref(props.check1);
const refcheck2= ref(props.check2);
const refcheck3= ref(props.check3);
const changeStatement = () => {
  if (refcheck1.value) {
    refpanelOpen.value = false;
  } else {
    refpanelOpen.value = true;
  }
  if(refcheck2.value){
    refHideQuestionType.value = true;
  }
  else{
    refHideQuestionType.value = false;
  }
  refhideStatement.value = refcheck3.value;
};
const { t } = useI18n()
</script>

<template>
  <h1>{{t('statement')}}</h1>
  <div class="checkboxs">
    <v-checkbox class="checkbox" :label="t('panelClosed')" v-model="refcheck1"></v-checkbox>
    <v-checkbox class="checkbox" :label="t('hideQuestionType')" v-model="refcheck2"/>
    <v-checkbox class="checkbox" :label="t('hideStatement')" v-model="refcheck3"/>
  </div>
  <v-btn class="button" @click="changeStatement">{{t('send')}}</v-btn>
  <ContentBlock class="cb"
                :title="title"
                :subtitle="refQuestionType"
                :collapsible="true"
                v-model:open="refpanelOpen"
                v-model:isSubtitleHidden="refHideQuestionType"
  >
    <div v-html="contentStatement" v-if="!refhideStatement"></div>
  </ContentBlock>
</template>

<style scoped>
.checkbox{
  margin-right: 3%;
}
.checkboxs{
  display: flex;
}
.checkboxs ::v-deep(.v-label){
  font-size: small;
  font-weight: bold;
  color: black;
}
.button{
  margin-bottom: 9%;
  margin-top: -2%;
  background-color: lightgray;
}
.button:hover{
  background-color: darkgray;
}
</style>
<i18n>
{
  "en": {
     "statement": "Statement",
     "send": "Send",
     "panelClosed": "panelClosed",
     "hideQuestionType": "hideQuestionType",
     "hideStatement": "hideStatement"
  },
  "fr": {
     "statement": "Enoncé",
     "send": "Envoyer",
     "panelClosed": "Fermer le volet",
     "hideQuestionType": "Masquer le type de la question",
     "hideStatement": "Masquer l'énoncé"
  }
}
</i18n>
