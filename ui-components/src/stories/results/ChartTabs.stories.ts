import type { Meta, StoryObj } from '@storybook/vue3'
import ChartTabs from '@/components/results/ChartTabs.vue'


const meta = {
  title: 'results/ChartTabs',
  component: ChartTabs,
  tags: ['autodocs'],
} satisfies Meta<typeof ChartTabs>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    dataDefinitiveVoteChart: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceChartLeft: [20,20,20,40],
    dataConfidenceChartRight: [25,20,20,35],
    dataPeerChartLeft: [15,70,5,5,5],
    dataPeerChartRight: [50,20,10,10,10],
    displayPeerTab: false,
    displayConfidenceTab: false,
  },
}
export const WithConfidenceTab: Story = {
  args: {
    dataDefinitiveVoteChart: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceChartLeft: [20,20,20,40],
    dataConfidenceChartRight: [25,20,20,35],
    dataPeerChartLeft: [15,70,5,5,5],
    dataPeerChartRight: [50,20,10,10,10],
    displayPeerTab: false,
    displayConfidenceTab: true,
  },
}
export const WithPeerChart: Story = {
  args: {
    dataDefinitiveVoteChart: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceChartLeft: [20,20,20,40],
    dataConfidenceChartRight: [25,20,20,35],
    dataPeerChartLeft: [15,70,5,5,5],
    dataPeerChartRight: [50,20,10,10,10],
    displayPeerTab: true,
    displayConfidenceTab: true,
  },
}
export const WithTemporaryData: Story = {
  args: {
    dataDefinitiveVoteChart: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTemporaryVoteChart: [{ itemIndex: 1, value: 20, isCorrect: true }, { itemIndex: 2, value: 5, isCorrect: false }, { itemIndex: 3, value: 50,isCorrect: false }, { itemIndex: 4, value: 25,isCorrect: false }],
    dataConfidenceChartLeft: [20,20,20,40],
    dataConfidenceChartRight: [25,20,20,35],
    dataPeerChartLeft: [15,70,5,5,5],
    dataPeerChartRight: [50,20,10,10,10],
    displayPeerTab: true,
    displayConfidenceTab: true,
  },
}
