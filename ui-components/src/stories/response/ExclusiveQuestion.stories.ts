import type { Meta, StoryObj } from '@storybook/vue3'
import ExclusiveQuestion from '@/components/response/ExclusiveQuestion.vue'


const meta = {
  title: 'response/ExclusiveQuestion',
  component: ExclusiveQuestion,
  tags: ['autodocs'],
} satisfies Meta<typeof ExclusiveQuestion>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    answers: [1, 2, 3],
    selected:1
  },
}
