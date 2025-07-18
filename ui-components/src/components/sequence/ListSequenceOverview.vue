<script setup lang="ts">

import {type PropType, ref} from "vue";
import SequenceOverview from "@/components/sequence/SequenceOverview.vue"
import type {Sequence} from "@/components/sequence/Sequence.types";

const props = defineProps({
  /**
   * All the sequences of a subject. It's an array of Object composed of 3 attributes : the state of the sequence so it can be 'NOT_STARTED' | 'CLOSED' | 'IN_PROGRESS' . The second attribute is a 2-uplet : the title and the statement of the question. The third attribute is the id of the sequence.
   */
  sequences: {
    type: Array as PropType<Sequence[]>
  },
})

const emits = defineEmits(["changeSelectedQuestion"])

const selectedSequenceIndex = ref<number | null>(null)

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
