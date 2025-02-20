<script setup lang="ts">

import {OptionType} from "@/components/evaluation/draxo/OptionType";
import {watch} from "vue";
import {useI18n} from "vue-i18n";
import {Criteria} from "@/components/evaluation/draxo/Criteria";
import {Option} from "@/components/evaluation/draxo/Option";

const {t} = useI18n();

interface DraxoGridProps {
  /**
   * The value of the criteria D
   */
  criteriaD: OptionType | null,
  /**
   * The value of the criteria R
   */
  criteriaR: OptionType | null,
  /**
   * The value of the criteria A
   */
  criteriaA: OptionType | null,
  /**
   * The value of the criteria X
   */
  criteriaX: OptionType | null,
  /**
   * The value of the criteria O
   */
  criteriaO: OptionType | null
}

const props = defineProps<DraxoGridProps>()


const criteriaOptions = new Map<Criteria, Option | null>();

const updateCriteriaOptions = () => {
  criteriaOptions.set(Criteria.D, Option.get(props.criteriaD));
  criteriaOptions.set(Criteria.R, Option.get(props.criteriaR));
  criteriaOptions.set(Criteria.A, Option.get(props.criteriaA));
  criteriaOptions.set(Criteria.X, Option.get(props.criteriaX));
  criteriaOptions.set(Criteria.O, Option.get(props.criteriaO));
};

// Initialize the map
updateCriteriaOptions();

// Watch for changes in props and update the map
watch(() => props, updateCriteriaOptions, {deep: true});


</script>

<template>
  <div class="DRAXO-grid" id="expanded">
    <div class="custom-step" :class="criteriaOptions.get(criteria)?.cssClass" v-for="criteria in Criteria.values()"
         :key="criteria.capitalLetter">
      <div class="custom-step-content">
        <span>{{ t(criteria.header()) }}</span>

        <v-icon v-if="criteriaOptions.get(criteria)?.cssClass === 'positive'" icon="mdi-check-bold"></v-icon>
        <v-icon v-else-if="criteriaOptions.get(criteria)?.cssClass === 'negative'" icon="mdi-close"></v-icon>
        <v-icon v-else-if="criteriaOptions.get(criteria)?.cssClass === 'unknown'" icon="mdi-help"></v-icon>
      </div>
    </div>
  </div>
  <div class="DRAXO-grid" id="collasped">
    <div class="custom-step" :class="criteriaOptions.get(criteria)?.cssClass" v-for="criteria in Criteria.values()"
         :key="criteria.capitalLetter">
      <div class="custom-step-content">
        <span class="capital-letter">{{ t(criteria.capitalLetter) }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.DRAXO-grid {
  display: flex;
  flex-direction: row;
  width: 100%;
  font-size: 1rem;
}

.custom-step {
  font-size: 1em;
  position: relative;
  display: flex;
  flex-direction: row;
  flex: 1 0 auto;
  flex-wrap: wrap;
  vertical-align: middle;
  align-items: center;
  justify-content: center;
  margin: 0 0;
  padding: 1em 0 1em 0;
  height: 3.5em;

  /* By default a step is disabled */
  background-color: #F2F2F2;
  border: 1px solid #D9D9D9;
  color: #757575;
}

.custom-step.positive {
  background-color: #99DBBB;
  border: 1px solid #7A7A7A;
  color: #01562d;
}

.custom-step.negative {
  background-color: #FCB5D0;
  border: 1px solid #7A7A7A;
  color: #802345;
}

.custom-step.unknown {
  background-color: white;
  border: 1px solid #7A7A7A;
  color: black;
}

.custom-step.negative, .custom-step.unknown {
  font-weight: bold;
}

.custom-step > .custom-step-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  text-align: center;

  z-index: 3;

  font-size: 1rem;
}

.custom-step:first-child {
  border-radius: .28571429rem 0 0 .28571429rem;
}

.custom-step:last-child {
  border-radius: 0 .28571429rem .28571429rem 0;
}

#expanded {
  .custom-step::after {
    position: absolute;
    z-index: 2;
    content: '';
    top: 50.5%;
    right: -0.7px;
    background-color: inherit;
    width: 2.5em;
    height: 2.5em;

    /* We only keep the top and right border */
    border-width: 0 1px 1px 0;
    border-style: solid;
    border-color: inherit;
    border-radius: 1px;

    transform: translateY(-50%) translateX(50%) rotate(-45deg);
  }

  .custom-step:last-child::after {
    display: none;
  }

  .custom-step:not(:first-child) {
    border-left-width: 0 !important;
  }

  .custom-step:not(:last-child) {
    border-right-width: 0 !important;
  }

  .custom-step:not(:first-child) > .custom-step-content {
    margin-left: 1.1em;
  }

  /* If their is an icon next to the text (span), add a little margin */

  .custom-step-content span:has(~ .v-icon) {
    margin-right: 1.1em;
  }
}

#collasped {
  display: none;

  .custom-step {
    border-left: 1px solid #7A7A7A;
    border-right: 1px solid #7A7A7A;
    border-bottom: 1px solid #7A7A7A;
    border-top: 1px solid #7A7A7A;
  }
}

@media (max-width: 768px) {
  #expanded {
    display: none;
  }

  #collasped {
    display: flex;
  }
}
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
