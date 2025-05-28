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
    questionContent: "Content of Question 1",
    providedAnswers: 4,
    responsesToEvaluate:
            [{
              id: 1,
              questionType: 'MultipleChoice',
              explanation: 'Une Réponse',
              choices: [1],
              confidence: 'Confiant(e)'
            },
              {
                id: 2,
                questionType: 'MultipleChoice',
                explanation: 'Une Autre Réponse',
                choices: [2, 3],
                confidence: 'Confiant(e)'
              }],
    updatedAnswer:
            {
              id: 3,
              questionType: 'MultipleChoice',
              explanation: 'Ma réponse',
              choices: [1],
              confidence: 'Confiant(e)'
            },
    sequenceInProgress: true
  }
}
export const Exclusive: Story = {
  args: {
    questionTitle: "Question 1",
    questionContent: "Content of Question 1",
    providedAnswers: 4,
    responsesToEvaluate:
            [{
              id: 1,
              questionType: 'ExclusiveChoice',
              explanation: 'Une Réponse',
              choice: 1,
              trust: 'Confiant(e)'
            },
              {
                id: 2,
                questionType: 'ExclusiveChoice',
                explanation: 'Une Autre Réponse',
                choice: 2,
                trust: 'Confiant(e)'
              }],
    updatedAnswer:
            {
              id: 3,
              questionType: 'ExclusiveChoice',
              explanation: 'Ma réponse',
              choice: 3,
              trust: 'Confiant(e)'
            },
    sequenceInProgress: true
  }
}
export const Open: Story = {
  args: {
    questionTitle: "Question 1",
    questionContent: "Content of Question 1",
    providedAnswers: 4,
    responsesToEvaluate:
            [{
              id: 1,
              questionType: 'OpenEnded',
              explanation: 'Une Réponse',
              trust: 'Confiant(e)'
            },
              {
                id: 2,
                questionType: 'OpenEnded',
                explanation: 'Une Autre Réponse',
                trust: 'Confiant(e)'
              }],
    updatedAnswer:
            {
              id: 3,
              questionType: 'OpenEnded',
              explanation: 'Ma réponse',
              trust: 'Confiant(e)'
            },
    sequenceInProgress: true
  }
}
export const SequenceClosed: Story = {
  args: {
    questionTitle: "Question 1",
    questionContent: "Content of Question 1",
    providedAnswers: 4,
    responsesToEvaluate:
            [{
              id: 1,
              questionType: 'OpenEnded',
              explanation: 'Une Réponse',
              trust: 'Confiant(e)'
            },
              {
                id: 2,
                questionType: 'OpenEnded',
                explanation: 'Une Autre Réponse',
                trust: 'Confiant(e)'
              }],
    updatedAnswer:
            {
              id: 3,
              questionType: 'OpenEnded',
              explanation: 'Ma réponse',
              trust: 'Confiant(e)'
            },
    sequenceInProgress: false
  }
}
