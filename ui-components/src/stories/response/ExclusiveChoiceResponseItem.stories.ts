import type { Meta, StoryObj } from '@storybook/vue3'
import ExclusiveChoiceResponseItem from '@/components/response/ExclusiveChoiceResponseItem.vue'
import {ref} from "vue";


const meta = {
  title: 'response/ExclusiveChoiceResponseItem',
  component: ExclusiveChoiceResponseItem,
  tags: ['autodocs'],
} satisfies Meta<typeof ExclusiveChoiceResponseItem>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  render: (args) => ({
    components: { ExclusiveChoiceResponseItem },
    setup() {
      const selected = ref<number>()
      return { args, selected }
    },
    template: `
        <ExclusiveChoiceResponseItem
          v-bind="args"
          v-model:selected="selected"
        />
    `
  }),
  args: {
    nbCandidateItem: 3,
    selected:1
  },
}
