import type { Meta, StoryObj } from '@storybook/vue3'
import Explanation from '@/components/results/Explanation.vue'
import type {MultipleChoiceResponse, OpenEndedResponse} from "@/models/Response";


const meta = {
  title: 'result/Explanation',
  component: Explanation,
  tags: ['autodocs'],
} satisfies Meta<typeof Explanation>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    answer: {
      id: 1,
      questionType: 'MultipleChoice',
      explanation: 'Mon explication',
      choices: [1,3],
      trust: "",
    } satisfies MultipleChoiceResponse,
    grade: 2,
    numberOfPeerReview: 1,
    teacher: false,
  },
}
export const OpenQuestion: Story = {
  args: {
    answer: {
      id: 2,
      questionType: 'OpenEnded',
      explanation: 'Ma Réponse',
      trust: "",
    } satisfies OpenEndedResponse,
    grade: 2,
    numberOfPeerReview: 1,
    teacher: false,
  },
}
