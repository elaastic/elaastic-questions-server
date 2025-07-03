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
  /**
   * A boolean. True if the statement is hidden. False if it's displayed.
   */
  hideStatement: {
    type: Boolean,
    default: false
  }
})
const filteredStatement = computed(() => {
  if (!props.question?.statement) return '';
  return props.question.statement.replace(/<img[^>]*>/gi, '');
});
</script>

<template>
  <v-card
          :class="['border-sm','rounded-0', props.isSelected ? 'background_orange' : 'background_black']"
          :max-height="125"
          :elevation="0"
  >
    <v-card-title class="font-weight-bold mt-4">
      <v-chip label>{{ question?.questionNumber }}</v-chip>
      <span class="ml-4">{{ question.title }}</span>
    </v-card-title>

    <v-card-text
            :class="['d-flex', props.isSelected ? 'text_orange' : 'text_black']"
    >
      <div v-if="!hideStatement" class="flex-1-1-0 mr-4" v-html="filteredStatement" />
      <LogoSVG
              v-if="!isSelected && !hideStatement"
              :state="props.sequenceState"
      />
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
.background_white{
  background-color:white;
}
.background_orange{
  background-color: #f9fbe7;
}
</style>
