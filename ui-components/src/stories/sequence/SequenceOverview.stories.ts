import type { Meta, StoryObj } from '@storybook/vue3-vite'

import SequenceOverview from '@/components/sequence/SequenceOverview.vue'
import { type SequenceOverviewProps } from '@/components/sequence/SequenceOverview.vue'
import franceMap from '../assets/france_map.jpg'
import { type SequenceState, SequenceStatus } from '@/components/sequence/Sequence.types'
import { Phase } from '@/components/sequence/Sequence.types'

const meta = {
  title: 'sequence/SequenceOverview',
  component: SequenceOverview,
  tags: ['autodocs', 'atomic'],
  parameters: {
    docs: {
      description: {
        component:
          'This component is used to display a sequence overview. It is used in the assignment overview which allows to navigate between the sequences.',
      },
    },
  },
} satisfies Meta<typeof SequenceOverview>

export default meta
type Story = StoryObj<typeof meta>

/**
 * Sequence for numbering questions
 */
const questionIndexSequence = () => {
  let index = 0
  return () => ++index
}

/**
 * Get the next question index in the sequence
 */
const getNextQuestionIndex = questionIndexSequence()

/**
 * Options for generating a sequence for the stories
 */
interface CreateSequenceOptions {
  title?: string
  statement?: string
  sequenceState: SequenceState
}

/**
 * Generate question
 * @param title if not provided, the default title will be used
 * @param statement if not provided, the default statement will be used
 * @param sequenceState the sequence state
 */
const createSequence = ({ title, statement, sequenceState }: CreateSequenceOptions) => ({
  sequence: {
    id: Date.now(),
    question: {
      title: title ?? 'To be or not to be... this is the question',
      statement:
        statement ??
        `<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut. </p>`,
    },
    state: sequenceState,
  },
})

export const Default: Story = {
  args: {
    ...createSequence({
      title: 'The default question',
      sequenceState: { sequenceStatus: SequenceStatus.IN_PROGRESS, phases: [Phase.RESPONSE] },
    }),
    sequenceIndex: getNextQuestionIndex(),
  },
}

export const NotStarted: Story = {
  args: {
    ...createSequence({
      title: 'This sequence is not started',
      sequenceState: { sequenceStatus: SequenceStatus.NOT_STARTED },
    }),

    sequenceIndex: getNextQuestionIndex(),
  },
  parameters: {
    docs: {
      description: {
        story: 'When a sequence is not started, the question statement must not be seen.',
      },
    },
  },
}

export const Blended: Story = {
  args: {
    ...createSequence({
      sequenceState: {
        sequenceStatus: SequenceStatus.IN_PROGRESS,
        phases: [Phase.RESPONSE, Phase.CONFRONTING_VIEWPOINT],
      },
    }),

    sequenceIndex: getNextQuestionIndex(),
  },
  parameters: {
    docs: {
      description: {
        story: 'In blended mode, the 1st two phases are opened when the sequence is in progress.',
      },
    },
  },
}

export const Distant: Story = {
  args: {
    ...createSequence({
      sequenceState: {
        sequenceStatus: SequenceStatus.IN_PROGRESS,
        phases: [Phase.RESPONSE, Phase.CONFRONTING_VIEWPOINT, Phase.RESULTS],
      },
    }),

    sequenceIndex: getNextQuestionIndex(),
  },
  parameters: {
    docs: {
      description: {
        story: 'In distant mode, all the phases are opened when the sequence is in progress.',
      },
    },
  },
}

export const Selected: Story = {
  args: {
    ...createSequence({
      sequenceState: { sequenceStatus: SequenceStatus.CLOSED },
    }),
    selected: true,

    sequenceIndex: getNextQuestionIndex(),
  },
}

const geographyStatement = `
        <p>What is the capital of France?</p>
        <img src="${franceMap}" alt="France map"/>
        <p>A) Lille</p>
        <p>B) Nantes</p>
        <p>C) Lyon</p>
        <p>D) The "D" answer</p>
        <p>E) Paris</p>
        <p>F) Toulouse</p>
        <p>G) Strasbourg</p>
        <p>H) Marseille</p>
      `

export const ExampleWithFilteredContent: Story = {
  args: {
    ...createSequence({
      title: `Geography`,
      statement: geographyStatement,
      sequenceState: { sequenceStatus: SequenceStatus.CLOSED },
    }),
    sequenceIndex: getNextQuestionIndex(),
  },
  parameters: {
    docs: {
      description: {
        story:
          'This component only display an abstract of the statement. Images are filtered. Only the first paragraph is displayed. The statement is truncated if it is too long.',
      },
    },
  },
}

export const ExampleWithLongTitle: Story = {
  args: {
    ...createSequence({
      title: `Very very long title so that we can check what is going with the layout of this component on in that specific limit use-case`,
      sequenceState: { sequenceStatus: SequenceStatus.CLOSED },
    }),
    sequenceIndex: getNextQuestionIndex(),
  },
  parameters: {
    docs: {
      description: {
        story: 'The title is truncated if it is too long.',
      },
    },
  },
}

export const ExampleWithSmallerWidth: Story = {
  render: (args: SequenceOverviewProps) => ({
    components: { SequenceOverview },
    setup() {
      return {
        args,
      }
    },
    template: `
      <v-card :max-width="500">
        <v-card-item>
          <SequenceOverview v-bind="args" />
        </v-card-item>
      </v-card>
    `,
  }),
  args: {
    ...createSequence({
      sequenceState: { sequenceStatus: SequenceStatus.CLOSED },
    }),
    sequenceIndex: getNextQuestionIndex(),
  },
}
