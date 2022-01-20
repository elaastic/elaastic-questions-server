/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

var elaastic = elaastic || {}

elaastic.renderConfidenceGraph = function (elViewSelector, choiceSpecification, results, userChoiceList, i18n) {

  i18n = i18n || {
    percentageOfVoters: 'percentage of voters',
    choice: 'choice',
    noAnswer: 'none'
  }

  if (!_.isEmpty(results)) {

    var graphData = []
    var resultsByCorrectness =
        { "✓" : {
            "NOT_CONFIDENT_AT_ALL": 0,
            "NOT_REALLY_CONFIDENT": 0,
            "CONFIDENT": 0,
            "TOTALLY_CONFIDENT": 0
          },
          "x" : {
            "NOT_CONFIDENT_AT_ALL": 0,
            "NOT_REALLY_CONFIDENT": 0,
            "CONFIDENT": 0,
            "TOTALLY_CONFIDENT": 0
          }
        }
    _.each(results,
        function(currentConfidenceDistribution, currentChoice){
          if(choiceSpecification.expectedChoiceList.includes(parseInt(currentChoice) + 1)){
            resultsByCorrectness["✓"].NOT_CONFIDENT_AT_ALL += isNaN(currentConfidenceDistribution.NOT_CONFIDENT_AT_ALL)? 0 : currentConfidenceDistribution.NOT_CONFIDENT_AT_ALL;
            resultsByCorrectness["✓"].NOT_REALLY_CONFIDENT += isNaN(currentConfidenceDistribution.NOT_REALLY_CONFIDENT)? 0 : currentConfidenceDistribution.NOT_REALLY_CONFIDENT;
            resultsByCorrectness["✓"].CONFIDENT += isNaN(currentConfidenceDistribution.CONFIDENT)? 0 : currentConfidenceDistribution.CONFIDENT
            resultsByCorrectness["✓"].TOTALLY_CONFIDENT += isNaN(currentConfidenceDistribution.TOTALLY_CONFIDENT)? 0 : currentConfidenceDistribution.TOTALLY_CONFIDENT;
          } else {
            resultsByCorrectness["x"].NOT_CONFIDENT_AT_ALL += isNaN(currentConfidenceDistribution.NOT_CONFIDENT_AT_ALL)? 0 : currentConfidenceDistribution.NOT_CONFIDENT_AT_ALL;
            resultsByCorrectness["x"].NOT_REALLY_CONFIDENT += isNaN(currentConfidenceDistribution.NOT_REALLY_CONFIDENT)? 0 : currentConfidenceDistribution.NOT_REALLY_CONFIDENT;
            resultsByCorrectness["x"].CONFIDENT += isNaN(currentConfidenceDistribution.CONFIDENT)? 0 : currentConfidenceDistribution.CONFIDENT
            resultsByCorrectness["x"].TOTALLY_CONFIDENT += isNaN(currentConfidenceDistribution.TOTALLY_CONFIDENT)? 0 : currentConfidenceDistribution.TOTALLY_CONFIDENT;
          }
        }
    )
    var nbCorrectItem = choiceSpecification.expectedChoiceList.length;
    var nbIncorrectItem = choiceSpecification.itemCount - choiceSpecification.expectedChoiceList.length;
    resultsByCorrectness["✓"].NOT_CONFIDENT_AT_ALL = resultsByCorrectness["✓"].NOT_CONFIDENT_AT_ALL/nbCorrectItem;
    resultsByCorrectness["✓"].NOT_REALLY_CONFIDENT = resultsByCorrectness["✓"].NOT_REALLY_CONFIDENT/nbCorrectItem;
    resultsByCorrectness["✓"].CONFIDENT = resultsByCorrectness["✓"].CONFIDENT/nbCorrectItem;
    resultsByCorrectness["✓"].TOTALLY_CONFIDENT = resultsByCorrectness["✓"].TOTALLY_CONFIDENT/nbCorrectItem;
    resultsByCorrectness["x"].NOT_CONFIDENT_AT_ALL = resultsByCorrectness["x"].NOT_CONFIDENT_AT_ALL/nbIncorrectItem;
    resultsByCorrectness["x"].NOT_REALLY_CONFIDENT = resultsByCorrectness["x"].NOT_REALLY_CONFIDENT/nbIncorrectItem;
    resultsByCorrectness["x"].CONFIDENT = resultsByCorrectness["x"].CONFIDENT/nbIncorrectItem;
    resultsByCorrectness["x"].TOTALLY_CONFIDENT = resultsByCorrectness["x"].TOTALLY_CONFIDENT/nbIncorrectItem;

    _.each(resultsByCorrectness, (currentConfidenceDistribution, currentChoice) =>
    _.each(currentConfidenceDistribution, (currentPerc, currentCF) => (
        graphData.push({"choice": currentChoice, "confidenceDegree": currentCF, "percentage": currentPerc/100})
    )
  )
  )

    var preferredWidth = choiceSpecification.itemCount * 75
    var vegaView = $(elViewSelector)

    function computeMaxWidth () { return vegaView.width() - 25 }

    function computeWidth () {
      return Math.min(preferredWidth, computeMaxWidth())
    }

    var color = [{"confidenceDegree":i18n.cf4, "color":"#1770ab"},
      {"confidenceDegree":i18n.cf3, "color":"#94c6da"},
      {"confidenceDegree":i18n.cf2, "color":"#f3a583"},
      {"confidenceDegree":i18n.cf1, "color":"#c30d24"}]
    var arrayColor = color.sort((a, b) => a.confidenceDegree > b.confidenceDegree && 1 || -1).map(x => x.color)
    graphData.forEach(function(i){switch(i.confidenceDegree){
      case "NOT_CONFIDENT_AT_ALL":
        i.confidenceDegree = i18n.cf1;
        break;
      case "NOT_REALLY_CONFIDENT":
        i.confidenceDegree = i18n.cf2;
        break;
      case "CONFIDENT":
        i.confidenceDegree = i18n.cf3;
        break;
      case "TOTALLY_CONFIDENT":
        i.confidenceDegree = i18n.cf4;
        break;
    }
    })
    var spec = {
      "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
      "data": { "values":graphData
      },
      "transform": [
        {"calculate": "datum.choice == 'x' ? 'incorrect' : 'correct'", "as": "correctness"}
      ],
      "params": [
        {
          "name": "highlight",
          "select": {"type": "point", "on": "mouseover"}
        }
      ],
      "spacing": 0,
      "hconcat": [{
        "transform": [{
          "filter": {"field": "correctness", "equal": "correct"}
        }],
        "title": i18n.correct,
        "mark": "bar",
        "encoding": {
          "y": {"field": "confidenceDegree",
            "type": "ordinal",
            "axis": null,
            "sort": [i18n.cf4, i18n.cf3, i18n.cf2, i18n.cf1]},
          "x": {
            "aggregate": "sum", "field": "percentage",
            "title": i18n.percentageOfVoters, "scale": {"domain": [0, 1]},
            "axis": {
              "labelExpr": "(isNaN(toNumber(datum.label[0]))? (slice(datum.label, 1)) : datum.label) *100"},
            "sort": "descending"
          },
          "color": {
            "field": "confidenceDegree",
            "scale": {"range": arrayColor},
            "legend": null
          },
          "tooltip": {"field": "percentage",
            "type": "nominal",
            "format": ".1%"},
          "opacity": {
            "condition": [
              {
                "param": "highlight",
                "empty": false,
                "value": 0.7
              }
            ],
            "value": 1
          }
        }
      }, {
        "width": 20,
        "view": {"stroke": null},
        "mark": {
          "type": "text",
          "align": "center"
        },
        "encoding": {
          "y": {"field": "confidenceDegree",
            "type": "ordinal",
            "axis": null,
            "sort": [i18n.cf4, i18n.cf3, i18n.cf2, i18n.cf1]},
          "text": {"field": "confidenceDegree", "type": "ordinal"}
        }
      }, {
        "transform": [{
          "filter": {"field": "correctness", "equal": "incorrect"}
        }],
        "title": i18n.incorrect,
        "mark": "bar",
        "encoding": {
          "y": {
            "field": "confidenceDegree", "title":null,
            "axis": null,
            "sort": [i18n.cf4, i18n.cf3, i18n.cf2, i18n.cf1]
          },
          "x": {
            "aggregate": "sum", "field": "percentage",
            "title": i18n.percentageOfVoters, "scale": {"domain": [0, 1]},
            "axis": {
              "labelExpr": "(isNaN(toNumber(datum.label[0]))? (slice(datum.label, 1)) : datum.label) *100"}
          },
          "color": {
            "field": "confidenceDegree",
            "legend": null
          },
          "tooltip": {"field": "percentage",
            "type": "nominal",
            "format": ".1%"},
          "opacity": {
            "condition": [
              {
                "param": "highlight",
                "empty": false,
                "value": 0.7
              }
            ],
            "value": 1
          }
        }
      }],
      "config": {
        "view": {"stroke": null},
        "axis": {"grid": true}
      }
    }

    vegaEmbed(elViewSelector, spec, {"actions": false});
  }
}

