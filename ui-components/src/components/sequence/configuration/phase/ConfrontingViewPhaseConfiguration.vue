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

import Link from "@/components/util/Link.vue";
import {useI18n} from "vue-i18n";
import {ref, watch} from "vue";

const {t} = useI18n();

type EvaluationMethod = 'ALL_AT_ONCE' | 'DRAXO'

export interface ConfrontingViewPhaseConfig {
  phaseActive: boolean;
  nbResponseToEvaluate: number;
  evaluationMethod: EvaluationMethod;
}

export interface ConfrontingViewPhaseProps {
  /**
   * Configuration de la phase
   */
  modelValue?: ConfrontingViewPhaseConfig,
  /**
   * Maximal number of responses to evaluate
   */
  maxResponseToEvaluate?: number;
  /**
   * If the student must give an textual explanation of their response
   */
  studentGiveExplanation?: boolean;
  /**
   * Whether the explanation by AI feature is activated or not
   */
  aiIsActivated?: boolean;
}

export type ConfrontingViewPhaseEvent = (event: 'update:modelValue', value: ConfrontingViewPhaseConfig) => void;

const props = withDefaults(defineProps<ConfrontingViewPhaseProps>(), {
  maxResponseToEvaluate: 5,
  studentGiveExplanation: false,
  aiIsActivated: false
});
const emit = defineEmits<ConfrontingViewPhaseEvent>();

const EVALUATION_METHOD_OPTIONS: EvaluationMethod[] = [
  'ALL_AT_ONCE',
  'DRAXO'
]

const confrontingViewsPhaseActive = ref<boolean>(props.modelValue?.phaseActive ?? true);
const nbResponseToEvaluate = ref<number>(props.modelValue?.nbResponseToEvaluate ?? props.maxResponseToEvaluate);
const evaluationMethod = ref<EvaluationMethod>(props.modelValue?.evaluationMethod ?? EVALUATION_METHOD_OPTIONS[0]);

// Watchers pour émettre les changements
watch(() => confrontingViewsPhaseActive.value, () => configUpdated());
watch(() => nbResponseToEvaluate.value, () => configUpdated());
watch(() => evaluationMethod.value, () => configUpdated());

watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    confrontingViewsPhaseActive.value = newValue.phaseActive;
    nbResponseToEvaluate.value = newValue.nbResponseToEvaluate;
    evaluationMethod.value = newValue.evaluationMethod;
  }
}, { deep: true });

const labelForEM = (evaluationMethodKey: EvaluationMethod) => {
  return t(`evaluationMethod.${evaluationMethodKey}`)
}

const configUpdated = () => {
  emit('update:modelValue', {
    phaseActive: confrontingViewsPhaseActive.value,
    nbResponseToEvaluate: nbResponseToEvaluate.value,
    evaluationMethod: evaluationMethod.value
  });
};

// Emit the initial configuration when the component is mounted
configUpdated()
</script>

<template>
  <v-card
          elevation="0" :title="t('title')"
          prepend-icon="mdi-comment-multiple">
    <template v-slot:text class="ps-2">
      <v-switch v-model="confrontingViewsPhaseActive" class="mb-n4" color="primary">
        <template v-slot:label>
          <span v-if="confrontingViewsPhaseActive">{{ t('active') }}</span>
          <span v-if="!confrontingViewsPhaseActive">{{ t('deactive') }}</span>
        </template>
      </v-switch>

      <v-divider thickness="2" v-if="confrontingViewsPhaseActive"></v-divider>

      <v-expand-transition>
        <div v-if="confrontingViewsPhaseActive">
          <v-expand-transition>
            <div v-if="studentGiveExplanation">
              <!-- Number of Responses to Evaluate -->
              <v-row align="center" justify="start">
                <v-col cols="auto">
                  <p>
                    {{ t('studentsEvaluate') }}
                  </p>
                </v-col>
                <v-col cols="auto">
                  <v-select
                          variant="outlined"
                          density="compact"
                          v-model="nbResponseToEvaluate"
                          :items="maxResponseToEvaluate > 0 ? Array.from({length: maxResponseToEvaluate}, (_, i) => i + 1) : [1]"
                          class="mt-4"
                          style="min-width: 50px;"
                  >
                  </v-select>
                </v-col>
                <v-col cols="auto"><p>{{ t('answers') }}</p></v-col>
              </v-row>

              <!-- Evaluation Method -->
              <div class="d-flex flex-column align-start">
                <v-radio-group
                        :label="t('evaluationMethod.title')"
                        v-model="evaluationMethod"

                >
                  <v-radio
                          v-for="option in EVALUATION_METHOD_OPTIONS"
                          :key="option"
                          :label="labelForEM(option)"
                          :value="option"></v-radio>
                </v-radio-group>
                <v-alert type="info" variant="outlined" class="align-self-end " density="compact">
                  <Link
                          href="https://elaastic.github.io/elaastic-questions-server/en/key_concepts/DRAXO"
                          :text="t('evaluationMethod.draxoDocumentation')"
                          target="_blank"
                  />
                </v-alert>
              </div>
            </div>
          </v-expand-transition>

          <!-- Student can change their response - Notice -->
          <v-alert
            class="mt-4"
            variant="tonal"
            icon="$info"
            border="start"
            border-color="info"
            :text="t('changeResponseNotice')"
            style="white-space: pre-line"
          ></v-alert>
        </div>
      </v-expand-transition>
    </template>
  </v-card>
</template>

<style scoped>

</style>

<i18n>
{
  "en": {
    "title": "Comparing viewpoints",
    "studentsEvaluate": "Students evaluate",
    "answers": "answers",
    "evaluationMethod": {
      "title": "Evaluation method:",
      "ALL_AT_ONCE": "Single evaluation criterion \"Degree of agreement\" without textual feedback",
      "DRAXO": "DRAXO criteria grid with textual feedback",
      "draxoDocumentation": "More information on the DRAXO evaluation grid"
    },
    "active": "The phase is actived",
    "deactive": "The phase is deactived",
    "changeResponseNotice": "Students can change their response."
  },
  "fr": {
    "title": "Confrontation de points de vue",
    "studentsEvaluate": "Les étudiants évaluent",
    "answers": "réponses",
    "evaluationMethod": {
      "title": "Méthode d'évaluation",
      "ALL_AT_ONCE": "Critère d'évaluation unique \"Degré d'accord\" sans feedback textuel",
      "DRAXO": "Grille de critères DRAXO avec feedback textuel possible",
      "draxoDocumentation": "Plus d'informations sur la grille d'évaluation DRAXO"
    },
    "active": "La phase est activée",
    "deactive": "La phase est désactivée",
    "changeResponseNotice": "Les étudiants peuvent modifier leur réponse."
  }
}
</i18n>
