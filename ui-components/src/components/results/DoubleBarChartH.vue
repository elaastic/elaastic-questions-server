<script setup lang="ts">
import {ref, watchEffect} from 'vue'
import embed from 'vega-embed'
import type { VisualizationSpec } from 'vega-embed'
const props = defineProps({
  /**
   * The data of the left chart. It's an array of 2-uplet : the number of the answer and its value .
   */
  dataLeft: {
    type: Array<{ itemIndex: string, value: number }>,
    default: () => []
  },
  /**
   * The data of the right chart. It's an array of 2-uplet : the number of the answer and its value .
   */
  dataRight: {
    type: Array<{ itemIndex: string, value: number }>,
    default: () => []
  },
  /**
   * The title of the left chart.
   */
  titleLeft: {
    type: String,
    default: ""
  },
  /**
   * The title of the right chart.
   */
  titleRight: {
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
   * The width of the charts.
   */
  width: {
    type: Number,
    default: 500
  },
})

const vegaContainer = ref<HTMLDivElement | null>(null)

watchEffect(() => {

  const leftData = props.dataLeft.map(item => ({
    Choix: item.itemIndex,
    Value: -item.value,
  }))

  const rightData = props.dataRight.map(item => ({
    Choix: item.itemIndex,
    Value: item.value,
  }))

  const spec: VisualizationSpec = {
    $schema: 'https://vega.github.io/schema/vega-lite/v5.json',
    config: { view: { stroke: "transparent" } },
    hconcat: [
      // Left Chart
      {
        width: props.width / 2,
        title: props.titleLeft,
        data: { values: leftData },
        layer: [
          {
            mark: { type: 'bar', tooltip: true },
            encoding: {
              y: {
                field: 'Choix',
                type: 'nominal',
                axis: { title: null, labelAngle: 0, labels: false, ticks: false, domain: false },
                sort: null,
              },

              x: {
                field: 'Value',
                type: 'quantitative',
                scale: {
                  domain: [-100, 0],
                  nice: false
                },
                axis: {
                  title: props.xLabel,
                  values: [-100, -80, -60, -40, -20, 0],
                  labelExpr: "abs(toNumber(datum.value))"
                }
              },
              color: {
                field: 'Choix',
                type: 'nominal',
                scale: {
                  range: ['#B3E5FC', '#B71C1C', '#FFAB91', '#0D47A1','#BDBDBD']
                },
                legend: null
              }
            }
          },
        ]
      },

      // Middle label
      {
        width: 50,
        data: { values: props.dataLeft },
        mark: { type: 'text',fontSize: 14, dy: 4},
        encoding: {
          y: { field: 'itemIndex', type: 'nominal', axis: null, sort: null },
          text: { field: 'itemIndex', type: 'nominal' }
        }
      },

      // Right Chart
      {
        width: props.width / 2,
        title: props.titleRight,
        data: { values: rightData },
        layer: [
          {
            mark: { type: 'bar', tooltip: true },
            encoding: {
              y: {
                field: 'Choix',
                type: 'nominal',
                axis: { title: null, labelAngle: 0, labels: false, ticks: false, domain: false },
                sort: null,
              },

              x: {
                field: 'Value',
                type: 'quantitative',
                scale: {
                  domain: [0, 100],
                  nice: false
                },
                axis: {
                  title: props.xLabel,
                  values: [0, 20, 40, 60, 80, 100]
                }
              },
              color: {
                field: 'Choix',
                type: 'nominal',
                scale: {
                  range: ['#B3E5FC', '#B71C1C', '#FFAB91', '#0D47A1','#BDBDBD']
                },
                legend: null
              }
            }
          },
        ]
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

<style scoped>

</style>
