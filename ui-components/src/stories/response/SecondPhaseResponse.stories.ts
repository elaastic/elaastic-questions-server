import type { Meta, StoryObj } from '@storybook/vue3'
import SecondPhaseResponse from '@/components/response/SecondPhaseResponse.vue'


const meta = {
  title: 'response/SecondPhaseResponse',
  component: SecondPhaseResponse,
  tags: ['autodocs'],
} satisfies Meta<typeof SecondPhaseResponse>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    questionTitle: "Question 1",
    questionType: "MultipleChoice",
    questionContent: "Content of Question 1",
    providedAnswers: 4,
    responsesToEvaluate:
            [{
              id: 1,
              questionType: 'MultipleChoice',
              explanation: 'Une Réponse',
              choices: [1],
              trust: 'Confiant(e)'
            },
              {
                id: 2,
                questionType: 'MultipleChoice',
                explanation: 'Une Autre Réponse',
                choices: [2, 3],
                trust: 'Confiant(e)'
              }],
    updatedAnswer:
            {
              id: 3,
              questionType: 'MultipleChoice',
              explanation: 'Ma réponse',
              choices: [1],
              trust: 'Confiant(e)'
            },
  }
}
