import type { Meta, StoryObj } from '@storybook/vue3'
import ElExplanation from '@/components/explanation/ElExplanation.vue'
import type { ExclusiveChoiceResponse, MultipleChoiceResponse, OpenEndedResponse } from '@/models/Response'


const meta = {
  title: 'explanation/ElExplanation',
  component: ElExplanation,
  tags: ['autodocs'],
} satisfies Meta<typeof ElExplanation>

export default meta

type Story = StoryObj<typeof meta>

export const MultipleChoice: Story = {
  args: {
    response: {
      id: 1,
      questionType: 'MultipleChoice',
      explanation: 'Mon explication',
      choices: [1,3],
      trust: "",
    } satisfies MultipleChoiceResponse,
    grade: 2,
    numberOfPeerReview: 1,
    providedByTeacher: false,
  },
}

export const SingleChoice: Story = {
  args: {
    response: {
      id: 3,
      questionType: 'ExclusiveChoice',
      explanation: 'Mon explication',
      choice: 2,
      trust: "",
    } satisfies ExclusiveChoiceResponse,
    grade: 2,
    numberOfPeerReview: 1,
    providedByTeacher: false,
  },
}

export const OpenQuestion: Story = {
  args: {
    answer: {
      id: 2,
      questionType: 'OpenEnded',
      explanation: 'A generated explanation',
      trust: "",
    } satisfies OpenEndedResponse,
    grade: 2,
    numberOfPeerReview: 1,
    providedByTeacher: false,
  },
}
