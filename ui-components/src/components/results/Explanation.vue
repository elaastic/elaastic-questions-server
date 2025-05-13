<script setup lang="ts">
import {type PropType, ref} from "vue";
import ExplanationTop from "@/components/results/ExplanationTop.vue";
import type {AnyResponse, ExclusiveChoiceResponse, MultipleChoiceResponse} from "@/models/Response";

const props = defineProps({
  answer: {
    type: Object as PropType<AnyResponse>,
    default: () => ({}),
  },
  grade: {
    type: Number,
    default: 0
  },
  numberOfPeerReview: {
    type: Number,
    default: 0
  },
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

</script>

<template>
  <div class="card-wrapper">
    <div class="icones">
      <v-tooltip text="Retirer des meilleures réponses" location="bottom">
        <template v-slot:activator="{ props }">
          <v-btn size="small" v-bind="props" icon>★</v-btn>
        </template>
      </v-tooltip>

      <v-tooltip text="Ajouter aux meilleures réponses" location="bottom">
        <template v-slot:activator="{ props }">
          <v-btn size="small" v-bind="props" icon>☆</v-btn>
        </template>
      </v-tooltip>

      <v-tooltip text="Masquer la réponse" location="bottom">
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
          <strong v-if="props.answer.questionType==='MultipleChoice'">Réponses: {{ selectedMultipleAnswers }}</strong>
          <strong v-if="props.answer.questionType==='ExclusiveChoice'">Réponses: {{ selectedExclusiveAnswers }}</strong>
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
  margin-left: -2%;
}
.card-wrapper {
  position: relative;
  width: 800px;
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
