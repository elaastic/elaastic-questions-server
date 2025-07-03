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
