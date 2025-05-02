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
    providedAnswers: ["1", "2", "3"],
    selectedAnswers: [],
    selectionsConfiance: [
      {label: "Pas du tout confiant(e)", value: "Pas du tout confiant(e)"},
      {label: "Pas vraiment confiant(e)", value: "Pas vraiment confiant(e)"},
      {label: "Confiant(e)", value: "Confiant(e)"},
      {label: "Tout à fait confiant(e)", value: "Tout à fait confiant(e)"}
    ],
    selectedConfiance: "Confiant(e)",
    text: "Votre Réponse",
    estQCM: true,
  },
}
