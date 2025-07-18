import type { Meta, StoryObj } from '@storybook/vue3-vite'

import ListSequenceOverview from '@/components/sequence/ListSequenceOverview.vue'
import {Phase, SequenceStatus} from "@/components/sequence/Sequence.types";

const meta = {
  title: 'sequence/ListSequenceOverview',
  component: ListSequenceOverview,
  tags: ['autodocs', 'atomic'],
  parameters: {
    docs: {
      description: {
        component:
          'This component is used to display several SequenceOverview components',
      },
    },
  },
} satisfies Meta<typeof ListSequenceOverview>

export default meta
type Story = StoryObj<typeof meta>

const sequenceExamples = [{
  id: Date.now(),
  state: {sequenceStatus: SequenceStatus.IN_PROGRESS, phases: [Phase.RESPONSE]},
  question: {
    title: 'A question',
    statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
  },
},
  {
    id: Date.now(),
    state: {sequenceStatus: SequenceStatus.CLOSED},
    question: {
      title: 'An other question',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
    }
  },
  {
    id: Date.now(),
    state: {sequenceStatus: SequenceStatus.NOT_STARTED},
    question: {
      title: 'The final question',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
    }
  }]

export const Default: Story = {
  args: {
    sequences: sequenceExamples,
  },
}
export const WidthRestricted: Story = {
  render: (args) => ({
    components: {ListSequenceQuestion: ListSequenceOverview},
    setup(){
      return {
        args
      }
    },
    template: `
      <v-card :max-width="500">
        <v-card-item>
          <ListSequenceQuestion v-bind="args" ></ListSequenceQuestion>
        </v-card-item>
      </v-card>
    `
  }),
  args: {
    sequences: sequenceExamples,
  },
}
