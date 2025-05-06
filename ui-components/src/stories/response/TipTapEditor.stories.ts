import type { Meta, StoryObj } from '@storybook/vue3'
import TipTap from '@/components/response/TipTapEditor.vue'


const meta = {
  title: 'response/TipTapEditor',
  component: TipTap,
  tags: ['autodocs'],
} satisfies Meta<typeof TipTap>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    modelValue: "Contenu par défaut",
    isReadonly: false,
    isDark: false,
  },
}
export const Readonly: Story = {
  args: {
    modelValue: "Contenu par défaut",
    isReadonly: true,
    isDark: false,
  },
}
export const Dark: Story = {
  args: {
    modelValue: "Contenu par défaut",
    isReadonly: false,
    isDark: true,
  },
}
