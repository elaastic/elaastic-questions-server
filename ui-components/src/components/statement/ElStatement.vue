<script setup lang="ts">

import ElContentBlock from "@/components/player/ElContentBlock.vue";
import {type PropType, ref} from "vue";
import {useI18n} from "vue-i18n";
import type {QuestionType} from "@/models/Response";

const props = defineProps({
  /**
   * The title of the statement.
   */
  title: {
    type: String,
    required: true
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
   * A boolean. true if the type of the question is hidden. false if not.
   */
  hideQuestionType: {
    type: Boolean,
    default: false
  }
})
const refpanelOpen = ref(props.panelOpen);
const refQuestionType = ref(props.questionType);
const refHideQuestionType = ref(props.hideQuestionType);
const refhideStatement = ref(props.hideStatement);
const { t } = useI18n()
</script>

<template>
  <h1>{{t('statement')}}</h1>
  <ElContentBlock class="cb"
                :title="title"
                :subtitle="refQuestionType"
                :collapsible="true"
                v-model:open="refpanelOpen"
                v-model:showSubtitle="refHideQuestionType"
  >
    <slot v-if="!refhideStatement"/>
  </ElContentBlock>
</template>

<style scoped>
</style>
<i18n>
{
  "en": {
     "statement": "Statement"
  },
  "fr": {
     "statement": "Enoncé"
  }
}
</i18n>
