import type { Meta, StoryObj } from '@storybook/vue3'

import EvaluationCard from '@/stories/evaluation/EvaluationCard.vue'
import { ref } from 'vue'

const meta = {
  title: 'Evaluation/EvaluationCard',
  component: EvaluationCard,
  tags: ['autodocs']
} satisfies Meta<EvaluationCard>

export default meta
type Story = StoryObj<typeof meta>

const render = (args) => ({
  components: { EvaluationCard },
  setup() {
    const evaluationValue = ref(null)
    return { ...args, evaluationValue }
  },
  template: `
      <evaluation-card v-model="evaluationValue" :evaluation-num="evaluationNum" :choices="choices" :explanation="explanation" />
    `
})

export const ExclusiveChoice: Story = {
  render,
  args: {
    evaluationNum: 3,
    choices: [1],
    explanation: "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
  }
}

export const MultipleChoice: Story = {
  render,
  args: {
    ...ExclusiveChoice.args,
    choices: [1, 4],
  }
}

export const OpenEnded: Story = {
  render,
  args: {
    ...ExclusiveChoice.args,
    choices: undefined,
  }
}
