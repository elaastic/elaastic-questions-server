import { expect, userEvent, within } from '@storybook/test'
import type { Meta, StoryObj } from '@storybook/vue3'

import LikertRating from '@/stories/LikertRating.vue'

const meta = {
  title: 'Rating/Likert',
  component: LikertRating,
  render: () => ({
    components: { LikertRating },
    template: '<likert-rating />'
  }),
  tags: ['autodocs'],
} satisfies Meta<typeof LikertRating>

export default meta
type Story = StoryObj<typeof meta>

export const Hello: Story = {};
