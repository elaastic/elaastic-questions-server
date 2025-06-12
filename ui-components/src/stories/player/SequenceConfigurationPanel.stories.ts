import type { Meta, StoryObj } from '@storybook/vue3'

import SequenceConfigurationPanel, {
  type ExecutionContextType,
  type ValuationMethodType
} from '@/components/player/SequenceConfigurationPanel.vue'
import {ref} from "vue";

const handleSequence = (sequence: {executionContext: ExecutionContextType, studentsProvideExplanation: boolean, valuationMethod: ValuationMethodType, withChatGPTExplanation: boolean, nbOfAnswerEvaluated: number}) => {
  console.log(sequence)
}

const meta = {
  title: 'player/SequenceConfigurationPanel',
  component: SequenceConfigurationPanel,
  tags: ['autodocs']
} satisfies Meta<typeof SequenceConfigurationPanel>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  render: (args) => ({
    components: {SequenceConfigurationPanel, handleSequence},
    setup(){
      const executionContext = ref<ExecutionContextType>();
      const studentProvideExplanation=ref<boolean>();
      const valuationMethod = ref<ValuationMethodType>();
      const withChatGPTExplanation = ref<boolean>();
      const nbOfAnswerEvaluatedLocal = ref<number>();
      return{
        executionContext, args, studentProvideExplanation, valuationMethod, withChatGPTExplanation, nbOfAnswerEvaluatedLocal, handleSequence
      }
    },
    template: `
        <SequenceConfigurationPanel
          v-bind="args"
          v-model:executionContext="executionContext"
          v-model:students-provide-explanation="studentProvideExplanation"
          v-model:valuation-method="valuationMethod"
          v-model:with-chat-g-p-t-explanation="withChatGPTExplanation"
          v-model:nb-of-answer-evaluated="nbOfAnswerEvaluatedLocal"
          @update:sequence="handleSequence"
        />
    `
  }),
  args: {
    isOpenQuestion: false
  }
}
export const OpenQuestion: Story = {
  render: (args) => ({
    components: {SequenceConfigurationPanel, handleSequence},
    setup(){
      const executionContext = ref<ExecutionContextType>();
      const studentProvideExplanation=ref<boolean>();
      const valuationMethod = ref<ValuationMethodType>();
      const withChatGPTExplanation = ref<boolean>();
      const nbOfAnswerEvaluatedLocal = ref<number>();
      return{
        executionContext, args, studentProvideExplanation, valuationMethod, withChatGPTExplanation, nbOfAnswerEvaluatedLocal, handleSequence
      }
    },
    template: `
        <SequenceConfigurationPanel
          v-bind="args"
          v-model:executionContext="executionContext"
          v-model:students-provide-explanation="studentProvideExplanation"
          v-model:valuation-method="valuationMethod"
          v-model:with-chat-g-p-t-explanation="withChatGPTExplanation"
          v-model:nb-of-answer-evaluated="nbOfAnswerEvaluatedLocal"
          @update:sequence="handleSequence"
        />
    `
  }),
  args: {
    isOpenQuestion: true
  }
}
