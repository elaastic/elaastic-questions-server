<script setup lang="ts">
import {type PropType, ref} from "vue";
import ExplanationTop from "@/components/results/ExplanationTop.vue";
import type {AnyResponse, ExclusiveChoiceResponse, MultipleChoiceResponse} from "@/models/Response";
import {useI18n} from "vue-i18n";

const props = defineProps({
  /**
   * The answers selected by the user.
   */
  answer: {
    type: Object as PropType<AnyResponse>,
    default: () => ({}),
  },
  /**
   * The average grade out of 5 given by reviewers.
   */
  grade: {
    type: Number,
    default: 0
  },
  /**
   * The number of reviewers
   */
  numberOfPeerReview: {
    type: Number,
    default: 0
  },
  /**
   * A boolean. true if it's the teacher's explanation. false if it's a student explanation.
   */
  teacher: {
    type: Boolean,
    default: false
  },
});
const selectedMultipleAnswers = ref(
        props.answer.questionType === 'MultipleChoice'
                ? [...(props.answer as MultipleChoiceResponse).choices]
                : []
);

const selectedExclusiveAnswers = ref<number>(
        props.answer.questionType === 'ExclusiveChoice'
                ? (props.answer as ExclusiveChoiceResponse).choice
                : 0
);
const { t } = useI18n()
</script>

<template>
  <div class="card-wrapper">
    <div class="icones">
      <v-tooltip :text="t('remove-from-best-answers')" location="bottom">
        <template v-slot:activator="{ props }">
          <v-btn size="small" v-bind="props" icon>★</v-btn>
        </template>
      </v-tooltip>

      <v-tooltip :text="t('add-to-best-answers')" location="bottom">
        <template v-slot:activator="{ props }">
          <v-btn size="small" v-bind="props" icon>☆</v-btn>
        </template>
      </v-tooltip>

      <v-tooltip :text="t('hide-answer')" location="bottom">
        <template v-slot:activator="{ props }">
          <v-btn size="small" v-bind="props" icon>👁</v-btn>
        </template>
      </v-tooltip>
    </div>

    <v-card :class="['card', teacher ? 'teacher-bg' : 'default-bg']">
      <v-card-title>
        <ExplanationTop
                class="expTop"
                :grade="grade"
                :number-of-peer-review="numberOfPeerReview"
                :teacher="teacher"
        />
      </v-card-title>
      <v-card-text>
        <div class="txt">
          <strong v-if="props.answer.questionType==='MultipleChoice'"> {{t('answer')}}{{ selectedMultipleAnswers }}</strong>
          <strong v-if="props.answer.questionType==='ExclusiveChoice'">{{t('answer')}} {{ selectedExclusiveAnswers }}</strong>
          {{ props.answer.explanation }}
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>



<style scoped>
.txt{
  color: #00695C;
  margin-top: 2%;
}
.expTop{
  margin-top: 0%;
  margin-left: -3%;
}
.card-wrapper {
  position: relative;
  width: 280px;
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
  background-color: #F9FBE7;
}
::v-deep(.v-card-title) {
  padding-top: 0px;
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
    "answer": "Answer:"

  },
  "fr": {
    "remove-from-best-answers": "Retirer des meilleures réponses",
    "add-to-best-answers": "Ajouter aux meilleures réponses",
    "hide-answer": "Masquer la réponse",
    "answer": "Réponses:"
  }
}
</i18n>
