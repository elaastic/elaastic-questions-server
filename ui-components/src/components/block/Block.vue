<script setup lang="ts">
const props = defineProps({
  title: {
    type: String,
    default: ""
  }
})
</script>

<template>
  <v-card>
    <v-card-title>
      <h1>{{title}}</h1>
    </v-card-title>
    <hr color="lightgray" class="line"/>
    <v-card-text>
      <slot/>
    </v-card-text>
  </v-card>
</template>

<style scoped>
.line {
  border-top: 2px ;
  margin-top: -1%;
}
</style>
