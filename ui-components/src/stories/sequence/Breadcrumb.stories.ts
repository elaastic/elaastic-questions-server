import type { Meta, StoryObj } from '@storybook/vue3-vite'

import Breadcrumb from '@/components/sequence/Breadcrumb.vue'

const meta = {
  title: 'sequence/Breadcrumb',
  component: Breadcrumb,
  tags: ['autodocs', 'atomic'],
} satisfies Meta<typeof Breadcrumb>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    course: "Geography",
    subject: "Capitals",
    audience: "E.Le",
    scholarYear: "2022 - 2023",
  }
}
export const WithoutDirectory: Story = {
  args: {
    subject: "Capitals",
    audience: "E.Le",
    scholarYear: "2022 - 2023",
  }
}
export const WithQuestionCurrent: Story = {
  args: {
    subject: "Capitals",
    audience: "E.Le",
    scholarYear: "2022 - 2023",
    questionCurrent: "Capitale de la France ?"
  }
}
export const WithDirectoryAndQuestionCurrent: Story = {
  args: {
    course: "Geography",
    subject: "Capitals",
    audience: "E.Le",
    scholarYear: "2022 - 2023",
    questionCurrent: "Capitale de la France ?"
  }
}
