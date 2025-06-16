import type {Meta, StoryObj} from '@storybook/vue3'
import ResponseForm from '@/components/response/ResponseForm.vue'
import {
  ConfidenceDegree,
  type ExclusiveChoiceResponse,
  type MultipleChoiceResponse,
  type OpenEndedResponse
} from "@/models/Response";

const meta = {
  title: 'response/ResponseForm',
  component: ResponseForm,
  tags: ['autodocs'],
} satisfies Meta<typeof ResponseForm>

export default meta

type Story = StoryObj<typeof meta>


const providedAnswers= 9;

export const Default: Story = {
  args: {
    providedAnswers,
    answer: {
      id: 1,
      questionType: 'MultipleChoice',
      explanation: '',
      choices: [],
      confidence: ConfidenceDegree.CONFIDENT,
    } satisfies MultipleChoiceResponse,
  },
};
export const Exclusive: Story = {
  args: {
    providedAnswers,
    answer: {
      id: 2,
      questionType: 'ExclusiveChoice',
      explanation: '',
      choice: 1,
      confidence: ConfidenceDegree.CONFIDENT,
    } satisfies ExclusiveChoiceResponse
  },
}
export const Open: Story = {
  args: {
    answer: {
      id: 3,
      questionType: 'OpenEnded',
      explanation: 'Ma Réponse',
      confidence: ConfidenceDegree.CONFIDENT,
    } satisfies OpenEndedResponse
  },
}
export const AnswerProvided: Story = {
  args: {
    providedAnswers,
    answer: {
      id: 3,
      questionType: 'MultipleChoice',
      explanation: 'Ma Réponse',
      choices: [1,5],
      confidence: ConfidenceDegree.CONFIDENT,
    } satisfies MultipleChoiceResponse
  },
}
