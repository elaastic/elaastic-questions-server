import type { Meta, StoryObj } from '@storybook/vue3-vite'

import SequenceQuestion from '@/components/sequence/SequenceQuestion.vue'

const meta = {
  title: 'sequence/SequenceQuestion',
  component: SequenceQuestion,
  tags: ['autodocs', 'atomic'],
} satisfies Meta<typeof SequenceQuestion>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  render: (args) => ({
    components: {SequenceQuestion},
    setup(){
      return{
        args
      }
    },
    template: `
      <SequenceQuestion v-bind="args">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut.</p>
      </SequenceQuestion>
    `
  }),
  args: {
    question: {title: "Question 1", questionNumber: 1},
    sequenceState: 'NOT_STARTED'
  }
}
export const Closed: Story = {
  render: (args) => ({
    components: {SequenceQuestion},
    setup(){
      return{
        args
      }
    },
    template: `
      <SequenceQuestion v-bind="args">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut.</p>
      </SequenceQuestion>
    `
  }),
  args: {
    question: {title: "Question 1", questionNumber: 1},
    sequenceState: 'CLOSED'
  }
}
export const ResponsePhase: Story = {
  render: (args) => ({
    components: {SequenceQuestion},
    setup(){
      return{
        args
      }
    },
    template: `
      <SequenceQuestion v-bind="args">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut.</p>
      </SequenceQuestion>
    `
  }),
  args: {
    question: {title: "Question 1", questionNumber: 1},
    sequenceState: 'RESPONSE_PHASE'
  }
}
export const ConfrontingViewpoint: Story = {
  render: (args) => ({
    components: {SequenceQuestion},
    setup(){
      return{
        args
      }
    },
    template: `
      <SequenceQuestion v-bind="args">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut.</p>
      </SequenceQuestion>
    `
  }),
  args: {
    question: {title: "Question 1", questionNumber: 1},
    sequenceState: 'CONFRONTING_VIEWPOINT'
  }
}
export const ResultsPhase: Story = {
  render: (args) => ({
    components: {SequenceQuestion},
    setup(){
      return{
        args
      }
    },
    template: `
      <SequenceQuestion v-bind="args">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut.</p>
      </SequenceQuestion>
    `
  }),
  args: {
    question: {title: "Question 1", questionNumber: 1},
    sequenceState: 'RESULTS_PHASE'
  }
}
export const IsSelected: Story = {
  render: (args) => ({
    components: {SequenceQuestion},
    setup(){
      return{
        args
      }
    },
    template: `
      <SequenceQuestion v-bind="args">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut.</p>
      </SequenceQuestion>
    `
  }),
  args: {
    question: {title: "Question 1", questionNumber: 1},
    isSelected: true,
    sequenceState: 'CLOSED'
  }
}
export const ConcreteExample: Story = {
  render: (args) => ({
    components: {SequenceQuestion},
    setup(){
      return{
        args
      }
    },
    template: `
      <SequenceQuestion v-bind="args">
        <p>Quelle est la capitale de la France ?</p>
        <br/>
        <p>A)Lille</p>
        <p>B)Nantes</p>
        <p>C)Lyon</p>
        <p>D)La réponse D</p>
        <p>E)Paris</p>
        <p>F)Toulouse</p>
        <p>G)Strasbourg</p>
        <p>H)Marseille</p>
      </SequenceQuestion>
    `
  }),
  args: {
    question: {title: "Capitale de la France ?", questionNumber: 1},
    isSelected: true,
    sequenceState: 'CLOSED'
  }
}
