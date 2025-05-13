<script setup lang="ts">
import {ref, watchEffect, computed, type PropType} from 'vue'
import embed from 'vega-embed'
import type { VisualizationSpec } from 'vega-embed'
const props = defineProps({
  data: {
    type: Array<{ choix: number, value: number, isCorrect: Boolean }>,
    default: () => []
  },
  title: {
    type: String,
    default: ""
  },
  xLabel: {
    type: String,
    default: ""
  },
  yLabel: {
    type: String,
    default: ""
  },
  width: {
    type: Number,
    default: 500
  },
})

const vegaContainer = ref<HTMLDivElement | null>(null)

watchEffect(() => {
  if (!props.data || !Array.isArray(props.data)) return

  const spec: VisualizationSpec = {
    $schema: 'https://vega.github.io/schema/vega-lite/v5.json',
    width: props.width,
    title: props.title,
    data: {
      values: props.data.map(item => ({
        "Choix": item.choix,
        "Pourcentage des votants": item.value,
        "Pourcentage avec %": item.value + '%',
        isCorrect: item.isCorrect
      }))
    },
    encoding: {
      x: { field: 'Choix', type: 'nominal', axis: { title: props.xLabel } },
      y: { field: 'Pourcentage des votants', type: 'quantitative', axis: { title: props.yLabel } }
    },
    layer: [
      {
        mark: {
          type: 'bar',
          tooltip: true
        },
        encoding: {
          color: {
            condition: {
              test: "datum.isCorrect == true",
              value: "#10b981"
            },
            value: "#ef4444"
          }
        }
      },
      {
        mark: {
          type: 'text',
          align: 'center',
          baseline: 'bottom',
          dy: -5,
          fontSize: 13,
          fontWeight: 'bold'
        },
        encoding: {
          text: { field: 'Pourcentage avec %', type: 'nominal' },

        }
      }
    ]
  }



  if (vegaContainer.value) {
    embed(vegaContainer.value, spec, { actions: false })
  }
})
</script>

<template>
  <div ref="vegaContainer"></div>
</template>

