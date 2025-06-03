<script setup lang="ts">
import {type PropType} from "vue";
import {useI18n} from "vue-i18n";

export type StepState = 'COMPLETED' | 'ACTIVE' | 'DISABLED';
const props = defineProps({
  /**
   * A boolean. true if the statistics are shown. false if not.
   */
  showStatistics: {
    type: Boolean,
    default: false,
  },
  /**
   * The number of answer at the first attempt of the test.
   */
  numberOfAnswerFirstAttempt: {
    type: Number,
  },
  /**
   * The number of answer at the second attempt of the test.
   */
  numberOfAnswerSecondAttempt: {
    type: Number,
  },
  /**
   * The number of person who gave a review of an answer from someone else.
   */
  numberOfReviewers: {
    type: Number,
  },
  /**
   * A boolean. true if students gave explanations. false if not.
   */
  studentsProvideExplanation: {
    type: Boolean,
    default: true
  },
  /**
   * The state of the first step. It could be : 'DISABLED', 'COMPLETED', 'ACTIVE'
   */
  responseSubmissionState: {
    type: Object as PropType<StepState>,
  },
  /**
   * The state of the second step. It could be : 'DISABLED', 'COMPLETED', 'ACTIVE'
   */
  evaluationState: {
    type: Object as PropType<StepState>,
  },
  /**
   * The state of the third step. It could be : 'DISABLED', 'COMPLETED', 'ACTIVE'
   */
  readState: {
    type: Object as PropType<StepState>,
  }
});
const { t } = useI18n();
</script>

