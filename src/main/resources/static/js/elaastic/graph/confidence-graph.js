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

var elaastic = elaastic || {};

elaastic.renderConfidenceGraph = function(elViewSelector, participationData, i18n) {
    var i18n = i18n || {
        phase: "Phase",
        participationRate: "Participation Rate"
    };

    let participationRatePhase1 = Math.round((participationData.nbParticipentsPhase1 / participationData.nbRegisteredUsers) * 100);
    let participationRatePhase2 = Math.round((participationData.nbParticipentsPhase2 / participationData.nbRegisteredUsers) * 100);

    var spec = {
        "$schema": "https://vega.github.io/schema/vega/v5.json",
        "width": 150,
        "height": 200,
        "padding": 5,

        "data": [
            {
                "name": "table",
                "values": [
                    {
                        "category": i18n.phase + " 1",
                        "amount": participationRatePhase1,
                        "label": participationRatePhase1 + "% (" + participationData.nbParticipentsPhase1 + ")"
                    },
                    {
                        "category": i18n.phase + " 2",
                        "amount": participationRatePhase2,
                        "label": participationRatePhase2 + "% (" + participationData.nbParticipentsPhase2 + ")"
                    }
                ]
            }
        ],

        "signals": [
            {
                "name": "tooltip",
                "value": {},
                "on": [
                    {"events": "rect:mouseover", "update": "datum"},
                    {"events": "rect:mouseout",  "update": "{}"}
                ]
            }
        ],

        "scales": [
            {
                "name": "xscale",
                "type": "band",
                "domain": {"data": "table", "field": "category"},
                "range": "width",
                "padding": 0.2,
                "round": true
            },
            {
                "name": "yscale",
                'domain': [0, 100],
                "nice": true,
                "range": "height"
            }
        ],

        "axes": [
            { "orient": "bottom", "scale": "xscale" },
            {
                "orient": "left",
                "scale": "yscale",
                "values": [0, 25, 50, 75, 100],
                "grid": true,
                "title": i18n.participationRate
            }
        ],

        "marks": [
            {
                "type": "rect",
                "name": "bars",
                "from": {"data": "table"},
                "encode": {
                    "enter": {
                        "x": {"scale": "xscale", "field": "category"},
                        "width": {"scale": "xscale", "band": 1},
                        "y": {"scale": "yscale", "field": "amount"},
                        "y2": {"scale": "yscale", "value": 0}
                    },
                    "update": {
                        "fill": {"value": "#4f7691"}
                    },
                    "hover": {
                        "fill": {"value": "#5d819a"}
                    }
                }
            },
            {
                "type": "text",
                "from": { "data": "bars" },
                "encode": {
                    "enter": {
                        "align": {"value": "center"},
                        "baseline": {"value": "bottom"},
                        "fill": {"value": "#333"},
                        "x": {"field": "x", "offset": { "field": "width", "mult": 0.5 }},
                        "y": {"field": "y", "offset": -2},
                        "text": { "field": "datum.label" }
                    }
                }
            }
        ]
    };

    function render(spec) {
        new vega.View(vega.parse(spec))
            .renderer('canvas')  // set renderer (canvas or svg)
            .initialize(elViewSelector) // initialize view within parent DOM container
            .hover()             // enable hover encode set processing
            .run();
    }
    render(spec);
};
