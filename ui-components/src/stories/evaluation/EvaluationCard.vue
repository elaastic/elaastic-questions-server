<script setup lang="ts">
import ChoiceChip from '@/stories/response/ChoiceChip.vue'
import LikertScale from '@/stories/evaluation/LikertScale.vue'
import { computed } from 'vue'
import type { LikertValue } from '@/stories/evaluation/Likert'
import { type AnyResponse } from '@/models/Response'

export interface EvaluationCardProps {
  /**
   * The evaluation value of this card
   */
  modelValue: LikertValue,
  /**
   * Evaluation number in the list of evaluations
   */
  evaluationNum?: number | null,
  /**
   * The alternative response to evaluate
   */
  response: AnyResponse
}

const props = withDefaults(defineProps<EvaluationCardProps>(), {
  evaluationNum: null
})

interface EvaluationCartEvents {
  /** Fires when the user changes its evaluation of this response */
  (event: 'update:modelValue', value: LikertValue): void;
}

const emit = defineEmits<EvaluationCartEvents>()

const evaluationValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})
const explanationLabel = computed(() => props.response.questionType == 'OpenEnded' ? 'Answer' : 'Explanation')
const explanation = props.response.explanation

const getChoices = (response: AnyResponse) => {
  switch (response.questionType) {
    case 'ExclusiveChoice':
      return [response.choice]

    case 'MultipleChoice':
      return response.choices

    case 'OpenEnded':
      return undefined
  }
}
const choices = getChoices(props.response)
</script>

<template>
  <v-card
    class="fill-height mx-auto border-bottom d-flex flex-column"
  >
    <v-card-item class="bg-surface-light">
      <div class="d-flex justify-space-between w-100 align-center">
        <div class="text-overline mb-1">
          Alternative answer <span v-if="evaluationNum">#{{ evaluationNum }}</span>
        </div>
        <v-chip v-if="!evaluationValue" class="ml-auto" color="red">Require evaluation</v-chip>
        <v-icon v-else class="ml-auto" color="green">mdi-checkbox-marked</v-icon>
      </div>

    </v-card-item>

    <v-card-item>
      <v-container>
        <v-text-field v-if="choices" label="Choix" model-value=" " readonly variant="plain">
          <choice-chip v-for="choice in choices" :key="choice" :value="choice" color="grey" />
        </v-text-field>

        <v-text-field :label="explanationLabel" variant="plain" model-value=" " readonly>
          <div v-html="explanation" />
        </v-text-field>
      </v-container>
    </v-card-item>

    <v-spacer></v-spacer>
    <v-divider />
    <v-card-item>
      <v-label class="mb-2">To what extent do you agree with the explanation given?</v-label>
      <div class="d-flex justify-center">
        <div>
          <likert-scale v-model="evaluationValue"
                        min-label="Strongly disagree"
                        max-label="Strongly agree"
                        color="primary" />
        </div>
      </div>
    </v-card-item>
  </v-card>
</template>

<style scoped>

</style>
