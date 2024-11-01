export abstract interface Response {
  id: number
  explanation: string
}

export interface ExclusiveChoiceResponse extends Response {
  choice: number
}

export interface MultipleChoiceResponse extends Response {
  choices: number[]
}

export interface OpenEndedResponse extends Response {}
