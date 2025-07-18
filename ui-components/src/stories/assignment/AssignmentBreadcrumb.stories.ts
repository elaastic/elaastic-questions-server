import type { Meta, StoryObj } from '@storybook/vue3-vite'

import AssignmentBreadcrumb from '@/components/assignment/AssignmentBreadcrumb.vue'

const meta = {
  title: 'assignment/AssignmentBreadcrumb',
  component: AssignmentBreadcrumb,
  tags: ['autodocs', 'atomic'],
} satisfies Meta<typeof AssignmentBreadcrumb>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    id: 1256,
    course: {title: "Geography", id: 23},
    subject: {title: "Capitals", id: 530},
    audience: "E.Le",
    scholarYear: "2022 - 2023",
  }
}
export const WithoutDirectory: Story = {
  args: {
    id: 1256,
    subject: {title: "Capitals", id: 530},
    audience: "E.Le",
    scholarYear: "2022 - 2023",
  }
}
export const AudienceNotProvided: Story = {
  args: {
    id: 1256,
    course: {title: "Geography", id: 23},
    subject: {title: "Capitals", id: 530},
    scholarYear: "2022 - 2023",
  }
}
