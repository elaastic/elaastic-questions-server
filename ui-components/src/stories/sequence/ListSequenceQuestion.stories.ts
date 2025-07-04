import type { Meta, StoryObj } from '@storybook/vue3-vite'

import ListSequenceQuestion from '@/components/sequence/ListSequenceQuestion.vue'

const meta = {
  title: 'sequence/ListSequenceQuestion',
  component: ListSequenceQuestion,
  tags: ['autodocs', 'atomic'],
} satisfies Meta<typeof ListSequenceQuestion>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    questions: [
      {
        state: 'NOT_STARTED',
        question: {
          title: 'Question 1',
          statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
          questionNumber: 1,
        }
      },
      {
        state: 'RESPONSE_PHASE',
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
          questionNumber: 2,
        }
      },
      {
        state: 'CLOSED',
        question: {
          title: 'Capitale de la France ?',
          statement: `
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>
            <p>A) Lille</p>
            <p>B) Nantes</p>
            <p>C) Lyon</p>
            <p>D) La réponse D</p>
            <p>E) Paris</p>
            <p>F) Toulouse</p>
            <p>G) Strasbourg</p>
            <p>H) Marseille</p>
        `,
          questionNumber: 3,
        }
      }
    ]
  },
}
export const HideStatements: Story = {
  args: {
    questions: [
      {
        state: 'NOT_STARTED',
        question: {
          title: 'Question 1',
          statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
          questionNumber: 1,
        }
      },
      {
        state: 'RESPONSE_PHASE',
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
          questionNumber: 2,
        }
      },
      {
        state: 'CLOSED',
        question: {
          title: 'Capitale de la France ?',
          statement: `
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>
            <p>A) Lille</p>
            <p>B) Nantes</p>
            <p>C) Lyon</p>
            <p>D) La réponse D</p>
            <p>E) Paris</p>
            <p>F) Toulouse</p>
            <p>G) Strasbourg</p>
            <p>H) Marseille</p>
        `,
          questionNumber: 3,
        }
      }
    ],
    hideStatements: true
  },
}
export const WidthRestricted: Story = {
  render: (args) => ({
    components: {ListSequenceQuestion},
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
    questions: [
      {
        state: 'NOT_STARTED',
        question: {
          title: 'Question 1',
          statement: `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
          questionNumber: 1,
        }
      },
      {
        state: 'RESPONSE_PHASE',
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
          questionNumber: 2,
        }
      },
      {
        state: 'CLOSED',
        question: {
          title: 'Capitale de la France ?',
          statement: `
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>
            <p>A) Lille</p>
            <p>B) Nantes</p>
            <p>C) Lyon</p>
            <p>D) La réponse D</p>
            <p>E) Paris</p>
            <p>F) Toulouse</p>
            <p>G) Strasbourg</p>
            <p>H) Marseille</p>
        `,
          questionNumber: 3,
        }
      }
    ]
  },
}
