<script setup lang="ts">
import {useI18n} from 'vue-i18n'
import {ref} from "vue";
import Link from '@/components/util/Link.vue'

const {t} = useI18n()

type ExecutionContext = string
type EvaluationMethod = string

export interface SequenceConfigurationProps {
  /**
   * Maximal number of responses to evaluate
   */
  maxResponseToEvaluate: number,
  /**
   * Whether the explanation by AI fonctionality is activated or not
   */
  aiIsActivated: boolean,
  /**
   * The question is open or not.
   */
  questionIsOpen: boolean
}

export interface SequenceConfigurationEvents {
  /**
   * Fires when the user clicks on the submit button
   */
  (event: 'submitSequenceConfiguration',
   executionContext: ExecutionContext,
   studentGiveExplanation: boolean,
   confrontingViewsPhase?: {
     nbResponseToEvaluate: number,
     evaluationMethod: EvaluationMethod,
     evaluationByIA: boolean
   }): void;

  /**
   * Fires when the user clicks on the cancel button
   */
  (event: 'cancelSequenceConfiguration'): void;
}

const props = withDefaults(defineProps<SequenceConfigurationProps>(), {
  maxResponseToEvaluate: 5,
  aiIsActivated: false
})
const emit = defineEmits<SequenceConfigurationEvents>()

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

const EMOption: EvaluationMethod[] = [
  'ALL_AT_ONCE',
  'DRAXO'
]
const labelForEM = (evaluationMethodKey: EvaluationMethod) => {
  return t(`sequenceConfiguration.phase.confrontingViews.evaluationMethod.${evaluationMethodKey}`)
}

const executionContext = ref<ExecutionContext>(ECOption[0])
const studentGiveExplanation = ref<boolean>(true)
const nbResponseToEvaluate = ref<number>(props.maxResponseToEvaluate)
const evaluationMethod = ref<EvaluationMethod>(EMOption[0])
const evaluationByIa = ref<boolean>(false)

const onSubmit = () => {
  emit('submitSequenceConfiguration',
          executionContext.value,
          studentGiveExplanation.value,
          studentGiveExplanation.value ? {
            nbResponseToEvaluate: nbResponseToEvaluate.value,
            evaluationMethod: evaluationMethod.value,
            evaluationByIA: props.aiIsActivated && evaluationByIa.value
          } : undefined
  )
}
const onCancel = () => {
  emit('cancelSequenceConfiguration')
}
</script>

