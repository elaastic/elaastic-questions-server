import type { Meta, StoryObj } from '@storybook/vue3'
import ProgressBar from '@/components/progressBar/ProgressBar.vue'


const meta = {
  title: 'progressBar/ProgressBar',
  component: ProgressBar,
  tags: ['autodocs'],
} satisfies Meta<typeof ProgressBar>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    steps: [false, true, false]
  },
}

