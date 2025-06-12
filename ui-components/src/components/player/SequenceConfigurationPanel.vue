<script setup lang="ts">
import {computed, type PropType} from "vue";
import {useI18n} from "vue-i18n";

export type ExecutionContextType = "Face-to-face" | "Distant" | "Blended";
export type ValuationMethodType = "Agreement-degree" | "DRAXO";
const props = defineProps({
  /**
   * The type of the execution for the sequence. It could be "Face-to-face" (the teacher controls the sequence), "Distant" (the student has the possibility to do all the steps in a row) or "Blended" (like "Distant" but the student can't see the results) .
   */
  executionContext: {
    type: Object as PropType<ExecutionContextType>,
    default: "Face-to-face"
  },
  /**
   *  A boolean. True if students have to write an explanation for their answer. False if not. (doesn't concern OpenEndedQuestion)
   */
  studentsProvideExplanation: {
    type: Boolean,
    default: true
  },
  /**
   *  The method for the valuation. It could be "Agreement-degree" or "DRAXO". (More information on DRAXO on the link)
   */
  valuationMethod: {
    type: Object as PropType<ValuationMethodType>,
    default: "Agreement-degree"
  },
  /**
   *  A boolean. True if answers are reviewed by ChatGPT. False if not.
   */
  withChatGPTExplanation: {
    type: Boolean,
    default: false
  },
  /**
   *  The number of answers each student can review.
   */
  nbOfAnswerEvaluated: {
    type: Number,
    default: 5
  },
  /**
   *  A boolean. True if the question of the sequence is OpenEnded. False if not.
   */
  isOpenQuestion: {
    type: Boolean
  }
})

const emits = defineEmits(["update:executionContext", "update:studentsProvideExplanation", "update:valuationMethod", "update:withChatGPTExplanation", "update:nbOfAnswerEvaluated", "update:sequence"])
const executionContextLocal = computed({
  get : () => props.executionContext,
  set: (newValue) => emits("update:executionContext", newValue)
});
const studentsProvideExplanationLocal = computed({
  get: () => props.studentsProvideExplanation,
  set: (newValue) => emits("update:studentsProvideExplanation", newValue)
})
const valuationMethodLocal = computed({
  get: () => props.valuationMethod,
  set: (newValue) => emits("update:valuationMethod", newValue)
})
const withChatGPTExplanationLocal = computed({
  get: () => props.withChatGPTExplanation,
  set: (newValue) => emits("update:withChatGPTExplanation", newValue)
})
const nbOfAnswerEvaluatedLocal = computed({
  get: () => props.nbOfAnswerEvaluated,
  set: (newValue) => emits("update:nbOfAnswerEvaluated", newValue)
})

const startSequence = (executionContext: ExecutionContextType, studentsProvideExplanation: boolean, valuationMethod: ValuationMethodType, withChatGPTExplanation: boolean, nbOfAnswerEvaluated: number) => {
  const sequence = !studentsProvideExplanation && !props.isOpenQuestion
                  ? {executionContext: executionContext, studentsProvideExplanation: studentsProvideExplanation, valuationMethod: null, withChatGPTExplanation: null, nbOfAnswerEvaluated: null}
                  : {executionContext: executionContext, studentsProvideExplanation: studentsProvideExplanation, valuationMethod: valuationMethod, withChatGPTExplanation: withChatGPTExplanation, nbOfAnswerEvaluated: nbOfAnswerEvaluated};
  emits("update:sequence", sequence);
}

const { t } = useI18n()
</script>

