import type { Meta, StoryObj } from '@storybook/vue3'
import QCM from '@/components/response/QCM.vue'


const meta = {
  title: 'response/QCM',
  component: QCM,
  tags: ['autodocs'],
} satisfies Meta<typeof QCM>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    answers: [1,2,3],
    selected: []
  },
}
