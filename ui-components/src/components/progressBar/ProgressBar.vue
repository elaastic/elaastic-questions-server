<script setup lang="ts">
import type { PropType } from "vue";

const props = defineProps({
  steps: {
    type: Array as PropType<boolean[]>,
    default: () => []
  }
});
</script>

<template>
  <ul class="stepper">
    <li :class="['stepper__item',props.steps[0] ? 'current' : 'complete', props.steps[0] ? 'arrow-blue' : 'arrow-gray']">
      <span :class="[props.steps[0] ? 'stepper__title_current' : 'stepper_title']">Phase 1</span>
      <div class="stepper_content">Réponse Argumentée</div>
      <svg xmlns="http://www.w3.org/2000/svg" width="100" height="50" viewBox="0 0 110 67" fill="none" :stroke="props.steps[0] ? '#1976D2' : 'black'" stroke-width="2">
        <ellipse cx="50" cy="35" rx="40" ry="20" :fill="props.steps[0] ? '#BBDEFB' : 'lightgray'" />
        <path d="M40 60 L30 67 L50 60 Z" :fill="props.steps[0] ? '#BBDEFB' : 'lightgray'" :stroke="props.steps[0] ? '#1976D2' : 'black'" stroke-width="2" />
      </svg>
    </li>

    <li :class="['stepper__item',props.steps[1] ? 'current' : 'complete',props.steps[1] ? 'arrow-blue' : 'arrow-gray']">
      <span :class="[props.steps[1] ? 'stepper__title_current' : 'stepper_title']">Phase 2</span>
      <div class="stepper_content">Confrontation de points de vue</div>
      <svg xmlns="http://www.w3.org/2000/svg" width="120" height="60" viewBox="0 0 160 90" fill="none" :stroke="props.steps[1] ? '#1976D2' : 'black'" stroke-width="2">
        <ellipse cx="50" cy="40" rx="40" ry="22" :fill="props.steps[1] ? '#BBDEFB' : 'lightgray'" />
        <path d="M40 65 L30 75 L50 65 Z" :fill="props.steps[1] ? '#BBDEFB' : 'lightgray'" :stroke="props.steps[1] ? '#1976D2' : 'black'" stroke-width="2" />
        <ellipse cx="110" cy="40" rx="40" ry="22" :fill="props.steps[1] ? '#1976D2' : 'black'" />
        <path d="M120 65 L130 75 L110 65 Z" :fill="props.steps[1] ? '#1976D2' : 'black'" :stroke="props.steps[1] ? '#1976D2' : 'black'" stroke-width="2" />
      </svg>
    </li>

    <li :class="['stepper__item', props.steps[2] ? 'current' : 'complete']">
      <span :class="[props.steps[2] ? 'stepper__title_current' : 'stepper_title']">Phase 3</span>
      <div class="stepper_content">Résultats</div>
      <svg xmlns="http://www.w3.org/2000/svg" width="80" height="60" viewBox="0 0 100 80" fill="none" :stroke="props.steps[2] ? '#1976D2' : 'black'" stroke-width="2">
        <line x1="15" y1="10" x2="15" y2="70" :stroke="props.steps[2] ? '#1976D2' : 'black'" stroke-width="2"/>
        <line x1="15" y1="70" x2="90" y2="70" :stroke="props.steps[2] ? '#1976D2' : 'black'" stroke-width="2"/>
        <rect x="25" y="50" width="10" height="20" :fill="props.steps[2] ? '#1976D2' : 'black'" />
        <rect x="45" y="35" width="10" height="35" :fill="props.steps[2] ? '#1976D2' : 'black'" />
        <rect x="65" y="20" width="10" height="50" :fill="props.steps[2] ? '#1976D2' : 'black'" />
      </svg>
    </li>
  </ul>
</template>

<style scoped>
.stepper {
  display: flex;
  padding: 0;
  list-style: none;
  position: relative;

}

.stepper__item {
  position: relative;
  padding: 10px 10px 10px 30px;
  flex-grow: 1;
  min-width: 200px;
  text-align: center;
  transition: background 0.3s;
}

/* Flèche entre les étapes */
.stepper__item:not(:last-child)::after {
  content: "";
  position: absolute;
  top: 50%;
  right: -12px;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-left: 12px solid lightgray; /* valeur par défaut */
  z-index: 2;
  transition: border-left-color 0.3s;
}


/* Bordures & couleurs */
.stepper__item.current {
  background: #BBDEFB;
  border-top: 1px solid darkgray;
  border-bottom: 1px solid darkgray;
}

.stepper__item.complete {
  background: lightgray;
  border-top: 1px solid darkgray;
  border-bottom: 1px solid darkgray;
}

.stepper__item.complete:hover {
  background: #BBDEFB;
  color: #1976D2;
}

.stepper__item:first-child {
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
  border-left: 1px solid darkgray;
}

.stepper__item:last-child {
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
  border-right: 1px solid darkgray;
}

/* Titre */
.stepper_title {
  display: block;
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 4px;
}
.stepper__title_current{
  color: #1976D2;
  display: block;
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 4px;
}
.stepper_content{
  color: black;
}

.stepper__item.arrow-gray:not(:last-child)::after {
  border-left-color: lightgray;
}

/* Si étape active (bleue) */
.stepper__item.arrow-blue:not(:last-child)::after {
  border-left-color: #BBDEFB;
}
</style>
