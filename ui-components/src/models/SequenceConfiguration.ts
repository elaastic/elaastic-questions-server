/**
 * This file defines the type relates to the configuration of a sequence
 */
import type { ResponsePhaseConfig } from '@/components/sequence/configuration/phase/ResponsePhaseConfiguration.vue'
import type { ConfrontingViewPhaseConfig } from '@/components/sequence/configuration/phase/ConfrontingViewPhaseConfiguration.vue'
import type { ResultPhaseConfig } from '@/components/sequence/configuration/phase/ResultPhaseConfiguration.vue'

export type ExecutionContext = 'FaceToFace' | 'Distance' | 'Blended'

export interface SequenceConfiguration {
  executionContext: ExecutionContext
  responsePhaseConfig?: ResponsePhaseConfig | undefined
  confrontingViewsPhaseConfig: ConfrontingViewPhaseConfig | undefined
  resultPhaseConfig?: ResultPhaseConfig | undefined
}
