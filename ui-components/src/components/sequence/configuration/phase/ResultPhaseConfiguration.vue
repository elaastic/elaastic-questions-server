<!--
  - Elaastic - formative assessment system
  - Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
  -
  - This program is free software: you can redistribute it and/or modify
  - it under the terms of the GNU Affero General Public License as
  - published by the Free Software Foundation, either version 3 of the
  - License, or (at your option) any later version.
  -
  - This program is distributed in the hope that it will be useful,
  - but WITHOUT ANY WARRANTY; without even the implied warranty of
  - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  - GNU Affero General Public License for more details.
  -
  - You should have received a copy of the GNU Affero General Public License
  - along with this program.  If not, see <https://www.gnu.org/licenses/>.
  -->

<script setup lang="ts">

import {useI18n} from 'vue-i18n';
import {ref, watch} from 'vue';

const {t} = useI18n();

export interface ResultPhaseConfig {
  evaluationByIa: boolean;
}

export interface ResultPhaseProps {
  modelValue?: ResultPhaseConfig,
  aiIsActivated?: boolean;
}

export type ResultPhaseEvent = (event: 'update:modelValue', value: ResultPhaseConfig) => void;

const props = withDefaults(defineProps<ResultPhaseProps>(), {
  aiIsActivated: false
});
const emit = defineEmits<ResultPhaseEvent>();

const evaluationByIa = ref<boolean>(props.modelValue?.evaluationByIa ?? false);

watch(() => evaluationByIa.value, () => configUpdate());

watch(() => props.modelValue, (newValue) => {
  if (newValue && newValue.evaluationByIa !== evaluationByIa.value) {
    evaluationByIa.value = newValue.evaluationByIa;
  }
}, { deep: true });

const configUpdate = () => {
  emit('update:modelValue', {
    "evaluationByIa": evaluationByIa.value
  });
};

configUpdate()
</script>

<template>
  <v-card elevation="0" :title="t('title')" prepend-icon="mdi-chart-bar">
    <template v-slot:text class="ps-2">
      <!-- IA Evaluation -->
      <v-row align="center" justify="start" v-if="aiIsActivated">
        <v-col cols="auto">
          <v-checkbox
            v-model="evaluationByIa"
            :label="t('IAEvaluation.label')"
            class="mt-4"
          >
          </v-checkbox>
        </v-col>
        <v-col cols="auto">
          <v-tooltip
            :text="t('IAEvaluation.notice')"
            location="top"
          >
            <template v-slot:activator="{ props }">
              <v-icon v-bind="props" icon="mdi-help-circle">
              </v-icon>
            </template>
          </v-tooltip>

        </v-col>
      </v-row>
    </template>
  </v-card>
</template>

<style scoped>

</style>

<i18n>
{
  "en": {
    "title": "Results",
    "IAEvaluation": {
      "label": "ChatGPT Explanations",
      "notice": "For each student explanation, ChatGPT automatically produces a justified evaluation based on the explanation provided by the teacher."
    }
  },
  "fr": {
    "title": "Résultats",
    "IAEvaluation": {
      "label": "Explications de ChatGPT",
      "notice": "Pour chaque explication d'étudiant, ChatGPT produit automatiquement une évaluation argumentée basée sur l'explication fournie par l'enseignant."
    }
  }
}
</i18n>