<template>
  <h1> {{t('configure-sequence')}}</h1>

  <div class="d-md-flex">
    <h4 class="mt-2 mr-2">{{t('execution-context')}} : </h4>
    <v-radio-group v-model="executionContextLocal" >
      <div class="flex-xs-column align-xs-start d-sm-flex flex-sm-wrap">
        <v-radio :value="'Face-to-face'" :label="t('face-to-face')"> </v-radio>
        <v-radio :value="'Distant'" :label="t('distant')"> </v-radio>
        <v-radio :value="'Blended'" :label="t('blended')"> </v-radio>
      </div>
    </v-radio-group>
  </div>
  <v-card color="#0097A7" :variant="'tonal'" >
    <v-card-item class="border-md">
      <p v-if="executionContextLocal === 'Face-to-face'">
        {{t('context')}} <strong>"{{t('face-to-face')}}"</strong> {{t('face-to-face-text-1')}}.<br/>
        {{t('face-to-face-text-2')}}.<br/>
        {{t('face-to-face-text-3')}}.
      </p>
      <p v-if="executionContextLocal === 'Distant'">
        {{t('context')}} <strong>"{{t('distant')}}"</strong> {{t('distant-text-1')}}.<br/>
        {{t('distant-text-2')}}.<br/>
        {{t('distant-text-3')}}.
      </p>
      <p v-if="executionContextLocal === 'Blended'">
        {{t('context')}} <strong>"{{t('blended')}}"</strong> {{t('blended-text-1')}}. <br/>
        {{t('blended-text-2')}}.<br/>
        {{t('blended-text-3')}}.
      </p>
    </v-card-item>
  </v-card>

  <v-divider class="mt-5"></v-divider>
  <v-checkbox v-if="!props.isOpenQuestion" v-model="studentsProvideExplanationLocal" :label="t('students-provide-explanation')"></v-checkbox>
  <div v-show="studentsProvideExplanationLocal">
    <div class="d-inline-flex">
      <v-checkbox :model-value="true" :label="t('students-review')" :disabled="true"></v-checkbox>
      <v-select
        v-model="nbOfAnswerEvaluatedLocal"
        :items="[5,4,3,2,1]"
        single-line
        class="mr-2 ml-2">
      </v-select>
      <p v-if="nbOfAnswerEvaluatedLocal !== 1" class="mt-4">{{t('answers')}}</p>
      <p v-if="nbOfAnswerEvaluatedLocal === 1" class="mt-4">{{t('answer')}}</p>
    </div>
    <h4>{{t('valuation-method')}} : </h4>
    <v-radio-group v-model="valuationMethodLocal">
      <v-radio value="Agreement-degree" :label="t('single-assessment-criterion')+' &quot;'+t('agreement-degree')+ ' &quot; '+t('without-textual-feedback')"></v-radio>
      <v-radio value="DRAXO" :label="t('DRAXO-description')"></v-radio>
    </v-radio-group>
    <a
      href="https://elaastic.github.io/elaastic-questions-server/en/key_concepts/DRAXO"
      target="_blank"
      rel="noopener"
      class="text-decoration-none text-primary position-absolute right-0"
    >
      <v-icon>mdi-help-circle-outline</v-icon>
      {{t('more-informations-on-DRAXO-grid-evaluation')}}
    </a>
    <h4 class="mt-10">{{t('automatical-feedback')}} : </h4>
    <v-checkbox v-model="withChatGPTExplanationLocal">
      <template v-slot:label>
        <p>{{t('ChatGPT-explanations')}}</p>

        <v-tooltip :text="t('tooltip-text')" location="top">
          <template #activator="{ props }">
            <v-icon v-bind="props" class="ml-2 cursor-pointer ">
              mdi-help-circle
            </v-icon>
          </template>
        </v-tooltip>

      </template>
    </v-checkbox>
  </div>
  <div class="position-absolute right-0">
    <v-btn class="text-none mr-3" color="#7CB342" @click="startSequence(executionContextLocal, studentsProvideExplanationLocal, valuationMethodLocal, withChatGPTExplanationLocal, nbOfAnswerEvaluatedLocal)">{{t('start-sequence')}}</v-btn>
    <v-btn class="text-none mr-3" color="#BDBDBD">{{t('cancel')}}</v-btn>
  </div>

  </template>

<style scoped>

</style>

