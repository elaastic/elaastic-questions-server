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

package org.elaastic.questions.subject

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentRepository
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.attachment.Attachment
import org.elaastic.questions.attachment.AttachmentService
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.subject.statement.StatementRepository
import org.elaastic.questions.subject.statement.StatementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.util.*
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional


@Service
@Transactional
class SubjectService(
    @Autowired val subjectRepository: SubjectRepository,
    @Autowired val statementService: StatementService,
    @Autowired val statementRepository: StatementRepository,
    @Autowired val assignmentService: AssignmentService,
    @Autowired val assignmentRepository: AssignmentRepository,
    @Autowired val entityManager: EntityManager,
    @Autowired val sequenceService: SequenceService,
    @Autowired val responseService: ResponseService,
    @Autowired val sharedSubjectRepository: SharedSubjectRepository,
    @Autowired val attachmentService: AttachmentService,

    ) {

    val LOG: Logger = Logger.getLogger(SubjectService::class.toString())

    fun get(id: Long, fetchStatementsAndAssignments: Boolean = false): Subject {
        // TODO (+) i18n error message
        return when (fetchStatementsAndAssignments) {
            true -> subjectRepository.findOneWithStatementsAndAssignmentsById(id)
            false -> subjectRepository.findOneById(id)
        } ?: throw EntityNotFoundException("There is no subject for id \"$id\"")
    }

    fun get(user: User, id: Long, fetchStatementsAndAssignments: Boolean = false): Subject {
        get(id, fetchStatementsAndAssignments).let {
            if (!user.isTeacher()) {
                throw AccessDeniedException("You are not authorized to access to this subject")
            }
            if (user != it.owner)
                if (sharedSubjectRepository.findByTeacherAndSubject(user, it) == null)
                    throw AccessDeniedException("The subject \"${it.title}\" is not shared with you")
            return it
        }
    }

    fun findByGlobalId(globalId: String): Subject? {
        return subjectRepository.findByGlobalId(globalId)
    }

    fun save(subject: Subject): Subject {
        subjectRepository.save(subject).let { savedSubject ->
            savedSubject.course?.let { course ->
                course.subjects.add(savedSubject)
            }
            return savedSubject
        }
    }

    fun createFromExportData(
        user: User,
        subjectData: ExportSubjectData,
    ): Subject {
        val subject = save(
            Subject(
                title = subjectData.title,
                owner = user
            )
        )

        subjectData.statements.forEachIndexed { index, statementExport ->
            val statement = addStatement(
                subject,
                Statement(
                    title = statementExport.title,
                    owner = user,
                    content = statementExport.content,
                    choiceSpecification = statementExport.choiceSpecification,
                    rank = index,
                    questionType = statementExport.questionType,
                    expectedExplanation = statementExport.expectedExplanation,
                )
            )
            statementExport.attachment?.let { attachmentExport ->
                attachmentExport.attachmentFile?.let { file ->
                    attachmentService.saveStatementAttachment(
                        statement,
                        Attachment(
                            name = attachmentExport.name,
                            size = file.length(),
                            originalFileName = attachmentExport.originalFileName,
                            mimeType = attachmentExport.mimeType,
                        ),
                        file.inputStream()
                    )
                }
            }
        }

        return subject
    }


    fun findAllByOwner(
        owner: User,
        pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
    )
            : Page<Subject> {
        return subjectRepository.findAllByOwner(owner, pageable)
    }

    fun findAllWithoutCourseByOwner(
        owner: User,
        pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
    )
            : Page<Subject> {
        return subjectRepository.findAllByOwnerAndCourseIsNull(owner, pageable)
    }


    fun touch(subject: Subject) {
        subject.lastUpdated = Date()
        subjectRepository.save(subject)
    }

    fun addStatement(subject: Subject, statement: Statement): Statement {
        statement.subject = subject
        statement.rank = (subject.statements.map { it.rank }.maxOrNull() ?: 0) + 1
        statementService.save(statement)
        subject.statements.add(statement)
        addCorrespondingSequenceInAssignments(statement, subject)
        touch(subject)
        return statement
    }

    private fun addCorrespondingSequenceInAssignments(statement: Statement, subject: Subject) {
        subject.assignments.forEach {
            assignmentService.addSequence(it, statement)
        }
    }

    fun countAllStatement(subject: Subject): Int {
        return statementRepository.countAllBySubject(subject)
    }

    fun count(): Long {
        return subjectRepository.count()
    }

    fun countWithoutCourse(owner: User): Long {
        return subjectRepository.countByCourseIsNullAndOwner(owner)
    }

    fun delete(user: User, subject: Subject) {
        require(user == subject.owner) {
            "Only the owner can delete an assignment"
        }
        for (statement: Statement in subject.statements) {
            statementService.delete(statement)
        }
        for (assignment: Assignment in subject.assignments) {
            assignmentService.delete(subject.owner, assignment)
        }
        subjectRepository.delete(subject) // all other linked entities are deletes by DB cascade
    }

    fun removeStatement(user: User, statement: Statement) {
        removeStatementFromSubject(user, statement)
        deleteStatementIfNotUsed(statement)
    }

    fun removeStatementFromSubject(user: User, statement: Statement) {
        require(user == statement.owner) {
            "Only the owner can delete a statement"
        }
        val subject = statement.subject!!
        touch(subject)
        subject.statements.remove(statement)
        statement.subject = null
        statementService.save(statement)
        entityManager.flush()
        updateAllStatementRank(subject)
    }

    private fun deleteStatementIfNotUsed(statement: Statement) {
        val statementAlreadyUsed = statementService.responsesExistForStatement(statement) || statementService.actionsExistForStatement(statement)
        if (!statementAlreadyUsed) {
            sequenceService.findAllSequencesByStatement(statement).forEach {
                assignmentService.removeSequence(statement.owner, it)
            }
            statementService.delete(statement)
        }
    }

    fun moveUpStatement(subject: Subject, statementId: Long) {
        val idsArray = subject.statements.map { it.id }.toTypedArray()
        val pos = idsArray.indexOf(statementId)

        if (pos == -1)
            throw IllegalStateException("This statement $statementId does not belong to subject ${subject.id}")
        if (pos == 0)
            return  // Nothing to do

        entityManager.createNativeQuery(
            "UPDATE statement SET rank = CASE " +
                    "WHEN id=${statementId} THEN ${pos} " +
                    "WHEN id=${idsArray[pos - 1]} THEN ${pos + 1} " +
                    " END " +
                    "WHERE id in (${idsArray[pos - 1]}, ${statementId})"
        ).executeUpdate()
    }

    fun moveDownStatement(subject: Subject, statementId: Long) {
        val idsArray = subject.statements.map { it.id }.toTypedArray()
        val pos = idsArray.indexOf(statementId)
        val posValue = pos + 1

        if (pos == -1)
            throw IllegalStateException("This statement $statementId does not belong to subject ${subject.id}")
        if (pos == subject.statements.size - 1)
            return  // Nothing to do

        entityManager.createNativeQuery(
            "UPDATE statement SET rank = CASE " +
                    "WHEN id=${statementId} THEN ${posValue + 1} " +
                    "WHEN id=${idsArray[pos + 1]} THEN ${posValue} " +
                    " END " +
                    "WHERE id in (${idsArray[pos + 1]}, ${statementId})"
        ).executeUpdate()
    }


    fun updateAllStatementRank(subject: Subject) {
        val statementIds = subject.statements.map { it.id }
        if (statementIds.isEmpty()) return // Nothing to do

        entityManager.createNativeQuery(
            "UPDATE statement SET rank = CASE " +
                    statementIds.mapIndexed { index, id ->
                        "WHEN id=$id THEN $index"
                    }.joinToString(" ") +
                    " END " +
                    "WHERE id in (${statementIds.joinToString(",")})"
        ).executeUpdate()

        subject.statements.mapIndexed { index, statement -> statement.rank = index + 1 }
    }

    fun addAssignment(subject: Subject, assignment: Assignment): Assignment {
        assignment.subject = subject
        assignment.rank = (subject.assignments.map { it.rank }.maxOrNull() ?: 0) + 1
        assignmentService.save(assignment)
        assignmentService.buildFromSubject(assignment, subject);
        subject.assignments.add(assignment)
        touch(subject)
        return assignment
    }

    fun moveUpAssignment(subject: Subject, assignmentId: Long) {
        val idsArray = subject.assignments.map { it.id }.toTypedArray()
        val pos = idsArray.indexOf(assignmentId)

        if (pos == -1)
            throw IllegalStateException("This assignment $assignmentId does not belong to subject ${subject.id}")
        if (pos == 0)
            return  // Nothing to do

        entityManager.createNativeQuery(
            "UPDATE assignment SET rank = CASE " +
                    "WHEN id=${assignmentId} THEN ${pos} " +
                    "WHEN id=${idsArray[pos - 1]} THEN ${pos + 1} " +
                    " END " +
                    "WHERE id in (${idsArray[pos - 1]}, ${assignmentId})"
        ).executeUpdate()
    }

    fun moveDownAssignment(subject: Subject, assignmentId: Long) {
        val idsArray = subject.assignments.map { it.id }.toTypedArray()
        val pos = idsArray.indexOf(assignmentId)
        val posValue = pos + 1

        if (pos == -1)
            throw IllegalStateException("This assignment $assignmentId does not belong to subject ${subject.id}")
        if (pos == subject.assignments.size - 1)
            return  // Nothing to do

        entityManager.createNativeQuery(
            "UPDATE assignment SET rank = CASE " +
                    "WHEN id=${assignmentId} THEN ${posValue + 1} " +
                    "WHEN id=${idsArray[pos + 1]} THEN ${posValue} " +
                    " END " +
                    "WHERE id in (${idsArray[pos + 1]}, ${assignmentId})"
        ).executeUpdate()
    }

    fun removeAssignment(user: User, assignment: Assignment) {
        require(user == assignment.owner) {
            "Only the owner can delete an assignment"
        }
        val subject = assignment.subject!!
        touch(subject)
        subject.assignments.remove(assignment)
        entityManager.flush()
        assignmentRepository.delete(assignment) // all other linked entities are deletes by DB cascade
        entityManager.flush()
        entityManager.clear()
        updateAllAssignmentRank(subject)
    }

    fun updateAllAssignmentRank(subject: Subject) {
        val assignmentIds = subject.assignments.map { it.id }
        if (assignmentIds.isEmpty()) return // Nothing to do

        entityManager.createNativeQuery(
            "UPDATE assignment SET rank = CASE " +
                    assignmentIds.mapIndexed { index, id ->
                        "WHEN id=$id THEN $index"
                    }.joinToString(" ") +
                    " END " +
                    "WHERE id in (${assignmentIds.joinToString(",")})"
        ).executeUpdate()

        subject.assignments.mapIndexed { index, assignment -> assignment.rank = index + 1 }
    }

    fun sharedToTeacher(user: User, subject: Subject): SharedSubject? {
        if (subject.owner == user) {
            return null
        }

        return sharedSubjectRepository.findByTeacherAndSubject(
            user,
            subject
        ) ?: sharedSubjectRepository.save(
            SharedSubject(user, subject)
        )
    }

    fun findAllSharedSubjects(
        user: User,
        pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
    ): Page<Subject> {
        return sharedSubjectRepository.findAllSubjectsForTeacher(user, pageable)
    }

    fun duplicate(user: User, initialSubject: Subject, inSameCourse: Boolean = true): Subject {
        val indexTitle = subjectRepository.countAllStartingWithTitle(user, initialSubject.title) + 1
        val duplicateTitle = if (indexTitle == 1) initialSubject.title else initialSubject.title + " ($indexTitle) "
        val duplicateSubject = Subject(
            duplicateTitle,
            user
        )
        duplicateSubject.parentSubject = initialSubject
        if (inSameCourse) {
            duplicateSubject.course = initialSubject.course
        }
        save(duplicateSubject)
        entityManager.flush()
        for (statement: Statement in initialSubject.statements) {
            importStatementInSubject(statement, duplicateSubject)
        }
        return duplicateSubject
    }

    fun import(user: User, sharedSubject: Subject): Subject {
        if (sharedSubjectRepository.findByTeacherAndSubject(user, sharedSubject) == null)
            throw EntityNotFoundException("The subject \"${sharedSubject.title}\" is not shared with you")

        return duplicate(user, sharedSubject, false)
    }

    fun isUsedAsParentSubject(user: User, parentSubject: Subject): Boolean {
        return subjectRepository.countAllByParentSubject(user, parentSubject) > 0
    }

    fun importStatementInSubject(
        statement: Statement,
        subject: Subject,
        titleSuffixIfCopyInSameSubject: String = " (Copy)"
    ): Statement {
        var duplicatedStatement = statementService.duplicate(statement)
        if (subject == statement.subject) {
            duplicatedStatement.title += titleSuffixIfCopyInSameSubject
        }
        duplicatedStatement.owner = subject.owner
        return addStatement(subject, duplicatedStatement)
    }

    fun newVersionOfStatementInSubject(statement: Statement): Statement {
        var duplicatedStatement = statementService.duplicate(statement)
        // update sequences with new statement
        sequenceService.findAllNotTerminatedSequencesByStatement(statement).forEach {
            it.statement = duplicatedStatement
            sequenceService.save(it)
        }
        return duplicatedStatement
    }

    fun migrateAssignmentsTowardSubjects() {
        // get all assignments without subject
        assignmentRepository.findAllBySubjectIsNull().forEach { assignment ->
            // create a subject
            Subject(
                title = assignment.title,
                owner = assignment.owner
            ).let { subject ->
                save(subject)
                // for each sequence
                assignment.sequences.forEach { sequence ->
                    // add the corresponding statement to the subject
                    addStatement(subject, sequence.statement)
                    // for all responses (both 1 and 2 attempts) attach statement to response
                    if (sequence.responseSubmissionInteractionIsInitialized()) {
                        responseService.findAll(sequence, false)[1].forEach { response ->
                            response.statement = sequence.statement
                            responseService.responseRepository.save(response)
                        }
                        responseService.findAll(sequence, false)[2].forEach { response ->
                            response.statement = sequence.statement
                            responseService.responseRepository.save(response)
                        }
                    }
                }
                // finally, attach the subject to the assignment
                assignment.subject = subject
                assignmentService.save(assignment)
            }
        }
    }

    fun findFirstSubjectByOwner(owner: User): Subject? {
        return subjectRepository.findFirstByOwner(owner)
    }
}
