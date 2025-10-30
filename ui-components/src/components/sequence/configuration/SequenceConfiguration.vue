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
import { computed, ref, watch } from 'vue'
import ResponsePhaseConfiguration from '@/components/sequence/configuration/phase/ResponsePhaseConfiguration.vue'
import ConfrontingViewPhaseConfiguration from '@/components/sequence/configuration/phase/ConfrontingViewPhaseConfiguration.vue'
import ResultPhaseConfiguration from '@/components/sequence/configuration/phase/ResultPhaseConfiguration.vue'
import type { ExecutionContext, SequenceConfiguration } from '@/models/SequenceConfiguration'

const { t } = useI18n()

export interface SequenceConfigurationProps {
  /**
   * Configuration of the sequence
   */
  modelValue?: SequenceConfiguration
  /**
   * Maximal number of responses to evaluate
   */
  maxResponseToEvaluate: number
  /**
   * Whether the explanation by AI feature is activated or not
   */
  aiIsActivated: boolean
  /**
   * The question is open or not.
   */
  questionIsOpen: boolean
}

export interface SequenceConfigurationEvents {
  /**
   * Fires when the user clicks on "start sequence" button
   */
  (event: 'startSequence', request: SequenceConfiguration): void

  /**
   * Fires when the user clicks on the save button. So he just wants to save the sequence configuration, not start it yet.
   */
  (event: 'saveSequenceConfiguration', request: SequenceConfiguration): void

  /**
   * Fires when the user clicks on the cancel button
   */
  (event: 'cancelSequenceConfiguration'): void
}

const props = withDefaults(defineProps<SequenceConfigurationProps>(), {
  maxResponseToEvaluate: 5,
  aiIsActivated: false,
})
const emit = defineEmits<SequenceConfigurationEvents>()

const EXECUTION_CONTEXT_OPTIONS: ExecutionContext[] = ['FaceToFace', 'Distance', 'Blended']
const noticeForEC = (executionContextKey: ExecutionContext) => {
  return t(`sequenceConfiguration.executionContext.${executionContextKey}.notice`)
}
const labelForEC = (executionContextKey: ExecutionContext) => {
  return t(`sequenceConfiguration.executionContext.${executionContextKey}.title`)
}

const executionContext = ref<ExecutionContext>(props.modelValue?.executionContext ?? EXECUTION_CONTEXT_OPTIONS[0])
const responsePhaseConfig = ref(props.modelValue?.responsePhaseConfig ?? { studentGiveExplanation: true })
const confrontingViewConfig = ref(props.modelValue?.confrontingViewsPhaseConfig)
const resultPhaseConfig = ref(props.modelValue?.resultPhaseConfig ?? { evaluationByIa: false })
const snackBar = ref<boolean>(false)
const configSaved = ref(false)

watch(executionContext, () => (configSaved.value = false))
watch(responsePhaseConfig, () => (configSaved.value = false), { deep: true })
watch(confrontingViewConfig, () => (configSaved.value = false), { deep: true })
watch(resultPhaseConfig, () => (configSaved.value = false), { deep: true })

const sequenceConfig = computed(() => {
  return {
    executionContext: executionContext.value,
    responsePhaseConfig: responsePhaseConfig.value,
    confrontingViewsPhaseConfig: confrontingViewConfig.value,
    resultPhaseConfig: resultPhaseConfig.value,
  }
})

const onStartSequence = () => {
  emit('startSequence', sequenceConfig.value)
}
const onSave = () => {
  emit('saveSequenceConfiguration', sequenceConfig.value)
  snackBar.value = true
  configSaved.value = true
}
const onCancel = () => {
  emit('cancelSequenceConfiguration')
}
</script>

