<script setup lang="ts">
import {useI18n} from 'vue-i18n'
import {computed, ref} from "vue";
import type {Selection} from "@/components/util/SelectorResponsive.vue";

const {t} = useI18n()

type ExecutionContext = string

const ECOption: ExecutionContext[] = [
  'FaceToFace',
  'Distance',
  'Blended'
]

const noticeForEC = (executionContextKey: ExecutionContext) => {
  return t(`sequenceConfiguration.executionContext.${executionContextKey}.notice`)
}
const labelForEC = (executionContextKey: ExecutionContext) => {
  return t(`sequenceConfiguration.executionContext.${executionContextKey}.title`)
}

const executionContext = ref<ExecutionContext>(ECOption[0])

const readyToSend = computed(() => {
  return executionContext.value !== undefined
})
</script>

<template>
  <v-card
          :title="t('sequenceConfiguration.title')"
  >
    <v-radio-group inline
                   :label="t('sequenceConfiguration.executionContext.title') + ' : ' + executionContext"
                   v-model="executionContext"
    >
      <v-radio
              v-for="option in ECOption"
              :key="option"
              :label="labelForEC(option)"
              :value="option"></v-radio>
    </v-radio-group>

      <v-alert
        v-if="executionContext !== undefined"
        :text="noticeForEC(executionContext)"
        type="info"
        variant="tonal"
        style="white-space: pre-line"
      >
      </v-alert>


    <v-card-actions>
      <v-btn
        :disabled="!readyToSend"
        class="text-none text-subtitle-1 text-white"
        color="#95c155"
        variant="flat"
      >
        {{ t('submit') }}
      </v-btn>
      <v-btn
              class="text-none text-subtitle-1"
              text="Cancel"
      ></v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>

</style>

<i18n>
{
  "en": {
    "submit": "Start sequence",
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
    "submit": "Démarrer la séquence",
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
          "notice": "Le contexte \"Hybride\" correspond à une situation pédagogique se déroulant à distance suivie d'une restitution des résultats en présentiel.\nL'enseignant contrôle l'ouverture de la séquence et la publication des résultats.\nLes apprenants peuvent enchaîner les deux premières phases à leur rythme mais ne découvriront les résultats qu'au moment de leur publication."
        }
      }
    }
  }
}
</i18n>
