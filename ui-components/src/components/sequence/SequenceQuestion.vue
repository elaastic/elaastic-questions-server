<script setup lang="ts">
import ChoiceChip from '@/components/response/ChoiceChip.vue'
import { computed, type PropType } from 'vue'

export type Question = { title: string; questionNumber: number }
export type SequenceState = 'NOT_STARTED' | 'RESPONSE_PHASE' | 'CONFRONTING_VIEWPOINT' | 'RESULTS_PHASE' | 'CLOSED'

const props = defineProps({
  /**
   * The question of the sequence. It is composed of the title and the number of the question.
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
   * The state of the sequence. It could be : 'NOT_STARTED' | 'RESPONSE_PHASE' | 'CONFRONTING_VIEWPOINT' | 'RESULTS_PHASE' | 'CLOSED'
   */
  sequenceState: {
    type: Object as PropType<SequenceState>,
    required: true,
  },
})
const color = computed(() => {
  return props.isSelected ? '#f9fbe7' : 'white'
})
</script>

<template>
  <v-card class="border-sm rounded-0" :max-height="125" :elevation="0" :style="'background-color: ' + color">
    <v-card-title class="font-weight-bold">
      <v-chip label>{{ question?.questionNumber }}</v-chip>
      {{ props.question.title }}

      <span v-show="!isSelected">
        <svg
          class="position-absolute right-0 mt-3"
          v-if="props.sequenceState === 'RESPONSE_PHASE'"
          xmlns="http://www.w3.org/2000/svg"
          width="100"
          height="50"
          viewBox="0 0 110 67"
          fill="none"
          :stroke="'lightgray'"
          stroke-width="4"
        >
          <ellipse cx="50" cy="35" rx="40" ry="20" :fill="'none'" />
          <path d="M40 60 L30 67 L50 60 Z" :fill="'none'" :stroke="'lightgray'" stroke-width="4" />
        </svg>

        <svg
          class="position-absolute right-0 mt-3"
          v-if="props.sequenceState === 'CONFRONTING_VIEWPOINT'"
          xmlns="http://www.w3.org/2000/svg"
          width="120"
          height="60"
          viewBox="0 0 160 90"
          fill="none"
          :stroke="'lightgray'"
          stroke-width="4"
        >
          <ellipse cx="50" cy="40" rx="40" ry="22" :fill="'none'" />
          <path d="M40 65 L30 75 L50 65 Z" :fill="'none'" :stroke="'lightgray'" stroke-width="4" />
          <ellipse cx="110" cy="40" rx="40" ry="22" :fill="'lightgray'" />
          />
          <path d="M120 65 L130 75 L110 65 Z" :fill="'lightgray'" :stroke="'lightgray'" stroke-width="4" />
        </svg>

        <svg
          class="position-absolute right-0 mt-3"
          v-if="props.sequenceState === 'RESULTS_PHASE'"
          xmlns="http://www.w3.org/2000/svg"
          width="80"
          height="60"
          viewBox="0 0 100 80"
          fill="none"
          :stroke="'lightgray'"
          stroke-width="4"
        >
          <line x1="15" y1="10" x2="15" y2="70" :stroke="'lightgray'" stroke-width="4" />
          <line x1="15" y1="70" x2="90" y2="70" :stroke="'lightgray'" stroke-width="4" />
          <rect x="25" y="50" width="10" height="20" :fill="'lightgray'" />
          <rect x="45" y="35" width="10" height="35" :fill="'lightgray'" />
          <rect x="65" y="20" width="10" height="50" :fill="'lightgray'" />
        </svg>

        <svg
          class="position-absolute right-0"
          v-if="props.sequenceState === 'CLOSED'"
          width="100"
          height="100"
          viewBox="0 0 100 150"
          xmlns="http://www.w3.org/2000/svg"
        >
          <rect
            x="20"
            y="60"
            width="70"
            height="40"
            rx="10"
            ry="10"
            fill="lightgray"
            stroke="lightgray"
            stroke-width="2"
          />
          <path d="M30 60 V40 A20 20 0 0 1 77 40 V60" fill="none" stroke="lightgray" stroke-width="5" />
        </svg>

        <svg
          class="position-absolute right-0 mt-7"
          v-if="props.sequenceState === 'NOT_STARTED'"
          width="100"
          height="20"
          viewBox="0 0 100 20"
          xmlns="http://www.w3.org/2000/svg"
        >
          <line x1="30" y1="10" x2="70" y2="10" stroke="lightgray" stroke-width="6" stroke-linecap="round" />
        </svg>
      </span>
    </v-card-title>
    <v-card-text :style="props.isSelected ? 'color: #5D4037' : 'color: black'" style="margin-right: 85px">
      <slot />
    </v-card-text>
  </v-card>
</template>

<style scoped></style>
