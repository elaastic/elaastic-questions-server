import type { Meta, StoryObj } from '@storybook/vue3-vite'

import SequenceQuestion from '@/components/sequence/SequenceQuestion.vue'
import franceMap from '../assets/france_map.jpg';

const meta = {
  title: 'sequence/SequenceQuestion',
  component: SequenceQuestion,
  tags: ['autodocs', 'atomic'],
} satisfies Meta<typeof SequenceQuestion>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    question: {
      title: 'Question 1',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
      questionNumber: 1,
    },    sequenceState: 'NOT_STARTED',
  },
}
export const Closed: Story = {
  args: {
    question: {
      title: 'Question 1',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
      questionNumber: 1,
    },    sequenceState: 'CLOSED',
  },
}
export const ResponsePhase: Story = {
  args: {
    question: {
      title: 'Question 1',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
      questionNumber: 1,
    },    sequenceState: 'RESPONSE_PHASE',
  },
}
export const ConfrontingViewpoint: Story = {
  args: {
    question: {
      title: 'Question 1',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
      questionNumber: 1,
    },    sequenceState: 'CONFRONTING_VIEWPOINT',
  },
}
export const ResultsPhase: Story = {
  args: {
    question: {
      title: 'Question 1',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
      questionNumber: 1,
    },    sequenceState: 'RESULTS_PHASE',
  },
}
export const Blended: Story = {
  args: {
    question: {
      title: 'Question 1',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
      questionNumber: 1,
    },    sequenceState: 'BLENDED',
  },
}
export const Distant: Story = {
  args: {
    question: {
      title: 'Question 1',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
      questionNumber: 1,
    },
    sequenceState: 'DISTANT',
  },
}
export const IsSelected: Story = {
  args: {
    question: {
      title: 'Question 1',
      statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
      questionNumber: 1,
    },
    isSelected: true,
    sequenceState: 'CLOSED',
  },
}
export const ConcreteExample: Story = {
  args: {
    question: {
      title: 'Capitale de la France ?',
      statement: `
        <p>Quelle est la capitale de la France ?</p>
        <p>A) Lille</p>
        <p>B) Nantes</p>
        <p>C) Lyon</p>
        <p>D) La réponse D</p>
        <p>E) Paris</p>
        <p>F) Toulouse</p>
        <p>G) Strasbourg</p>
        <p>H) Marseille</p>
        `,
      questionNumber: 1,
    },
    isSelected: true,
    sequenceState: 'CLOSED',
  },
}
export const ExampleWithImageFiltered: Story = {
  args: {
    question: {
      title: 'Capitale de la France ?',
      statement: `
        <p>Quelle est la capitale de la France ?</p>
        <img src="${franceMap}" alt="France map"/>
        <p>A) Lille</p>
        <p>B) Nantes</p>
        <p>C) Lyon</p>
        <p>D) La réponse D</p>
        <p>E) Paris</p>
        <p>F) Toulouse</p>
        <p>G) Strasbourg</p>
        <p>H) Marseille</p>
        `,
      questionNumber: 1,
    },
    isSelected: true,
    sequenceState: 'CLOSED',
  },
}