elaastic.renderPConfGraph = function (elViewSelector, choiceSpecification, results, userChoiceList, i18n) {

  i18n = i18n || {
    percentageOfVoters: 'percentage of voters',
    choice: 'choice',
    noAnswer: 'none'
  }

  if (!_.isEmpty(results)) {

    var graphData = []
    var resultsByCorrectness =
        { "✓" : {
            "NOT_CONFIDENT_AT_ALL": 0,
            "NOT_REALLY_CONFIDENT": 0,
            "CONFIDENT": 0,
            "TOTALLY_CONFIDENT": 0
          },
          "x" : {
            "NOT_CONFIDENT_AT_ALL": 0,
            "NOT_REALLY_CONFIDENT": 0,
            "CONFIDENT": 0,
            "TOTALLY_CONFIDENT": 0
          }
        }
    _.each(results,
        function(currentConfidenceDistribution, currentChoice){
          if(choiceSpecification.expectedChoiceList.includes(parseInt(currentChoice) + 1)){
            resultsByCorrectness["✓"].NOT_CONFIDENT_AT_ALL += currentConfidenceDistribution.NOT_CONFIDENT_AT_ALL;
            resultsByCorrectness["✓"].NOT_REALLY_CONFIDENT += currentConfidenceDistribution.NOT_REALLY_CONFIDENT;
            resultsByCorrectness["✓"].CONFIDENT += currentConfidenceDistribution.CONFIDENT
            resultsByCorrectness["✓"].TOTALLY_CONFIDENT += currentConfidenceDistribution.TOTALLY_CONFIDENT;
          } else {
            resultsByCorrectness["x"].NOT_CONFIDENT_AT_ALL += currentConfidenceDistribution.NOT_CONFIDENT_AT_ALL;
            resultsByCorrectness["x"].NOT_REALLY_CONFIDENT += currentConfidenceDistribution.NOT_REALLY_CONFIDENT;
            resultsByCorrectness["x"].CONFIDENT += currentConfidenceDistribution.CONFIDENT
            resultsByCorrectness["x"].TOTALLY_CONFIDENT += currentConfidenceDistribution.TOTALLY_CONFIDENT;
          }
        }
    )
    var nbCorrectItem = choiceSpecification.expectedChoiceList.length;
    var nbIncorrectItem = choiceSpecification.itemCount - choiceSpecification.expectedChoiceList.length;
    resultsByCorrectness["✓"].NOT_CONFIDENT_AT_ALL = resultsByCorrectness["✓"].NOT_CONFIDENT_AT_ALL/nbCorrectItem;
    resultsByCorrectness["✓"].NOT_REALLY_CONFIDENT = resultsByCorrectness["✓"].NOT_REALLY_CONFIDENT/nbCorrectItem;
    resultsByCorrectness["✓"].CONFIDENT = resultsByCorrectness["✓"].CONFIDENT/nbCorrectItem;
    resultsByCorrectness["✓"].TOTALLY_CONFIDENT = resultsByCorrectness["✓"].TOTALLY_CONFIDENT/nbCorrectItem;
    resultsByCorrectness["x"].NOT_CONFIDENT_AT_ALL = resultsByCorrectness["x"].NOT_CONFIDENT_AT_ALL/nbIncorrectItem;
    resultsByCorrectness["x"].NOT_REALLY_CONFIDENT = resultsByCorrectness["x"].NOT_REALLY_CONFIDENT/nbIncorrectItem;
    resultsByCorrectness["x"].CONFIDENT = resultsByCorrectness["x"].CONFIDENT/nbIncorrectItem;
    resultsByCorrectness["x"].TOTALLY_CONFIDENT = resultsByCorrectness["x"].TOTALLY_CONFIDENT/nbIncorrectItem;

    _.each(resultsByCorrectness, (currentConfidenceDistribution, currentChoice) =>
    _.each(currentConfidenceDistribution, (currentPerc, currentCF) => (
        graphData.push({"choice": currentChoice, "confidenceDegree": currentCF, "percentage": currentPerc/100})
    )
  )
  )
    var preferredWidth = choiceSpecification.itemCount * 75
    var vegaView = $(elViewSelector)

    function computeMaxWidth () { return vegaView.width() - 25 }

    function computeWidth () {
      return Math.min(preferredWidth, computeMaxWidth())
    }

    graphData.forEach(function(i){switch(i.confidenceDegree){
      case "NOT_CONFIDENT_AT_ALL":
        i.confidenceDegree = i18n.cf1;
        break;
      case "NOT_REALLY_CONFIDENT":
        i.confidenceDegree = i18n.cf2;
        break;
      case "CONFIDENT":
        i.confidenceDegree = i18n.cf3;
        break;
      case "TOTALLY_CONFIDENT":
        i.confidenceDegree = i18n.cf4;
        break;
    }
    })
    var spec = {
      "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
      "data": { "values":graphData
      },
      "width": computeWidth(),
      "layer" : [{
        "params": [
          {
            "name": "highlight",
            "select": {"type": "point", "on": "mouseover"}
          }
        ],
        "transform": [
          {
            /* this won't work if one of the confidence degrees contains a double quote (ex : Tout à fait d"accord) */
            "calculate": "if(datum.confidenceDegree === '" + i18n.cf1 + "',-2,0) + if(datum.confidenceDegree==='" + i18n.cf2 + "',-1,0) + if(datum.confidenceDegree ==='" + i18n.cf3 + "',1,0) + if(datum.confidenceDegree ==='" + i18n.cf4 + "',2,0)",
            "as": "q_order"
          },
          {
            /* this won't work if one of the confidence degrees contains a double quote (ex : Tout à fait d"accord) */
            "calculate": "if(datum.confidenceDegree === '" + i18n.cf2 + "' || datum.confidenceDegree === '" + i18n.cf1 + "', datum.percentage,0)",
            "as": "signed_percentage"
          },
          {"stack": "percentage", "as": ["v1", "v2"], "groupby": ["choice"]},
          {
            "joinaggregate": [
              {
                "field": "signed_percentage",
                "op": "sum",
                "as": "offset"
              }
            ],
            "groupby": ["choice"]
          },
          {"calculate": "datum.v1 - datum.offset", "as": "ny"},
          {"calculate": "datum.v2 - datum.offset", "as": "ny2"}
        ],
        "mark": {"type": "bar", "opacity": 1, "width": 43},
        "encoding": {
          "x": {
            "field": "ny",
            "type": "quantitative",
            "title": i18n.percentageOfVoters,
            "axis": {
              "labelExpr": "(isNaN(toNumber(datum.label[0]))? (slice(datum.label, 1)) : datum.label) *100"
            }
          },
          "tooltip": {
            "field": "percentage",
            "type": "nominal",
            "format": ".1%"
          },
          "x2": {"field": "ny2"},
          "y": {
            "field": "choice",
            "type": "nominal",
            "title": i18n.choice,
            "axis": {
              "labelAngle": 0,
              "offset": 5,
              "ticks": false,
              "domain": false,
              "labelColor": {
                "condition": {
                  "test": {
                    "field": "value",
                    "equal": "✓"
                  },
                  "value": "#016936"
                },
                "value": "#b03060"
              }
            },
            "scale": {
              //"domain": Array.from({length: choiceSpecification.itemCount}, (_, i) => i + 1)
              "domain": ["✓", "x"]
            }
          },
          "color": {
            "field": "confidenceDegree",
            "type": "nominal",
            "title": i18n.legend,
            "scale": {
              "domain": [i18n.cf4, i18n.cf3, i18n.cf2, i18n.cf1],
              "range": ["#1770ab", "#94c6da", "#f3a583", "#c30d24"],
              "type": "ordinal"
            }
          },
          "opacity": {
            "condition": [
              {
                "param": "highlight",
                "empty": false,
                "value": 0.7
              }
            ],
            "value": 1
          }
        }
      }, {
        "mark": { "type": "rule", "color": "black", "size": 2},
        "encoding": {
          "x": {
            "datum": 0
          }
        }
      }]
    }


    // signals: orientedSpec.signals,
    //
    // 'scales': orientedSpec.scales,
    //
    // 'axes': orientedSpec.axes,
    //
    // 'marks': orientedSpec.marks
    //}

    vegaEmbed(elViewSelector, spec, {"actions": false});
  }
}

