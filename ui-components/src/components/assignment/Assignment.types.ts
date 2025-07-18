/**
 * A course is a collection of subjects
 *
 */
export interface Course {
  id: number
  title: string
}

/**
 * A subject is a collection of questions.
 * An Assignment is created from a subject.
 */
export interface Subject {
  id: number
  title: string
}
