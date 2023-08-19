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

/**
 * @author John Tranier
 */
var elaastic = elaastic || {};

(function() {
    let baseUrl = '';

    elaastic.draxo = {
        initialize(url) {
            baseUrl = url;
        },

        loadReviews(event, responseId) {
            event.preventDefault()

            let elmBtnLoadReviews = event.target;
            let target = findElmReviewsContainer(
                findElmExplanationContainer(elmBtnLoadReviews)
            )

            $(target).html(buildHtmlLoader())

            $.ajax({
                url: baseUrl+'/'+responseId,
                method: 'GET',
                success: function(data) {
                    $(target).html(data);
                },
                error: function(error) {
                    let errorMessage = error.responseJSON ? error.responseJSON.error : error.responseText
                    $(target).html(buildHtmlError(errorMessage));
                }
            });
        }
    }

    function findElmExplanationContainer(elmBtnLoadReviews) {
        return elmBtnLoadReviews.closest('.explanation')
    }

    function findElmReviewsContainer(elmExplanationContainer) {
        return elmExplanationContainer.querySelector('.reviews-container')
    }

    function buildHtmlLoader() {
        return "<div class=\"ui active centered inline loader\"></div>"
    }

    function buildHtmlError(errorMessage) {
        return "<div class=\"ui negative message\">\n" +
            "  <p>" + errorMessage +
            "</p></div>"
    }
})()

