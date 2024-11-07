package org.elaastic.player.dashboard

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.test.directive.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class SequenceMonitoringModelTest(

) {
    private val namesUnsorted: List<String> = mutableListOf(
        "Zoe",
        "Hannah",
        "Grace",
        "Frank",
        "Eve",
        "David",
        "Charlie",
        "Bob",
        "Alice"
    )

    @Test
    fun `In a Face to Face sequence a LearnersMonitoringModel can only have on phase in IN_PROGRESS state`() {
        tGiven("A wrong LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.FaceToFace,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("An IllegalArgumentException should be thrown") {
            assertThrows(
                IllegalArgumentException::class.java,
                it,
                "A Face to Face sequence can't have two phases in IN_PROGRESS"
            )
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.FaceToFace,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.NOT_STARTED,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Face to Face sequence can have one phase in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.FaceToFace,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(
                it,
                "A Face to Face sequence can have no phase in IN_PROGRESS -> The sequence is not started"
            )
        }
    }

    @Test
    fun `In a Blended sequence a LearnersMonitoringModel can only have two phase in IN_PROGRESS state`() {
        tGiven("A wrong LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.IN_PROGRESS, // Only one phase in IN_PROGRESS /!\. It's wrong
                    DashboardPhaseState.NOT_STARTED,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("An IllegalArgumentException should be thrown") {
            assertThrows(
                IllegalArgumentException::class.java,
                it,
                "A Blended sequence can't have one of the two first phases in IN_PROGRESS state"
            )
        }
        tGiven("A wrong LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.IN_PROGRESS, // Only one phase in IN_PROGRESS /!\. It's wrong
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("An IllegalArgumentException should be thrown") {
            assertThrows(
                IllegalArgumentException::class.java,
                it,
                "A Blended sequence can't have one of the two first phases in IN_PROGRESS state"
            )
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Blended sequence can have two phase in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.STOPPED,
                    DashboardPhaseState.STOPPED,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Blended sequence can have the read phase in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Blended sequence can have no phase in IN_PROGRESS -> The sequence is not started")
        }
    }

    @Test
    fun `In a Distance sequence a LearnersMonitoringModel all phase can be in IN_PROGRESS state`() {
        tGiven("A wrong LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.Distance,
                    DashboardPhaseState.IN_PROGRESS, // Only one phases in IN_PROGRESS /!\. It's wrong
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertThrows(
                IllegalArgumentException::class.java,
                it,
                "A Distance sequence can't have only one phases in IN_PROGRESS"
            )
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.Distance,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(
                it,
                "A Distance sequence can have no phase in IN_PROGRESS -> The sequence is not started"
            )
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.Distance,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Distance sequence can all the phase in IN_PROGRESS -> The sequence is started")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                SequenceMonitoringModel(
                    ExecutionContext.Distance,
                    DashboardPhaseState.STOPPED,
                    DashboardPhaseState.STOPPED,
                    learners = mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(
                it,
                "A Distance sequence can have the read phase in IN_PROGRESS while the two others are STOPPED -> The sequence is stopped"
            )
        }
    }


    @Test
    fun `setLearners should sort the learners (with the same state) according to their name`() {
        val learnersMonitoringModel = SequenceMonitoringModel(
            ExecutionContext.FaceToFace,
            DashboardPhaseState.IN_PROGRESS,
            DashboardPhaseState.NOT_STARTED,
            mutableListOf()
        )

        tGiven("A list unsorted list of LearnerMonitoringModel") {
            assertNotEquals(namesUnsorted, namesUnsorted.sorted(), "The names shouldn't be sorted")

            val learners: MutableList<LearnerMonitoringModel> = mutableListOf()
            namesUnsorted.forEach {
                learners.add(
                    LearnerMonitoringModel(
                        1,
                        it,
                        LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED,
                        sequenceMonitoringModel = learnersMonitoringModel
                    )
                )
            }
            learners
        }.tWhen("I set the learners") {
            learnersMonitoringModel.setLearners(it)
            it
        }.tThen("The learners should be sorted by name") {
            val learnersSorted = learnersMonitoringModel.learners.map { it.learnerName }
            assertEquals(namesUnsorted.sorted(), learnersSorted, "The names should be sorted")
        }
    }

    @Test
    fun `In a remote sequence the setLearners should sort them by the number of IN_PROGRESS state descending`() {
        val learnersMonitoringModel = SequenceMonitoringModel(
            ExecutionContext.Distance,
            DashboardPhaseState.IN_PROGRESS,
            DashboardPhaseState.IN_PROGRESS,
            mutableListOf()
        )
        tGiven("A list of LearnerMonitoringModel") {
            val learners: MutableList<LearnerMonitoringModel> = mutableListOf(
                LearnerMonitoringModel(
                    1,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // 0 IN_PROGRESS
                    LearnerStateOnPhase.ACTIVITY_TERMINATED,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    1,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // 0 IN_PROGRESS
                    LearnerStateOnPhase.ACTIVITY_TERMINATED,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    1,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // 1 IN_PROGRESS
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    1,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // 2 IN_PROGRESS
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
            )
            learners
        }.tWhen("I set the learners") {
            learnersMonitoringModel.setLearners(it)
        }.tThen("The learners should be sorted by the number of IN_PROGRESS state descending") {
            val learnersSorted =
                learnersMonitoringModel.learners.map { it.getLevelByStateCell(LearnerMonitoringModel.StateCell.IN_PROGRESS) }
            assertEquals(
                listOf(2, 1, 0, 0),
                learnersSorted,
                "The learners should be sorted by the number of IN_PROGRESS state descending"
            )
        }
    }

    @Test
    fun `In a blended sequence the setLearners should sort them by the number of IN_PROGRESS state descending`() {
        val learnersMonitoringModel = SequenceMonitoringModel(
            ExecutionContext.Blended,
            DashboardPhaseState.IN_PROGRESS,
            DashboardPhaseState.IN_PROGRESS,
            mutableListOf()
        )
        tGiven("A list of LearnerMonitoringModel") {
            val learners: MutableList<LearnerMonitoringModel> = mutableListOf(
                LearnerMonitoringModel(
                    1,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    1,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // IN_PROGRESS
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    1,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // IN_PROGRESS
                    // IN_PROGRESS
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
            )
            learners
        }.tWhen("I set the learners") {
            learnersMonitoringModel.setLearners(it)
        }.tThen("The learners should be sorted by the number of IN_PROGRESS state descending") {
            val learnersSorted = learnersMonitoringModel.learners.map {
                it.getLevelByStateCell(LearnerMonitoringModel.StateCell.IN_PROGRESS)
            }
            assertEquals(
                listOf(2, 1, 0),
                learnersSorted,
                "The learners should be sorted by the number of IN_PROGRESS state descending"
            )
        }
    }

    @Test
    fun `In a Face to Face sequence and Phase 1 active, setLearners should sort the learners by the number of IN_PROGRESS state descending then alphabitacly`() {
        val learnersMonitoringModel = SequenceMonitoringModel(
            ExecutionContext.FaceToFace,
            DashboardPhaseState.IN_PROGRESS,
            DashboardPhaseState.NOT_STARTED,
            mutableListOf()
        )
        tGiven("A list of LearnerMonitoringModel") {
            val learners: MutableList<LearnerMonitoringModel> = mutableListOf(
                LearnerMonitoringModel(
                    4,
                    "Bob",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // 0 IN_PROGRESS
                    LearnerStateOnPhase.WAITING,
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    3,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // 0 IN_PROGRESS
                    LearnerStateOnPhase.WAITING,
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    2,
                    "David",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // 1 IN_PROGRESS
                    LearnerStateOnPhase.WAITING,
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    1,
                    "Charlie",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // 1 IN_PROGRESS
                    LearnerStateOnPhase.WAITING,
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
            )
            learners
        }.tWhen("I set the learners") {
            learnersMonitoringModel.setLearners(it)
        }.tThen("The learners should be sorted by the number of IN_PROGRESS state descending") {
            val learnersSorted = learnersMonitoringModel.learners.map {
                it.getLevelByStateCell(LearnerMonitoringModel.StateCell.IN_PROGRESS)
            }
            assertEquals(
                listOf(1, 1, 0, 0),
                learnersSorted,
                "The learners should be sorted by the number of IN_PROGRESS state descending"
            )
            val firstLearnerWhoHaveFinished = learnersMonitoringModel.learners.find {
                it.learnerStateOnPhase1 == LearnerStateOnPhase.ACTIVITY_TERMINATED
            }
            assertEquals(
                "Alice",
                firstLearnerWhoHaveFinished?.learnerName,
                "Between two learners who have finished the first phase, the one with the smallest name should be first"
            )
        }
    }

    @Test
    fun `In a Face to Face and Phase 2 active, setLearners should sort the learners by the number of IN_PROGRESS state descending then NOT_TERMINATED  and then alphabitacly`() {
        val learnersMonitoringModel = SequenceMonitoringModel(
            ExecutionContext.FaceToFace,
            DashboardPhaseState.STOPPED,
            DashboardPhaseState.IN_PROGRESS,
            mutableListOf()
        )
        tGiven("A list of LearnerMonitoringModel") {
            val learners: MutableList<LearnerMonitoringModel> = mutableListOf(
                LearnerMonitoringModel(
                    6,
                    "Bob",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    5,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    4,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // NOT_TERMINATED
                    LearnerStateOnPhase.ACTIVITY_TERMINATED,     // TERMINATED
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    3,
                    "David",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED,     // TERMINATED
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // IN_PROGRESS
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    2,
                    "David",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // NOT_TERMINATED
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // IN_PROGRESS
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    1,
                    "Charlie",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // NOT_TERMINATED
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // IN_PROGRESS
                    LearnerStateOnPhase.WAITING,
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
            )
            learners
        }.tWhen("I set the learners") {
            learnersMonitoringModel.setLearners(it)
        }.tThen("The learners should be sorted by the number of IN_PROGRESS state descending") {
            val learnersIdSorted = learnersMonitoringModel.learners.map {
                it.userId
            }
            // The list of learners has been created in such a way that the ids are in ascending order
            // starting from 1 when sorting.
            val longList: List<Long> = listOf(1, 2, 3, 4, 5, 6)
            assertEquals(longList, learnersIdSorted)

            val firstLearnerWhoHaveFinished = learnersMonitoringModel.learners.find {
                it.learnerStateOnPhase1 == LearnerStateOnPhase.ACTIVITY_TERMINATED
                        && it.learnerStateOnPhase2 == LearnerStateOnPhase.ACTIVITY_TERMINATED
            }
            assertEquals(
                "Alice",
                firstLearnerWhoHaveFinished?.learnerName,
                "Between two learners who have all finished, the one with the smallest name should be first"
            )

            // Late mean that they didn't finish the first phase and still in progress in the second phase
            val firstLearnerWhoAreLate = learnersMonitoringModel.learners.find {
                it.learnerStateOnPhase1 == LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
                        && it.learnerStateOnPhase2 == LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
            }
            assertEquals(
                "Charlie",
                firstLearnerWhoAreLate?.learnerName,
                "Between two learners who are late, the one with the smallest name should be first"
            )
        }
    }

    @Test
    fun `In a Face to Face and Phase 3 active, setLearners should sort the learners by the number of NOT_TERMINATED state descending then NOT_TERMINATED in phase 1 and then alphabitacly`() {
        val learnersMonitoringModel = SequenceMonitoringModel(
            ExecutionContext.FaceToFace,
            DashboardPhaseState.STOPPED,
            DashboardPhaseState.STOPPED,
            mutableListOf()
        )
        tGiven("A list of LearnerMonitoringModel") {
            val learners: MutableList<LearnerMonitoringModel> = mutableListOf(
                LearnerMonitoringModel(
                    6,
                    "Bob",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    5,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    LearnerStateOnPhase.ACTIVITY_TERMINATED, // TERMINATED
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    4,
                    "David",
                    LearnerStateOnPhase.ACTIVITY_TERMINATED,     // TERMINATED
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // NOT_TERMINATED
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    3,
                    "Bob",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // NOT_TERMINATED
                    LearnerStateOnPhase.ACTIVITY_TERMINATED,     // TERMINATED
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    2,
                    "Alice",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // NOT_TERMINATED
                    LearnerStateOnPhase.ACTIVITY_TERMINATED,     // TERMINATED
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    1,
                    "David",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // NOT_TERMINATED
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // IN_PROGRESS
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
                LearnerMonitoringModel(
                    0,
                    "Albert",
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // NOT_TERMINATED
                    LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED, // IN_PROGRESS
                    sequenceMonitoringModel = learnersMonitoringModel
                ),
            )
            learners
        }.tWhen("I set the learners") {
            learnersMonitoringModel.setLearners(it)
        }.tThen("The learners should be sorted by the number of IN_PROGRESS state descending") {
            val learnersIdSorted = learnersMonitoringModel.learners.map {
                it.userId
            }
            // The list of learners has been created in such a way that the ids are in ascending order
            // starting from 1 when sorting.
            val longList: List<Long> = listOf(0, 1, 2, 3, 4, 5, 6)
            assertEquals(longList, learnersIdSorted)

            // Late mean that they didn't finish the first phase and still in progress in the second phase
            val firstLearnerWhoAreLate = learnersMonitoringModel.learners.find {
                it.learnerStateOnPhase1 == LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
                        && it.learnerStateOnPhase2 == LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
            }
            assertEquals(
                "Albert",
                firstLearnerWhoAreLate?.learnerName,
                "Between two learners who are late, the one with the smallest name should be first"
            )

            val learnerWhoHaveJustFinishOnePhase = learnersMonitoringModel.learners.find {
                it.getLevelByStateCell(LearnerMonitoringModel.StateCell.NOT_TERMINATED) == 1
            }
            assertEquals(
                "Alice",
                learnerWhoHaveJustFinishOnePhase?.learnerName,
                "Between two learners who have just finished one phase, the one with the smallest name should be first"
            )
        }
    }
}