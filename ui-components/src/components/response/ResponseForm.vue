<script setup lang="ts">
import TextBar from "@/components/util/TextBar.vue";
import QCM from "@/components/response/QCM.vue";
import SelectorResponsive from "@/components/util/SelectorResponsive.vue";
import {computed, type PropType, ref} from "vue";
import type {Selection} from "@/components/util/SelectorResponsive.vue";
import {useI18n} from "vue-i18n";
import Ckeditor from "@/components/response/Ckeditor.vue";

const props = defineProps({
  providedAnswers: {
    type: Array as PropType<string[]>,
    default: () => []
  },
  selectedAnswers: {
    type: Array as PropType<string[]>,
    default: () => []
  },
  selectionsConfiance: {
    type: Array as PropType<Selection[]>,
    default: () => []
  },
  selectedConfiance: {
    type: String,
    default:"Confiant(e)"
  },
  text: {
    type: String,
    default: "Contenu par défaut"
  },
  estQCM: {
    type: Boolean,
    default: true,
  }


});
const selectedLocalAnswers = ref([...props.selectedAnswers]);
const selectedLocalConfiance= ref(props.selectedConfiance);
const emit = defineEmits(["update:selected"])

const text_ref = ref(props.text);
const plainText = computed(() => {
  const tempEl = document.createElement('div');
  tempEl.innerHTML = text_ref.value;
  return tempEl.textContent || '';
});

const { t } = useI18n()
</script>

<template>
  <h1>{{t('answer')}}</h1>
  <v-card>
    <v-card-title>
    </v-card-title>
    <div v-if="estQCM">
      <TextBar v-if="selectedLocalAnswers.length===0" color="red" value="Veuillez soumettre une réponse"></TextBar>
      <QCM :answers="providedAnswers" v-model:selected="selectedLocalAnswers" />
    </div>
    <div>
      <h4>{{t('textual-answer')}}</h4>
      <ckeditor v-model="text_ref" />
    </div>
    <div>
      <h4>{{t('trust-degree')}}</h4>
      <SelectorResponsive :selections="selectionsConfiance" v-model:selected="selectedLocalConfiance" />
    </div>
    <v-btn color="secondary">{{t('save')}}</v-btn>
  </v-card>

</template>

<style scoped>
</style>
<i18n>
  {
  "en": {
    "answer": "Answer  ",
    "textual-answer": "Textual Answer",
    "trust-degree": "Trust Degree",
    "save": "Save"
  },
  "fr": {
    "answer": "Réponse  ",
    "textual-answer": "Réponse Textuelle",
    "trust-degree": "Votre degré de confiance",
    "save": "Enregistrer"
  }
}
</i18n>
