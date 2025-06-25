/**
 * This file defines the types relates to responses (provided by a
 * learner to a question)
 * @author John Tranier
 */

export type QuestionType = 'OpenEnded' | 'ExclusiveChoice' | 'MultipleChoice'
export type ConfidenceDegree = 'NotConfidentAtAll' | 'NotReallyConfident' | 'Confident' | 'TotallyConfident'

export interface Response {
  id: number
  questionType: QuestionType
  explanation: string,
  confidence: ConfidenceDegree
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

