<script setup lang="ts">

import {type PropType, ref} from "vue";
import QuestionManager from "@/components/sequence/QuestionManager.vue";
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
const selectedTab = ref<String>('Questions')
const open = ref<boolean>(true)

const { t } = useI18n()
</script>

<template>
  <div v-if="open" class="d-flex flex-column pa-6">
    <v-btn-group
            variant="outlined"
            divided
            class="mb-8"
    >
      <v-btn
              @click="selectedTab = 'Questions'"
              :class="['text-none', selectedTab === 'Questions' ? 'btn_selected' : 'btn_unselected']"
      >
        <v-icon icon="mdi-format-list-bulleted" />
        {{ t('questions-list') }}
      </v-btn>
      <v-btn
              @click="selectedTab = 'Participants'"
              :class="['text-none', selectedTab === 'Participants' ? 'btn_selected' : 'btn_unselected']"
      >
        <v-icon icon="mdi-account-group" />
        {{ t('participants') }} ({{numberOfParticipants}})
      </v-btn>
    </v-btn-group>
    <v-btn
            class="position-absolute right-0 bg-blue-grey-lighten-5 mt-1 "
            @click="open = false"
            :elevation="0"
    >
      <v-icon icon="mdi-chevron-double-left" />
    </v-btn>
    <QuestionManager
            v-if="selectedTab === 'Questions'"
            :title="title"
            :questions="questions"
            :number-of-participants="numberOfParticipants"
            :id-assignment="idAssignment"
            :course="course"
            :scholar-year="scholarYear"
            :subject="subject"
            :audience="audience"
            :hide-statements="hideStatements"
    />
    <span v-show="selectedTab === 'Participants'">TODO</span>
  </div>
  <v-btn
          v-if="!open"
          class="position-absolute left-0 bg-blue-grey-lighten-5 text-none mt-1"
          @click="open = true"
  >
    {{ t('open-tab') }}
    <v-icon icon="mdi-chevron-double-right" />
  </v-btn>
</template>

<style scoped>
.btn_selected{
  background-color: lightgray;
}
.btn_unselected{
  background-color: #EEEEEE;
}
</style>
<i18n>
{
  "en": {
    "questions-list": "Questions list",
    "participants": "Participants",
    "open-tab": "Open the tab"
  },
  "fr": {
    "questions-list" : "Liste des questions",
    "participants": "Participants",
    "open-tab": "Ouvrir le volet"
  }
}
</i18n>
