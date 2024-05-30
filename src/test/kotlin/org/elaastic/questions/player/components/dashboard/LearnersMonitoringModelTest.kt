package org.elaastic.questions.player.components.dashboard

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.player.phase.LearnerPhaseType
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class LearnersMonitoringModelTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
) {
    val namesUnsorted: List<String> = mutableListOf(
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
                LearnersMonitoringModel(
                    ExecutionContext.FaceToFace,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("An IllegalArgumentException should be thrown") {
            assertThrows(IllegalArgumentException::class.java, it, "A Face to Face sequence can't have two phases in IN_PROGRESS")
        }

        tGiven("A wrong LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.FaceToFace,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    mutableListOf()
                )
            }
            block
        }.tThen("An IllegalArgumentException should be thrown") {
            assertThrows(IllegalArgumentException::class.java, it, "A Face to Face sequence can't have three phases in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.FaceToFace,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Face to Face sequence can have one phase in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.FaceToFace,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Face to Face sequence can have no phase in IN_PROGRESS -> The sequence is not started")
        }
    }

    @Test
    fun `In a Blended sequence a LearnersMonitoringModel can only have two phase in IN_PROGRESS state`() {
        tGiven("A wrong LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.IN_PROGRESS, // Only one phase in IN_PROGRESS /!\. It's wrong
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("An IllegalArgumentException should be thrown") {
            assertThrows(IllegalArgumentException::class.java, it, "A Blended sequence can't have one of the two first phases in IN_PROGRESS state")
        }
        tGiven("A wrong LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.IN_PROGRESS, // Only one phase in IN_PROGRESS /!\. It's wrong
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("An IllegalArgumentException should be thrown") {
            assertThrows(IllegalArgumentException::class.java, it, "A Blended sequence can't have one of the two first phases in IN_PROGRESS state")
        }

        tGiven("A wrong LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.IN_PROGRESS, // All three phases in IN_PROGRESS /!\. It's wrong
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    mutableListOf()
                )
            }
            block
        }.tThen("An IllegalArgumentException should be thrown") {
            assertThrows(IllegalArgumentException::class.java, it, "A Blended sequence can't have three phases in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Blended sequence can have two phase in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.STOPPED,
                    DashboardPhaseState.STOPPED,
                    DashboardPhaseState.IN_PROGRESS,
                    mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Blended sequence can have the read phase in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.Blended,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
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
                LearnersMonitoringModel(
                    ExecutionContext.Distance,
                    DashboardPhaseState.IN_PROGRESS, // Only two phases in IN_PROGRESS /!\. It's wrong
                    DashboardPhaseState.IN_PROGRESS, //
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("An IllegalArgumentException should be thrown") {
            assertThrows(IllegalArgumentException::class.java, it, "A Distance sequence can't have only two phases in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.Distance,
                    DashboardPhaseState.IN_PROGRESS, // Only one phases in IN_PROGRESS /!\. It's wrong
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertThrows(IllegalArgumentException::class.java, it, "A Distance sequence can't have only one phases in IN_PROGRESS")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.Distance,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    DashboardPhaseState.NOT_STARTED,
                    mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Distance sequence can have no phase in IN_PROGRESS -> The sequence is not started")
        }

        tGiven("A correct LearnersMonitoringModel") {
            val block: () -> Unit = {
                LearnersMonitoringModel(
                    ExecutionContext.Distance,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    DashboardPhaseState.IN_PROGRESS,
                    mutableListOf()
                )
            }
            block
        }.tThen("No exception should be thrown") {
            assertDoesNotThrow(it, "A Distance sequence can all the phase in IN_PROGRESS -> The sequence is started")
        }
    }


    @Test
    fun `setLearners should sort the learners (with the same state) according to their name`() {
        val learnersMonitoringModel = LearnersMonitoringModel(
            ExecutionContext.FaceToFace,
            DashboardPhaseState.IN_PROGRESS,
            DashboardPhaseState.NOT_STARTED,
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
                        learnersMonitoringModel = learnersMonitoringModel
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

}