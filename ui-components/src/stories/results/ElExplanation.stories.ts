import type { Meta, StoryObj } from '@storybook/vue3'
import ElExplanation from '@/components/results/ElExplanation.vue'
import type { ExclusiveChoiceResponse, MultipleChoiceResponse, OpenEndedResponse } from '@/models/Response'


const meta = {
  title: 'results/ElExplanation',
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
    grade: 3.5,
    numberOfPeerReview: 4,
    providedByTeacher: false,
  },
}

export const OpenQuestion: Story = {
  args: {
    response: {
      id: 2,
      questionType: 'OpenEnded',
      explanation: 'A generated explanation',
      trust: "",
    } satisfies OpenEndedResponse,
    grade: null,
    numberOfPeerReview: 0,
    providedByTeacher: false,
  },
}