<i18n>
{
  "en": {
    "configure-sequence": "Configure sequence",
    "execution-context": "Execution context",
    "face-to-face": "Face to face",
    "distant": "Distant",
    "blended": "Blended",
    "context": "The context",
    "face-to-face-text-1": "corresponds to an educational situation taking place in a classroom or lecture theatre",
    "face-to-face-text-2": "The teacher controls the start of the sequence and then the transition to the following phases",
    "face-to-face-text-3": "Learners must complete each phase within the allotted time and wait until the next phase begins.",
    "distant-text-1": "corresponds to an educational situation in which learners are autonomous",
    "distant-text-2": "The teacher only controls the opening and closing of the sequence",
    "distant-text-3": "Each learner can follow the sequence at his or her own pace, and then immediately see the results presented",
    "blended-text-1": "corresponds to a distance learning situation followed by a face-to-face presentation of the results",
    "blended-text-2": "The teacher controls the opening of the sequence and the publication of the results",
    "blended-text-3": "Learners can go through the first two phases at their own pace, but will only discover the results when they are published",
    "students-provide-explanation": "Students provide an explanation",
    "students-review": "Students review",
    "answers": "answers",
    "answer": "answer",
    "valuation-method": "Valuation method",
    "single-assessment-criterion": "Single assessment criterion",
    "agreement-degree": "Agreement degree",
    "without-textual-feedback": "without textual feedback",
    "DRAXO-description": "DRAXO criteria grid with possible text feedback",
    "more-informations-on-DRAXO-grid-evaluation": "More informations on DRAXO grid evaluation",
    "automatical-feedback": "Automatical feedback",
    "ChatGPT-explanations": "ChatGPT explanations",
    "tooltip-text": "For each student explanation, ChatGPT provide automatically an argued review based on the explanation provided by the teacher",
    "start-sequence": "Start the sequence",
    "cancel": "Cancel"
  },
  "fr": {
    "configure-sequence": "Configurer la séquence",
    "execution-context": "Contexte d'execution",
    "face-to-face": "Face à face",
    "distant": "À distance",
    "blended": "Hybride",
    "context": "Le contexte",
    "face-to-face-text-1": "correspond à une situation pédagogique se déroulant en classe ou en amphithéâtre",
    "face-to-face-text-2": "L'enseignant contrôle le démarrage de la séquence puis le passage aux phases suivantes",
    "face-to-face-text-3": "Les apprenants doivent accomplir chaque phase dans le temps imparti et patienter jusqu'à l'ouverture de la phase suivante",
    "distant-text-1": "correspond à une situation pédagogique pour laquelle les apprenants sont en situation d'autonomie",
    "distant-text-2": "L'enseignant ne contrôle que l'ouverture et la fermeture de la séquence",
    "distant-text-3": "Chaque apprenant a la possibilité d'enchaîner les phases de la séquence à son rythme, puis de découvrir immédiatement la présentation des résultats",
    "blended-text-1": "correspond à une situation pédagogique se déroulant à distance suivie d'une restitution des résultats en présentiel",
    "blended-text-2":"L'enseignant contrôle l'ouverture de la séquence et la publication des résultats",
    "blended-text-3": "Les apprenants peuvent enchaîner les deux premières phases à leur rythme mais ne découvriront les résultats qu'au moment de leur publication",
    "students-provide-explanation": "Les étudiants fournissent une explication",
    "students-review": "Les étudiants évaluent",
    "answers": "réponses",
    "answer": "réponse",
    "valuation-method": "Méthodes d'évaluation",
    "single-assessment-criterion": "Critère d'évaluation unique",
    "agreement-degree": "Degré d'accord",
    "without-textual-feedback": "sans feedback textuel",
    "DRAXO-description": "Grille de critères DRAXO avec feedback textuel possible",
    "more-informations-on-DRAXO-grid-evaluation": "Plus d'informations sur la grille d’évaluation DRAXO",
    "automatical-feedback": "Feedbacks automatiques",
    "ChatGPT-explanations": "Explications de ChatGPT",
    "tooltip-text": "Pour chaque explication d'étudiant, ChatGPT produit automatiquement une évaluation argumentée basée sur l'explication fournie par l'enseignant",
    "start-sequence": "Démarrer la séquence",
    "cancel": "Annuler"
  }
}
</i18n>
