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
    temporaryData: [{ itemIndex: 1, value: 60, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: true }, { itemIndex: 3, value: 30,isCorrect: false }],
    title: "Résultats du sondage",
    width: 500,
    definitiveData: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 60, isCorrect: true }, { itemIndex: 3, value: 50,isCorrect: false }],
  }
}
export const OnlyDefinitive: Story = {
  args: {
    title: "Résultats du sondage",
    width: 500,
    definitiveData: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 60, isCorrect: true }, { itemIndex: 3, value: 50,isCorrect: false }],
  }
}
