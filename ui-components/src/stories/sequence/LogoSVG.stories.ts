import type { Meta, StoryObj } from '@storybook/vue3-vite'

import LogoSVG from '@/components/sequence/LogoSVG.vue'

const meta = {
  title: 'sequence/LogoSVG',
  component: LogoSVG,
  tags: ['autodocs', 'atomic'],
} satisfies Meta<typeof LogoSVG>

export default meta
type Story = StoryObj<typeof meta>

export const NotStarted: Story = {
  args: {
    state: 'NOT_STARTED',
  },
}
export const ResponsePhase: Story = {
  args: {
    state: 'RESPONSE_PHASE',
  },
}
export const ConfrontingViewpoint: Story = {
  args: {
    state: 'CONFRONTING_VIEWPOINT',
  },
}
export const ResultsPhase: Story = {
  args: {
    state: 'RESULTS_PHASE',
  },
}
export const Closed: Story = {
  args: {
    state: 'CLOSED',
  },
}
export const Distant: Story = {
  args: {
    state: 'DISTANT',
  },
}
export const Blended: Story = {
  args: {
    state: 'BLENDED',
  },
}
