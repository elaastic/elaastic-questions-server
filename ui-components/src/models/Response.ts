type QuestionType = 'OpenEnded' | 'ExclusiveChoice' | 'MultipleChoice'

export interface Response {
  id: number
  questionType: QuestionType
  explanation: string
}

export interface ExclusiveChoiceResponse extends Response {
  questionType: 'ExclusiveChoice'
  choice: number
}

export interface MultipleChoiceResponse extends Response {
  questionType: 'MultipleChoice'
  choices: number[]
}

export interface OpenEndedResponse extends Response {
  questionType: 'OpenEnded'
}

export type AnyResponse = OpenEndedResponse | MultipleChoiceResponse | ExclusiveChoiceResponse

export const getChoices = (response: AnyResponse) => {
  switch (response.questionType) {
    case 'ExclusiveChoice':
      return [response.choice]

    case 'MultipleChoice':
      return response.choices

    case 'OpenEnded':
      return undefined
  }
}
