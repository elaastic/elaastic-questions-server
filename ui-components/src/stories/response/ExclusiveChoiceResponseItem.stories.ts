import type { Meta, StoryObj } from '@storybook/vue3'
import ExclusiveChoiceResponseItem from '@/components/response/ExclusiveChoiceResponseItem.vue'


const meta = {
  title: 'response/ExclusiveChoiceResponseItem',
  component: ExclusiveChoiceResponseItem,
  tags: ['autodocs'],
} satisfies Meta<typeof ExclusiveChoiceResponseItem>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    nbCandidateItem: 3,
    selected:1
  },
}
