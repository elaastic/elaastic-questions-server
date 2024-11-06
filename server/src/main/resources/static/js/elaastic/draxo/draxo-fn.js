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

(function () {
    let draxoBaseUrl = ''

    elaastic.draxo = {
        initialize(url) {
            draxoBaseUrl = url
        },

        loadReviews(event, responseId) {
            event.preventDefault()

            let elmBtnLoadReviews = event.target.parentNode
            let target = findElmReviewsContainer(
                findElmExplanationContainer(elmBtnLoadReviews)
            )

            console.log(event.target)

            const hideReviewLink = $(elmBtnLoadReviews).find('.hide-review')
            const seeReviewLink = $(elmBtnLoadReviews).find('.see-review')

            if (hideReviewLink.is(':visible')) {
                hideReviewLink.hide()
                seeReviewLink.show()
            } else {
                hideReviewLink.show()
                seeReviewLink.hide()
            }

            if ($(target).html().length > 0) {
                // The reviews are already loaded
                if ($(target).is(':visible')) {
                    // The reviews are visible -> hide them
                    $(target).slideUp();
                } else {
                    // The reviews are hidden -> show them
                    $(target).slideDown();
                }
            } else {
                // The reviews are not loaded
                $(target).html(buildHtmlLoader())

                let data = {}
                if (new URLSearchParams(location.search).get('hideName')) {
                    data.hideName = true
                }

                $.ajax({
                    url: draxoBaseUrl + '/' + responseId,
                    method: 'GET',
                    data,
                    success: function (data) {
                        const targetElm = $(target)
                        const availableWidth = targetElm.width()

                        targetElm.html(data)
                        if (availableWidth < 900) {
                            $('.ui.mini.steps').addClass('vertical')
                        }
                    },
                    error: function (error) {
                        let errorMessage = error.responseJSON ? error.responseJSON.error : error.responseText
                        $(target).html(buildHtmlError(errorMessage))
                    }
                })
            }
        }
    }

    elaastic.chatGPT = {
        loadReview(event, responseId) {
            event.preventDefault()

            let elmBtnLoadReviews = event.target.parentNode
            let target = findElmReviewsContainer(
                findElmExplanationContainer(elmBtnLoadReviews)
            )
            console.log(event.target)

            const hideReviewLink = $(elmBtnLoadReviews).find('.hide-review')
            const seeReviewLink = $(elmBtnLoadReviews).find('.see-review')

            if (hideReviewLink.is(':visible')) {
                hideReviewLink.hide()
                seeReviewLink.show()
            } else {
                hideReviewLink.show()
                seeReviewLink.hide()
            }

            if ($(target).html().length > 0) {
                // The reviews are already loaded
                if ($(target).is(':visible')) {
                    // The reviews are visible -> hide them
                    $(target).slideUp();
                } else {
                    // The reviews are hidden -> show them
                    $(target).slideDown();
                }
            } else {
                // The reviews are not loaded
                $(target).html(buildHtmlLoader())

                let data = {}

                $.ajax({
                    url: '/chatGptEvaluation/' + responseId,
                    method: 'GET',
                    data,
                    success: function (data) {
                        const targetElm = $(target)
                        const availableWidth = targetElm.width()

                        targetElm.html(data)
                        if (availableWidth < 900) {
                            $('.ui.mini.steps').addClass('vertical')
                        }
                    },
                    error: function (error) {
                        let errorMessage = error.responseJSON ? error.responseJSON.error : error.responseText
                        $(target).html(buildHtmlError(errorMessage))
                    }
                })
            }
        }
    }

    function findElmExplanationContainer(elmBtnLoadReviews) {
        return elmBtnLoadReviews.closest('.explanation')
    }

    function findElmReviewsContainer(elmExplanationContainer) {
        return elmExplanationContainer.querySelector('.reviews-container')
    }

    function buildHtmlLoader() {
        return '<div class="ui active centered inline loader"></div>'
    }

    function buildHtmlError(errorMessage) {
        return '<div class="ui negative message">\n' +
            '  <p>' + errorMessage +
            '</p></div>'
    }
})()

