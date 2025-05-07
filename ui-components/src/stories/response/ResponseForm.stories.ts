import type { Meta, StoryObj } from '@storybook/vue3'
import ResponseForm from '@/components/response/ResponseForm.vue'

const meta = {
  title: 'response/ResponseForm',
  component: ResponseForm,
  tags: ['autodocs'],
} satisfies Meta<typeof ResponseForm>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    providedAnswers: ['1', '2', '3', '4', '5', '6', '7', '8'],
    selectedAnswers: [],
    selectionsConfiance: [
      { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
      { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
      { label: 'Confiant(e)', value: 'Confiant(e)' },
      { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
    ],
    selectedConfiance: 'Confiant(e)',
    defaultText: 'Votre Réponse',
    isMCQ: true,
    isSend: false,
  },
}
export const Selected: Story = {
  args: {
    providedAnswers: ['1', '2', '3', '4', '5', '6', '7', '8'],
    selectedAnswers: ['1'],
    selectionsConfiance: [
      { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
      { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
      { label: 'Confiant(e)', value: 'Confiant(e)' },
      { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
    ],
    selectedConfiance: 'Confiant(e)',
    defaultText: 'Votre Réponse',
    isMCQ: true,
    isSend: false,
  },
}
export const NoMCQ: Story = {
  args: {
    selectionsConfiance: [
      { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
      { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
      { label: 'Confiant(e)', value: 'Confiant(e)' },
      { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
    ],
    selectedConfiance: 'Confiant(e)',
    defaultText: 'Votre Réponse',
    isMCQ: false,
    isSend: false,
  },
}
export const Sent: Story = {
  args: {
    isSend: true,
  },
}
