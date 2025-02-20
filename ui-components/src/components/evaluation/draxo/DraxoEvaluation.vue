<script setup lang="ts">

import {Criteria} from "@/components/evaluation/draxo/Criteria";
import DraxoGrid, {type DraxoGridProps} from "@/components/evaluation/draxo/DraxoGrid.vue";
import EvaluationReaction from "@/components/moderation/EvaluationReaction.vue";
import {Option} from "@/components/evaluation/draxo/Option";
import {useI18n} from "vue-i18n";
import {OptionType} from "@/components/evaluation/draxo/OptionType";

const {t} = useI18n();

interface DraxoEvaluationProps {
  rejectedCriteria: Criteria | null,
  rejectedOption: Option | null,
  grader: string,
  graderComment: string,
  score: number | null,
  isTeacher: boolean
}

const props = defineProps<DraxoEvaluationProps>()

const criteria = (): DraxoGridProps => {
  const rejectedCriteria = props.rejectedCriteria;
  const result = new Map<Criteria, Option|null>();
  let afterRejected = false;

  for (const criteria of Criteria.values()) {
    if (!afterRejected) {
      if (!criteria.equals(rejectedCriteria)) {
        // From the start to the rejected criteria => YES
        result.set(criteria, Option.YES);
      } else {
        afterRejected = true;
        // TODO use correct value
        // For the rejected criteria => NO
        result.set(criteria, props.rejectedOption);
      }
    } else {
      // From the rejected criteria to the end => null
      result.set(criteria, null);
    }
  }

  return {
    criteriaD: result.get(Criteria.D)!!,
    criteriaR: result.get(Criteria.R)!!,
    criteriaA: result.get(Criteria.A)!!,
    criteriaX: result.get(Criteria.X)!!,
    criteriaO: result.get(Criteria.O)!!
  }
}

</script>

<template>
  <v-card class="ma-4 pa-4">
    <!-- Grader / Score / Actions -->
    <div class="d-flex align-center mb-4">
      <div class="d-flex align-center">
        <v-icon>mdi-account</v-icon>
        <!-- TODO i18n -->
        <span class="ml-2">Évaluateur :</span>
        <v-chip class="ml-2" small>{{ grader}}</v-chip>
      </div>

      <v-spacer></v-spacer>

      <div class="d-flex align-center">
        <!-- TODO i18n -->

        <span>Score :</span>
        <v-chip class="ml-2" small>{{ score }}</v-chip>
      </div>

      <v-btn icon class="ml-2">
        <v-icon>mdi-eye</v-icon>
      </v-btn>
    </div>

    <v-card-title class="justify-center text-h6">
      Grille d'évaluation
    </v-card-title>
    <DraxoGrid v-bind="criteria()"/>

    <v-row class="mt-4" v-if="rejectedCriteria !== null">
      <v-col cols="6">
        <div class="mb-2">{{ rejectedCriteria.i18nCode  }}</div>
        <p>{{ t('option.no') }}</p>
      </v-col>

      <v-col cols="6">
        <div class="mb-2">Commentaire :</div>
        <p>{{ graderComment }}</p>
      </v-col>
    </v-row>
    <EvaluationReaction :is-chat-g-p-t="false" :is-teacher="isTeacher" :be-a-dialog="false" :selected-grade="null" :content-to-report="'Oui'"/>
  </v-card>
</template>

<style scoped>

</style>

<i18n>
{
  "en": {
    "option": {
      "yes": "Yes",
      "no": "No",
      "partially": "Partially",
      "dont_know": "Don't know",
      "no_opinion": "No opinion"
    },
    "criteria": {
      "understandable": {
        "header": "unDerstandable",
        "question": "I understand what the answer says"
      },
      "relevant": {
        "header": "Relevant",
        "question": "I think the answer corresponds to the question asked"
      },
      "agreed": {
        "header": "Agreed",
        "question": "I agree with the proposed answer"
      },
      "exhaustive": {
        "header": "eXhaustive",
        "question": "I think the answer can be improved"
      },
      "optimal": {
        "header": "Optimal",
        "question": "I think the answer can be improved"
      }
    }
  },
  "fr": {
    "option": {
      "yes": "Oui",
      "no": "Non",
      "partially": "Partiellement",
      "dontKnow": "Ne sais pas",
      "noOpinion": "Pas d'opinion"
    },
    "criteria": {
      "understandable": {
        "header": "Déchiffrable",
        "question": "Je comprend ce qui est écrit dans la réponse"
      },
      "relevant": {
        "header": "Répond à la question",
        "question": "Je trouve que la réponse correspond à la question posée"
      },
      "agreed": {
        "header": "Acceptable",
        "question": "Je suis d'accord avec la réponse proposée"
      },
      "exhaustive": {
        "header": "eXhaustive",
        "question": "Je pense que la réponse est complète"
      },
      "optimal": {
        "header": "Optimale",
        "question": "Je pense que la réponse peut être améliorée"
      }
    }
  }
}
</i18n>
