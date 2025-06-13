import type { Meta, StoryObj } from '@storybook/vue3'
import Steps from '@/components/sequence/Steps.vue'


const meta = {
  title: 'Steps/Steps',
  component: Steps,
  tags: ['autodocs'],
} satisfies Meta<typeof Steps>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    responseSubmissionState: 'ACTIVE',
    evaluationState: 'DISABLED',
    readState: 'DISABLED',
    studentsProvideExplanation: true
  },
}
export const Step2: Story = {
  args: {
    responseSubmissionState: 'COMPLETED',
    evaluationState: 'ACTIVE',
    readState: 'DISABLED',
    studentsProvideExplanation: true
  },
}
export const Step3: Story = {
  args: {
    responseSubmissionState: 'COMPLETED',
    evaluationState: 'COMPLETED',
    readState: 'ACTIVE',
    studentsProvideExplanation: true
  },
}
export const ShowStatistics: Story = {
  args: {
    responseSubmissionState: 'COMPLETED',
    evaluationState: 'ACTIVE',
    readState: 'DISABLED',
    showStatistics: true,
    numberOfAnswerFirstAttempt: 10,
    numberOfAnswerSecondAttempt: 8,
    numberOfReviewers: 4,
    studentsProvideExplanation: true
  },
}