elaastic.renderGraph = function (elViewSelector, choiceSpecification, results, userChoiceList, i18n) {
  i18n = i18n || {
    percentageOfVoters: 'percentage of voters',
    choice: 'choice',
    noAnswer: 'none'
  }

  if (!_.isEmpty(results)) {
    var nbItem = choiceSpecification.itemCount
    var correctIndexList = choiceSpecification.expectedChoiceList

    var graphData = []
    var hasSecondAttempt = !(typeof results[2] === 'undefined')

    _.each([1, 2],
        attempt => {
      _.times(
          nbItem,
          i => {
        var isCorrect = _.contains(correctIndexList, i + 1)
        results[attempt] && results[attempt][i + 1] != undefined && graphData.push({
          choice: i + 1,
          value: results[attempt][i + 1],
          isCorrect: isCorrect,
          color: isCorrect + '-' + attempt,
          attempt: attempt
        })
      }
  )

    if (results[2] && ((results[1] && results[1][0]) || (results[2][0]))) {
      graphData.push({
        choice: 'ø',
        value: results[attempt] ? results[attempt][0] : 0,
        noResponse: true,
        attempt: attempt
      })
    } else if (attempt === 1 && results[1] && results[1][0]) {
      graphData.push({
        choice: 'ø',
        value: results[1][0],
        noResponse: true,
        attempt: attempt
      })
    }

  }
  )

    var userChoiceListData = _.collect(
        userChoiceList,
        choice => {
      var isCorrect = _.contains(correctIndexList, choice)
      return {
        value: choice,
        isCorrect: isCorrect
      }
    }
  )

    var preferredWidth = nbItem * 75 * (hasSecondAttempt ? 1.75 : 1)
    var vegaView = $(elViewSelector)

    function computeMaxWidth () { return vegaView.width() - 25 }

    function computeWidth () {
      return Math.min(preferredWidth, computeMaxWidth())
    }

    var horizontalSpec = {
      signals: [
        {
          name: 'correctedWidth',
          update: 'width - 20'
        }
      ],
      'scales': [
        {
          'name': 'yscale',
          'type': 'band',
          'domain': {'data': 'table', 'field': 'choice'},
          'range': 'height',
          'padding': 0.3,
          'round': true
        },
        {
          'name': 'xscale',
          'domain': [0, 100],
          'nice': true,
          'range': [0, {signal: 'correctedWidth'}]
        },
        {
          name: 'correct-color',
          type: 'ordinal',
          domain: [1, 2],
          'range': ['#016936', '#a6d96a']
        },
        {
          name: 'incorrect-color',
          type: 'ordinal',
          domain: [1, 2],
          'range': ['#b03060', '#fdae61']
        },
        {
          name: 'noAnswer-color',
          type: 'ordinal',
          domain: [1, 2],
          'range': ['gold', '#fff3b2']
        }
      ],

      'axes': [
        {
          'orient': 'bottom',
          'scale': 'xscale',
          grid: true,
          values: [0, 25, 50, 75, 100],
          title: i18n.percentageOfVoters
        },
        {
          'orient': 'left',
          'scale': 'yscale',
          title: i18n.choice
        }
      ],

      'marks': [
        {
          type: 'symbol',
          from: {data: 'userChoiceList'},
          encode: {
            enter: {
              shape: {value: 'circle'},
              size: {value: 300},
              'stroke': [
                {
                  test: 'datum.isCorrect',
                  value: '#016936'
                },
                {
                  value: '#b03060'
                }
              ],
              fill: [
                {
                  test: 'datum.isCorrect',
                  value: '#016936'
                },
                {
                  value: '#b03060'
                }
              ],
              fillOpacity: {value: 0.25},
              'y': {'scale': 'yscale', 'field': 'value', band: 0.5},
              'x': {'scale': 'xscale', 'value': 0, offset: -11},
              zindex: {value: 1}
            }
          }
        },
        {
          type: 'group',
          from: {
            facet: {
              data: 'table',
              name: 'facet',
              groupby: 'choice'
            }
          },
          encode: {
            enter: {
              y: {
                scale: 'yscale',
                field: 'choice'
              }
            }
          },
          signals: [
            {
              name: 'height',
              update: 'bandwidth(\'yscale\')'
            },
            {
              'name': 'tooltip',
              'value': {},
              'on': [
                {'events': 'rect:mouseover', 'update': 'datum'},
                {'events': 'rect:mouseout', 'update': '{}'}
              ]
            }
          ],
          scales: [
            {
              name: 'pos',
              type: 'band',
              range: 'height',
              domain: {
                data: 'facet',
                field: 'attempt'
              }
            }
          ],
          marks: [
            {
              'type': 'rect',
              'from': {'data': 'facet'},
              'encode': {
                'enter': {
                  'y': {'scale': 'pos', 'field': 'attempt', offset: 1},
                  'height': {'scale': 'pos', 'band': 1, offset: -2},
                  'x': {'scale': 'xscale', 'field': 'value'},
                  'x2': {'scale': 'xscale', 'value': 0}
                },
                'update': {
                  'fill':
                      [
                        {
                          test: 'datum.noResponse',
                          scale: 'noAnswer-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          test: 'datum.isCorrect',
                          scale: 'correct-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          test: '!datum.isCorrect',
                          scale: 'incorrect-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          value: 'blue'
                        }
                      ]
                }
              }
            },
            {
              'type': 'rect',
              'from': {'data': 'facet'},
              'encode': {
                'enter': {
                  'y': {'scale': 'pos', 'field': 'attempt', offset: 1},
                  'height': {'scale': 'pos', 'band': 1, offset: -2},
                  'x': {'scale': 'xscale', 'value': 0, offset: -2},
                  'x2': {'scale': 'xscale', 'value': 0, offset: -5},
                  opacity: {value: 0.75},
                  'fill':
                      [
                        {
                          test: 'datum.noResponse',
                          scale: 'noAnswer-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          test: 'datum.isCorrect',
                          scale: 'correct-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          test: '!datum.isCorrect',
                          scale: 'incorrect-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          value: 'yellow'
                        }
                      ]
                }
              }
            },
            {
              'type': 'text',
              'from': {'data': 'facet'},
              'encode': {
                'enter': {
                  'align': {'value': 'left'},
                  'baseline': {'value': 'middle'},
                  'fill': {'value': 'black'},
                  fontWeight: {value: 'bold'},
                  y: {'scale': 'pos', 'field': 'attempt', 'band': 0.5},
                  x: {'scale': 'xscale', 'field': 'value', 'offset': 2},
                  text: {field: 'labelValue'}
                }
              }
            }
          ]
        }
      ]
    }

    var verticalSpec = {
      'scales': [
        {
          'name': 'xscale',
          'type': 'band',
          'domain': {'data': 'table', 'field': 'choice'},
          'range': 'width',
          'padding': 0.3,
          'round': true
        },
        {
          'name': 'yscale',
          'domain': [0, 100],
          'nice': true,
          'range': 'height'
        },
        {
          name: 'correct-color',
          type: 'ordinal',
          domain: [1, 2],
          'range': ['#016936', '#a6d96a']
        },
        {
          name: 'incorrect-color',
          type: 'ordinal',
          domain: [1, 2],
          'range': ['#b03060', '#fdae61']
        },
        {
          name: 'noAnswer-color',
          type: 'ordinal',
          domain: [1, 2],
          'range': ['gold', '#fff3b2']
        }
      ],

      'axes': [
        {
          'orient': 'bottom',
          'scale': 'xscale',
          title: i18n.choice
        },
        {
          'orient': 'left',
          'scale': 'yscale',
          grid: true,
          values: [0, 25, 50, 75, 100],
          title: i18n.percentageOfVoters
        }
      ],

      'marks': [
        {
          type: 'symbol',
          from: {data: 'userChoiceList'},
          encode: {
            enter: {
              shape: {value: 'circle'},
              size: {value: 300},
              'stroke': [
                {
                  test: 'datum.isCorrect',
                  value: '#016936'
                },
                {
                  value: '#b03060'
                }
              ],
              fill: [
                {
                  test: 'datum.isCorrect',
                  value: '#016936'
                },
                {
                  value: '#b03060'
                }
              ],
              fillOpacity: {value: 0.25},
              'x': {'scale': 'xscale', 'field': 'value', band: 0.5},
              'y': {'scale': 'yscale', 'value': 0, offset: 12},
              zindex: {value: 1}
            }
          }
        },
        {
          type: 'group',
          from: {
            facet: {
              data: 'table',
              name: 'facet',
              groupby: 'choice'
            }
          },
          encode: {
            enter: {
              x: {
                scale: 'xscale',
                field: 'choice'
              }
            }
          },
          signals: [
            {
              name: 'width',
              update: 'bandwidth(\'xscale\')'
            },
            {
              'name': 'tooltip',
              'value': {},
              'on': [
                {'events': 'rect:mouseover', 'update': 'datum'},
                {'events': 'rect:mouseout', 'update': '{}'}
              ]
            }
          ],
          scales: [
            {
              name: 'pos',
              type: 'band',
              range: 'width',
              domain: {
                data: 'facet',
                field: 'attempt'
              }
            }
          ],
          marks: [
            {
              'type': 'rect',
              'from': {'data': 'facet'},
              'encode': {
                'enter': {
                  'x': {'scale': 'pos', 'field': 'attempt', offset: 3},
                  'width': {'scale': 'pos', 'band': 1, offset: -6},
                  'y': {'scale': 'yscale', 'field': 'value'},
                  'y2': {'scale': 'yscale', 'value': 0}
                },
                'update': {
                  'fill':
                      [
                        {
                          test: 'datum.noResponse',
                          scale: 'noAnswer-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          test: 'datum.isCorrect',
                          scale: 'correct-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          test: '!datum.isCorrect',
                          scale: 'incorrect-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          value: 'yellow'
                        }
                      ]
                }
              }
            },
            {
              'type': 'rect',
              'from': {'data': 'facet'},
              'encode': {
                'enter': {
                  'x': {'scale': 'pos', 'field': 'attempt', offset: 3},
                  'width': {'scale': 'pos', 'band': 1, offset: -6},
                  'y': {'scale': 'yscale', 'value': 0, offset: 2},
                  'y2': {'scale': 'yscale', 'value': 0, offset: 5},
                  zindex: {value: 0},
                  opacity: {value: 0.75},
                  'fill':
                      [
                        {
                          test: 'datum.noResponse',
                          scale: 'noAnswer-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          test: 'datum.isCorrect',
                          scale: 'correct-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          test: '!datum.isCorrect',
                          scale: 'incorrect-color',
                          data: 'table',
                          'field': 'colorIndex'
                        },
                        {
                          value: 'yellow'
                        }
                      ]

                }
              }
            },
            {
              'type': 'text',
              'from': {'data': 'facet'},
              'encode': {
                'enter': {
                  'align': {'value': 'center'},
                  'baseline': {'value': 'bottom'},
                  'fill': {'value': '#333'},
                  fontWeight: {value: 'bold'},

                  'x': {'scale': 'pos', 'field': 'attempt', 'band': 0.5},
                  'y': {'scale': 'yscale', 'field': 'value', 'offset': -2},
                  'text': {'field': 'labelValue'},
                }
              }
            }
          ]
        }
      ]
    }

    var orientedSpec =
        (nbItem > 5 || computeMaxWidth() < 300) ?
            horizontalSpec : verticalSpec

    var spec = {
      '$schema': 'https://vega.github.io/schema/vega/v4.json',
      'width': computeWidth(),
      'height': 200,
      'padding': 5,

      'data': [
        {
          'name': 'table',
          'values': graphData,
          transform: [
            {
              type: 'formula',
              as: 'labelValue',
              expr: 'round(datum.value) + \'%\''
            },
            {
              type: 'joinaggregate',
              fields: ['attempt'],
              ops: ['max'],
              as: ['nbAttempt']
            },
            {
              type: 'formula',
              as: 'colorIndex',
              expr: 'datum.nbAttempt - datum.attempt + 1'
            }
          ]
        },
        {
          name: 'userChoiceList',
          values: userChoiceListData
        }
      ],

      signals: orientedSpec.signals,

      'scales': orientedSpec.scales,

      'axes': orientedSpec.axes,

      'marks': orientedSpec.marks
    }
    var view
    render(spec)
    function render (spec) {
      view = new vega.View(vega.parse(spec))
          .renderer('canvas')  // set renderer (canvas or svg)
          .initialize(elViewSelector) // initialize view within parent DOM container
          .hover()             // enable hover encode set processing
          .run()

      $(window).on('resize', function () {
        view.signal('width', computeWidth()).run('enter')
      })
    }
  }
}


