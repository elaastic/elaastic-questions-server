<script setup lang="ts">
import ContentBlock from "@/components/player/ContentBlock.vue";
import ChartTabs from "@/components/results/ChartTabs.vue";
import Explanation from "@/components/results/Explanation.vue";
import type {AnyResponse} from "@/models/Response";
import {onMounted, type PropType, ref} from "vue";
import TextBar from "@/components/util/TextBar.vue";
import type {QuestionType} from "@/models/Response"
import {useI18n} from "vue-i18n";
const props = defineProps({
  /**
   * The data of the results chart. It's an array of 3-uplet : the number of the answer, the percentage of students who have chosen this answer and a boolean to say if it's the good answer.
   */
  dataVote: {
    type: Array<{ choix: number, value: number, isCorrect: boolean }>,
    default: () => []
  },
  /**
   * The percentage of each feeling for students who answered correctly. The feeling in the order are : "Completely confident", "Confident", "Not really confident", "Not confident at all"
   */
  dataTrustGoodAnswer: {
    type: Array<number>,
    default: () => []
  },
  /**
   * The percentage of each feeling for students who answered wrongly. The feeling in the order are : "Completely confident", "Confident", "Not really confident", "Not confident at all"
   */
  dataTrustBadAnswer: {
    type: Array<number>,
    default: () => []
  },
  /**
   * The percentage of each level of agreement for correct answer(s). The level are : "Completely agree", "Agree", "Neither agree nor disagree", "Not agree", "Not agree at all"
   */
  dataPeerGoodAnswer: {
    type: Array<number>,
    default: () => []
  },
  /**
   * The percentage of each level of agreement for wrong answer(s). The level are : "Completely agree", "Agree", "Neither agree nor disagree", "Not agree", "Not agree at all"
   */
  dataPeerBadAnswer: {
    type: Array<number>,
    default: () => []
  },
  /**
   * A boolean. true if the peer tab has to be sown. false if not.
   */
  displayPeerTab: {
    type: Boolean,
    default: false
  },
  /**
   * A boolean. true if the trust tab has to be sown. false if not.
   */
  displayTrustTab: {
    type: Boolean,
    default: false
  },
  /**
   * The answers and explanations given by students and possibly the teacher. If there was a review, it also has a grade and a number of reviewer
   */
  explanations: {
    type: Array<{answer: AnyResponse, grade: number, nbPeer: number, isTeacher: boolean}>,
    default: () => []
  },
  /**
   * The type of the question related to the results. It could be : MultipleChoice, OpenEnded or ExclusiveChoice
   */
  qType: {
    type: Object as PropType<QuestionType>,
    default: () => 'MultipleChoice',
  },
})

const teacherExp = ref<{answer: AnyResponse, grade: number, nbPeer: number, isTeacher: boolean} | null>(null)
const studentExplanations = ref<typeof props.explanations>([])


onMounted(() => {
  teacherExp.value = props.explanations.find(exp => exp.isTeacher) ?? null
  studentExplanations.value = props.explanations.filter(exp => !exp.isTeacher)
})

const { t } = useI18n()
</script>


<template>
    <ContentBlock :title="t('results')" :readonly="true" :state="0" side="">
    <v-tooltip :text="t('update-results')" location="bottom">
      <template v-slot:activator="{ props }">
        <v-btn size="small" v-bind="props" icon class="wheel">⟳</v-btn>
      </template>
    </v-tooltip>
      <div v-if="dataVote.length!==0 && props.qType !== 'OpenEnded'">
        <ChartTabs
                :dataVoteChart="props.dataVote"
                :dataTrustChartLeft="props.dataTrustGoodAnswer"
                :dataTrustChartRight="props.dataTrustBadAnswer"
                :dataPeerChartLeft="props.dataPeerGoodAnswer"
                :dataPeerChartRight="props.dataPeerBadAnswer"
                :displayPeerTab="props.displayPeerTab"
                :display-trust-tab="props.displayTrustTab"
        />
      </div>
      <TextBar v-if="explanations.length === 0" class="textbar" :value="t('no-contribution')" color="#FFF8E1"></TextBar>
      <div v-if="explanations.length !== 0">
        <div v-if="teacherExp">
          <Explanation class="exp"
                  :answer="teacherExp.answer"
                  :grade="teacherExp.grade"
                  :number-of-peer-review="teacherExp.nbPeer"
                  :teacher="true"
          />
          <v-divider class="line" thickness="2"></v-divider>
        </div>
        <Explanation class="exp"
                v-for="item in studentExplanations"
                :key="item.answer.id"
                :answer="item.answer"
                :grade="item.grade"
                :number-of-peer-review="item.nbPeer"
                :teacher="false"
        />
        <v-btn class="button text-none">{{t('see-all-explanations')}}</v-btn>
      </div>
    </ContentBlock>

</template>


<style scoped>
.exp{
  width: 100%;
  margin-top: 3%;
}
.button{
  margin-top: 2%;
  background-color: lightgray;
}
.button:hover{
  background-color: darkgray;
}
.line {
  width: 100%;
  margin-top: 2%;
  margin-bottom: 3%;
}
.wheel {
  color: mediumblue !important;
  position: absolute;
  top: 0;
  right: 0;
  box-shadow: none !important;
  background-color: transparent !important;
}
.textbar{
  box-sizing: border-box;
  border-radius: 4px;
  border: 1px solid #8D6E63;
}


</style>
<i18n>
{
  "en": {
    "results": "Results",
    "update-results": "Update results",
    "no-contribution": "No contribution.",
    "see-all-explanations": "See all explanations"
  },
  "fr": {
    "results": "Resultats",
    "update-results": "Mettre à jour les résultats",
    "no-contribution": "Aucune contribution.",
    "see-all-explanations": "Voir toutes les explications"
  }
}
</i18n>
