<script setup lang="ts">
import {useI18n} from "vue-i18n";

const props=defineProps({
  /**
   * The average grade out of 5 given by reviewers.
   */
  grade: {
    type: Number,
    default: 0
  },
  /**
   * The number of reviewers.
   */
  numberOfPeerReview: {
    type: Number,
    default: 0
  },
  /**
   * A boolean. true if it's the teacher's explanation. false if it's a student explanation.
   */
  teacher: {
    type: Boolean,
    default: false
  },
});
const { t } = useI18n()
</script>

<template>
  <v-card  class="fit-content" color="#00695C" >
    <v-card-text class="info-row tight-card-text">
      <div v-if="teacher">
        <p><strong> 🎓 {{t('teacher-explanation')}}</strong></p>
      </div>

      <div v-if="numberOfPeerReview!==0" class="row-elements">
        <div><strong>{{ grade }}/5</strong></div>

        <div v-if="numberOfPeerReview===1" class="gray">{{ numberOfPeerReview }} {{t('peer-review')}}</div>
        <div v-if="numberOfPeerReview>1" class="gray">{{ numberOfPeerReview }} {{t('peer-reviews')}}</div>

        <div v-if="teacher" class="gray">
          <p class="yellow_hover">{{t('see-reviews')}}</p>
        </div>
      </div>

      <div v-else class="gray">{{t('no-peer-review')}}</div>

      <div v-if="!teacher" class="gray">
        <p class="yellow_hover">{{t('see-details')}}</p>
      </div>
    </v-card-text>
  </v-card>
</template>

<style scoped>
.yellow_hover:hover{
  color: yellow;
  cursor: pointer;
}
.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
  font-size: 14px;
}

.row-elements {
  display: flex;
  gap: 12px;
  align-items: center;
}

.fit-content {
  width: fit-content;
  max-width: 100%;
  padding: 12px;
}

.gray{
  color: lightgray;
}
.tight-card-text {
  padding-top: 0;
  padding-bottom: 0;
}

.tight-card-text p {
  margin: 0;
}


</style>
<i18n>
{
  "en": {
    "teacher-explanation": "Teacher explanation",
    "peer-review": "peer review",
    "peer-reviews": "peer reviews",
    "see-reviews": "(see reviews)",
    "no-peer-review": "- No peer review",
    "see-details": "(See details)"
  },
  "fr": {
    "teacher-explanation": "Explication de l'enseignant",
    "peer-review": "évaluation par les pairs",
    "peer-reviews": "évaluations par les pairs",
    "see-reviews": "(voir les évaluations)",
    "no-peer-review": "- Aucune évaluation par les pairs",
    "see-details": "(Afficher les détails)"
  }
}
</i18n>
