import type { Meta, StoryObj } from '@storybook/vue3'
import Ckeditor from '@/components/response/Ckeditor.vue'


const meta = {
  title: 'response/Ckeditor',
  component: Ckeditor,
  tags: ['autodocs'],
} satisfies Meta<typeof Ckeditor>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    modelValue: ""
  },
}
