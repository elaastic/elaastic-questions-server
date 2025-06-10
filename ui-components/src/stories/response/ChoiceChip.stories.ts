import type { Meta, StoryObj } from '@storybook/vue3-vite'

import ChoiceChip from '@/components/response/ChoiceChip.vue'

const meta = {
  title: 'Response/ChoiceChip',
  component: ChoiceChip,
  tags: ['autodocs', 'atomic'],
} satisfies Meta<typeof ChoiceChip>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    value: 4,
    color: 'primary'
  }
}
