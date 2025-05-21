<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import embed from 'vega-embed'
import type { VisualizationSpec } from 'vega-embed'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n({ useScope: 'local' })
const props = defineProps({
  /**
   * The chart's data. It's an array of 3-uplet : the number of the answer, its value and a boolean to say if it's the good answer.
   */
  data: {
    type: Array<{ itemIndex: number, value: number, isCorrect: boolean }>,
    default: () => []
  },
  /**
   * The title of the chart.
   */
  title: {
    type: String,
  },
  /**
   * The title of x axis.
   */
  xLabel: {
    type: String,
  },
  /**
   * The title of y axis.
   */
  yLabel: {
    type: String,
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
  locale.value
  if (!props.data || !Array.isArray(props.data)) return

  const choiceLabel = t('chart.choice')
  const percentageLabel = t('chart.voterPercentage')
  const percentageWithUnitLabel = t('chart.voterPercentageWithUnit')

  const values = props.data.map(item => ({
    [choiceLabel]: item.itemIndex,
    [percentageLabel]: item.value,
    [percentageWithUnitLabel]: item.value + '%',
    isCorrect: item.isCorrect
  }))

  const spec: VisualizationSpec = {
    $schema: 'https://vega.github.io/schema/vega-lite/v5.json',
    width: props.width,
    title: props.title,
    data: { values },
    encoding: {
      x: { field: choiceLabel, type: 'nominal', axis: { title: props.xLabel, labelAngle: 0 } },
      y: {
        field: percentageLabel,
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
          text: { field: percentageWithUnitLabel, type: 'nominal' }
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

<i18n lang="json">
{
  "en": {
    "chart": {
      "choice": "Choice",
      "voterPercentage": "Voter Percentage",
      "voterPercentageWithUnit": "Voter Percentage (%)"
    }
  },
  "fr": {
    "chart": {
      "choice": "Choix",
      "voterPercentage": "Pourcentage des votants",
      "voterPercentageWithUnit": "Pourcentage (%)"
    }
  }
}
</i18n>
