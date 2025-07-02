<script setup lang="ts">

import { computed, type PropType } from 'vue'
import LogoSVG, {type SequenceState} from "@/components/sequence/LogoSVG.vue";

export type Question = { title: string; statement: string; questionNumber: number }

const props = defineProps({
  /**
   * The question of the sequence. It is composed of a title, the statement and the number of the question.
   */
  question: {
    type: Object as PropType<Question>,
    required: true,
  },
  /**
   * A boolean. True if the sequence is selected. False if not.
   */
  isSelected: {
    type: Boolean,
    default: false,
  },
  /**
   * The state of the sequence. It could be : 'NOT_STARTED' | 'RESPONSE_PHASE' | 'CONFRONTING_VIEWPOINT' | 'RESULTS_PHASE' | 'CLOSED' | 'DISTANT' | 'BLENDED'
   */
  sequenceState: {
    type: Object as PropType<SequenceState>,
    required: true,
  },
})
</script>

<template>
  <v-card class="border-sm rounded-0" :max-height="125" :elevation="0" :style="props.isSelected ? 'background-color: #f9fbe7' : 'background-color:white'">
    <v-card-title class="font-weight-bold">
      <v-chip label>{{ question?.questionNumber }}</v-chip>
      {{ question.title }}
      <span  class="position-absolute right-0 mt-7" >
        <LogoSVG v-if="!isSelected" :state="props.sequenceState" />
      </span>
    </v-card-title>
    <v-card-text
            :class="props.isSelected ? 'text_orange' : 'text_black'"
            style="margin-right: 280px"
            v-html="question.statement"
    >
    </v-card-text>
  </v-card>
</template>

<style scoped>
.text_black{
  color: black;
}
.text_orange{
  color: #5D4037;
}
</style>
