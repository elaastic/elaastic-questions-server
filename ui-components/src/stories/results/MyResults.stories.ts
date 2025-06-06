import type { Meta, StoryObj } from '@storybook/vue3'
import MyResults from '@/components/results/MyResults.vue'
import type {ExclusiveChoiceResponse, MultipleChoiceResponse, OpenEndedResponse} from "@/models/Response";


const meta = {
  title: 'results/MyResults',
  component: MyResults,
  tags: ['autodocs'],
} satisfies Meta<typeof MyResults>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    answers: [{itemIndex: 1, isCorrect: true}, {itemIndex: 2, isCorrect: false}, {itemIndex: 3, isCorrect: false}, {itemIndex: 4, isCorrect: false}, {itemIndex: 5, isCorrect: false}, {itemIndex: 6, isCorrect: false}, {itemIndex: 7, isCorrect: false}, {itemIndex: 8, isCorrect: false}, {itemIndex: 9, isCorrect: false}, {itemIndex: 10, isCorrect: false}],
    explanationFirstAttempt: {
      answer: {
        id: 1,
        questionType: 'ExclusiveChoice',
        explanation: 'Ma réponse',
        choice: 1,
        confidence: 'Confiant(e)',
      } satisfies ExclusiveChoiceResponse,
      grade: 0,
      nbPeer: 0,
      isTeacher: false,
    },
    explanationSecondAttempt: {
      answer: {
        id: 2,
        questionType: 'ExclusiveChoice',
        explanation: 'Mon autre réponse',
        choice: 2,
        confidence: 'Confiant(e)'
      } satisfies ExclusiveChoiceResponse,
      grade: 0,
      nbPeer: 0,
      isTeacher: false,
    }
  },
}
export const MultipleChoice: Story = {
  args: {
    answers: [{itemIndex: 1, isCorrect: true}, {itemIndex: 2, isCorrect: true}, {itemIndex: 3, isCorrect: false}, {itemIndex: 4, isCorrect: false}, {itemIndex: 5, isCorrect: false}],
    explanationFirstAttempt: {
      answer: {
        id: 1,
        questionType: 'MultipleChoice',
        explanation: 'Ma réponse',
        choices: [1],
        confidence: 'Confiant(e)',
      } satisfies MultipleChoiceResponse,
      grade: 0,
      nbPeer: 0,
      isTeacher: false,
    },
    explanationSecondAttempt: {
      answer: {
        id: 2,
        questionType: 'MultipleChoice',
        explanation: 'Mon autre réponse',
        choices: [1, 2],
        confidence: 'Confiant(e)'
      } satisfies MultipleChoiceResponse,
      grade: 0,
      nbPeer: 0,
      isTeacher: false,
    }
  },
}
export const MultipleChoiceImproveScore: Story = {
  args: {
    answers: [{itemIndex: 1, isCorrect: true}, {itemIndex: 2, isCorrect: true}, {itemIndex: 3, isCorrect: false}, {itemIndex: 4, isCorrect: false}, {itemIndex: 5, isCorrect: false}],
    explanationFirstAttempt: {
      answer: {
        id: 1,
        questionType: 'MultipleChoice',
        explanation: 'Ma réponse',
        choices: [1, 3],
        confidence: 'Confiant(e)',
      } satisfies MultipleChoiceResponse,
      grade: 0,
      nbPeer: 0,
      isTeacher: false,
    },
    explanationSecondAttempt: {
      answer: {
        id: 2,
        questionType: 'MultipleChoice',
        explanation: 'Mon autre réponse',
        choices: [1, 2, 3],
        confidence: 'Confiant(e)'
      } satisfies MultipleChoiceResponse,
      grade: 0,
      nbPeer: 0,
      isTeacher: false,
    }
  },
}
export const Open: Story = {
  args: {
    answers: [{itemIndex: 1, isCorrect: true}, {itemIndex: 2, isCorrect: true}, {itemIndex: 3, isCorrect: false}, {itemIndex: 4, isCorrect: false}, {itemIndex: 5, isCorrect: false}],
    explanationFirstAttempt: {
      answer: {
        id: 1,
        questionType: 'OpenEnded',
        explanation: 'Ma réponse',
        confidence: 'Confiant(e)',
      } satisfies OpenEndedResponse,
      grade: 0,
      nbPeer: 0,
      isTeacher: false,
    },
    explanationSecondAttempt: {
      answer: {
        id: 2,
        questionType: 'OpenEnded',
        explanation: 'Mon autre réponse',
        confidence: 'Confiant(e)'
      } satisfies OpenEndedResponse,
      grade: 0,
      nbPeer: 0,
      isTeacher: false,
    }
  },
}
export const OpenWithExplanationGraded: Story = {
  args: {
    answers: [{itemIndex: 1, isCorrect: true}, {itemIndex: 2, isCorrect: true}, {itemIndex: 3, isCorrect: false}, {itemIndex: 4, isCorrect: false}, {itemIndex: 5, isCorrect: false}],
    explanationFirstAttempt: {
      answer: {
        id: 1,
        questionType: 'OpenEnded',
        explanation: 'Ma réponse',
        confidence: 'Confiant(e)',
      } satisfies OpenEndedResponse,
      grade: 3.5,
      nbPeer: 3,
      isTeacher: false,
    },
    explanationSecondAttempt: {
      answer: {
        id: 2,
        questionType: 'OpenEnded',
        explanation: 'Mon autre réponse',
        confidence: 'Confiant(e)'
      } satisfies OpenEndedResponse,
      grade: 4,
      nbPeer: 1,
      isTeacher: false,
    }
  },
}

