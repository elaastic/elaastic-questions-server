(function () {
    new Vue({
      el: '#draxo-form-app',
      data () {
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
        explanation () {
          if (!this.criteriaValuation[this.currentCriteria]) {
            return ''
          }

          return this.criteriaValuation[this.currentCriteria].explanation || ''
        },
      },
      methods: {
        hasBeenAnswered (criteria) {
          return this.criteriaValuation[criteria]
        },

        selectOption (criteria, optionId) {
          if (isNegativeOption(optionId, this.scales[criteria])) {
            let currentExplanation = this.criteriaValuation[criteria]?.explanation

            this.$set(this.criteriaValuation, criteria, {
              criteria,
              optionId,
              explanation: currentExplanation || ''
            })

          } else {
            this.$set(this.criteriaValuation, criteria, {criteria, optionId})
          }

          if(!isPositiveOption(optionId, this.scales[criteria])) {
            this.removeUnusedCriteria()
          }
        },
        updateExplanation (event) {
          if (!this.criteriaValuation[this.currentCriteria]) {
            return // should not happen
          }

          this.criteriaValuation[this.currentCriteria].explanation = event.target.value
        },
        removeUnusedCriteria () {
          let currentCriteriaIndex = this.criteriaList.indexOf(this.currentCriteria)
          for (let i = currentCriteriaIndex + 1; i < this.criteriaList.length; i++) {
            this.$set(this.criteriaValuation, this.criteriaList[i], null)
          }

        },
        submitEvaluation () {
          this.removeUnusedCriteria(this.criteriaValuation)

          let csrfToken = $('meta[name=\'_csrf\']').attr('content')
          let csrfHeader = $('meta[name=\'_csrf_header\']').attr('content')

          this.showGenericError = false
          this.loading = true
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
                  throw new Error(`Erreur HTTP: ${response.statusText}`)
                }
                this.loading = false
                return response.json()
              }
            )
            .then(data => {
              this.criteriaValuation = data.criteriaValuation
              this.currentCriteria = data.currentCriteria
              this.submitted = true

              eventBus.$emit('evaluationSubmitted')
            })
            .catch(error => {
              this.loading = false
              this.showGenericError = true
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
            firstCriteria () {
              return this.criteriaList[0]
            },
            lastCriteria () {
              return this.criteriaList[this.criteriaList.length - 1]
            },
            reachableCriteriaList () {
              let results = []

              for (let i = 0; i < this.criteriaList.length; i++) {
                let criteria = this.criteriaList[i]
                results.push(criteria)

                if (hasBlockingOption(criteria, this.criteriaValuation, this.scales)) break
              }

              return results
            },
            canGoPrevious () {
              return this.currentCriteria !== this.firstCriteria
            },
            canGoNext () {
              return this.currentCriteria !== this.lastCriteria &&
                this.positiveOptionSelected
            },
            canSubmit () {
              return this.criteriaValuation[this.currentCriteria] &&
                (this.currentCriteria === this.lastCriteria ||
                  !this.positiveOptionSelected
                )
            },
            awaitedExplanation () {
              return this.negativeOptionSelected
            },
            currentScale () {
              return this.scales[this.currentCriteria]
            },
            negativeOptionSelected () {
              let currentOption = this.criteriaValuation[this.currentCriteria]
              return currentOption &&
                isNegativeOption(currentOption.optionId, this.currentScale)
            },
            positiveOptionSelected () {
              let currentOption = this.criteriaValuation[this.currentCriteria]

              return currentOption &&
                isPositiveOption(currentOption.optionId, this.currentScale)
            }
          },
          methods: {
            nextCriteria () {
              this.$emit('select-criteria', nextCriteria(this.currentCriteria, this.criteriaList))
              $
            },
            previousCriteria () {
              this.$emit('select-criteria', previousCriteria(this.currentCriteria, this.criteriaList))
            },
            canGoTo (criteria) {
              let requiredIndex = this.criteriaList.indexOf(criteria)

              // Check there is only undefined or positive options before
              for (let i = 0; i < requiredIndex; i++) {
                let c = this.criteriaList[i]
                let valuation = this.criteriaValuation[c]
                if (valuation && valuation.optionId && !isPositiveOption(valuation.optionId, this.scales[c])) {
                  return false
                }
              }

              return true
            },
            gotoCriteria (criteria) {
              if (!this.reachableCriteriaList.includes(criteria)) {
                return // Can't go there...
              }

              let requiredIndex = this.criteriaList.indexOf(criteria)

              // Auto-select positive option for the previous criteria
              for (let i = 0; i < requiredIndex; i++) {
                let c = this.criteriaList[i]
                this.$emit('select-option', c, this.scales[c].find((option) => option.type === 'POSITIVE').id)
              }

              this.$emit('select-criteria', criteria)
            },
            selectOption (currentCriteria, option) {
              this.$emit('select-option', currentCriteria, option.id)
            },
            submitEvaluation () {
              this.$emit('submit-evaluation')
            }
          },
          template: '#draxo-evaluation-template',
        },
      }
    })

    function isPositiveOption (optionId, scale) {
      let option = scale.find((option) => option.id === optionId)
      return option.type === 'POSITIVE'
    }

    function isNegativeOption (optionId, scale) {
      let option = scale.find((option) => option.id === optionId)
      return option.type === 'NEGATIVE'
    }

    function nextCriteria (criteria, criteriaList) {
      return criteriaList[criteriaList.indexOf(criteria) + 1]
    }

    function previousCriteria (criteria, criteriaList) {
      return criteriaList[criteriaList.indexOf(criteria) - 1]
    }

    function hasBlockingOption (criteria, criteriaValuation, scales) {
      let valuation = criteriaValuation[criteria]
      return valuation && valuation.optionId && !isPositiveOption(valuation.optionId, scales[criteria])
    }

    function toServerFormat (criteriaValuation, criteriaList) {
      return criteriaList
        .map(criteria => criteriaValuation[criteria])
        .filter(criteriaEvaluation => criteriaEvaluation) // remove nulls
    }
  }

)
()