<template>

  <div v-if="props.showStatistics">
    <v-tooltip :text="t('number-first-attempt')" location="bottom" >
      <template v-slot:activator="{ props: tooltipProps}" >
        <v-btn size="small" v-bind="tooltipProps" class="nb_rep_1" icon>
          <svg xmlns="http://www.w3.org/2000/svg" width="30" height="50" viewBox="0 0 110 80" fill="none" :stroke="'white'" stroke-width="4">
            <ellipse cx="50" cy="35" rx="40" ry="20" :fill="'lightgray'" />
            <path d="M40 60 L30 67 L50 60 Z" :fill="'lightgray'" :stroke="'white'" stroke-width="2" />
          </svg>
          {{props.numberOfAnswerFirstAttempt}}
        </v-btn>
      </template>
    </v-tooltip>
    <v-tooltip :text="t('number-of-reviewers')" location="bottom">
      <template v-slot:activator="{props: tooltipProps}">
        <v-btn size="small" v-bind="tooltipProps" class="nb_rep_2" icon>
          <svg xmlns="http://www.w3.org/2000/svg" width="30" height="60" viewBox="0 0 160 90" fill="none" :stroke="'white'" stroke-width="2">
            <ellipse cx="50" cy="40" rx="40" ry="22" :fill="'lightgray'" />
            <path d="M40 65 L30 75 L50 65 Z" :fill="'lightgray'" :stroke="'white'" stroke-width="2" />
            <ellipse cx="110" cy="40" rx="40" ry="22" :fill="'white'" />
            <path d="M120 65 L130 75 L110 65 Z" :fill="'white'" :stroke="'white'" stroke-width="2" />
          </svg>
          {{props.numberOfReviewers}}
        </v-btn>
      </template>
    </v-tooltip>
    <v-tooltip :text="t('number-second-attempt')" location="bottom">
      <template v-slot:activator="{props: tooltipProps}">
        <v-btn size="small" v-bind="tooltipProps" class="nb_rep_3" icon>
          <svg xmlns="http://www.w3.org/2000/svg" width="30" height="50" viewBox="0 0 110 80" fill="none" :stroke="'white'" stroke-width="4">
            <ellipse cx="50" cy="35" rx="40" ry="20" :fill="'lightgray'" />
            <path d="M40 60 L30 67 L50 60 Z" :fill="'lightgray'" :stroke="'white'" stroke-width="2" />
          </svg>
          {{props.numberOfAnswerSecondAttempt}}
        </v-btn>
      </template>
    </v-tooltip>
  </div>


  <ul class="stepper">

    <li :class="['stepper__item',
                  props.responseSubmissionState === 'ACTIVE' ? 'active' : props.responseSubmissionState === 'COMPLETED' ? 'completed' : 'disabled',
                  props.responseSubmissionState === 'ACTIVE' ? 'arrow-blue' : props.responseSubmissionState === 'COMPLETED' ? 'arrow-gray' : 'arrow-white']">
      <span :class="['stepper__title',
                      props.responseSubmissionState === 'ACTIVE' ? 'stepper__title_active' : props.responseSubmissionState === 'COMPLETED' ? 'stepper_title_completed' : 'stepper__title_disabled']">{{t('step-1')}}</span>
      <div :class="props.responseSubmissionState === 'DISABLED' ? 'stepper_content_disabled' : 'stepper_content'">
        {{props.studentsProvideExplanation ? t('reasoned-response') : t('first-response')}}</div>
      <svg xmlns="http://www.w3.org/2000/svg" width="100" height="50" viewBox="0 0 110 67" fill="none" :stroke="props.responseSubmissionState === 'ACTIVE' ? '#1976D2' : props.responseSubmissionState === 'COMPLETED' ? 'black' : 'lightgray'" stroke-width="2">
        <ellipse cx="50" cy="35" rx="40" ry="20" :fill="props.responseSubmissionState === 'ACTIVE' ? '#BBDEFB' : props.responseSubmissionState === 'COMPLETED' ? 'lightgray' : 'white'" />
        <path d="M40 60 L30 67 L50 60 Z"
              :fill="props.responseSubmissionState === 'ACTIVE' ? '#BBDEFB' : props.responseSubmissionState === 'COMPLETED' ? 'lightgray' : 'white'"
              :stroke="props.responseSubmissionState === 'ACTIVE' ? '#1976D2' : props.responseSubmissionState === 'COMPLETED' ? 'black' : 'lightgray'" stroke-width="2" />
      </svg>
    </li>

    <li :class="['stepper__item',
                  props.evaluationState === 'ACTIVE' ? 'active' : props.evaluationState === 'COMPLETED' ? 'completed' : 'disabled',
                  props.evaluationState === 'ACTIVE' ? 'arrow-blue' : props.evaluationState === 'COMPLETED' ? 'arrow-gray' : 'arrow-white']">
      <span :class="['stepper__title',
                      props.evaluationState === 'ACTIVE' ? 'stepper__title_active' : props.evaluationState === 'COMPLETED' ? 'stepper_title_completed' : 'stepper__title_disabled']">{{t('step-2')}}</span>
      <div :class="props.evaluationState === 'DISABLED' ? 'stepper_content_disabled' : 'stepper_content'">
        {{t('confronting-viewpoints')}}</div>
      <svg xmlns="http://www.w3.org/2000/svg" width="120" height="60" viewBox="0 0 160 90" fill="none" :stroke="props.evaluationState === 'ACTIVE' ? '#1976D2' : props.evaluationState === 'COMPLETED' ? 'black' : 'lightgray'" stroke-width="2">
        <ellipse cx="50" cy="40" rx="40" ry="22" :fill="props.evaluationState === 'ACTIVE' ? '#BBDEFB' : props.evaluationState === 'COMPLETED' ? 'lightgray' : 'white'" />
        <path d="M40 65 L30 75 L50 65 Z"
              :fill="props.evaluationState === 'ACTIVE' ? '#BBDEFB' : props.evaluationState === 'COMPLETED' ? 'lightgray' : 'white'"
              :stroke="props.evaluationState === 'ACTIVE' ? '#1976D2' : props.evaluationState === 'COMPLETED' ? 'black' : 'lightgray'" stroke-width="2" />
        <ellipse cx="110" cy="40" rx="40" ry="22" :fill="props.evaluationState === 'ACTIVE' ? '#1976D2' : props.evaluationState === 'COMPLETED' ? 'black' : 'lightgray'" /> />
        <path d="M120 65 L130 75 L110 65 Z"
              :fill="props.evaluationState === 'ACTIVE' ? '#1976D2' : props.evaluationState === 'COMPLETED' ? 'black' : 'lightgray'"
              :stroke="props.evaluationState === 'ACTIVE' ? '#1976D2' : props.evaluationState === 'COMPLETED' ? 'black' : 'lightgray'" stroke-width="2" />
      </svg>
    </li>

    <li :class="['stepper__item',
                  props.readState === 'ACTIVE' ? 'active' : props.readState === 'COMPLETED' ? 'completed' : 'disabled']">
      <span :class="['stepper__title',
                      props.readState === 'ACTIVE' ? 'stepper__title_active' : props.readState === 'COMPLETED' ? 'stepper_title_completed' : 'stepper__title_disabled']">{{t('step-3')}}</span>
      <div :class="props.readState === 'DISABLED' ? 'stepper_content_disabled' : 'stepper_content'">
        {{t('Results')}}</div>
      <svg xmlns="http://www.w3.org/2000/svg" width="80" height="60" viewBox="0 0 100 80" fill="none" :stroke="props.readState === 'ACTIVE' ? '#1976D2' : props.readState === 'COMPLETED' ? 'black' : 'lightgray'" stroke-width="2">
        <line x1="15" y1="10" x2="15" y2="70" :stroke="props.readState === 'ACTIVE' ? '#1976D2' : props.readState === 'COMPLETED' ? 'black' : 'lightgray'" stroke-width="2"/>
        <line x1="15" y1="70" x2="90" y2="70" :stroke="props.readState === 'ACTIVE' ? '#1976D2' : props.readState === 'COMPLETED' ? 'black' : 'lightgray'" stroke-width="2"/>
        <rect x="25" y="50" width="10" height="20" :fill="props.readState === 'ACTIVE' ? '#1976D2' : props.readState === 'COMPLETED' ? 'black' : 'lightgray'" />
        <rect x="45" y="35" width="10" height="35" :fill="props.readState === 'ACTIVE' ? '#1976D2' : props.readState === 'COMPLETED' ? 'black' : 'lightgray'" />
        <rect x="65" y="20" width="10" height="50" :fill="props.readState === 'ACTIVE' ? '#1976D2' : props.readState === 'COMPLETED' ? 'black' : 'lightgray'" />
      </svg>
    </li>

  </ul>
