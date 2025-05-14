<script setup lang="ts">

import {ref} from "vue";
import BarChart from "@/components/results/BarChart.vue";
import DoubleBarChartH from "@/components/results/DoubleBarChartH.vue";
import {useI18n} from "vue-i18n";

const props = defineProps({
  /**
   * The tab selected at the beginning.
   */
  tab: {
    type: String,
    default: "votes"
  },
  /**
   * The chart's data of the vote's tab.
   */
  dataVoteChart: {
    type: Array<{ choix: number, value: number, isCorrect: boolean }>,
    default: () => []
  },
  /**
   * The left chart's data of the confiance's tab.
   */
  dataTrustChartLeft: {
    type: Array<number>,
    default: () => []
  },
  /**
   * The right chart's data of the confiance's tab.
   */
  dataTrustChartRight: {
    type: Array<number>,
    default: () => []
  },
  /**
   * The left chart's data of the pair's tab.
   */
  dataPeerChartLeft: {
    type: Array<number>,
    default: () => []
  },
  /**
   * The right chart's data of the pair's tab.
   */
  dataPeerChartRight: {
    type: Array<number>,
    default: () => []
  },
  /**
   * A boolean. true if the peer's tab has to be shown. false if not.
   */
  displayPeerTab: {
    type: Boolean,
    default: false
  }
});
const selectedTab = ref(props.tab);
const { t } = useI18n()
</script>

<template>
    <v-tabs v-model="selectedTab">
      <v-tab value="votes">
        {{t('distribution-of-votes')}}
      </v-tab>
      <v-tab value="trust">
        {{t('trust-degree')}}
      </v-tab>
      <v-tab v-if="displayPeerTab" value="peers">
        {{t('peer-review')}}
      </v-tab>
    </v-tabs>
  <v-tabs-window v-model="selectedTab">
    <v-tabs-window-item value="votes">
      <BarChart
              :data="props.dataVoteChart"
              title=""
              :x-label="t('choice')"
              :y-label= "t('percentage-of-voters')"
              :width="500"
      ></BarChart>
    </v-tabs-window-item>
    <v-tabs-window-item value="trust">
      <DoubleBarChartH
              :data-left="[{ choix: t('completely-confident'), value: props.dataTrustChartLeft.at(0)}, { choix: t('confident'), value: props.dataTrustChartLeft.at(1)}, { choix: t('not-really-confident'), value: props.dataTrustChartLeft.at(2)}, { choix: t('not-confident-at-all'), value: props.dataTrustChartLeft.at(3)}]"
              :data-right="[{ choix: t('completely-confident'), value: props.dataTrustChartRight.at(0) }, { choix: t('confident'), value: props.dataTrustChartRight.at(1) }, { choix: t('not-really-confident'), value: props.dataTrustChartRight.at(2) }, { choix: t('not-confident-at-all'), value: props.dataTrustChartRight.at(3)}]"
              :title-left= "t('good-answer')"
              :title-right="t('bad-answer')"
              :x-label="t('percentage-of-voters')"
              :width="500"></DoubleBarChartH>
    </v-tabs-window-item>
    <v-tabs-window-item value="peers">
      <DoubleBarChartH
              :data-left="[{ choix: t('completely-agree'), value: props.dataPeerChartLeft.at(0)}, { choix: t('agree'), value: props.dataPeerChartLeft.at(1)}, { choix: t('neither-agree-nor-disagree'), value: props.dataPeerChartLeft.at(2)}, { choix: t('not-agree'), value: props.dataPeerChartLeft.at(3)}, { choix: t('not-agree-at-all'), value: props.dataPeerChartLeft.at(4)}]"
              :data-right="[{ choix: t('completely-agree'), value: props.dataPeerChartRight.at(0)}, { choix: t('agree'), value: props.dataPeerChartRight.at(1)}, { choix: t('neither-agree-nor-disagree'), value: props.dataPeerChartRight.at(2)}, { choix: t('not-agree'), value: props.dataPeerChartRight.at(3)}, { choix: t('not-agree-at-all'), value: props.dataPeerChartRight.at(4)}]"
              :title-left="t('good-answer')"
              :title-right="t('bad-answer')"
              :x-label="t('percentage-of-voters')"
              :width="500"></DoubleBarChartH>
    </v-tabs-window-item>
  </v-tabs-window>
</template>

<style scoped>
</style>

<i18n>
 {
  "en": {
    "distribution-of-votes": "Distribution of votes",
    "trust-degree": "Trust Degree",
    "peer-review": "Peer review",
    "choice": "Choice",
    "percentage-of-voters": "Percentage of voters",
    "completely-confident": "Completely confident",
    "confident": "Confident",
    "not-really-confident": "Not really confident",
    "not-confident-at-all": "Not confident at all",
    "good-answer": "Good answer(s)",
    "bad-answer": "Bad answer(s)",
    "completely-agree": "Completely agree",
    "agree": "Agree",
    "neither-agree-nor-disagree": "Neither agree nor disagree",
    "not-agree": "Not agree",
    "not-agree-at-all": "Not agree at all"
  },
  "fr": {
    "distribution-of-votes": "Répartition des votes",
    "trust-degree": "Degré de confiance (phase 1)",
    "peer-review": "Evaluation par les pairs",
    "choice": "Choix",
    "percentage-of-voters": "Pourcentage des votants",
    "completely-confident": "Tout à fait confiant(e)",
    "confident": "Confiant(e)",
    "not-really-confident": "Pas vraiment confiant(e)",
    "not-confident-at-all": "Pas du tout confiant(e)",
    "good-answer": "Bonne(s) réponse(s)",
    "bad-answer": "Mauvaise(s) réponse(s)",
    "completely-agree": "Tout à fait d'accord",
    "agree": "D'accord",
    "neither-agree-nor-disagree": "Ni d'accord ni en désaccord",
    "not-agree": "Pas d'accord",
    "not-agree-at-all": "Pas du tout d'accord"
  }
 }
</i18n>
