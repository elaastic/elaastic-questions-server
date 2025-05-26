import type { Meta, StoryObj } from '@storybook/vue3'
import ResponseForm from '@/components/response/ResponseForm.vue'
import type {ExclusiveChoiceResponse, MultipleChoiceResponse, OpenEndedResponse} from "@/models/Response";
const meta = {
  title: 'response/ResponseForm',
  component: ResponseForm,
  tags: ['autodocs'],
} satisfies Meta<typeof ResponseForm>

export default meta

type Story = StoryObj<typeof meta>

const confidenceSelections = [
  { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
  { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
  { label: 'Confiant(e)', value: 'Confiant(e)' },
  { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
];
const providedAnswers= 9;

export const Default: Story = {
  args: {
    providedAnswers,
    confidenceSelections,
    answer: {
      id: 1,
      questionType: 'MultipleChoice',
      explanation: '',
      choices: [],
      confidence: confidenceSelections[2].value,
    } satisfies MultipleChoiceResponse,
  },
};
export const Exclusive: Story = {
  args: {
    providedAnswers,
    confidenceSelections,
    answer: {
      id: 2,
      questionType: 'ExclusiveChoice',
      explanation: '',
      choice: 1,
      confidence: confidenceSelections[2].value,
    } satisfies ExclusiveChoiceResponse
  },
}
export const Open: Story = {
  args: {
    confidenceSelections,
    answer: {
      id: 3,
      questionType: 'OpenEnded',
      explanation: 'Ma Réponse',
      confidence: confidenceSelections[2].value,
    } satisfies OpenEndedResponse
  },
}
export const AnswerProvided: Story = {
  args: {
    providedAnswers,
    confidenceSelections,
    answer: {
      id: 3,
      questionType: 'MultipleChoice',
      explanation: 'Ma Réponse',
      choices: [1,5],
      confidence: confidenceSelections[2].value,
    } satisfies MultipleChoiceResponse
  },
}