</template>

<style scoped>
.stepper {
  display: flex;
  padding: 0;
  list-style: none;
}

.stepper__item:not(:last-child)::after {
  content: "";
  position: absolute;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  z-index: 2;
}

.stepper__item.arrow-blue:not(:last-child)::after {
  filter: drop-shadow(0 0 0.5px #1976D2);
}

.stepper__item.arrow-gray:not(:last-child)::after {
  filter: drop-shadow(0 0 0.5px #666);
}

.stepper__item.arrow-white:not(:last-child)::after {
  filter: drop-shadow(0 0 0.5px gray);
}

.stepper__item{
  border: 1px solid darkgray;
  border-radius: 8px;
  margin-bottom: 0px;
}

.stepper__item.active {
  background: #BBDEFB;
}

.stepper__item.completed {
  background: lightgray;
}

.stepper__item.disabled {
  background: white;
}
.stepper__title {
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 4px;
}
.stepper_title_completed{
  color: black;
}

.stepper__title_active {
  color: #1976D2;
}

.stepper__title_disabled {
  color: lightgray;
}

.stepper_content{
  color: black;
}
.stepper_content_disabled{
  color: lightgray;
}




@media (min-width: 900px){
  .nb_rep_1 {
    position: absolute;
    top: -10px;
    left: 3%;
    z-index: 10;
    display: flex;
    gap: 0;
    color: white;
    font-weight: bold;
  }
  .nb_rep_2{
    position: absolute;
    top: -10px;
    left: 36%;
    z-index: 10;
    display: flex;
    gap: 0;
    color: white;
    font-weight: bold;
  }
  .nb_rep_3{
    position: absolute;
    top: -10px;
    left: 45%;
    z-index: 10;
    display: flex;
    gap: 0;
    color: white;
    font-weight: bold;
  }
  .v-btn {
    min-width: 32px !important;
    height: 32px !important;
    width: 48px !important;
    padding: 0 !important;
    border-radius: 8px !important;
    background-color: darkgray;
  }
  .stepper {
    position: relative;
  }

  .stepper__item {
    position: relative;
    padding: 10px 10px 10px 30px;
    flex-grow: 1;
    min-width: 200px;
    text-align: center;
  }

  /* Right arrow */
  .stepper__item:not(:last-child)::after {
    top: 50%;
    right: -12px;
    border-top: 8px solid transparent;
    border-bottom: 8px solid transparent;
    border-left: 12px solid lightgray;
  }

  /* Blue arrow with border */
  .stepper__item.arrow-blue:not(:last-child)::after {
    border-left-color: #BBDEFB;
  }

  /* Gray arrow with border */
  .stepper__item.arrow-gray:not(:last-child)::after {
    border-left-color: lightgray;
  }
  .stepper__item.arrow-white:not(:last-child)::after {
    border-left-color: white;
  }
}





@media (max-width: 900px) {
  .nb_rep_1 {
    position: absolute;
    top: -5px;
    left: 8%;
    z-index: 10;
    display: flex;
    gap: 0;
    color: white;
    font-weight: bold;
  }
  .nb_rep_2{
    position: absolute;
    top:150px;
    left: 8%;
    z-index: 10;
    display: flex;
    gap: 0;
    color: white;
    font-weight: bold;
  }
  .nb_rep_3{
    position: absolute;
    top: 150px;
    left: 120px;
    z-index: 10;
    display: flex;
    gap: 0;
    color: white;
    font-weight: bold;
  }
  .v-btn {
    min-width: 32px !important;
    height: 32px !important;
    width: 48px !important;
    padding: 0 !important;
    border-radius: 8px !important;
    background-color: darkgray;
  }
  .stepper {
    flex-direction: column;
    align-items: stretch;
    width: 100%;
  }

  .stepper__item {
    position: relative;
    padding: 20px 10px;
    text-align: center;
    transition: background 0.3s;
  }

  /* Bottom arrow */
  .stepper__item:not(:last-child)::after {
    bottom: -12px;
    left: 50%;
    transform: translateX(-50%);
    border-left: 8px solid transparent;
    border-right: 8px solid transparent;
    border-top: 12px solid lightgray;
  }

  /* Blue arrow with border */
  .stepper__item.arrow-blue:not(:last-child)::after {
    border-top-color: #BBDEFB;
  }

  /* Gray arrow with border */
  .stepper__item.arrow-gray:not(:last-child)::after {
    border-top-color: lightgray;
  }

  .stepper__item.arrow-white:not(:last-child)::after {
    border-left-color: white;
  }

}

</style>
<i18n>
{
  "en": {
    "step-1": "Step 1",
    "reasoned-response": "Reasoned response",
    "step-2": "Step 2",
    "confronting-viewpoints": "Confronting viewpoints",
    "step-3": "Step 3",
    "Results": "Results",
    "first-response": "First response",
    "number-first-attempt": "Number of answer for the first attempt",
    "number-of-reviewers": "Number of reviewers",
    "number-second-attempt": "Number of answer for the second attempt"
  },
  "fr": {
    "step-1": "Phase 1",
    "reasoned-response": "Réponse argumentée",
    "step-2": "Phase 2",
    "confronting-viewpoints": "Confrontation de points de vue",
    "step-3": "Phase 3",
    "Results": "Résultats",
    "first-response": "Première Réponse",
    "number-first-attempt": "Nombre de réponses à la tentative n°1",
    "number-of-reviewers": "Nombre d'évaluateurs",
    "number-second-attempt": "Nombre de réponses à la tentative n°2"
  }
}
</i18n>
