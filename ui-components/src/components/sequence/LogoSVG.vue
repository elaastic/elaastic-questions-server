<script setup lang="ts">

import type {PropType} from "vue";

export type SequenceState = 'NOT_STARTED' | 'RESPONSE_PHASE' | 'CONFRONTING_VIEWPOINT' | 'RESULTS_PHASE' | 'CLOSED' | 'DISTANT' | 'BLENDED'
const props = defineProps({
  /**
   * The state to which the logo will correspond
   */
  state: {
    type: Object as PropType<SequenceState>
  }
})
</script>

<template>
    <svg
      class="mt-3" v-if="props.state === 'RESPONSE_PHASE' || props.state === 'BLENDED' || props.state === 'DISTANT'"
      xmlns="http://www.w3.org/2000/svg"
      width="100"
      height="50"
      viewBox="0 12 110 67"
      fill="none"
      :stroke="'lightgray'"
      stroke-width="4"
    >
      <ellipse cx="50" cy="35" rx="40" ry="20" :fill="'none'" />
      <path d="M40 60 L30 67 L50 60 Z" :fill="'none'" :stroke="'lightgray'" stroke-width="4" />
    </svg>


    <svg v-if="props.state === 'CONFRONTING_VIEWPOINT' || props.state === 'BLENDED' || props.state === 'DISTANT'"
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
      <path d="M120 65 L130 75 L110 65 Z" :fill="'lightgray'" :stroke="'lightgray'" stroke-width="4" />
    </svg>


    <svg
      v-if="props.state === 'RESULTS_PHASE' || props.state === 'DISTANT'"
      xmlns="http://www.w3.org/2000/svg"
      width="80"
      height="60"
      viewBox="0 0 100 80"
      fill="none"
      :stroke="'lightgray'"
      stroke-width="4"
    >
      <line x1="15" y1="10" x2="15" y2="70" :stroke="'lightgray'" stroke-width="4"/>
      <line x1="15" y1="70" x2="90" y2="70" :stroke="'lightgray'" stroke-width="4"/>
      <rect x="25" y="50" width="10" height="20" :fill="'lightgray'" />
      <rect x="45" y="35" width="10" height="35" :fill="'lightgray'" />
      <rect x="65" y="20" width="10" height="50" :fill="'lightgray'" />
    </svg>

    <svg
      class="mt-n4"
      v-if="props.state === 'CLOSED'"
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
      <path d="M30 60 V40 A20 20 0 0 1 77 40 V60" fill="none" stroke="lightgray" stroke-width="5"/>
    </svg>

    <svg
      class="mt-4"
      v-if="props.state === 'NOT_STARTED'"
      width="100"
      height="20"
      viewBox="0 0 100 20"
      xmlns="http://www.w3.org/2000/svg"
    >
      <line x1="30" y1="10" x2="70" y2="10" stroke="lightgray" stroke-width="6" stroke-linecap="round" />
    </svg>
</template>

<style scoped>

</style>
