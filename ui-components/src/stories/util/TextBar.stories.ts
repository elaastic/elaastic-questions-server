import type { Meta, StoryObj } from '@storybook/vue3'

import TextBar from '@/components/util/TextBar.vue'

const meta = {
  title: 'util/TextBar',
  component: TextBar,
  tags: ['autodocs']
} satisfies Meta<typeof TextBar>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    value: "Veuillez soumettre votre réponse",
    color: 'primary'
  }
}
export const Variant: Story = {
  args: {
    value: "Soumettez une réponse",
    color: 'yellow'
  }
}
