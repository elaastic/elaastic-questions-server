import type { Meta, StoryObj } from '@storybook/vue3'

import EvaluationCard from '@/stories/evaluation/EvaluationCard.vue'
import { ref } from 'vue'
import type { LikertValue } from '@/stories/evaluation/Likert'
import type { MultipleChoiceResponse } from '@/models/Response'

const meta = {
  title: 'Evaluation/EvaluationCard',
  component: EvaluationCard,
  tags: ['autodocs']
} satisfies Meta<typeof EvaluationCard>

export default meta
type Story = StoryObj<typeof meta>

export const ExclusiveChoice: Story = {
  render: (args) => ({
    components: { EvaluationCard },
    setup() {
      const evaluationValue = ref<LikertValue>(args.modelValue || null)
      return { ...args, evaluationValue }
    },
    template: `
      <evaluation-card v-model="evaluationValue"
                       :evaluation-num="evaluationNum"
                       :choices="choices"
                       :response="response" />
    `
  }),
  args: {
    modelValue: null,
    evaluationNum: 3,
    response: {
      id: 1,
      questionType: 'ExclusiveChoice',
      choice: 1,
      explanation: 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.'
    }
  }
}

export const MultipleChoice: Story = {
  render: ExclusiveChoice.render,
  args: {
    ...ExclusiveChoice.args,
    response: {
      id: 2,
      questionType: 'MultipleChoice',
      choices: [1, 4],
      explanation: 'Another explanation'
    },
  }
}

export const OpenEnded: Story = {
  render: ExclusiveChoice.render,
  args: {
    ...ExclusiveChoice.args,
    response: {
      id: 3,
      questionType: 'OpenEnded',
      explanation: 'TODO'
    }
  }
}
