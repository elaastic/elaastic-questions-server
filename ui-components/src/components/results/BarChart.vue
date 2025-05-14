<script setup lang="ts">
import {ref, watchEffect} from 'vue'
import embed from 'vega-embed'
import type { VisualizationSpec } from 'vega-embed'
const props = defineProps({
  /**
   * The chart's data. It's an array of 3-uplet : the number of the answer, its value and a boolean to say if it's the good answer.
   */
  data: {
    type: Array<{ choix: number, value: number, isCorrect: boolean }>,
    default: () => []
  },
  /**
   * The title of the chart.
   */
  title: {
    type: String,
    default: ""
  },
  /**
   * The title of x axis.
   */
  xLabel: {
    type: String,
    default: ""
  },
  /**
   * The title of y axis.
   */
  yLabel: {
    type: String,
    default: ""
  },
  /**
   * The width of the chart.
   */
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
      x: { field: 'Choix', type: 'nominal', axis: { title: props.xLabel },  },
      y: {
        field: 'Pourcentage des votants',
        type: 'quantitative',
        axis: { title: props.yLabel, values: [0, 25, 50, 75, 100] },
        scale: {
          domain: [0,100],
          nice: false
        },
      }
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
              value: "#1B5E20"
            },
            value: "#AD1457"
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

