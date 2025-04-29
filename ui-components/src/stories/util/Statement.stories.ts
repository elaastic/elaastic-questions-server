import type { Meta, StoryObj } from '@storybook/vue3'
import Statement from '@/components/util/Statement.vue'

const meta = {
  title: 'util/Statement',
  component: Statement,
  tags: ['autodocs'],
} satisfies Meta<typeof Statement>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    title: "Test",
    content: "Test",
  }
}

