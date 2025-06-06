import type { Meta, StoryObj } from '@storybook/vue3'
import ChoiceFrame from '@/components/results/ChoiceFrame.vue'


const meta = {
  title: 'response/ChoiceFrame',
  component: ChoiceFrame,
  tags: ['autodocs'],
} satisfies Meta<typeof ChoiceFrame>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    value: 1,
    isChecked: false,
    isCorrect: true
  },
}
export const CheckedCorrect: Story = {
  args: {
    value: 1,
    isChecked: true,
    isCorrect: true
  },
}
export const CheckedWrong: Story = {
  args: {
    value: 1,
    isChecked: true,
    isCorrect: false
  },
}
export const NoCheckedWrong: Story = {
  args: {
    value: 1,
    isChecked: false,
    isCorrect: false
  },
}
