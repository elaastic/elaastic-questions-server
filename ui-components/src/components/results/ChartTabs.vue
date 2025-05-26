<script setup lang="ts">

import {onBeforeUnmount, onMounted, ref} from "vue";
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
    type: Array<{ itemIndex: number, value: number, isCorrect: boolean }>,
    default: () => []
  },
  /**
   * The left chart's data of the confident's tab.
   */
  dataConfidenceChartLeft: {
    type: Array<number>,
    default: () => []
  },
  /**
   * The right chart's data of the confident's tab.
   */
  dataConfidenceChartRight: {
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
  },
  /**
   * A boolean. true if the confidence tab has to be sown. false if not.
   */
  displayConfidenceTab: {
    type: Boolean,
    default: false
  },
});
const selectedTab = ref(props.tab);
const { t } = useI18n()

const chartWidth = ref(500)

const updateChartWidth = () => {
  chartWidth.value = window.innerWidth < 900 ? window.innerWidth * 0.3 : 500
}
onMounted(() => {
  updateChartWidth()
  window.addEventListener('resize', updateChartWidth)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateChartWidth)
})
</script>

<template>
  <v-tabs v-model="selectedTab">
    <v-tab value="votes" class="text-none">
      <strong v-if="selectedTab==='votes'">{{t('distribution-of-votes')}}</strong>
      <div v-else>{{t('distribution-of-votes')}}</div>
    </v-tab>
    <v-tab v-if="displayConfidenceTab" value="confidence" class="text-none">
      <strong v-if="selectedTab==='confidence'">{{t('confidence-degree')}}</strong>
      <div v-else>{{t('confidence-degree')}}</div>
    </v-tab>
    <v-tab v-if="displayPeerTab" value="peers" class="text-none">
      <strong v-if="selectedTab==='peers'">{{t('peer-review')}}</strong>
      <div v-else>{{t('peer-review')}}</div>
    </v-tab>
  </v-tabs>
  <div class="chartTabs">
  <v-tabs-window v-model="selectedTab">
    <v-tabs-window-item value="votes">
      <BarChart class="voteChart"
              :data="props.dataVoteChart"
              title=""
              :x-label="t('choice')"
              :y-label= "t('percentage-of-voters')"
              :width="chartWidth"
      ></BarChart>
    </v-tabs-window-item>
    <v-tabs-window-item value="confidence">
      <DoubleBarChartH class="chart"
              :data-left="[{ itemIndex: t('completely-confident'), value: props.dataConfidenceChartLeft.at(0) ?? -1}, { itemIndex: t('confident'), value: props.dataConfidenceChartLeft.at(1) ?? -1}, { itemIndex: t('not-really-confident'), value: props.dataConfidenceChartLeft.at(2) ?? -1}, { itemIndex: t('not-confident-at-all'), value: props.dataConfidenceChartLeft.at(3) ?? -1}]"
              :data-right="[{ itemIndex: t('completely-confident'), value: props.dataConfidenceChartRight.at(0) ?? -1}, { itemIndex: t('confident'), value: props.dataConfidenceChartRight.at(1) ?? -1 }, { itemIndex: t('not-really-confident'), value: props.dataConfidenceChartRight.at(2) ?? -1}, { itemIndex: t('not-confident-at-all'), value: props.dataConfidenceChartRight.at(3) ?? -1}]"
              :title-left= "t('good-answer')"
              :title-right="t('bad-answer')"
              :x-label="t('percentage-of-voters')"
              :width="chartWidth"></DoubleBarChartH>
    </v-tabs-window-item>
    <v-tabs-window-item value="peers">
      <DoubleBarChartH class="chart"
              :data-left="[{ itemIndex: t('completely-agree'), value: props.dataPeerChartLeft.at(0) ?? -1}, { itemIndex: t('agree'), value: props.dataPeerChartLeft.at(1) ?? -1}, { itemIndex: t('neither-agree-nor-disagree'), value: props.dataPeerChartLeft.at(2) ?? -1}, { itemIndex: t('not-agree'), value: props.dataPeerChartLeft.at(3) ?? -1}, { itemIndex: t('not-agree-at-all'), value: props.dataPeerChartLeft.at(4) ?? -1}]"
              :data-right="[{ itemIndex: t('completely-agree'), value: props.dataPeerChartRight.at(0) ?? -1}, { itemIndex: t('agree'), value: props.dataPeerChartRight.at(1) ?? -1}, { itemIndex: t('neither-agree-nor-disagree'), value: props.dataPeerChartRight.at(2) ?? -1}, { itemIndex: t('not-agree'), value: props.dataPeerChartRight.at(3) ?? -1}, { itemIndex: t('not-agree-at-all'), value: props.dataPeerChartRight.at(4) ?? -1}]"
              :title-left="t('good-answer')"
              :title-right="t('bad-answer')"
              :x-label="t('percentage-of-voters')"
              :width="chartWidth"></DoubleBarChartH>
    </v-tabs-window-item>
  </v-tabs-window>
  </div>
</template>

<style scoped>
.voteChart{
  margin-left: 20%;
  margin-top: 3%;
  width: 100%
}
.chart{
  margin-top: 3%;
  margin-left: 18%;
}

.chartTabs{
  box-sizing: border-box;
  border-radius: 2px;
  border: 1px solid gray;
}
@media (min-width: 900px) {
  .voteChart{
    margin-left: 25%;
  }
}
</style>

<i18n>
 {
  "en": {
    "distribution-of-votes": "Distribution of votes",
    "confidence-degree": "Confidence Degree",
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
    "confidence-degree": "Degré de confiance (phase 1)",
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
