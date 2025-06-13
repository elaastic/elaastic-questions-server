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
    dataDefinitiveVote: [],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [],
    displayConfidenceTab: false
  }
}

export const HasResults: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [],
    displayConfidenceTab: false
  }
}
export const NoEvaluation: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
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
          confidence: "",
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
          confidence: "",
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
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 0,
        isTeacher: true,
      }
    ],
    displayConfidenceTab: false
  }
}
export const WithPeerReview: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
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
          confidence: "",
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
          confidence: "",
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
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: true,
      }
    ],
    displayConfidenceTab: false
  }
}
export const OnlyTeacher: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 100, isCorrect: true }, { itemIndex: 2, value: 0, isCorrect: false }, { itemIndex: 3, value: 0,isCorrect: false }, { itemIndex: 4, value: 0,isCorrect: false }],
    dataConfidenceGoodAnswer: [0,100,0,0],
    dataConfidenceBadAnswer: [0,0,0,0],
    dataPeerGoodAnswer: [0,0,0,0,0],
    dataPeerBadAnswer: [0,0,0,0,0],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [
      {
        response: {
          id: 1,
          questionType: 'MultipleChoice',
          explanation: 'Mon explication',
          choices: [1],
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 0,
        nbPeer: 0,
        isTeacher: true,
      },
    ],
    displayConfidenceTab: true
  }
}
export const NoTeacher: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
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
          confidence: "",
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
          confidence: "",
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
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 0,
        isTeacher: false,
      }
    ],
    displayConfidenceTab: false
  }
}
export const OpenQuestion: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
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
          confidence: "",
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
          confidence: "",
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
          confidence: "",
        } satisfies OpenEndedResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: true,
      }
    ],
    displayConfidenceTab: true
  }
}
export const ConfidenceChart: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
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
          confidence: "",
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
          confidence: "",
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
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 1,
        isTeacher: true,
      }
    ],
    displayConfidenceTab: true
  }
}
export const PeerReviewChart: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
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
          confidence: "",
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
          confidence: "",
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
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 2,
        isTeacher: true,
      }
    ],
    displayConfidenceTab: true
  }
}
export const TemporaryDataResultsOnly: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTemporaryVote: [{ itemIndex: 1, value: 15, isCorrect: true }, { itemIndex: 2, value: 30, isCorrect: false }, { itemIndex: 3, value: 60,isCorrect: false }, { itemIndex: 4, value: 5,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
    dataPeerGoodAnswer: [15,70,5,5,5],
    dataPeerBadAnswer: [50,20,10,10,10],
    displayPeerTab: false,
    qType: 'MultipleChoice',
    explanations: [],
    displayConfidenceTab: false
  }
}
export const TemporaryData: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTemporaryVote: [{ itemIndex: 1, value: 15, isCorrect: true }, { itemIndex: 2, value: 30, isCorrect: false }, { itemIndex: 3, value: 60,isCorrect: false }, { itemIndex: 4, value: 5,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
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
          confidence: "",
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
          confidence: "",
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
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 2,
        isTeacher: true,
      }
    ],
    displayConfidenceTab: false
  }
}
export const TemporaryDataNoEvaluation: Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTemporaryVote: [{ itemIndex: 1, value: 15, isCorrect: true }, { itemIndex: 2, value: 30, isCorrect: false }, { itemIndex: 3, value: 60,isCorrect: false }, { itemIndex: 4, value: 5,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
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
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 0,
        nbPeer: 0,
        isTeacher: false,
      },
      {
        response: {
          id: 2,
          questionType: 'MultipleChoice',
          explanation: 'Ma Réponse',
          choices: [2],
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 0,
        nbPeer: 0,
        isTeacher: false,
      },
      {
        response: {
          id: 3,
          questionType: 'MultipleChoice',
          explanation: 'La Réponse de l\'enseignant',
          choices: [1],
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 0,
        nbPeer: 0,
        isTeacher: true,
      }
    ],
    displayConfidenceTab: false
  }
}
export const TemporaryDataWithPeerAndConfidenceTab : Story = {
  args: {
    dataDefinitiveVote: [{ itemIndex: 1, value: 10, isCorrect: true }, { itemIndex: 2, value: 40, isCorrect: false }, { itemIndex: 3, value: 30,isCorrect: false }, { itemIndex: 4, value: 20,isCorrect: false }],
    dataTemporaryVote: [{ itemIndex: 1, value: 15, isCorrect: true }, { itemIndex: 2, value: 30, isCorrect: false }, { itemIndex: 3, value: 60,isCorrect: false }, { itemIndex: 4, value: 5,isCorrect: false }],
    dataConfidenceGoodAnswer: [20,20,20,40],
    dataConfidenceBadAnswer: [25,20,20,35],
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
          confidence: "",
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
          confidence: "",
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
          confidence: "",
        } satisfies MultipleChoiceResponse,
        grade: 2,
        nbPeer: 2,
        isTeacher: true,
      }
    ],
    displayConfidenceTab: true
  }
}
