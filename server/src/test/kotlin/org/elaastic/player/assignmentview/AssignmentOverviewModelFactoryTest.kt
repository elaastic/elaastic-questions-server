package org.elaastic.player.assignmentview

import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.InteractionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AssignmentOverviewModelFactoryTest {

    val chart = AssignmentOverviewModelFactory.chartIcon
    val lock = AssignmentOverviewModelFactory.lockIcon
    val minus = AssignmentOverviewModelFactory.minusIcon
    val comment = AssignmentOverviewModelFactory.commentIcon
    val comments = AssignmentOverviewModelFactory.commentsIcon
    
    fun testOfResolveIcons(
        excepectedIcons: List<AssignmentOverviewModel.PhaseIcon>,
        isTeacher: Boolean,
        executionContext: ExecutionContext,
        state: State,
        resultsArePublished: Boolean,
        activeInteractionType: InteractionType?
    ) {
        val actualIcons = AssignmentOverviewModelFactory.resolveIcons(
            isTeacher,
            executionContext,
            state,
            resultsArePublished,
            activeInteractionType
        )
        assertEquals(actualIcons, excepectedIcons)
    }

    @Test
    fun `test resolveIcons when FaceToFace, stopped, result aren't publish and is teacher`() {
        testOfResolveIcons(
            listOf(lock),
            true,
            ExecutionContext.FaceToFace,
            State.afterStop,
            resultsArePublished = false,
            null
        )
    }

    @Test
    fun `test resolveIcons when FaceToFace, stopped, result are publish and is teacher`() {
        testOfResolveIcons(
            listOf(chart),
            true,
            ExecutionContext.FaceToFace,
            State.afterStop,
            resultsArePublished = true,
            null
        )
    }

    @Test
    fun `test resolveIcons when FaceToFace, not stopped, interaction is ResponsSubmission and is teacher`() {
        testOfResolveIcons(
            listOf(comment),
            true,
            ExecutionContext.FaceToFace,
            State.beforeStart,
            resultsArePublished = false,
            InteractionType.ResponseSubmission
        )
        testOfResolveIcons(
            listOf(comment),
            true,
            ExecutionContext.FaceToFace,
            State.show,
            resultsArePublished = false,
            InteractionType.ResponseSubmission
        )
    }

    @Test
    fun `test resolveIcons when FaceToFace, not stopped, interaction is Evaluation and is teacher`() {
        testOfResolveIcons(
            listOf(comments),
            true,
            ExecutionContext.FaceToFace,
            State.beforeStart,
            resultsArePublished = false,
            InteractionType.Evaluation
        )
        testOfResolveIcons(
            listOf(comments),
            true,
            ExecutionContext.FaceToFace,
            State.show,
            resultsArePublished = false,
            InteractionType.Evaluation
        )
    }

    @Test
    fun `test resolveIcons when FaceToFace, not stopped, interaction is Read and is teacher`() {
        testOfResolveIcons(
            listOf(chart),
            true,
            ExecutionContext.FaceToFace,
            State.beforeStart,
            resultsArePublished = false,
            InteractionType.Read
        )
        testOfResolveIcons(
            listOf(chart),
            true,
            ExecutionContext.FaceToFace,
            State.show,
            resultsArePublished = false,
            InteractionType.Read
        )
    }

    @Test
    fun `test resolveIcons when FaceToFace, not stopped, interaction is null and is teacher`() {
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.FaceToFace,
            State.beforeStart,
            resultsArePublished = false,
            null
        )
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.FaceToFace,
            State.show,
            resultsArePublished = false,
            null
        )
    }

    @Test
    fun `test resolveIcons when Distance, stopped, result aren't publish and is teacher`() {
        testOfResolveIcons(
            listOf(lock),
            true,
            ExecutionContext.Distance,
            State.afterStop,
            resultsArePublished = false,
            null
        )
    }

    @Test
    fun `test resolveIcons when Distance, stopped, result are publish and is teacher`() {
        testOfResolveIcons(
            listOf(chart),
            true,
            ExecutionContext.Distance,
            State.afterStop,
            resultsArePublished = true,
            null
        )
    }

    @Test
    fun `test resolveIcons when Distance, beforeStart, result aren't publish and is teacher`() {
        // resultArePublished = false
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.Distance,
            State.beforeStart,
            resultsArePublished = false,
            null
        )
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.Distance,
            State.beforeStart,
            resultsArePublished = false,
            InteractionType.ResponseSubmission
        )
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.Distance,
            State.beforeStart,
            resultsArePublished = false,
            InteractionType.Evaluation
        )
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.Distance,
            State.beforeStart,
            resultsArePublished = false,
            InteractionType.Read
        )
        // resultArePublished = true
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.Distance,
            State.beforeStart,
            resultsArePublished = true,
            null
        )
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.Distance,
            State.beforeStart,
            resultsArePublished = true,
            InteractionType.ResponseSubmission
        )
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.Distance,
            State.beforeStart,
            resultsArePublished = true,
            InteractionType.Evaluation
        )
        testOfResolveIcons(
            listOf(minus),
            true,
            ExecutionContext.Distance,
            State.beforeStart,
            resultsArePublished = true,
            InteractionType.Read
        )
    }

    @Test
    fun `test resolveIcons when Distance, show, result aren't publish and is teacher`() {
        testOfResolveIcons(
            listOf(comment, comments),
            true,
            ExecutionContext.Distance,
            State.show,
            resultsArePublished = false,
            InteractionType.ResponseSubmission
        )
        testOfResolveIcons(
            listOf(comment, comments),
            true,
            ExecutionContext.Distance,
            State.show,
            resultsArePublished = false,
            InteractionType.Evaluation
        )
        testOfResolveIcons(
            listOf(comment, comments),
            true,
            ExecutionContext.Distance,
            State.show,
            resultsArePublished = false,
            InteractionType.Read
        )
        testOfResolveIcons(
            listOf(comment, comments),
            true,
            ExecutionContext.Distance,
            State.show,
            resultsArePublished = false,
            null
        )
    }

    @Test
    fun `test resolveIcons when Distance, show, result are publish and is teacher`() {
        testOfResolveIcons(
            listOf(comment, comments, chart),
            true,
            ExecutionContext.Distance,
            State.show,
            resultsArePublished = true,
            InteractionType.ResponseSubmission
        )
        testOfResolveIcons(
            listOf(comment, comments, chart),
            true,
            ExecutionContext.Distance,
            State.show,
            resultsArePublished = true,
            InteractionType.Evaluation
        )
        testOfResolveIcons(
            listOf(comment, comments, chart),
            true,
            ExecutionContext.Distance,
            State.show,
            resultsArePublished = true,
            InteractionType.Read
        )
        testOfResolveIcons(
            listOf(comment, comments, chart),
            true,
            ExecutionContext.Distance,
            State.show,
            resultsArePublished = true,
            null
        )
    }
}