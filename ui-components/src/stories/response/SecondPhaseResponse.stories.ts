import type { Meta, StoryObj } from '@storybook/vue3'
import SecondPhaseResponse from '@/components/response/SecondPhaseResponse.vue'
import {ConfidenceDegree} from "@/models/Response";


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
              confidence: ConfidenceDegree.CONFIDENT
            },
              {
                id: 2,
                questionType: 'MultipleChoice',
                explanation: 'Une Autre Réponse',
                choices: [2, 3],
                confidence: ConfidenceDegree.CONFIDENT
              }],
    updatedAnswer:
            {
              id: 3,
              questionType: 'MultipleChoice',
              explanation: 'Ma réponse',
              choices: [1],
              confidence: ConfidenceDegree.CONFIDENT
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
              confidence: ConfidenceDegree.CONFIDENT
            },
              {
                id: 2,
                questionType: 'ExclusiveChoice',
                explanation: 'Une Autre Réponse',
                choice: 2,
                confidence: ConfidenceDegree.CONFIDENT
              }],
    updatedAnswer:
            {
              id: 3,
              questionType: 'ExclusiveChoice',
              explanation: 'Ma réponse',
              choice: 3,
              confidence: ConfidenceDegree.NOT_CONFIDENT_AT_ALL
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
              confidence: ConfidenceDegree.CONFIDENT
            },
              {
                id: 2,
                questionType: 'OpenEnded',
                explanation: 'Une Autre Réponse',
                confidence: ConfidenceDegree.CONFIDENT
              }],
    updatedAnswer:
            {
              id: 3,
              questionType: 'OpenEnded',
              explanation: 'Ma réponse',
              confidence: ConfidenceDegree.TOTALLY_CONFIDENT
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
              confidence: ConfidenceDegree.CONFIDENT
            },
              {
                id: 2,
                questionType: 'OpenEnded',
                explanation: 'Une Autre Réponse',
                confidence: ConfidenceDegree.CONFIDENT
              }],
    updatedAnswer:
            {
              id: 3,
              questionType: 'OpenEnded',
              explanation: 'Ma réponse',
              confidence: ConfidenceDegree.NOT_REALLY_CONFIDENT
            },
    sequenceInProgress: false
  }
}
