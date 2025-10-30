export type Question = { title: string; statement: string }

export const SequenceStatus = {
  NOT_STARTED: 'NOT_STARTED',
  CLOSED: 'CLOSED',
  IN_PROGRESS: 'IN_PROGRESS',
} as const

export type SequenceStatus = (typeof SequenceStatus)[keyof typeof SequenceStatus]

export const Phase = {
  RESPONSE: 'RESPONSE',
  CONFRONTING_VIEWPOINT: 'CONFRONTING_VIEWPOINT',
  RESULTS: 'RESULTS',
} as const

export type Phase = (typeof Phase)[keyof typeof Phase]

export type SequenceState =
  | { sequenceStatus: typeof SequenceStatus.NOT_STARTED }
  | { sequenceStatus: typeof SequenceStatus.CLOSED }
  | {
      sequenceStatus: typeof SequenceStatus.IN_PROGRESS
      phases: Phase[]
    }

export type Sequence = {
  id: number
  question: Question
  state: SequenceState
}
