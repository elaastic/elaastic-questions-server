<script setup lang="ts">

import EvaluationCard from '@/stories/evaluation/EvaluationCard.vue'
import { reactive } from 'vue'
import type { Response } from '@/models/Response'

type ResponseId = number
type LikertValue = number | null

interface ConfrontingViewpointProps {
  responses: Response[]
}

const props = defineProps<ConfrontingViewpointProps>()

const evaluations = reactive(
  props.responses.reduce((acc: { [key: ResponseId]: LikertValue }, response: Response) => {
    acc[response.id] = null;
    return acc;
  }, {})
);

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
                         v-bind="response.choices || response.choice ? { choices: response.choices || [response.choice] } : {}"
                         :explanation="response.explanation" />
      </v-col>
    </v-row>
  </template>

</template>

<style scoped>

</style>
