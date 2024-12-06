<script setup lang="ts">

import EvaluationCard from '@/stories/evaluation/EvaluationCard.vue'
import { reactive } from 'vue'
import type { AnyResponse } from '@/models/Response'
import type { LikertValue } from '@/stories/evaluation/Likert'

type ResponseId = number

export interface ConfrontingViewpointProps {
  /**
   * The responses to evaluate in this viewpoints confrontation
   */
  responses: AnyResponse[]
}

export interface ConfrontingViewpointEvents {
  /**
   * Fired when the user changes its evaluation of an alternative response
   * @param event
   * @param responseId
   * @param value the evaluation value
   */
  (event: 'evaluation-changed', responseId: number, value: LikertValue): void
}

const props = defineProps<ConfrontingViewpointProps>()
const emit = defineEmits<ConfrontingViewpointEvents>()

const evaluations = reactive(
  props.responses.reduce((acc: { [key: ResponseId]: LikertValue }, response: AnyResponse) => {
    acc[response.id] = null
    return acc
  }, {})
)

const onEvaluationChange = (responseId: number, value: LikertValue): void =>
  emit('evaluation-changed', responseId, value)


</script>

<template>
  <div class="text-h5">Confronting viewpoints</div>
  <v-divider color="primary" class="my-4"></v-divider>

  <template v-if="!responses.length">
    <v-alert
      class="mb-4"
      text="There is no alternative answer to evaluate."
      type="error"
      variant="tonal"
    />
  </template>
  <template v-else>
    <v-alert
      class="mb-4"
      text="Here are presented one or several alternative responses. Please indicate how much you agree with these answers."
      type="info"
      variant="tonal"
    />

    <v-row align="stretch" justify="center">
      <v-col v-for="(response, index) in responses"
             :key="response.id"
             cols="12"
             md="6"
             xl="4"
      >
        <evaluation-card :evaluation-num="index+1"
                         v-model="evaluations[response.id]"
                         @update:model-value="onEvaluationChange(response.id, $event)"
                         :response="response"
                         :explanation="response.explanation" />
      </v-col>
    </v-row>
  </template>

</template>

<style scoped>

</style>