elaastic.renderEvaluationGraph = function (elViewSelector, choiceSpecification, results, userChoiceList, i18n) {
  i18n = i18n || {
    percentageOfVoters: 'percentage of voters',
    choice: 'choice',
    noAnswer: 'none'
  }

  if (!_.isEmpty(results)) {
    var graphData = []
    var resultsByCorrectness =
        { "✓" : {
            "0": 0,
            "1": 0,
            "2": 0,
            "3": 0,
            "4": 0
          },
          "x" : {
            "0": 0,
            "1": 0,
            "2": 0,
            "3": 0,
            "4": 0
          }
        }
    _.each(results,
        function(currentEvaluationDistribution, currentChoice){
          if(choiceSpecification.expectedChoiceList.includes(parseInt(currentChoice) + 1)){
            resultsByCorrectness["✓"][0] += isNaN(currentEvaluationDistribution[0]) ? 0 : currentEvaluationDistribution[0];
            resultsByCorrectness["✓"][1] += isNaN(currentEvaluationDistribution[1]) ? 0 : currentEvaluationDistribution[1];
            resultsByCorrectness["✓"][2] += isNaN(currentEvaluationDistribution[2]) ? 0 : currentEvaluationDistribution[2];
            resultsByCorrectness["✓"][3] += isNaN(currentEvaluationDistribution[3]) ? 0 : currentEvaluationDistribution[3];
            resultsByCorrectness["✓"][4] += isNaN(currentEvaluationDistribution[4]) ? 0 : currentEvaluationDistribution[4];
          } else {
            resultsByCorrectness["x"][0] += isNaN(currentEvaluationDistribution[0]) ? 0 : currentEvaluationDistribution[0];
            resultsByCorrectness["x"][1] += isNaN(currentEvaluationDistribution[1]) ? 0 : currentEvaluationDistribution[1];
            resultsByCorrectness["x"][2] += isNaN(currentEvaluationDistribution[2]) ? 0 : currentEvaluationDistribution[2];
            resultsByCorrectness["x"][3] += isNaN(currentEvaluationDistribution[3]) ? 0 : currentEvaluationDistribution[3];
            resultsByCorrectness["x"][4] += isNaN(currentEvaluationDistribution[4]) ? 0 : currentEvaluationDistribution[4];
          }
        }
    )
    var nbCorrectItem = choiceSpecification.expectedChoiceList.length;
    var nbIncorrectItem = choiceSpecification.itemCount - choiceSpecification.expectedChoiceList.length;
    resultsByCorrectness["✓"][0] = resultsByCorrectness["✓"][0]/nbCorrectItem;
    resultsByCorrectness["✓"][1] = resultsByCorrectness["✓"][1]/nbCorrectItem;
    resultsByCorrectness["✓"][2] = resultsByCorrectness["✓"][2]/nbCorrectItem;
    resultsByCorrectness["✓"][3] = resultsByCorrectness["✓"][3]/nbCorrectItem;
    resultsByCorrectness["✓"][4] = resultsByCorrectness["✓"][4]/nbCorrectItem;
    resultsByCorrectness["x"][0] = resultsByCorrectness["x"][0]/nbIncorrectItem;
    resultsByCorrectness["x"][1] = resultsByCorrectness["x"][1]/nbIncorrectItem;
    resultsByCorrectness["x"][2] = resultsByCorrectness["x"][2]/nbIncorrectItem;
    resultsByCorrectness["x"][3] = resultsByCorrectness["x"][3]/nbIncorrectItem;
    resultsByCorrectness["x"][4] = resultsByCorrectness["x"][4]/nbIncorrectItem;

    _.each(resultsByCorrectness, (currentEvaluation, currentChoice) =>
    _.each(currentEvaluation, (currentPerc, currentE) => (
        graphData.push({"choice": currentChoice, "evaluation": currentE, "percentage": currentPerc/100})
    )
  )
  )
    var color = [{"evaluation":i18n.e5, "color":"#1770ab"},
      {"evaluation":i18n.e4, "color":"#94c6da"},
      {"evaluation":i18n.e3, "color":"#cccccc"},
      {"evaluation":i18n.e2, "color":"#f3a583"},
      {"evaluation":i18n.e1, "color":"#c30d24"}]
    var arrayColor = color.sort((a, b) => a.evaluation > b.evaluation && 1 || -1).map(x => x.color)
    var preferredWidth = choiceSpecification.itemCount * 75
    var vegaView = $(elViewSelector)

    function computeMaxWidth () { return vegaView.width() - 25 }

    function computeWidth () {
      return Math.min(preferredWidth, computeMaxWidth())
    }


    graphData.forEach(function(i){switch(i.evaluation){
      case "0":
        i.evaluation = i18n.e1;
        break;
      case "1":
        i.evaluation = i18n.e2;
        break;
      case "2":
        i.evaluation = i18n.e3;
        break;
      case "3":
        i.evaluation = i18n.e4;
        break;
      case "4":
        i.evaluation = i18n.e5;
        break;
    }
    })

    var spec = {
      "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
      "data": { "values":graphData
      },
      "transform": [
        {"calculate": "datum.choice == 'x' ? 'incorrect' : 'correct'", "as": "correctness"}
      ],
      "params": [
        {
          "name": "highlight",
          "select": {"type": "point", "on": "mouseover"}
        }
      ],
      "spacing": 0,
      "hconcat": [{
        "transform": [{
          "filter": {"field": "correctness", "equal": "correct"}
        }],
        "title": i18n.correct,
        "mark": "bar",
        "encoding": {
          "y": {
            "field": "evaluation",
            "axis": null,
            "type": "ordinal",
            "sort": [i18n.e5, i18n.e4, i18n.e3, i18n.e2, i18n.e1]
          },
          "x": {
            "aggregate": "sum",
            "field": "percentage",
            "title": i18n.percentageOfVoters, "scale": {"domain": [0, 1]},
            "axis": {
              "labelExpr": "(isNaN(toNumber(datum.label[0]))? (slice(datum.label, 1)) : datum.label) *100"},
            "sort": "descending"
          },
          "color": {
            "field": "evaluation",
            "scale": {"range": arrayColor},
            "legend": null
          },
          "tooltip": {"field": "percentage",
            "type": "nominal",
            "format": ".1%"},
          "opacity": {
            "condition": [
              {
                "param": "highlight",
                "empty": false,
                "value": 0.7
              }
            ],
            "value": 1
          }
        }
      }, {
        "width": 20,
        "view": {"stroke": null},
        "mark": {
          "type": "text",
          "align": "center"
        },
        "encoding": {
          "y": {"field": "evaluation",
            "type": "ordinal",
            "axis": null,
            "sort": [i18n.e5, i18n.e4, i18n.e3, i18n.e2, i18n.e1]},
          "text": {"field": "evaluation", "type": "ordinal"}
        }
      }, {
        "transform": [{
          "filter": {"field": "correctness", "equal": "incorrect"}
        }],
        "title": i18n.incorrect,
        "mark": "bar",
        "encoding": {
          "y": {
            "field": "evaluation",
            "title":null,
            "axis": null,
            "sort": [i18n.e5, i18n.e4, i18n.e3, i18n.e2, i18n.e1]
          },
          "x": {
            "aggregate": "sum", "field": "percentage",
            "title": i18n.percentageOfVoters, "scale": {"domain": [0, 1]},
            "axis": {
              "labelExpr": "(isNaN(toNumber(datum.label[0]))? (slice(datum.label, 1)) : datum.label) *100"}
          },

          "color": {
            "field": "evaluation",
            "legend": null
          },
          "tooltip": {"field": "percentage",
            "type": "nominal",
            "format": ".1%"},
          "opacity": {
            "condition": [
              {
                "param": "highlight",
                "empty": false,
                "value": 0.7
              }
            ],
            "value": 1
          }
        }
      }],
      "config": {
        "view": {"stroke": null},
        "axis": {"grid": true}
      }
    }

    vegaEmbed(elViewSelector, spec, {"actions": false});
  }
}