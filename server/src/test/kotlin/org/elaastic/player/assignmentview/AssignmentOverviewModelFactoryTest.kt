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

    @Test
    fun `test resolveIcons when FaceToFace, stopped, result aren't publish and is teacher`() {
        assertEquals(
            listOf(lock),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.afterStop,
                resultsArePublished = false,
                null
            )
        )
    }

    @Test
    fun `test resolveIcons when FaceToFace, stopped, result are publish and is teacher`() {
        assertEquals(
            listOf(chart),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.afterStop,
                resultsArePublished = true,
                null
            )
        )

    }

    @Test
    fun `test resolveIcons when FaceToFace, not stopped, interaction is ResponsSubmission and is teacher`() {
        assertEquals(
            listOf(comment),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.beforeStart,
                resultsArePublished = false,
                InteractionType.ResponseSubmission
            )
        )
        assertEquals(
            listOf(comment),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.show,
                resultsArePublished = false,
                InteractionType.ResponseSubmission
            )
        )
    }

    @Test
    fun `test resolveIcons when FaceToFace, not stopped, interaction is Evaluation and is teacher`() {
        assertEquals(
            listOf(comments),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.beforeStart,
                resultsArePublished = false,
                InteractionType.Evaluation
            )
        )
        assertEquals(
            listOf(comments),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.show,
                resultsArePublished = false,
                InteractionType.Evaluation
            )
        )
    }

    @Test
    fun `test resolveIcons when FaceToFace, not stopped, interaction is Read and is teacher`() {
        assertEquals(
            listOf(chart),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.beforeStart,
                resultsArePublished = false,
                InteractionType.Read
            )
        )
        assertEquals(
            listOf(chart),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.show,
                resultsArePublished = false,
                InteractionType.Read
            )
        )
    }

    @Test
    fun `test resolveIcons when FaceToFace, not stopped, interaction is null and is teacher`() {
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.beforeStart,
                resultsArePublished = false,
                null
            )
        )
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.FaceToFace,
                State.show,
                resultsArePublished = false,
                null
            )
        )
    }

    @Test
    fun `test resolveIcons when Distance, stopped, result aren't publish and is teacher`() {
        assertEquals(
            listOf(lock),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.afterStop,
                resultsArePublished = false,
                null
            )
        )
    }

    @Test
    fun `test resolveIcons when Distance, stopped, result are publish and is teacher`() {
        assertEquals(
            listOf(chart),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.afterStop,
                resultsArePublished = true,
                null
            )
        )
    }

    @Test
    fun `test resolveIcons when Distance, beforeStart, result aren't publish and is teacher`() {
        // resultArePublished = false
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.beforeStart,
                resultsArePublished = false,
                null
            )
        )
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.beforeStart,
                resultsArePublished = false,
                InteractionType.ResponseSubmission
            )
        )
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.beforeStart,
                resultsArePublished = false,
                InteractionType.Evaluation
            )
        )
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.beforeStart,
                resultsArePublished = false,
                InteractionType.Read
            )
        )
        // resultArePublished = true
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.beforeStart,
                resultsArePublished = true,
                null
            )
        )
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.beforeStart,
                resultsArePublished = true,
                InteractionType.ResponseSubmission
            )
        )
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.beforeStart,
                resultsArePublished = true,
                InteractionType.Evaluation
            )
        )
        assertEquals(
            listOf(minus),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.beforeStart,
                resultsArePublished = true,
                InteractionType.Read
            )
        )
    }

    @Test
    fun `test resolveIcons when Distance, show, result aren't publish and is teacher`() {
        assertEquals(
            listOf(comment, comments),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.show,
                resultsArePublished = false,
                InteractionType.ResponseSubmission
            )
        )
        assertEquals(
            listOf(comment, comments),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.show,
                resultsArePublished = false,
                InteractionType.Evaluation
            )
        )
        assertEquals(
            listOf(comment, comments),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.show,
                resultsArePublished = false,
                InteractionType.Read
            )
        )
        assertEquals(
            listOf(comment, comments),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.show,
                resultsArePublished = false,
                null
            )
        )
    }

    @Test
    fun `test resolveIcons when Distance, show, result are publish and is teacher`() {
        assertEquals(
            listOf(comment, comments, chart),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.show,
                resultsArePublished = true,
                InteractionType.ResponseSubmission
            )
        )
        assertEquals(
            listOf(comment, comments, chart),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.show,
                resultsArePublished = true,
                InteractionType.Evaluation
            )
        )
        assertEquals(
            listOf(comment, comments, chart),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.show,
                resultsArePublished = true,
                InteractionType.Read
            )
        )
        assertEquals(
            listOf(comment, comments, chart),
            AssignmentOverviewModelFactory.resolveIcons(
                true,
                ExecutionContext.Distance,
                State.show,
                resultsArePublished = true,
                null
            )
        )
    }
}