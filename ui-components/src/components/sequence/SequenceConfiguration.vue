<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { ref } from 'vue'
import Link from '@/components/util/Link.vue'

const { t } = useI18n()

type ExecutionContext = 'FaceToFace' | 'Distance' | 'Blended'
type EvaluationMethod = 'ALL_AT_ONCE' | 'DRAXO'
type SelectionTab = 'responsePhase' | 'confrontingViewPoints' | 'resultsPhase'

export interface SequenceConfigurationProps {
  /**
   * Maximal number of responses to evaluate
   */
  maxResponseToEvaluate: number,
  /**
   * Whether the explanation by AI feature is activated or not
   */
  aiIsActivated: boolean,
  /**
   * The question is open or not.
   */
  questionIsOpen: boolean,
  /**
   * The current tab selected by the user.
   */
  selectedTab: SelectionTab,
  /**
   * A boolean. True if learner can change its first answer. False if not.
   */
  modificationFirstAnswer: boolean,
}

export interface SequenceConfigurationEvents {
  /**
   * Fires when the user clicks on the submit button
   */
  (event: 'submitSequenceConfiguration', request: {
    executionContext: ExecutionContext,
    studentsProvideExplanation: boolean,
    responseToEvaluateCount: number,
    evaluationPhaseConfig: EvaluationMethod,
    evaluationByIA: boolean,
    modificationFirstAnswer: boolean,
  }): void;

  /**
   * Fires when the user clicks on the cancel button
   */
  (event: 'cancelSequenceConfiguration'): void;
}

const props = withDefaults(defineProps<SequenceConfigurationProps>(), {
  maxResponseToEvaluate: 5,
  aiIsActivated: false,
  selectedTab: 'responsePhase',
  modificationFirstAnswer: true,
})
const emit = defineEmits<SequenceConfigurationEvents>()

