<script setup lang="ts">

import Link from "@/components/util/Link.vue";
import {useI18n} from "vue-i18n";

const props = defineProps({
  /**
   * The course to which the assignment belongs.
   */
  course: {
    type: String
  },
  /**
   * The subject to which the assignment belongs.
   */
  subject: {
    type: String,
    required: true
  },
  /**
   * The audience of the assignment.
   */
  audience: {
    type: String,
    required: true
  },
  /**
   * The scholar year of the assignment.
   */
  scholarYear: {
    type: String,
    required: true
  },
  /**
   * The question selected by the user. If none is provided, nothing is displayed.
   */
  questionCurrent: {
    type: String
  }
})

const { t } = useI18n()
</script>

<template>
  <v-card class="border-sm rounded-0" :elevation="0">
    <v-card-text>
      <!-- Links currently don't work. They are just examples. -->
      <span v-if="course">
        <Link class="mr-2" :href="'https://elaastic.irit.fr/course/' + course" :text="'📁' + course" />
        <span>/</span>
      </span>

      <Link
              class="mr-2 ml-2 font-weight-bold"
              :href="'https://elaastic.irit.fr/subject/' + subject"
              :text="'📄' + subject"
      />
      <span>/</span>

      <v-tooltip location="top">
        <template #activator="{ props: tooltipProps }">
          <span v-bind="tooltipProps">
            <Link
                    class="ml-2"
                    :href="'https://elaastic.irit.fr/subject/' + audience + '?activeTab=assignments'"
                    :text="'📡' + audience + ' (' + scholarYear + ')'"
            />
          </span>
        </template>
        <span>{{ t('change-assignment') }}</span>
      </v-tooltip>

      <span>     [</span>
      <v-tooltip location="top">
        <template #activator="{ props: tooltipProps }">
          <span v-bind="tooltipProps">
            <Link :href="'https://elaastic.irit.fr/assignment/edit/'" :text="'📝'" />
          </span>
        </template>
        <span>{{ t('edit-properties') }}</span>
      </v-tooltip>
      <span>]     </span>

      <span v-if="questionCurrent">
        <span>/</span>
        <Link
                class="ml-2"
                :href="'https://elaastic.irit.fr/player/assignment/play/sequence/' + questionCurrent"
                :text="questionCurrent"
        />
      </span>
    </v-card-text>
  </v-card>
</template>

<style scoped>

</style>

<i18n>
{
  "en": {
    "change-assignment": "Change assignment",
    "edit-properties": "Edit properties"
  },
  "fr": {
    "change-assignment": "Changer de diffusion",
    "edit-properties": "Éditer les propriétés"
  }
}
</i18n>
