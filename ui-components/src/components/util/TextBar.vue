<script setup lang="ts">

const props = defineProps({
  /**
   * The text showed by the component
   */
  value: {
    type: String,
    default: ""
  },
  /**
   * The color of the background
   */
  color: {
    type: String,
    default: "primary",
  }
})
</script>

<template>
 <v-card :color="color">
   <v-card-text>
     {{value}}
   </v-card-text>
 </v-card>
</template>

<style scoped>

</style>
