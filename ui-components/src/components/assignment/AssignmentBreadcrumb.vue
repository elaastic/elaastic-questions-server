<script lang="ts">
export interface AssignmentBreadcrumbProps {
  id: number
  course?: { title: string; id: number }
  subject: { title: string; id: number }
  audience?: string
  scholarYear: string
}
</script>
<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const props = defineProps<AssignmentBreadcrumbProps>()
const audienceLocal = props.audience ? props.audience : 'na'

const emits = defineEmits(['goToCourse', 'goToSubject', 'goToDiffusionList', 'editProperties'])

const { t } = useI18n()
</script>

<template>
  <v-card class="border-sm rounded-0" :elevation="0">
    <v-card-text>
      <span v-if="course" class="cursor-pointer text-primary" @click="emits('goToCourse', course.id)">
        <v-icon class="text-black" icon="mdi-folder" />
        {{ course.title }}&nbsp;/
      </span>

      <span class="cursor-pointer text-primary font-weight-bold" @click="emits('goToSubject', subject.id)">
        <v-icon class="text-black" icon="mdi-book-open" />
        {{ subject.title }}&nbsp;/
      </span>

      <v-tooltip location="top">
        <template #activator="{ props: tooltipProps }">
          <span v-bind="tooltipProps">
            <span
              class="cursor-pointer text-primary"
              @click="emits('goToDiffusionList', subject.id)"
            >
              <v-icon class="text-black" icon="mdi-antenna" />
              {{ audienceLocal }} ({{ scholarYear }})
            </span>
          </span>
        </template>

        {{ t('change-assignment') }}
      </v-tooltip>

      <span> [</span>
      <v-tooltip location="top">
        <template #activator="{ props: tooltipProps }">
          <span v-bind="tooltipProps">
            <span class="cursor-pointer text-primary" @click="emits('editProperties', 'assignment/' + id + '/edit')">
              <v-icon icon="mdi-square-edit-outline" />
            </span>
          </span>
        </template>

        {{ t('edit-properties') }}
      </v-tooltip>
      <span>] </span>
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
