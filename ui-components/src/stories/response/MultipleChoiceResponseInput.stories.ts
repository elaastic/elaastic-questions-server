import type { Meta, StoryObj } from '@storybook/vue3'
import MultipleChoiceResponseInput from '@/components/response/MultipleChoiceResponseInput.vue'
import {ref} from "vue";


const meta = {
  title: 'response/MultipleChoiceResponseInput',
  component: MultipleChoiceResponseInput,
  tags: ['autodocs'],
} satisfies Meta<typeof MultipleChoiceResponseInput>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  render: (args) => ({
    components: { MultipleChoiceResponseInput },
    setup() {
      const selected = ref<number[]>([])
      return { args, selected }
    },
    template: `
        <MultipleChoiceResponseInput
          v-bind="args"
          v-model:selected="selected"
        />
    `
  }),
  args: {
    nbCandidateItem: 3,
    selected: []
  },
}
