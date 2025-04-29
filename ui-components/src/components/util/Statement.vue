<script setup lang="ts">

const props = defineProps({
  /**
   * The title of the statement
   */
  title: {
    type: String,
    default: ""
  },
  /**
   * The content of the statement, could be a string or an other type
   */
  content: {
    type: undefined,
    default: ""
  },
})

</script>

<template>
  <v-expansion-panels>
    <v-expansion-panel>
      <v-expansion-panel-title>
        {{title}}
      </v-expansion-panel-title>
      <v-expansion-panel-text>
        {{content}}
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<style scoped>

</style>
