<!--
  Display a an icon representing a sequence status or a phase

  @prop {SequenceStatus | Phase} iconId - The status or phase to display
  @prop {string} [size] - Optional size of the icon (e.g., "24px", "1.5rem")

-->
<script setup lang="ts">
import { computed } from 'vue'
import { SequenceStatus } from '@/components/sequence/Sequence.types'
import { Phase } from '@/components/sequence/Sequence.types'

/**
 * Interface describing the props accepted by the SequenceIcon component
 */
export interface SequenceIconProps {
  iconId: SequenceStatus | Phase
  size?: string
}

const props = defineProps<SequenceIconProps>()

/**
 * Map the iconId to the corresponding Semantic UI icon name
 */
const icon = computed(() => {
  switch (props.iconId) {
    case SequenceStatus.NOT_STARTED:
      return 'minus'

    case Phase.RESPONSE:
      return 'comment'

    case Phase.CONFRONTING_VIEWPOINT:
      return 'comments'

    case Phase.RESULTS:
      return 'bar chart'

    case SequenceStatus.CLOSED:
      return 'lock'

    default:
      throw new Error(`Unknown sequence icon type: ${props.iconId}`)
  }
})

const sizeStyle = computed(() => (props.size ? { fontSize: props.size } : {}))
</script>

<template>
  <i :class="`mt-2 big grey ${icon} icon`" :style="sizeStyle"></i>
</template>

<style scoped></style>
