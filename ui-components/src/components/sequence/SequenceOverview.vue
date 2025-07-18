<script lang="ts" xmlns:slot="http://www.w3.org/1999/html">
import { type Sequence } from '@/components/sequence/Sequence.types'
import { ref } from 'vue'

export interface SequenceOverviewProps {
  /**
   * The sequence presented by this component.
   */
  sequence: Sequence

  /**
   * Index of the sequence in the list of sequences.
   */
  sequenceIndex: number

  /**
   * A boolean. True if the sequence is selected. False if not.
   * False by default.
   */
  selected?: boolean
}
</script>

<script setup lang="ts">
import { computed } from 'vue'
import SequenceIcon from '@/components/sequence/SequenceIcon.vue'
import { SequenceStatus } from '@/components/sequence/Sequence.types'

const props = withDefaults(defineProps<SequenceOverviewProps>(), {
  selected: false,
})

/**
 * Convert the sequence state into a set of icons representing the status and / or the open phases
 */
const sequenceIcons = computed(() => {
  switch (props.sequence.state.sequenceStatus) {
    case SequenceStatus.NOT_STARTED:
    case SequenceStatus.CLOSED:
      return [props.sequence.state.sequenceStatus]
    case SequenceStatus.IN_PROGRESS:
      return props.sequence.state.phases
    default:
      console.error(`Unknown sequence status: ${props.sequence.state}`)
      throw 'Unknown sequence status'
  }
})

const statementVisible = computed(() => props.sequence.state.sequenceStatus !== SequenceStatus.NOT_STARTED)

const filteredStatement = computed(() => {
  if (!props.sequence.question?.statement) return ''
  return truncateText(props.sequence.question.statement)
})

function truncateText(statement: string): string {
  const withoutImages = statement.replace(/<img[^>]*>/gi, '')
  const match = withoutImages.match(/<p[^>]*>(.*?)<\/p>/i)
  return match ? match[1].trim() : ''
}

const selectedSequence = ref(props.selected ? [props.sequence.id] : false)
</script>

<template>
  <v-card>
    <v-list v-model:selected="selectedSequence" select-strategy="leaf" class="py-0">
      <v-list-item :value="props.sequence.id" active-class="bg-yellow-lighten-4" class="py-3">
        <v-list-item-title class="text-h6 py-3 font-weight-bold">
          <v-chip label>{{ sequenceIndex }}</v-chip>
          <span class="ml-3">{{ sequence.question.title }}</span>
        </v-list-item-title>

        <div v-if="statementVisible" class="text-truncate" v-html="filteredStatement" />

        <template v-slot:append>
          <v-list-item-action class="flex-column align-end">
            <SequenceIcon v-for="icon in sequenceIcons"
                          v-bind:key="icon"
                          :iconId="icon"
                          size="1.5em" />
          </v-list-item-action>
        </template>
      </v-list-item>
    </v-list>
  </v-card>
</template>

<style scoped></style>
