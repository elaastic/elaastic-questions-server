import type { Meta, StoryObj } from '@storybook/vue3'
import Results from '@/components/results/Results.vue'
import type {MultipleChoiceResponse, OpenEndedResponse} from "@/models/Response";


const meta = {
  title: 'results/Results',
  component: Results,
  tags: ['autodocs'],
} satisfies Meta<typeof Results>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {
  args: {
    dataVote: [],
    dataTrustGoodAnswer: [20,20,20,40],
    dataTrustBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [],
    displayTrustTab: false
  }
}

export const HasResults: Story = {
  args: {
    dataVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustGoodAnswer: [20,20,20,40],
    dataTrustBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [],
    displayTrustTab: false
  }
}
export const NoEvaluation: Story = {
  args: {
    dataVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustGoodAnswer: [20,20,20,40],
    dataTrustBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [
      {
        response: {
          id: 1,
          questionType: 'MultipleChoice',
          explanation: 'Mon explication',
          choices: [1,3],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 0,
        isTeacher: false,
      },
      {
        response: {
          id: 2,
          questionType: 'MultipleChoice',
          explanation: 'Ma Réponse',
          choices: [2],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 0,
        isTeacher: false,
      },
      {
        response: {
          id: 3,
          questionType: 'MultipleChoice',
          explanation: 'La Réponse de l\'enseignant',
          choices: [1],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 0,
        isTeacher: true,
      }
    ],
    displayTrustTab: false
  }
}
export const WithPeerReview: Story = {
  args: {
    dataVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustGoodAnswer: [20,20,20,40],
    dataTrustBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [
      {
        response: {
          id: 1,
          questionType: 'MultipleChoice',
          explanation: 'Mon explication',
          choices: [1,3],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: false,
      },
      {
        response: {
          id: 2,
          questionType: 'MultipleChoice',
          explanation: 'Ma Réponse',
          choices: [2],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: false,
      },
      {
        response: {
          id: 3,
          questionType: 'MultipleChoice',
          explanation: 'La Réponse de l\'enseignant',
          choices: [1],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: true,
      }
    ],
    displayTrustTab: false
  }
}
export const NoTeacher: Story = {
  args: {
    dataVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustGoodAnswer: [20,20,20,40],
    dataTrustBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [
      {
        response: {
          id: 1,
          questionType: 'MultipleChoice',
          explanation: 'Mon explication',
          choices: [1,3],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 0,
        isTeacher: false,
      },
      {
        response: {
          id: 2,
          questionType: 'MultipleChoice',
          explanation: 'Ma Réponse',
          choices: [2],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 0,
        isTeacher: false,
      },
      {
        response: {
          id: 3,
          questionType: 'MultipleChoice',
          explanation: '3ème réponse',
          choices: [1],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 0,
        isTeacher: false,
      }
    ],
    displayTrustTab: false
  }
}
export const OpenQuestion: Story = {
  args: {
    dataVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustGoodAnswer: [20,20,20,40],
    dataTrustBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'OpenEnded',
    explanations: [
      {
        response: {
          id: 1,
          questionType: 'OpenEnded',
          explanation: 'Mon explication',
          trust: "",
        } satisfies OpenEndedResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: false,
      },
      {
        response: {
          id: 2,
          questionType: 'OpenEnded',
          explanation: 'Ma Réponse',
          trust: "",
        } satisfies OpenEndedResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: false,
      },
      {
        response: {
          id: 3,
          questionType: 'OpenEnded',
          explanation: 'La Réponse de l\'enseignant',
          trust: "",
        } satisfies OpenEndedResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: true,
      }
    ],
    displayTrustTab: true
  }
}
export const TrustChart: Story = {
  args: {
    dataVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustGoodAnswer: [20,20,20,40],
    dataTrustBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [
      {
        response: {
          id: 1,
          questionType: 'MultipleChoice',
          explanation: 'Mon explication',
          choices: [1,3],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: false,
      },
      {
        response: {
          id: 2,
          questionType: 'MultipleChoice',
          explanation: 'Ma Réponse',
          choices: [2],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: false,
      },
      {
        response: {
          id: 3,
          questionType: 'MultipleChoice',
          explanation: 'La Réponse de l\'enseignant',
          choices: [1],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: true,
      }
    ],
    displayTrustTab: true
  }
}
export const PeerReviewChart: Story = {
  args: {
    dataVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTrustGoodAnswer: [20,20,20,40],
    dataTrustBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: true,
    qType: 'MultipleChoice',
    explanations: [
      {
        response: {
          id: 1,
          questionType: 'MultipleChoice',
          explanation: 'Mon explication',
          choices: [1,3],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: false,
      },
      {
        response: {
          id: 2,
          questionType: 'MultipleChoice',
          explanation: 'Ma Réponse',
          choices: [2],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 3,
        isTeacher: false,
      },
      {
        response: {
          id: 3,
          questionType: 'MultipleChoice',
          explanation: 'La Réponse de l\'enseignant',
          choices: [1],
          trust: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 2,
        isTeacher: true,
      }
    ],
    displayTrustTab: true
  }
}
