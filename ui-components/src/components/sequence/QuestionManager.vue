<script setup lang="ts">

import Breadcrumb from "@/components/sequence/Breadcrumb.vue";
import ListSequenceQuestion from "@/components/sequence/ListSequenceQuestion.vue";
import type {PropType} from "vue";
import type {SequenceState} from "@/components/sequence/LogoSVG.vue";
import type {Question} from "@/components/sequence/SequenceQuestion.vue";
import {useI18n} from "vue-i18n";



const props = defineProps({
  /**
   * The title of the assignment.
   */
  title: {
    type: String
  },
  /**
   * All the questions of a subject. It's an array of Object composed of 2 attributes : the state of the sequence related to the question so it can be 'NOT_STARTED' | 'RESPONSE_PHASE' | 'CONFRONTING_VIEWPOINT' | 'RESULTS_PHASE' | 'CLOSED' | 'DISTANT' | 'BLENDED'. The second attribute is a 3-uplet : the title, the statement, and the number of the question.,
   */
  questions: {
    type: Array as PropType<{ state: SequenceState; question: Question }[]>,
  },
  /**
   * The number of learners who answered at a question of the assignment.
   */
  numberOfParticipants: {
    type: Number
  },
  /**
   * The id of the assignment.
   */
  idAssignment: {
    type: Number
  },
  /**
   * The course to which the assignment belongs.
   */
  course: {
    type: Object as PropType<{title: string, id: number}>
  },
  /**
   * The subject to which the assignment belongs.
   */
  subject: {
    type: Object as PropType<{title: string, id: number}>,
    required: true
  },
  /**
   * The audience of the assignment.
   */
  audience: {
    type: String,
  },
  /**
   * The scholar year of the assignment.
   */
  scholarYear: {
    type: String,
    required: true
  },
  /**
   * A boolean. True if statements are hidden. False if they are displayed.
   */
  hideStatements: {
    type: Boolean,
    default: false
  }
})

const emits = defineEmits(["startAllSequences"])

const startAllSequences = () => {
  emits("startAllSequences");
}

const { t } = useI18n()
</script>

<template>

  <v-tooltip :text="t('nb-of-participants')" location="right">
    <template v-slot:activator="{props: tooltipProps}">
      <v-btn
              width="48"
              height="32"
              size="small"
              v-bind="tooltipProps"
              class="btn font-weight-bold position-static rounded-lg text-white bg-grey-darken-1 ml-6"
              icon>
        <v-icon icon="mdi-account-group" class="mr-2" />
        {{ props.numberOfParticipants }}
      </v-btn>
    </template>
  </v-tooltip>

  <v-card :elevation="0" :rounded="0" >
    <v-card-title class="title_color" >
      <v-icon icon="mdi-note-outline"/>
      <strong class="ml-5">{{ title }}</strong>
      <v-tooltip :text="t('start-all-sequences')" location="bottom">
        <template v-slot:activator="{props: props}">
          <v-btn
                  v-bind="props"
                  class="position-absolute right-0 mr-4 bg-green-darken-1"
                  @click="startAllSequences"
          >
            <v-icon icon="mdi-chevron-double-right" />
          </v-btn>
        </template>
      </v-tooltip>

    </v-card-title>
    <Breadcrumb :id="idAssignment" :course="course" :subject="subject" :audience="audience" :scholar-year="scholarYear"  />
    <ListSequenceQuestion :questions="questions" :hide-statements="hideStatements" />
  </v-card>
</template>

<style scoped>
.title_color{
  background-color: #EEEEEE;
}
.btn {
  top: -15px;
  z-index: 10;
}
</style>
<i18n>
{
  "en": {
    "nb-of-participants": "Number of participants",
    "start-all-sequences": "Start all sequences"
  },
  "fr": {
    "nb-of-participants": "Nombre de participants",
    "start-all-sequences": "Lancer toutes les séquences"
  }
}
</i18n>
