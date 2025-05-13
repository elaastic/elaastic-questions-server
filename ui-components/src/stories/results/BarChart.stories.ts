import type { Meta, StoryObj } from '@storybook/vue3'
import BarChart from '@/components/results/BarChart.vue'


const meta = {
  title: 'results/BarChart',
  component: BarChart,
  tags: ['autodocs'],
} satisfies Meta<typeof BarChart>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    data: [{ choix: 1, value: 60, isCorrect: true }, { choix: 2, value: 40, isCorrect: true }, { choix: 3, value: 30,isCorrect: false }],
    title: "Résultats du sondage",
    xLabel: "Choix",
    yLabel: "Pourcentage des votants",
    width: 500,
  }
}
