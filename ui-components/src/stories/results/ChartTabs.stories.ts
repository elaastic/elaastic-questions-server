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
    dataVoteChart: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustChartLeft: [20,20,20,40],
    dataTrustChartRight: [25,20,20,35],
    dataPeerChartLeft: [15,70,5,5,5],
    dataPeerChartRight: [50,20,10,10,10],
    displayPeerTab: false,
    displayTrustTab: false,
  },
}
export const WithTrustTab: Story = {
  args: {
    dataVoteChart: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustChartLeft: [20,20,20,40],
    dataTrustChartRight: [25,20,20,35],
    dataPeerChartLeft: [15,70,5,5,5],
    dataPeerChartRight: [50,20,10,10,10],
    displayPeerTab: false,
    displayTrustTab: true,
  },
}
export const WithPeerChart: Story = {
  args: {
    dataVoteChart: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustChartLeft: [20,20,20,40],
    dataTrustChartRight: [25,20,20,35],
    dataPeerChartLeft: [15,70,5,5,5],
    dataPeerChartRight: [50,20,10,10,10],
    displayPeerTab: true,
    displayTrustTab: true,
  },
}
