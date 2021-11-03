obIntro = function() {
    introJs().setOptions({
        steps: [
            {
                title: [[#{onboarding.introduction.title}]],
                intro: [[#{onboarding.introduction.content}]]
            }
        ]
    }).onchange(function (targetElement) {
            fetch('/userAccount/updateOnboardingChapter/introduction')
    }).start()
}

obCoursePage = function() {
    introJs().setOptions({
        steps: [
            {
                element: document.querySelector('.ob-course-creation-1'),
                intro: [[#{onboarding.content.course_creation.1}]]
            },
            {
                element: document.querySelector('.ob-course-creation-2'),
                intro: [[#{onboarding.content.course_creation.2}]]
            },
            {
                element: document.querySelector('.ob-course-creation-3'),
                intro: [[#{onboarding.content.course_creation.3}]]
            }
        ]
    }).onchange(function (targetElement) {
        if (targetElement.classList.contains('ob-course-creation-3')) {
            fetch('/userAccount/updateOnboardingChapter/course_page')
        }
    }).start()
}

obCourseCreationPage = function() {
    introJs().setOptions({
        steps: [
            {
                element: document.querySelector('.ob-course-creation-4'),
                intro: [[#{onboarding.content.course_creation.4}]]
            },
            {
                element: document.querySelector('.ob-course-creation-5'),
                intro: [[#{onboarding.content.course_creation.5}]]
            }
        ]
    }).onchange(function (targetElement) {
        if (targetElement.classList.contains('ob-course-creation-5')) {
            fetch('/userAccount/updateOnboardingChapter/course_creation_page')
        }
    }).start();
}

obSubjectPage = function(){
    introJs().setOptions({
        steps: [
            {
                element: document.querySelector('.ob-subject-creation-1'),
                intro: [[#{onboarding.content.subject_creation.1}]]
            },
            {
                element: document.querySelector('.ob-subject-creation-2'),
                intro: [[#{onboarding.content.subject_creation.2}]]
            },
            {
                element: document.querySelector('.ob-subject-creation-3'),
                intro: [[#{onboarding.content.subject_creation.3}]]
            }
        ]
    }).onchange(function (targetElement) {
        if (targetElement.classList.contains('ob-subject-creation-3')) {
            fetch('/userAccount/updateOnboardingChapter/subject_page')
        }
    }).start()
}

obSubjectCreationPage = function(){
    introJs().setOptions({
        steps: [
            {
                element: document.querySelector('.ob-subject-creation-4'),
                intro: [[#{onboarding.content.subject_creation.4}]]
            },
            {
                element: document.querySelector('.ob-subject-creation-5'),
                intro: [[#{onboarding.content.subject_creation.5}]]
            },
            {
                element: document.querySelector('.ob-subject-creation-6'),
                intro: [[#{onboarding.content.subject_creation.6}]]
            }
        ]
    }).onchange(function (targetElement) {
        if (targetElement.classList.contains('ob-subject-creation-3')) {
            fetch('/userAccount/updateOnboardingChapter/subject_creation_page')
        }
    }).start()
}

obSubjectEditionPage = function(){
    introJs().setOptions({
        steps: [
            {
                element: document.querySelector('.ob-question-creation-1'),
                intro: [[#{onboarding.content.question_creation.1}]]
            },
            {
                element: document.querySelector('.ob-question-creation-2'),
                intro: [[#{onboarding.content.question_creation.2}]]
            },
            {
                element: document.querySelector('.ob-question-creation-15'),
                intro: [[#{onboarding.content.question_creation.15}]]
            },
            {
                element: document.querySelector('.ob-question-creation-16'),
                intro: [[#{onboarding.content.question_creation.16}]]
            },
            {
                element: document.querySelector('.ob-question-creation-17'),
                intro: [[#{onboarding.content.question_creation.17}]]
            },
            {
                element: document.querySelector('.ob-question-creation-18'),
                intro: [[#{onboarding.content.question_creation.18}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-1'),
                intro: [[#{onboarding.content.assignment_creation.1}]]
            },
            {
                intro: [[#{onboarding.content.assignment_creation.2}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-3'),
                intro: [[#{onboarding.content.assignment_creation.3}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-13'),
                intro: [[#{onboarding.content.assignment_creation.9}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-14'),
                intro: [[#{onboarding.content.assignment_creation.10}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-15'),
                intro: [[#{onboarding.content.assignment_creation.11}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-16'),
                intro: [[#{onboarding.content.assignment_creation.12}]]
            }
        ]
    }).onchange(function (targetElement) {
        if (targetElement.classList.contains('ob-assignment-creation-16')) {
            fetch('/userAccount/updateOnboardingChapter/subject_edition_page')
        }
    }).onbeforechange(function (targetElement) {
        if (targetElement.classList.contains('ob-assignment-creation-1')) {
            document.getElementsByClassName('ob-assignment-creation-1')[0].click()
        }
    }).start()
}

obQuestionCreationPage = function(){
    introJs().setOptions({
        steps: [
            {
                element: document.querySelector('.ob-question-creation-3'),
                intro: [[#{onboarding.content.question_creation.3}]]
            },
            {
                element: document.querySelector('.ob-question-creation-4'),
                intro: [[#{onboarding.content.question_creation.4}]]
            },
            {
                element: document.querySelector('.ob-question-creation-5'),
                intro: [[#{onboarding.content.question_creation.5}]]
            },
            {
                element: document.querySelector('.ob-question-creation-6'),
                intro: [[#{onboarding.content.question_creation.6}]]
            },
            {
                element: document.querySelector('.ob-question-creation-7'),
                intro: [[#{onboarding.content.question_creation.7}]]
            },
            {
                element: document.querySelector('.ob-question-creation-8'),
                intro: [[#{onboarding.content.question_creation.8}]]
            },
            {
                element: document.querySelector('.ob-question-creation-9'),
                intro: [[#{onboarding.content.question_creation.9}]]
            },
            {
                element: document.querySelector('.ob-question-creation-10'),
                intro: [[#{onboarding.content.question_creation.10}]]
            },
            {
                element: document.querySelector('.ob-question-creation-11'),
                intro: [[#{onboarding.content.question_creation.11}]]
            },
            {
                element: document.querySelector('.ob-question-creation-12'),
                intro: [[#{onboarding.content.question_creation.12}]]
            },
            {
                element: document.querySelector('.ob-question-creation-14'),
                intro: [[#{onboarding.content.question_creation.14}]]
            }
        ]
    }).onchange(function (targetElement) {
        if (targetElement.classList.contains('ob-question-creation-14')) {
            fetch('/userAccount/updateOnboardingChapter/question_creation_page')
        }
    }).start()
}

obAssignmentCreationPage = function(){
    introJs().setOptions({
        steps: [
            {
                element: document.querySelector('.ob-assignment-creation-8'),
                intro: [[#{onboarding.content.assignment_creation.4}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-9'),
                intro: [[#{onboarding.content.assignment_creation.5}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-10'),
                intro: [[#{onboarding.content.assignment_creation.6}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-11'),
                intro: [[#{onboarding.content.assignment_creation.7}]]
            },
            {
                element: document.querySelector('.ob-assignment-creation-12'),
                intro: [[#{onboarding.content.assignment_creation.8}]]

            },
        ]
    }).onchange(function (targetElement) {
        if (targetElement.classList.contains('ob-assignment-creation-12')) {
            fetch('/userAccount/updateOnboardingChapter/assignment_creation_page')
        }
    }).start();
}

obPlayerPage = function(){
    introJs().setOptions({
        steps: [
            {
                intro: [[#{onboarding.content.prepare_sequence.1}]]
            },
            {
                element: document.querySelector('.ob-start-sequence-2'),
                intro: [[#{onboarding.content.prepare_sequence.2}]]
            },
            {
                element: document.querySelector('.ob-start-sequence-3'),
                intro: [[#{onboarding.content.prepare_sequence.3}]]
            },
            {
                element: document.querySelector('.ob-start-sequence-4'),
                intro: [[#{onboarding.content.prepare_sequence.4}]]
            },
            {
                element: document.querySelector('.ob-start-sequence-5'),
                intro: [[#{onboarding.content.prepare_sequence.5}]]
            }]
    }).onchange(function (targetElement) {
        if (targetElement.classList.contains('ob-start-sequence-5')) {
            fetch('/userAccount/updateOnboardingChapter/player_page')
        }
    }).start();
}

startObDependingOnPage = function(){
    // if (targetElement.classList.contains('ob-course-creation-3')) {
    //     fetch('/userAccount/updateOnboardingChapter/introduction')
    // }
    obCoursePage()
}