<template>
  <v-card
          class="d-flex flex-column"
          :title="t('sequenceConfiguration.title')"
  >
    <v-card-text>
      <!-- Execution Context -->
      <div class="mb-4">
        <v-radio-group inline
                       :label="t('sequenceConfiguration.executionContext.title')"
                       v-model="executionContext"
                       v-on:click="studentGiveExplanation = true"
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
      </div>

      <v-divider></v-divider>

      <!-- Student give a textual explanation -->
      <!--/*
      When the execution context is Distance or Blended, the student must give an explanation.
      So when this execution context are selected, the user can't update this checkbox.
      When the execution context is updated, the checkbox is reset to true.

      If the question is open, the student must give an explanation. So this checkbox isn't relevant and isn't displayed.
      */-->
      <v-checkbox
        v-if="!props.questionIsOpen"
        v-model="studentGiveExplanation"
        :disabled="executionContext !== ECOption[0]"
        :label="t('sequenceConfiguration.phase.response.studentsProvideAtextualExplanation')"
        class="mt-4"
      >
      </v-checkbox>

      <!-- Confronting View Phase -->
      <v-expand-transition>
        <v-sheet v-if="studentGiveExplanation">
          <!-- Number of Responses to Evaluate -->
          <v-row align="center" justify="start">
            <v-col cols="auto">
              <v-checkbox
                      v-model="studentGiveExplanation"
                      :label="t('sequenceConfiguration.phase.confrontingViews.studentsEvaluate')"
                      class="mt-4"
                      :disabled="true"
              >
              </v-checkbox>
            </v-col>
            <v-col cols="auto">
              <v-select
                      variant="outlined"
                      density="compact"
                      v-model="nbResponseToEvaluate"
                      :items="props.maxResponseToEvaluate > 0 ? Array.from({length: props.maxResponseToEvaluate}, (_, i) => i + 1) : []"
                      class="mt-4"
                      style="min-width: 50px;"
              >
              </v-select>
            </v-col>
            <v-col cols="auto">
              <p>
                {{ t('sequenceConfiguration.phase.confrontingViews.answers') }}
              </p>
            </v-col>
          </v-row>

          <!-- Evaluation Method -->
          <div class="d-flex flex-column align-start">
            <v-radio-group
                    :label="t('sequenceConfiguration.phase.confrontingViews.evaluationMethod.title')"
                    v-model="evaluationMethod"
            >
              <v-radio
                      v-for="option in EMOption"
                      :key="option"
                      :label="labelForEM(option)"
                      :value="option"></v-radio>
            </v-radio-group>
            <v-alert type="info" variant="outlined" class="align-self-end " density="compact">
              <Link
                      href="https://elaastic.github.io/elaastic-questions-server/en/key_concepts/DRAXO"
                      :text="t('sequenceConfiguration.phase.confrontingViews.evaluationMethod.draxoDocumentation')"
                      target="_blank"
              />
            </v-alert>
          </div>

          <!-- IA Evaluation -->
          <v-row align="center" justify="start" v-if="props.aiIsActivated">
            <v-col cols="auto">
              <v-checkbox
                      v-model="evaluationByIa"
                      :label="t('sequenceConfiguration.phase.confrontingViews.IAEvaluation.label')"
                      class="mt-4"
              >
              </v-checkbox>
            </v-col>
            <v-col cols="auto">
              <v-tooltip
                      :text="t('sequenceConfiguration.phase.confrontingViews.IAEvaluation.notice')"
                      location="top"
              >
                <template v-slot:activator="{ props }">
                  <v-icon v-bind="props" icon="mdi-help-circle">
                  </v-icon>
                </template>
              </v-tooltip>

            </v-col>
          </v-row>
        </v-sheet>
      </v-expand-transition>
    </v-card-text>

    <v-card-actions class="justify-end">
      <v-btn
              class="text-none text-subtitle-1 text-white"
              color="#95c155"
              variant="flat"
              @click="onSubmit"
      >
        {{ t('submit') }}
      </v-btn>
      <v-btn
              class="text-none text-subtitle-1"
              text="Cancel"
              variant="outlined"
              @click="onCancel"
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
      },
      "phase": {
        "response": {
          "studentsProvideAtextualExplanation": "Students provide a textual explanation"
        },
        "confrontingViews": {
          "studentsEvaluate": "Students evaluate",
          "answers": "answers",
          "evaluationMethod": {
            "title": "Evaluation method:",
            "ALL_AT_ONCE": "Single evaluation criterion \"Degree of agreement\" without textual feedback",
            "DRAXO": "DRAXO criteria grid with textual feedback",
            "draxoDocumentation": "More information on the DRAXO evaluation grid"
          },
          "IAEvaluation": {
            "label": "ChatGPT Explanations",
            "notice": "For each student explanation, ChatGPT automatically produces a justified evaluation based on the explanation provided by the teacher."
          }
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
      },
      "phase": {
        "response": {
          "studentsProvideAtextualExplanation": "Les étudiants fournissent une explication"
        },
        "confrontingViews": {
          "studentsEvaluate": "Les étudiants évaluent",
          "answers": "réponses",
          "evaluationMethod": {
            "title": "Méthode d'évaluation",
            "ALL_AT_ONCE": "Critère d'évaluation unique \"Degré d'accord\" sans feedback textuel",
            "DRAXO": "Grille de critères DRAXO avec feedback textuel possible",
            "draxoDocumentation": "Plus d'informations sur la grille d'évaluation DRAXO"
          },
          "IAEvaluation": {
            "label": "Explications de ChatGPT",
            "notice": "Pour chaque explication d'étudiant, ChatGPT produit automatiquement une évaluation argumentée basée sur l'explication fournie par l'enseignant."
          }
        }
      }
    }
  }
}
</i18n>
