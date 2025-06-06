<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import embed from 'vega-embed'
import type { VisualizationSpec } from 'vega-embed'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n({ useScope: 'local' })
const props = defineProps({
  /**
   * The chart's temporary data. It's an array of 3-uplet : the number of the answer, its value and a boolean to say if it's the good answer.
   */
  temporaryData: {
    type: Array<{ itemIndex: number, value: number, isCorrect: boolean }>,
    default: () => []
  },
  /**
   * The chart's definitive data. It's an array of 3-uplet : the number of the answer, its value and a boolean to say if it's the good answer.
   */
  definitiveData: {
    type: Array<{ itemIndex: number, value: number, isCorrect: boolean }>,
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
  if (!props.definitiveData || !Array.isArray(props.definitiveData)) return

  const choiceLabel = t('chart.choice')
  const percentageLabel = t('chart.voterPercentage')
  const percentageWithUnitLabel = t('chart.voterPercentageWithUnit')

  const valuesFirstAttempt = props.temporaryData.map(item => ({
    [choiceLabel]: item.itemIndex,
    [percentageLabel]: item.value,
    [percentageWithUnitLabel]: item.value + '%',
    isCorrect: item.isCorrect,
    attempt: 'Tentative 1',
    color: item.isCorrect ? "correct1" : "wrong1"
  }))
  const valuesSecondAttempt = props.definitiveData.map(item => ({
    [choiceLabel]: item.itemIndex,
    [percentageLabel]: item.value,
    [percentageWithUnitLabel]: item.value + '%',
    isCorrect: item.isCorrect,
    attempt: "Tentative 2",
    color: item.isCorrect ? "correct2" : "wrong2"
  }))

  const values = valuesFirstAttempt.concat(valuesSecondAttempt)
  const spec: VisualizationSpec = {
    $schema: 'https://vega.github.io/schema/vega-lite/v5.json',
    width: props.width,
    title: props.title,
    data: {values},
    encoding: {
      x: { field: choiceLabel, type: 'nominal', axis: { title: props.xLabel, labelAngle: 0 } },
      xOffset: {
        field: 'attempt'
      },
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
          tooltip: true,
          size: 40
        },
        encoding: {
          color: {
            field: 'color',
            type: 'nominal',
            scale: {
              domain: ['correct1', 'correct2', "wrong1", "wrong2"],
              range: ['#9CCC65', '#1B5E20',"#F9A825", "#AD1457"]
            },
            legend: null
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

<i18n>
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
