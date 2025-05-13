<script setup lang="ts">
const props = defineProps({
  /**
   * The title of the frame
   */
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
    <v-divider class="line" thickness="2"></v-divider>
    <v-card-text>
      <slot/>
    </v-card-text>
  </v-card>
</template>

<style scoped>
.line {
  margin-top: -1%;
  margin-right: 2%;
  margin-left: 2%;
}
</style>
