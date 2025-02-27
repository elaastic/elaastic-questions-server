<script setup lang="ts">

import UtilityGrade from "@/components/moderation/UtilityGrade.vue";
import {useI18n} from "vue-i18n";
import ReportModal from '@/components/moderation/report/ReportModal.vue'

const {t} = useI18n()

export interface EvaluationReactionProps {
  /**
   * Wether the evaluation has been done by ChatGPT or not
   */
  evaluationFromChatGpt: boolean
  /**
   * Whether the user is a teacher or not
   */
  viewByTeacher: boolean,
  /**
   * The selected grade if any
   */
  selectedGrade: string | null
  /**
   * The content to report
   */
  contentToReport: string
}

export interface EvaluationReactionEvents {
  (event: 'submitUtilityGrade', gradeSelected: string): void;

  (event: 'submitReport', reportReason: string[], reportDetail: string): void
}

const props = defineProps<EvaluationReactionProps>()
const emit = defineEmits<EvaluationReactionEvents>()

function submitUtilityGrade(gradeSelected: string) {
  emit('submitUtilityGrade', gradeSelected)
}

function submitReport(reportReason: string[], reportDetail: string) {
  emit('submitReport', reportReason, reportDetail)
}

</script>

<template>
  <v-row id="evaluation-reaction-container">
    <v-col>
      <UtilityGrade :evaluation-from-chat-gpt="props.evaluationFromChatGpt" :view-by-teacher="props.viewByTeacher" :selected-grade="props.selectedGrade"
                    @submitUtilityGrade="submitUtilityGrade"/>
    </v-col>
    <v-col v-if="!props.viewByTeacher">
      <ReportModal :content-to-report="contentToReport" :display-as-dialog="false" @submitReport="submitReport"/>
    </v-col>
  </v-row>
</template>

<style scoped>
  #evaluation-reaction-container {
    flex-direction: column;
  }
</style>

<i18n>
{
  "en": {
    "report": "Report"
  },
  "fr": {
    "report": "Signaler"
  }
}
</i18n>
