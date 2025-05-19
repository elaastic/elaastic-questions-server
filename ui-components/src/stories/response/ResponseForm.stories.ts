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

const trustSelections = [
  { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
  { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
  { label: 'Confiant(e)', value: 'Confiant(e)' },
  { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
];
const providedAnswers= [1,2,3,4,5,6,7,8,9];

export const Default: Story = {
  args: {
    providedAnswers,
    trustSelections,
    answer: {
      id: 1,
      questionType: 'MultipleChoice',
      explanation: '',
      choices: [],
      trust: trustSelections[2].value,
    } satisfies MultipleChoiceResponse,
  },
};
export const Exclusive: Story = {
  args: {
    providedAnswers,
    trustSelections,
    answer: {
      id: 2,
      questionType: 'ExclusiveChoice',
      explanation: '',
      choice: providedAnswers[0],
      trust: trustSelections[2].value,
    } satisfies ExclusiveChoiceResponse
  },
}
export const Open: Story = {
  args: {
    trustSelections,
    answer: {
      id: 3,
      questionType: 'OpenEnded',
      explanation: 'Ma Réponse',
      trust: trustSelections[2].value,
    } satisfies OpenEndedResponse
  },
}
export const AnswerProvided: Story = {
  args: {
    providedAnswers,
    trustSelections,
    answer: {
      id: 3,
      questionType: 'MultipleChoice',
      explanation: 'Ma Réponse',
      choices: [providedAnswers[0],providedAnswers[4]],
      trust: trustSelections[2].value,
    } satisfies MultipleChoiceResponse
  },
}
