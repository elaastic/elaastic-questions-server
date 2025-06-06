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
    dataLeft: [{ itemIndex: "Tout à fait confiant(e)", value: 40 }, { itemIndex: "Confiant(e)", value: 10 }, { itemIndex: "Pas vraiment confiant(e)", value: 15 }, { itemIndex: "Pas du tout confiant(e)", value: 35 }],
    dataRight: [{ itemIndex: "Tout à fait confiant(e)", value: 0 }, { itemIndex: "Confiant(e)", value: 80 }, { itemIndex: "Pas vraiment confiant(e)", value: 10 }, { itemIndex: "Pas du tout confiant(e)", value: 10 }],
    titleLeft: "Bonne(s) réponse(s)",
    titleRight: "Mauvaise(s) réponse(s)",
    xLabel: "Pourcentage des votants",
    width: 500,
  }
}
