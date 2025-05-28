import type { Meta, StoryObj } from '@storybook/vue3'
import FirstPhaseResponse from '@/components/response/FirstPhaseResponse.vue'


const meta = {
  title: 'response/FirstPhaseResponse',
  component: FirstPhaseResponse,
  tags: ['autodocs'],
} satisfies Meta<typeof FirstPhaseResponse>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    questionTitle: "Question 1",
    questionContent: "Content of Question 1",
    providedAnswers: 4,
    firstAnswer:
            {
              id: 0,
              questionType: 'MultipleChoice',
              explanation: '',
              choices: [],
              trust: 'Confiant(e)'
            },
    sequenceInProgress: true
  }
}
export const Exclusive: Story = {
  args: {
    questionTitle: "Question 1",
    questionContent: "Content of Question 1",
    providedAnswers: 4,
    firstAnswer:
            {
              id: 0,
              questionType: 'ExclusiveChoice',
              explanation: '',
              choice: 1,
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
    firstAnswer:
            {
              id: 0,
              questionType: 'OpenEnded',
              explanation: '',
              trust: 'Confiant(e)'
            },
    sequenceInProgress: true,
  }
}
export const SequenceClosed: Story = {
  args: {
    questionTitle: "Question 1",
    questionContent: "Content of Question 1",
    providedAnswers: 4,
    firstAnswer:
            {
              id: 0,
              questionType: 'OpenEnded',
              explanation: '',
              trust: 'Confiant(e)'
            },
    sequenceInProgress: false,
  }
}
