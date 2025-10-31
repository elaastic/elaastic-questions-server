import type { Meta, StoryObj } from '@storybook/vue3-vite'

import SequenceIcon from '@/components/sequence/SequenceIcon.vue'
import { Phase, SequenceStatus } from '@/components/sequence/Sequence.types'

const meta = {
  title: 'sequence/SequenceIcon',
  component: SequenceIcon,
  tags: ['autodocs', 'atomic'],
  parameters: {
    docs: {
      description: {
        component: 'This component is used to display icons representing the status of a sequence or a phase.',
      },
    },
  },
} satisfies Meta<typeof SequenceIcon>

export default meta
type Story = StoryObj<typeof meta>

export const NotStarted: Story = {
  args: {
    iconId: SequenceStatus.NOT_STARTED,
  },
}

export const ResponsePhase: Story = {
  args: {
    iconId: Phase.RESPONSE,
  },
}

export const ConfrontingViewpoint: Story = {
  args: {
    iconId: Phase.CONFRONTING_VIEWPOINT,
  },
}

export const ResultsPhase: Story = {
  args: {
    iconId: Phase.RESULTS,
  },
}

export const Closed: Story = {
  args: {
    iconId: SequenceStatus.CLOSED,
  },
}

export const SizeVariation: Story = {
  args: {
    iconId: Phase.RESPONSE,
    size: '4rem',
  },
  parameters: {
    docs: {
      description: {
        story: 'The size of the icon can be changed by passing a size prop.',
      },
    },
  },
}
