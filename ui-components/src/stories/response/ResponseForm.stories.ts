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

export const Default: Story = {
  args: {
    providedAnswers: [1,2,3,4,5,6,7,8,9],
    selectionsConfiance: [
      { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
      { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
      { label: 'Confiant(e)', value: 'Confiant(e)' },
      { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
    ],
    selectedConfiance: 'Confiant(e)',
    isSend: false,
    answer: {
      id: 1,
      questionType: 'MultipleChoice',
      explanation: '',
      choices: []
    } satisfies MultipleChoiceResponse
  },
}
export const Exclusive: Story = {
  args: {
    providedAnswers: [1,2,3,4,5,6,7,8,9],
    selectionsConfiance: [
      { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
      { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
      { label: 'Confiant(e)', value: 'Confiant(e)' },
      { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
    ],
    selectedConfiance: 'Confiant(e)',
    isSend: false,
    answer: {
      id: 2,
      questionType: 'ExclusiveChoice',
      explanation: '',
      choice: 1
    } satisfies ExclusiveChoiceResponse
  },
}
export const Open: Story = {
  args: {
    selectionsConfiance: [
      { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
      { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
      { label: 'Confiant(e)', value: 'Confiant(e)' },
      { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
    ],
    selectedConfiance: 'Confiant(e)',
    isSend: false,
    answer: {
      id: 3,
      questionType: 'OpenEnded',
      explanation: 'Ma Réponse',
    } satisfies OpenEndedResponse
  },
}
export const AnswerProvided: Story = {
  args: {
    providedAnswers: [1,2,3,4,5,6,7,8,9],
    selectionsConfiance: [
      { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
      { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
      { label: 'Confiant(e)', value: 'Confiant(e)' },
      { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
    ],
    selectedConfiance: 'Confiant(e)',
    isSend: false,
    answer: {
      id: 3,
      questionType: 'MultipleChoice',
      explanation: 'Ma Réponse',
      choices: [1,5]
    } satisfies MultipleChoiceResponse
  },
}
export const Sent: Story = {
  args: {
    isSend: true,
  },
}