const EXECUTION_CONTEXT_OPTIONS: ExecutionContext[] = [
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

const EVALUATION_METHOD_OPTIONS: EvaluationMethod[] = [
  'ALL_AT_ONCE',
  'DRAXO'
]
const labelForEM = (evaluationMethodKey: EvaluationMethod) => {
  return t(`sequenceConfiguration.phase.confrontingViews.evaluationMethod.${evaluationMethodKey}`)
}

const executionContext = ref<ExecutionContext>(EXECUTION_CONTEXT_OPTIONS[0])
const studentGiveExplanation = ref<boolean>(true)
const nbResponseToEvaluate = ref<number>(props.maxResponseToEvaluate)
const evaluationMethod = ref<EvaluationMethod>(EVALUATION_METHOD_OPTIONS[0])
const evaluationByIa = ref<boolean>(false)
const selectedTabLocal = ref(props.selectedTab)
const modificationFirstAnswerLocal = ref(props.modificationFirstAnswer)

const onSubmit = () => {
  emit('submitSequenceConfiguration',
          {
            executionContext: executionContext.value,
            studentsProvideExplanation: studentGiveExplanation.value,
            responseToEvaluateCount: nbResponseToEvaluate.value,
            evaluationPhaseConfig: evaluationMethod.value,
            evaluationByIA: props.aiIsActivated && evaluationByIa.value,
            modificationFirstAnswer: modificationFirstAnswerLocal.value,
          }
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
                  v-for="option in EXECUTION_CONTEXT_OPTIONS"
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

        <v-card>
          <v-tabs v-model="selectedTabLocal"  align-tabs="center" bg-color="#42A5F5" grow>
            <v-tab value="responsePhase" class="text-none" :style="'color:white'">
              <strong v-if="selectedTabLocal==='responsePhase'">{{ t('sequenceConfiguration.phase.response.response-phase') }} (1)</strong>
              <div v-else>{{ t('sequenceConfiguration.phase.response.response-phase') }} (1)</div>
            </v-tab>
            <v-tab value="confrontingViewPoints" class="text-none" :style="'color:white'" :disabled="!studentGiveExplanation">
              <strong v-if="selectedTabLocal==='confrontingViewPoints'">{{ t('sequenceConfiguration.phase.confrontingViews.confronting-viewpoint') }} (2)</strong>
              <div v-else>{{ t('sequenceConfiguration.phase.confrontingViews.confronting-viewpoint') }} (2)</div>
            </v-tab>
            <v-tab value="resultsPhase" class="text-none" :style="'color:white'" :disabled="!studentGiveExplanation">
              <strong v-if="selectedTabLocal==='resultsPhase'">{{ t('sequenceConfiguration.phase.results.results-phase') }} (3)</strong>
              <div v-else>{{ t('sequenceConfiguration.phase.results.results-phase') }} (3)</div>
            </v-tab>
          </v-tabs>

          <v-card-text>
            <v-tabs-window v-model="selectedTabLocal" >
              <v-tabs-window-item value="responsePhase">
                <!--/*
                When the execution context is Distance or Blended, the student must give an explanation.
                So when this execution context are selected, the user can't update this checkbox.
                When the execution context is updated, the checkbox is reset to true.

                If the question is open, the student must give an explanation. So this checkbox isn't relevant and isn't displayed.
                */-->
                <v-checkbox
                        v-model="studentGiveExplanation"
                        :disabled="executionContext !== EXECUTION_CONTEXT_OPTIONS[0] || props.questionIsOpen"
                        :label="t('sequenceConfiguration.phase.response.studentsProvideAtextualExplanation')"
                        class="mt-4"
                >
                </v-checkbox>
              </v-tabs-window-item>

              <v-tabs-window-item value="confrontingViewPoints">
                <!-- Number of Responses to Evaluate -->
                <v-sheet>
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
                              :items="maxResponseToEvaluate > 0 ? Array.from({length: maxResponseToEvaluate}, (_, i) => i + 1) : []"
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
                              v-for="option in EVALUATION_METHOD_OPTIONS"
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
                    <v-checkbox
                            :label="t('sequenceConfiguration.phase.confrontingViews.first-answer-change')"
                            v-model="modificationFirstAnswerLocal" >
                    </v-checkbox>
                  </div>
                </v-sheet>
              </v-tabs-window-item>

              <v-tabs-window-item value="resultsPhase">
                <!-- IA Evaluation -->
                <v-sheet>
                  <v-row align="center" justify="start" v-if="aiIsActivated">
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
              </v-tabs-window-item>

            </v-tabs-window>
          </v-card-text>
        </v-card>
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
          "response-phase": "Response phase",
          "studentsProvideAtextualExplanation": "Students provide a textual explanation"
        },
        "confrontingViews": {
          "confronting-viewpoint" : "Confronting viewpoint",
          "enable-confrontingViewPoint": "Enable ConfrontingViewPoint",
          "studentsEvaluate": "Students evaluate",
          "answers": "answers",
          "evaluationMethod": {
            "title": "Evaluation method:",
            "ALL_AT_ONCE": "Single evaluation criterion \"Degree of agreement\" without textual feedback",
            "DRAXO": "DRAXO criteria grid with textual feedback",
            "draxoDocumentation": "More information on the DRAXO evaluation grid"
          },
          "first-answer-change": "Learner can change its first answer",
          "IAEvaluation": {
            "label": "ChatGPT Explanations",
            "notice": "For each student explanation, ChatGPT automatically produces a justified evaluation based on the explanation provided by the teacher."
          }
        },
        "results": {
          "results-phase": "Results phase"
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
          "response-phase": "Phase de réponse",
          "studentsProvideAtextualExplanation": "Les étudiants fournissent une explication"
        },
        "confrontingViews": {
          "confronting-viewpoint" : "Confrontation de point de vue",
          "enable-confrontingViewPoint": "Activer la confrontation de point de vue",
          "studentsEvaluate": "Les étudiants évaluent",
          "answers": "réponses",
          "evaluationMethod": {
            "title": "Méthode d'évaluation",
            "ALL_AT_ONCE": "Critère d'évaluation unique \"Degré d'accord\" sans feedback textuel",
            "DRAXO": "Grille de critères DRAXO avec feedback textuel possible",
            "draxoDocumentation": "Plus d'informations sur la grille d'évaluation DRAXO"
          },
          "first-answer-change": "L'apprenant peut changer sa première réponse",
          "IAEvaluation": {
            "label": "Explications de ChatGPT",
            "notice": "Pour chaque explication d'étudiant, ChatGPT produit automatiquement une évaluation argumentée basée sur l'explication fournie par l'enseignant."
          }
        },
        "results": {
          "results-phase": "Phase de résultats"
        }
      }
    }
  }
}
</i18n>
