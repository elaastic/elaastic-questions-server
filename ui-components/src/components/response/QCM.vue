<script setup lang="ts">
import {ref} from 'vue'
import type {PropType} from "vue";

const props = defineProps({
  answers: {
    type: Array as PropType<string[]>, // Assurer que c'est un tableau de chaînes
    default: () => []
  },
  selected: {
    type: Array as PropType<string[]>, // Assurer que c'est un tableau de chaînes
    default: () => []
  }
})

const emit = defineEmits(['update:selected']) // Émettre l'événement de mise à jour de `selected`

// Valeur locale pour le `selected`
const selectedLocal = ref([...props.selected])

// Méthode pour émettre la mise à jour de `selected`
const updateSelected = (newSelected: string[]) => {
  emit('update:selected', newSelected) // Émettre l'événement de mise à jour
  selectedLocal.value = newSelected // Mettre à jour la valeur locale
}
</script>

<template>
  <p>{{ selectedLocal }}</p>
  <p>Votre réponse</p>
  <v-container fluid>
    <v-checkbox
            v-for="answer in answers"
            :key="answer"
            v-model="selectedLocal" :value="answer"
            @update:selected="updateSelected"
    >
    <template v-slot:label>
      <div>{{ answer }}</div>
    </template>
    </v-checkbox>
  </v-container>
</template>

<style scoped></style>
