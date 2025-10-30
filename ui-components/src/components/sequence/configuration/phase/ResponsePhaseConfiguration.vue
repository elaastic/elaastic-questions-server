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
import { useI18n } from 'vue-i18n'
import { ref, watch } from 'vue'

const { t } = useI18n()

export interface ResponsePhaseConfig {
  studentGiveExplanation: boolean
}

export interface ResponsePhaseProps {
  modelValue?: ResponsePhaseConfig
  explanationMandatory?: boolean
}

export type ResponsePhaseEvent = (event: 'update:modelValue', value: ResponsePhaseConfig) => void

const props = withDefaults(defineProps<ResponsePhaseProps>(), {
  explanationMandatory: false,
})
const emit = defineEmits<ResponsePhaseEvent>()

const studentGiveExplanation = ref<boolean>(props.modelValue?.studentGiveExplanation ?? false)

const updateConfig = () => {
  emit('update:modelValue', {
    studentGiveExplanation: studentGiveExplanation.value || props.explanationMandatory,
  })
}

// Watchers
watch(() => studentGiveExplanation.value, updateConfig)
watch(() => props.explanationMandatory, updateConfig)
watch(
  () => props.modelValue,
  newValue => {
    if (newValue && newValue.studentGiveExplanation !== studentGiveExplanation.value) {
      studentGiveExplanation.value = newValue.studentGiveExplanation
    }
  },
  { deep: true },
)

// Emit the initial value when the component is mounted
updateConfig()
</script>

<template>
  <v-card elevation="0" :title="t('title')" prepend-icon="mdi-comment-outline">
    <template v-slot:text class="ps-2">
      <!-- Student give a textual explanation -->
      <v-checkbox
        v-if="!explanationMandatory"
        v-model="studentGiveExplanation"
        :label="t('studentsProvideAtextualExplanation')"
      />
      <v-checkbox
        v-else
        :model-value="true"
        :disabled="explanationMandatory"
        :label="t('studentsProvideAtextualExplanation')"
      />
    </template>
  </v-card>
</template>

<style scoped></style>

<i18n>
{
  "en": {
    "title": "Well-argued Response",
    "studentsProvideAtextualExplanation": "Students provide a textual explanation"
  },
  "fr": {
    "title": "Réponse argumentée",
    "studentsProvideAtextualExplanation": "Les étudiants fournissent une explication"
  }
}
</i18n>
