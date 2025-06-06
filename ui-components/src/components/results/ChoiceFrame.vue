<script setup lang="ts">
const props = defineProps({
  value: {
    type: Number
  },
  isChecked: {
    type: Boolean
  },
  isCorrect: {
    type: Boolean,
  }
})
</script>

<template>
  <div class="container">
    <v-card variant="flat" class="v-card1">
      <v-card-title :class="['v-card-title-1', props.isCorrect ? 'correct' : 'wrong']">
        {{props.value}}
      </v-card-title>
    </v-card>
    <v-card variant="flat" class="v-card2">
      <v-icon v-if="isChecked" style="padding-top: 40%;padding-bottom: 40%;padding-right: 40%;padding-left: 44%;">mdi-check-bold</v-icon>
    </v-card>
  </div>
</template>

<style scoped>
.v-card-title-1{
  color: white;
  font-weight: bold;
}
.container{
  display: flex;
}
.v-card1{
  width: 60px;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  margin-bottom: 1%;
}
.v-card2{
  width: 55px;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  margin-bottom: 1%;
  background-color: lightgray;
}
.v-card2:hover{
  background-color: darkgray;
}
.wrong {
  background-color: #AD1457;
}
.wrong:hover {
  background-color: #880E4F;
}
.correct{
  background-color: #2E7D32;
}
.correct:hover {
  background-color: #1B5E20;
}

</style>