<template>
  <v-card class="d-flex flex-column" :title="t('sequenceConfiguration.title')">
    <v-card-text>
      <!-- Execution Context -->
      <div class="mb-4">
        <v-radio-group inline :label="t('sequenceConfiguration.executionContext.title')" v-model="executionContext">
          <v-radio
            v-for="option in EXECUTION_CONTEXT_OPTIONS"
            :key="option"
            :label="labelForEC(option)"
            :value="option"
          ></v-radio>
        </v-radio-group>
        <v-alert
          v-if="executionContext !== undefined"
          :text="noticeForEC(executionContext)"
          variant="tonal"
          icon="$info"
          border="start"
          border-color="info"
          style="white-space: pre-line"
        >
        </v-alert>
      </div>

      <v-divider></v-divider>

      <!-- Response Phase -->
      <v-card :elevation="6" class="mt-4">
        <ResponsePhaseConfiguration
          v-model="responsePhaseConfig"
          :explanationMandatory="executionContext !== EXECUTION_CONTEXT_OPTIONS[0] || questionIsOpen"
        ></ResponsePhaseConfiguration>
      </v-card>

      <!-- Confronting View Phase -->
      <v-card elevation="6" class="mt-4">
        <ConfrontingViewPhaseConfiguration
          v-model="confrontingViewConfig"
          :studentGiveExplanation="responsePhaseConfig.studentGiveExplanation"
          :aiIsActivated
          :maxResponseToEvaluate
        ></ConfrontingViewPhaseConfiguration>
      </v-card>

      <!-- Result Phase -->
      <v-card elevation="6" class="mt-4">
        <ResultPhaseConfiguration
          v-model="resultPhaseConfig"
          :aiIsActivated="props.aiIsActivated"
        ></ResultPhaseConfiguration>
      </v-card>
    </v-card-text>

    <v-card-actions class="justify-end">
      <v-btn class="text-none text-subtitle-1 text-white" color="#95c155" variant="flat" @click="onStartSequence">
        {{ t('startSequence') }}
      </v-btn>
      <v-btn
        class="text-none text-subtitle-1"
        :text="t('save-sequence')"
        color="#263238"
        :variant="!configSaved ? 'flat' : 'outlined'"
        @click="onSave"
        :disabled="configSaved"
      >
      </v-btn>
      <v-snackbar v-model="snackBar" :location="'right'" :timeout="2000">
        {{ t('sequence-updated') }}
        <template v-slot:actions>
          <v-btn @click="snackBar = false" color="red" variant="text">
            {{ t('close') }}
          </v-btn>
        </template>
      </v-snackbar>
      <v-btn class="text-none text-subtitle-1" :text="t('cancel')" variant="outlined" @click="onCancel"></v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped></style>

<i18n>
{
  "en": {
    "startSequence": "Start sequence",
    "save-sequence": "Save sequence",
    "cancel": "Cancel",
    "sequence-updated": "Sequence updated",
    "close": "Close",
    "sequenceConfiguration": {
      "title": "Sequence Configuration",
      "executionContext": {
        "title": "Execution Context",
        "FaceToFace": {
          "title": "Face to Face",
          "notice": "The \"Face to face\" context corresponds to a pedagogical situation taking place in class or in amphitheater.\nThe teacher controls the start of the sequence and then the transition to the next phases.\nLearners should complete each phase in the dedicated time and wait until the next phase opens."
        },
        "Distance": {
          "title": "Distance",
          "notice": "The \"Distance\" context corresponds to a pedagogical situation for which learners are in a situation of autonomy.\nThe teacher controls only the opening and closing of the sequence.\nEach learner has the opportunity to do one phase after the other at his own pace, and then immediately discover the results."
        },
        "Blended": {
          "title": "Blended",
          "notice": "The \"Hybrid\" context corresponds to a pedagogical situation taking place at a distance followed by a presentation of the results in face-to-face.\nThe teacher controls the opening of the sequence and the publication of the results.\nLearners can follow the first two phases at their own pace, but will not discover the results until they are published."
        }
      }
    }
  },
  "fr": {
    "startSequence": "Démarrer la séquence",
    "save-sequence": "Enregistrer la séquence",
    "cancel": "Annuler",
    "sequence-updated": "Séquence mise à jour",
    "close": "Fermer",
    "sequenceConfiguration": {
      "title": "Configuration de la séquence",
      "executionContext": {
        "title": "Contexte d'exécution",
        "FaceToFace": {
          "title": "Face à face",
          "notice": "Le contexte \"Face à face\" correspond à une situation pédagogique se déroulant en classe ou en amphithéâtre.\nL'enseignant contrôle le démarrage de la séquence puis le passage aux phases suivantes.\nLes apprenants doivent accomplir chaque phase dans le temps imparti et patienter jusqu'à l'ouverture de la phase suivante."
        },
        "Distance": {
          "title": "À distance",
          "notice": "Le contexte \"À distance\" correspond à une situation pédagogique pour laquelle les apprenants sont en situation d'autonomie.\nL'enseignant ne contrôle que l'ouverture et la fermeture de la séquence.\nChaque apprenant a la possibilité d'enchaîner les phases de la séquence à son rythme, puis de découvrir immédiatement la présentation des résultats."
        },
        "Blended": {
          "title": "Hybride",
          "notice": "Le contexte \"Hybride\" correspond à une situation pédagique se déroulant à distance suivie d'une restitution des résultats en présentiel.\nL'enseignant contrôle l'ouverture de la séquence et la publication des résultats.\nLes apprenants peuvent enchaîner les deux premières phases à leur rythme mais ne découvriront les résultats qu'au moment de leur publication."
        }
      }
    }
  }
}
</i18n>
