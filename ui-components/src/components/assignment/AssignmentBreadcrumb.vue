<script lang="ts">
import type { Course, Subject } from '@/components/assignment/Assignment.types'
import type { InternalBreadcrumbItem } from 'vuetify/components'

export interface AssignmentBreadcrumbProps {
  id: number
  course?: Course
  subject: Subject
  audience?: string
  scholarYear: string
}
</script>
<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { computed } from 'vue'

const props = defineProps<AssignmentBreadcrumbProps>()
const emits = defineEmits(['goToCourse', 'goToSubject', 'goToDiffusionList', 'editProperties'])

const { t } = useI18n()

interface BreadcrumbItem extends InternalBreadcrumbItem {
  icon: string
  title: string
  action: () => void
  disabled?: boolean
  tooltip?: string
  option?: {
    icon: string
    tooltip: string
    action: () => void
  }
}

const items = computed<BreadcrumbItem[]>(() => {
  return [
    ...(props.course
      ? [
          {
            icon: 'mdi-folder',
            title: props.course.title,
            action: () => emits('goToCourse', props.course?.id),
            disabled: false,
          },
        ]
      : []),
    {
      icon: 'mdi-book-open',
      title: props.subject.title,
      action: () => {
        emits('goToSubject', props.subject.id)
      },
    },
    {
      icon: 'mdi-antenna',
      title: `${props.audience ?? 'na'} ${props.scholarYear}`,
      action: () => {
        emits('goToDiffusionList', props.subject.id)
      },
      tooltip: t('change-assignment'),
      disabled: false,
      option: {
        icon: 'mdi-pencil',
        tooltip: t('edit-properties'),
        action: () => {
          emits('editProperties', props.id)
        },
      },
    },
  ]
})
</script>

<template>
  <v-breadcrumbs :items="items">
    <template #title="{ item }: { item: InternalBreadcrumbItem & BreadcrumbItem }">
      <v-btn variant="text" size="small" class="text-none mx-0 px-1" @click.prevent="item.action()">
        <v-tooltip v-if="item.tooltip" activator="parent" location="top">{{ item.tooltip }}</v-tooltip>
        <v-icon start size="18">{{ item.icon }}</v-icon>
        {{ item.title }}
      </v-btn>

      <span v-if="item.option">
        <v-btn class="ml-2" variant="outlined" size="small" @click.prevent="item.option.action()">
          <v-tooltip activator="parent" location="top">{{ item.option.tooltip }}</v-tooltip>
          <v-icon start size="18" class="mx-0">{{ item.option.icon }}</v-icon>
        </v-btn>
      </span>
    </template>
  </v-breadcrumbs>
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
