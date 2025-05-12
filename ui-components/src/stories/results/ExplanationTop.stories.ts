import type { Meta, StoryObj } from '@storybook/vue3'
import ExplanationTop from '@/components/results/ExplanationTop.vue'


const meta = {
  title: 'result/ExplanationTop',
  component: ExplanationTop,
  tags: ['autodocs'],
} satisfies Meta<typeof ExplanationTop>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    grade: 0,
    numberOfPeerReview: 0,
    teacher: false,
    peerReview: false
  },
}
export const Teacher: Story = {
  args: {
    grade: 0,
    numberOfPeerReview: 0,
    teacher: true,
    peerReview: false
  },
}
export const PeerReview: Story = {
  args: {
    grade: 2,
    numberOfPeerReview: 3,
    teacher: false,
    peerReview: true
  },
}
export const PeerReviewAndTeacher: Story = {
  args: {
    grade: 2,
    numberOfPeerReview: 3,
    teacher: true,
    peerReview: true
  },
}
