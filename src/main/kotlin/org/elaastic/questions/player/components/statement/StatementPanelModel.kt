package org.elaastic.questions.player.components.statement

data class StatementPanelModel(
        val panelClosed: Boolean = false,
        val hideQuestionType: Boolean = false,
        val hideStatement: Boolean = false
)