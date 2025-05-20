import type { Meta, StoryObj } from '@storybook/vue3'
import MultipleChoiceResponseInput from '@/components/response/MultipleChoiceResponseInput.vue'


const meta = {
  title: 'response/MultipleChoiceResponseInput',
  component: MultipleChoiceResponseInput,
  tags: ['autodocs'],
} satisfies Meta<typeof MultipleChoiceResponseInput>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    nbCandidateItem: 3,
    selected: []
  },
}
