(function () {
    new Vue({
        el: '#draxo-form-app',
        data() {
            return {
                criteriaValuation: serverData.criteriaValuation,
                currentCriteria: serverData.currentCriteria,
                criteriaList: serverData.criteriaList,
                messages: serverData.messages,
                scales: serverData.scales,
                submitted: false,
                showGenericError: false,
                loading: false,
            }
        },
        computed: {
            explanation() {
                if (!this.criteriaValuation[this.currentCriteria]) {
                    return ""
                }

                return this.criteriaValuation[this.currentCriteria].explanation || ""
            }
        },
        methods: {
            hasBeenAnswered(criteria) {
                return this.criteriaValuation[criteria];
            },

            selectOption(criteria, optionId) {
                if (isNegativeOption(optionId, this.scales[criteria])) {
                    let currentExplanation = this.criteriaValuation[criteria]?.explanation;

                    this.$set(this.criteriaValuation, criteria, {
                        criteria,
                        optionId,
                        explanation: currentExplanation || ""
                    })
                } else {
                    this.$set(this.criteriaValuation, criteria, {criteria, optionId})
                }
            },
            updateExplanation(event) {
                if (!this.criteriaValuation[this.currentCriteria]) {
                    return // should not happen
                }

                this.criteriaValuation[this.currentCriteria].explanation = event.target.value;
            },
            removeUnusedCriteria() {
                let currentCriteriaIndex = this.criteriaList.indexOf(this.currentCriteria);
                for (let i = currentCriteriaIndex + 1; i < this.criteriaList.length; i++) {
                    this.$set(this.criteriaValuation, this.criteriaList[i], null);
                }

            },
            submitEvaluation() {
                this.removeUnusedCriteria(this.criteriaValuation)

                let csrfToken = $("meta[name='_csrf']").attr("content");
                let csrfHeader = $("meta[name='_csrf_header']").attr("content");

                this.showGenericError = false;
                this.loading = true;
                fetch(serverData.submitUrl, {
                    method: 'POST',
                    credentials: 'include',
                    headers: {
                        [csrfHeader]: csrfToken,
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(toServerFormat(this.criteriaValuation, this.criteriaList))
                })
                    .then(response => {
                            if (!response.ok) {
                                throw new Error(`Erreur HTTP: ${response.statusText}`);
                            }
                            this.loading = false;
                            return response.json()
                        }
                    )
                    .then(data => {
                        this.criteriaValuation = data.criteriaValuation;
                        this.currentCriteria = data.currentCriteria;
                        this.submitted = true;

                        eventBus.$emit("evaluationSubmitted")
                    })
                    .catch(error => {
                        this.loading = false;
                        this.showGenericError = true;
                        console.info(error)
                    })

            },
        },
        components: {
            'draxo-form': {
                props: [
                    'currentCriteria',
                    'criteriaValuation',
                    'criteriaList',
                    'messages',
                    'scales',
                    'explanation',
                    'readOnly',
                    'loading'
                ],
                computed: {
                    firstCriteria() {
                        return this.criteriaList[0]
                    },
                    lastCriteria() {
                        return this.criteriaList[this.criteriaList.length - 1]
                    },
                    canGoPrevious() {
                        return this.currentCriteria !== this.firstCriteria
                    },
                    canGoNext() {
                        return this.currentCriteria !== this.lastCriteria &&
                            this.positiveOptionSelected
                    },
                    canSubmit() {
                        return this.criteriaValuation[this.currentCriteria] &&
                            (this.currentCriteria === this.lastCriteria ||
                                !this.positiveOptionSelected
                            )

                    },
                    awaitedExplanation() {
                        return this.negativeOptionSelected
                    },
                    currentScale() {
                        return this.scales[this.currentCriteria]
                    },
                    negativeOptionSelected() {
                        let currentOption = this.criteriaValuation[this.currentCriteria]
                        return currentOption &&
                            isNegativeOption(currentOption.optionId, this.currentScale)
                    },
                    positiveOptionSelected() {
                        let currentOption = this.criteriaValuation[this.currentCriteria]

                        return currentOption &&
                            isPositiveOption(currentOption.optionId, this.currentScale)
                    }
                },
                methods: {
                    nextCriteria() {
                        this.$emit('select-criteria', nextCriteria(this.currentCriteria, this.criteriaList))
                        this.currentCriteria = nextCriteria(this.currentCriteria, this.criteriaList)
                    },
                    previousCriteria() {
                        this.$emit('select-criteria', previousCriteria(this.currentCriteria, this.criteriaList))
                        this.currentCriteria = previousCriteria(this.currentCriteria, this.criteriaList)
                    },
                    selectOption(currentCriteria, option) {
                        this.$emit('select-option', currentCriteria, option.id)
                    },
                    submitEvaluation() {
                        this.$emit('submit-evaluation');
                    }
                },
                template: '#draxo-evaluation-template',
            },
        }
    })

    function isPositiveOption(optionId, scale) {
        let option = scale.find((option) => option.id === optionId)
        return option.type === 'POSITIVE'
    }

    function isNegativeOption(optionId, scale) {
        let option = scale.find((option) => option.id === optionId)
        return option.type === 'NEGATIVE'
    }

    function nextCriteria(criteria, criteriaList) {
        return criteriaList[criteriaList.indexOf(criteria) + 1]
    }

    function previousCriteria(criteria, criteriaList) {
        return criteriaList[criteriaList.indexOf(criteria) - 1]
    }

    function toServerFormat(criteriaValuation, criteriaList) {
        return criteriaList
            .map(criteria => criteriaValuation[criteria])
            .filter(criteriaEvaluation => criteriaEvaluation) // remove nulls
    }
})()