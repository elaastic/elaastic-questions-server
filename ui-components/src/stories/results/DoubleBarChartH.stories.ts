import type { Meta, StoryObj } from '@storybook/vue3'
import DoubleBarChartH from '@/components/results/DoubleBarChartH.vue'


const meta = {
  title: 'results/DoubleBarChartH',
  component: DoubleBarChartH,
  tags: ['autodocs'],
} satisfies Meta<typeof DoubleBarChartH>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    dataLeft: [{ choix: "Tout à fait confiant(e)", value: 40 }, { choix: "Confiant(e)", value: 10 }, { choix: "Pas vraiment confiant(e)", value: 15 }, { choix: "Pas du tout confiant(e)", value: 35 }],
    dataRight: [{ choix: "Tout à fait confiant(e)", value: 0 }, { choix: "Confiant(e)", value: 80 }, { choix: "Pas vraiment confiant(e)", value: 10 }, { choix: "Pas du tout confiant(e)", value: 10 }],
    titleLeft: "Bonne(s) réponse(s)",
    titleRight: "Mauvaise(s) réponse(s)",
    xLabel: "Pourcentage des votants",
    width: 500,
  }
}
