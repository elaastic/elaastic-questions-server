<script lang="ts">
export interface AssignmentBreadcrumbProps {
  id: number
  course?: {title: string, id: number}
  subject: {title: string, id: number}
  audience?: string
  scholarYear: string
}
</script>
<script setup lang="ts">
import {useI18n} from "vue-i18n";
import type {PropType} from "vue";

const props = defineProps<AssignmentBreadcrumbProps>()
const audienceLocal = props.audience ? props.audience : 'na';

const emits = defineEmits(['goToCourse', 'goToSubject', 'goToDiffusion', 'editProperties'])

const { t } = useI18n()
</script>

<template>
  <v-card class="border-sm rounded-0" :elevation="0">
    <v-card-text>
      <!-- Links currently don't work. They are just examples. -->
      <span
              v-if="course"
              class="cursor-pointer text-primary"
              @click="emits('goToCourse','course/' + course.id)"
      >
        <v-icon class="text-black" icon="mdi-folder" />
        {{ course.title }}/
      </span>

      <span
              class="cursor-pointer text-primary font-weight-bold"
              @click="emits('goToSubject', 'subject/' + subject.id)"
      >
        <v-icon class="text-black" icon="mdi-book-open" />
        {{ subject.title }}  /
      </span>

      <v-tooltip location="top">
        <template #activator="{ props: tooltipProps }">
          <span v-bind="tooltipProps">
            <span
                    class="cursor-pointer text-primary"
                    @click="emits('goToDiffusion', 'subject/' + subject.id + '?activeTab=assignments')"
            >
              <v-icon class="text-black" icon="mdi-antenna" />
              {{ audienceLocal }} ({{ scholarYear }})
            </span>
          </span>
        </template>
        <span>{{ t('change-assignment') }}</span>
      </v-tooltip>

      <span>     [</span>
      <v-tooltip location="top">
        <template #activator="{ props: tooltipProps }">
          <span v-bind="tooltipProps">
            <span
                    class="cursor-pointer text-primary"
                    @click="emits('editProperties', 'assignment/' + id + '/edit')"
            >
              <v-icon icon="mdi-square-edit-outline" />
            </span>
          </span>
        </template>
        <span>{{ t('edit-properties') }}</span>
      </v-tooltip>
      <span>]     </span>
    </v-card-text>
  </v-card>
</template>

<style scoped></style>

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
