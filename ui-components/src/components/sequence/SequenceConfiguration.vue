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

const executionContexte = ref<ExecutionContext>()

const readyToSend = computed(() => {
  return executionContexte.value !== undefined
})
</script>

<template>
  <v-card
          :title="t('sequenceConfiguration.title')"
  >
    <v-radio-group inline
                   :label="t('sequenceConfiguration.executionContext.title') + ' : ' + executionContexte"
                   v-model="executionContexte"
    >
      <v-radio
              v-for="option in ECOption"
              :key="option"
              :label="labelForEC(option)"
              :value="option"></v-radio>
    </v-radio-group>

      <v-alert v-if="executionContexte !== undefined" :text="noticeForEC(executionContexte)" type="info" variant="tonal">
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
          "notice": "blabla"
        },
        "Distance": {
          "title": "Distance",
          "notice": "blabla"
        },
        "Blended": {
          "title": "Blended",
          "notice": "blabla"
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
          "notice": "blabla"
        },
        "Distance": {
          "title": "À distance",
          "notice": "blabla"
        },
        "Blended": {
          "title": "Hybride",
          "notice": "blabla"
        }
      }
    }
  }
}
</i18n>
