<script setup lang="ts">
import { computed, type PropType, ref } from 'vue'
import ExplanationHeader from '@/components/results/ElExplanationHeader.vue'
import type { AnyResponse, MultipleChoiceResponse } from '@/models/Response'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  /**
   * The user response.
   */
  response: {
    type: Object as PropType<AnyResponse>,
    required: true,
  },
  /**
   * The average grade out of 5 given by reviewers.
   */
  grade: {
    type: Number,
  },
  /**
   * The number of reviewers
   */
  numberOfPeerReview: {
    type: Number,
  },
  /**
   * A boolean. true if it's the teacher's explanation. false if it's a student explanation.
   */
  providedByTeacher: {
    type: Boolean,
    default: false,
  },
  /**
   * A boolean. true if the answer is among the best answers. false if not.
   */
  isBestAnswer: {
    type: Boolean,
    default: false,
  },
  /**
   * A boolean. true if the answer is hidden. false if not.
   */
  isHidden: {
    type: Boolean,
    default: false,
  },
})
const emit = defineEmits(['update:is-best-answer', 'update:is-hidden'])
const { t } = useI18n()
const choices = computed(() => {
  if (props.response.questionType === 'MultipleChoice') {
    return `${t('responses')} [${(props.response as MultipleChoiceResponse).choices}]`
  } else if (props.response.questionType === 'ExclusiveChoice') {
    return `${t('response')} ${props.response.choice}`
  }

  return null
})

const bestAnswerLocal = ref(props.isBestAnswer)
const isHiddenLocal = ref(props.isHidden)
const outBest = () => {
  bestAnswerLocal.value = false
  emit('update:is-best-answer', props.response.id, bestAnswerLocal.value)
}
const inBest = () => {
  bestAnswerLocal.value = true
  emit('update:is-best-answer', props.response.id, bestAnswerLocal.value)
}
const hide = () => {
  isHiddenLocal.value = true
  emit('update:is-hidden', props.response.id, isHiddenLocal.value)
}
</script>

<template>
  <!--    <div class="icones">-->

  <v-card :class="['card', providedByTeacher ? 'teacher-bg' : 'default-bg']">
    <v-card-title class="d-flex align-center">
      <ExplanationHeader
        class="expTop"
        :grade="grade"
        :number-of-peer-review="numberOfPeerReview"
        :teacher="providedByTeacher"
      />

      <div class="ms-auto">
        <v-tooltip :text="t('remove-from-best-answers')" location="bottom">
          <template v-slot:activator="{ props }">
            <v-btn size="small" v-bind="props" @click="outBest" icon>
              <v-icon>mdi-star</v-icon>
            </v-btn>
          </template>
        </v-tooltip>

        <v-tooltip :text="t('add-to-best-answers')" location="bottom">
          <template v-slot:activator="{ props }">
            <v-btn size="small" v-bind="props" @click="inBest" icon>
              <v-icon>mdi-star-outline</v-icon>
            </v-btn>
          </template>
        </v-tooltip>

        <v-tooltip :text="t('hide-answer')" location="bottom">
          <template v-slot:activator="{ props }">
            <v-btn size="small" v-bind="props" @click="hide" icon><v-icon>mdi-eye</v-icon></v-btn>
          </template>
        </v-tooltip>
      </div>
    </v-card-title>

    <v-card-title> </v-card-title>
    <v-card-text>
      <div class="txt">
        <strong v-if="choices">{{ choices }}</strong>
        {{ props.response.explanation }}
      </div>
    </v-card-text>
  </v-card>
</template>

<style scoped>
.txt {
  color: #00695c;
  margin-top: 2%;
}

.expTop {
  margin-top: 0;
  margin-left: -3%;
}

.card {
  width: 100%;
  box-sizing: border-box;
  border-radius: 4px;
  border: 1px solid #2e7d32;
}

.default-bg {
  background-color: white;
}

.teacher-bg {
  background-color: #f9fbe7;
}

::v-deep(.v-card-title) {
  padding-top: 0;
  padding-bottom: 8px;
}

.icones {
  position: absolute;
  top: -20px;
  right: 0;
  z-index: 10;
  display: flex;
  gap: 0;
}

.v-btn {
  min-width: 32px !important;
  height: 32px !important;
  width: 32px !important;
  padding: 0 !important;
  border-radius: 0 !important;
  background-color: lightgray;
}
</style>
<i18n>
{
  "en": {
    "remove-from-best-answers": "Remove from best answers",
    "add-to-best-answers": "Add to best answers",
    "hide-answer": "Hide answer",
    "response": "Response:",
    "responses": "Responses:"
  },
  "fr": {
    "remove-from-best-answers": "Retirer des meilleures réponses",
    "add-to-best-answers": "Ajouter aux meilleures réponses",
    "hide-answer": "Masquer la réponse",
    "response": "Réponse :",
    "responses": "Réponses :"
  }
}
</i18n>
