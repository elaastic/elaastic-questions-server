package org.elaastic.user

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RoleTest {
    @Test
    fun testEquals() {
        val student = Role(Role.RoleId.STUDENT)
        val student2 = Role(Role.RoleId.STUDENT)

        assertNotEquals(null, student)
        assertNotEquals("", student)
        assertSame(student, student)
        assertEquals(student, student)

        assertNotSame(student.name, student2.name)
        assertEquals(student.name, student2.name)
        assertEquals(student, student2)

        val teacher = Role(Role.RoleId.TEACHER)
        assertNotEquals(student, teacher)

        val student3 = Role(Role.RoleId.STUDENT)
        assertEquals(student.name, student3.name)
        assertNotEquals(student, student3)
    }

    @Test
    fun `test equals with RoleId`() {
        val studentEnum = Role.RoleId.STUDENT
        val studentEntity = Role(studentEnum)

        assertNotSame(studentEntity.name, studentEnum.roleName)
        assertEquals(studentEntity.name, studentEnum.roleName)
        assertEquals(studentEntity, studentEnum)

        val teacherEntity = Role(Role.RoleId.TEACHER)
        assertNotEquals(studentEntity, teacherEntity)
        assertNotEquals(teacherEntity, studentEnum)

        val studentEntity2 = Role(Role.RoleId.STUDENT)
        assertEquals(studentEntity.name, studentEntity2.name)
        assertNotEquals(studentEntity, studentEntity2)
    }

    @Test
    fun `test equals with a String`() {
        val studentString = "student"
        val studentEntity = Role(studentString)

        assertEquals(studentEntity, studentString)
        assertNotEquals(studentString, studentEntity)
        assertNotEquals(studentEntity, "anotherString")
    }

    @Test
    fun `test List(Role) contain()`() {
        val student = Role(Role.RoleId.STUDENT)
        val teacher = Role(Role.RoleId.TEACHER)

        val roles: List<Role> = listOf(student, teacher)

        assertTrue(roles.contains(Role.RoleId.STUDENT))
        assertTrue(roles.contains(student))
        assertFalse(roles.contains(Role.RoleId.ADMIN))
        assertFalse(roles.contains(Role(Role.RoleId.ADMIN)))
    }

    @Test
    fun testHashCode() {
        val role1 = Role(Role.RoleId.STUDENT)
        val role2 = Role(Role.RoleId.STUDENT)

        assertNotSame(role1.name, role2.name)
        assertEquals(role1.hashCode(), role2.hashCode())

        val role3 = Role(Role.RoleId.TEACHER)
        assertNotEquals(role1.hashCode(), role3.hashCode())

        val role4 = Role(Role.RoleId.STUDENT)
        assertEquals(role1.name, role4.name)
        assertNotEquals(role1.hashCode(), role4.hashCode())
    }
 }