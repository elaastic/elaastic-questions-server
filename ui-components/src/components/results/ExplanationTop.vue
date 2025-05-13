<script setup lang="ts">
const props=defineProps({
  grade: {
    type: Number,
    default: 0
  },
  numberOfPeerReview: {
    type: Number,
    default: 0
  },
  teacher: {
    type: Boolean,
    default: false
  },
});
</script>

<template>
  <v-card  class="fit-content" color="#00695C" >
    <v-card-text class="info-row tight-card-text">
      <div v-if="teacher">
        <p><strong> 🎓 Explication de l'enseignant</strong></p>
      </div>

      <div v-if="numberOfPeerReview!==0" class="row-elements">
        <div><strong>{{ grade }}/5</strong></div>

        <div v-if="numberOfPeerReview===1" class="gray">{{ numberOfPeerReview }} évaluation par les pairs</div>
        <div v-if="numberOfPeerReview>1" class="gray">{{ numberOfPeerReview }} évaluations par les pairs</div>

        <div v-if="teacher" class="gray">
          <p class="yellow_hover">(voir les évaluations)</p>
        </div>
      </div>

      <div v-else class="gray">- Aucune évaluation par les pairs</div>

      <div v-if="!teacher" class="gray">
        <p class="yellow_hover">(Afficher les détails)</p>
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
