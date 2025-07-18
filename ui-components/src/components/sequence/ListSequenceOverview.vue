<script lang="ts" xmlns:slot="http://www.w3.org/1999/html">
import type {Sequence} from "@/components/sequence/Sequence.types";

export interface ListSequenceOverviewProps {
  /**
   * All the sequences of a subject. It's an array of Object composed of 3 attributes : the state of the sequence so it can be 'NOT_STARTED' | 'CLOSED' | 'IN_PROGRESS' . The second attribute is a 2-uplet : the title and the statement of the question. The third attribute is the id of the sequence.
   */
  sequences: Sequence[]
}
</script>
<script setup lang="ts">

import {ref} from "vue";
import SequenceOverview from "@/components/sequence/SequenceOverview.vue"

const props = defineProps<ListSequenceOverviewProps>()

const emits = defineEmits(["changeSelectedQuestion"])

const selectedSequenceIndex = ref<number>(0)

const handleSelectedQuestion = (newValue: number) => {
  selectedSequenceIndex.value = newValue
  emits("changeSelectedQuestion", newValue)
}

</script>

<template>
  <v-list>
    <SequenceOverview
            v-for="(q, index) in sequences"
            :key="selectedSequenceIndex === index ? `selected-${index}` : index"
            :sequence="q"
            :sequenceIndex="index+1"
            :selected="index === selectedSequenceIndex"
            @click="() => handleSelectedQuestion(index)"
    />
  </v-list>
</template>

<style scoped></style>
