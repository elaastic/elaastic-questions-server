import type { Meta, StoryObj } from '@storybook/vue3'
import ElExplanationHeader from '@/components/results/ElExplanationHeader.vue'


const meta = {
  title: 'results/ElExplanationHeader',
  component: ElExplanationHeader,
  tags: ['autodocs'],
} satisfies Meta<typeof ElExplanationHeader>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    grade: 0,
    numberOfPeerReview: 0,
    teacher: false,
    showDetails: false
  },
}
export const PeerReview: Story = {
  args: {
    grade: 2,
    numberOfPeerReview: 3,
    teacher: false,
    showDetails: false
  },
}
export const PeerReviewAndShowDetails: Story = {
  args: {
    grade: 2,
    numberOfPeerReview: 3,
    teacher: false,
    showDetails: true
  },
}
export const Teacher: Story = {
  args: {
    grade: 0,
    numberOfPeerReview: 0,
    teacher: true,
    showDetails: false
  },
}
export const PeerReviewAndTeacher: Story = {
  args: {
    grade: 2,
    numberOfPeerReview: 3,
    teacher: true,
    showDetails: false
  },
}
