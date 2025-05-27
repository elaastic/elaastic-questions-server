<script setup lang="ts">
import type { PropType } from "vue";
import {useI18n} from "vue-i18n";

const props = defineProps({
  /**
   * An array of 3 booleans. If a boolean equals true, the corresponding step is current.
   */
  steps: {
    type: Array as PropType<boolean[]>,
    default: () => []
  }
});
const { t } = useI18n();
</script>

<template>
  <ul class="stepper">
    <li :class="['stepper__item',props.steps[0] ? 'current' : 'not_current', props.steps[0] ? 'arrow-blue' : 'arrow-gray']">
      <span :class="['stepper__title', props.steps[0] ? 'stepper__title_current' : 'stepper_title_no_current']">{{t('step-1')}}</span>
      <div class="stepper_content">{{t('reasoned-response')}}</div>
      <svg xmlns="http://www.w3.org/2000/svg" width="100" height="50" viewBox="0 0 110 67" fill="none" :stroke="props.steps[0] ? '#1976D2' : 'black'" stroke-width="2">
        <ellipse cx="50" cy="35" rx="40" ry="20" :fill="props.steps[0] ? '#BBDEFB' : 'lightgray'" />
        <path d="M40 60 L30 67 L50 60 Z" :fill="props.steps[0] ? '#BBDEFB' : 'lightgray'" :stroke="props.steps[0] ? '#1976D2' : 'black'" stroke-width="2" />
      </svg>
    </li>

    <li :class="['stepper__item',props.steps[1] ? 'current' : 'not_current',props.steps[1] ? 'arrow-blue' : 'arrow-gray']">
      <span :class="['stepper__title', props.steps[1] ? 'stepper__title_current' : 'stepper_title_no_current']">{{t('step-2')}}</span>
      <div class="stepper_content">{{t('confronting-viewpoints')}}</div>
      <svg xmlns="http://www.w3.org/2000/svg" width="120" height="60" viewBox="0 0 160 90" fill="none" :stroke="props.steps[1] ? '#1976D2' : 'black'" stroke-width="2">
        <ellipse cx="50" cy="40" rx="40" ry="22" :fill="props.steps[1] ? '#BBDEFB' : 'lightgray'" />
        <path d="M40 65 L30 75 L50 65 Z" :fill="props.steps[1] ? '#BBDEFB' : 'lightgray'" :stroke="props.steps[1] ? '#1976D2' : 'black'" stroke-width="2" />
        <ellipse cx="110" cy="40" rx="40" ry="22" :fill="props.steps[1] ? '#1976D2' : 'black'" />
        <path d="M120 65 L130 75 L110 65 Z" :fill="props.steps[1] ? '#1976D2' : 'black'" :stroke="props.steps[1] ? '#1976D2' : 'black'" stroke-width="2" />
      </svg>
    </li>

    <li :class="['stepper__item', props.steps[2] ? 'current' : 'not_current']">
      <span :class="['stepper__title', props.steps[2] ? 'stepper__title_current' : 'stepper_title_no_current']">{{t('step-3')}}</span>
      <div class="stepper_content">{{t('Results')}}</div>
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
}

.stepper__item:not(:last-child)::after {
  content: "";
  position: absolute;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  z-index: 2;
}

.stepper__item.arrow-blue:not(:last-child)::after {
  filter: drop-shadow(0 0 0.5px #1976D2);
}

.stepper__item.arrow-gray:not(:last-child)::after {
  filter: drop-shadow(0 0 0.5px #666);
}

.stepper__item{
  border: 1px solid darkgray;
  border-radius: 8px;
  margin-bottom: 0px;
}

.stepper__item.current {
  background: #BBDEFB;
}

.stepper__item.not_current {
  background: lightgray;
}

.stepper__title {
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 4px;
}
.stepper__title_no_current{
  color: black;
}

.stepper__title_current {
  color: #1976D2;
}

.stepper_content{
  color: black;
}





@media (min-width: 900px){
  .stepper {
    position: relative;
  }

  .stepper__item {
    position: relative;
    padding: 10px 10px 10px 30px;
    flex-grow: 1;
    min-width: 200px;
    text-align: center;
  }

  /* Right arrow */
  .stepper__item:not(:last-child)::after {
    top: 50%;
    right: -12px;
    border-top: 8px solid transparent;
    border-bottom: 8px solid transparent;
    border-left: 12px solid lightgray;
  }

  /* Blue arrow with border */
  .stepper__item.arrow-blue:not(:last-child)::after {
    border-left-color: #BBDEFB;
  }

  /* Gray arrow with border */
  .stepper__item.arrow-gray:not(:last-child)::after {
    border-left-color: lightgray;
  }

}





@media (max-width: 900px) {
  .stepper {
    flex-direction: column;
    align-items: stretch;
    width: 100%;
  }

  .stepper__item {
    position: relative;
    padding: 20px 10px;
    text-align: center;
    transition: background 0.3s;
  }

  /* Bottom arrow */
  .stepper__item:not(:last-child)::after {
    bottom: -12px;
    left: 50%;
    transform: translateX(-50%);
    border-left: 8px solid transparent;
    border-right: 8px solid transparent;
    border-top: 12px solid lightgray;
  }

  /* Blue arrow with border */
  .stepper__item.arrow-blue:not(:last-child)::after {
    border-top-color: #BBDEFB;
  }

  /* Gray arrow with border */
  .stepper__item.arrow-gray:not(:last-child)::after {
    border-top-color: lightgray;
  }

}

</style>
<i18n>
{
  "en": {
    "step-1": "Step 1",
    "reasoned-response": "Reasoned response",
    "step-2": "Step 2",
    "confronting-viewpoints": "Confronting viewpoints",
    "step-3": "Step 3",
    "Results": "Results"
  },
  "fr": {
    "step-1": "Phase 1",
    "reasoned-response": "Réponse argumentée",
    "step-2": "Phase 2",
    "confronting-viewpoints": "Confrontation de points de vue",
    "step-3": "Phase 3",
    "Results": "Résultats"
  }
}
</i18n>
