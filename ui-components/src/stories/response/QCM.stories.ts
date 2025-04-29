import type { Meta, StoryObj } from '@storybook/vue3'
import QCM from '@/components/response/QCM.vue'

// Définir correctement le meta avec les props attendues
const meta = {
  title: 'response/QCM',
  component: QCM,
  tags: ['autodocs'],
} satisfies Meta<typeof QCM>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    answers: ["Pierre", "Paul", "Jacques"],
    selected: []
  },
}
