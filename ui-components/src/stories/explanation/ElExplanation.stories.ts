import type { Meta, StoryObj } from '@storybook/vue3'
import ElExplanation from '@/components/explanation/ElExplanation.vue'


const meta = {
  title: 'explanation/ElExplanation',
  component: ElExplanation,
  tags: ['autodocs'],
} satisfies Meta<typeof ElExplanation>

export default meta

type Story = StoryObj<typeof meta>

export const Defaylt: Story = {
  args: {
    responseId: 1,
    explanation: 'Some demo explanation',
    grade: 2,
    numberOfPeerReview: 1,
    providedByTeacher: false,
  },
}
