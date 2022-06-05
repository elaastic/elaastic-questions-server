var elaastic = elaastic || {}

var i18n

elaastic.manageOnboarding = function(elaasticQuestionsUrl){

    let obCoursePage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    title: i18n['onboarding.introduction.title'],
                    intro: i18n['onboarding.introduction.content']
                },
                {
                    element: document.querySelector('.ob-course-creation-1'),
                    intro: i18n['onboarding.content.course_creation.1']
                },
                {
                    element: document.querySelector('.ob-course-creation-2'),
                    intro: i18n['onboarding.content.course_creation.2']
                },
                {
                    element: document.querySelector('.ob-course-creation-3'),
                    intro: i18n['onboarding.content.course_creation.3']
                }
            ].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/course_page')
        }).start()
    }

    let obCourseCreationPage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    element: document.querySelector('.ob-course-creation-4'),
                    intro: i18n['onboarding.content.course_creation.4']
                },
                {
                    element: document.querySelector('.ob-course-creation-5'),
                    intro: i18n['onboarding.content.course_creation.5']
                }
            ].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/course_creation_page')
        }).start();
    }

    let obSubjectPage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    element: document.querySelector('.ob-subject-creation-1'),
                    intro: i18n['onboarding.content.subject_creation.1']
                },
                {
                    element: document.querySelector('.ob-subject-creation-2'),
                    intro: i18n['onboarding.content.subject_creation.2']
                },
                {
                    element: document.querySelector('.ob-subject-creation-3'),
                    intro: i18n['onboarding.content.subject_creation.3']
                }
            ].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/subject_page')
        }).start()
    }

    let obSubjectCreationPage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    element: document.querySelector('.ob-subject-creation-4'),
                    intro: i18n['onboarding.content.subject_creation.4']
                },
                {
                    element: document.querySelector('.ob-subject-creation-5'),
                    intro: i18n['onboarding.content.subject_creation.5']
                },
                {
                    element: document.querySelector('.ob-subject-creation-6'),
                    intro: i18n['onboarding.content.subject_creation.6']
                }
            ].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/subject_creation_page')
        }).start()
    }

    let obSubjectEditionPage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    element: document.querySelector('.ob-subject-creation-7'),
                    intro: i18n['onboarding.content.subject_creation.7']
                },
                {
                    element: document.querySelector('.ob-subject-creation-8'),
                    intro: i18n['onboarding.content.subject_creation.8']
                },
                {
                    element: document.querySelector('.ob-subject-creation-9'),
                    intro: i18n['onboarding.content.subject_creation.9']
                },
                {
                    element: document.querySelector('.ob-subject-creation-10'),
                    intro: i18n['onboarding.content.subject_creation.10']
                },
                {
                    element: document.querySelector('.ob-subject-creation-11'),
                    intro: i18n['onboarding.content.subject_creation.11']
                },
                {
                    element: document.querySelector('.ob-question-creation-2'),
                    intro: i18n['onboarding.content.question_creation.2']
                },
                {
                    element: document.querySelector('.ob-question-creation-15'),
                    intro: i18n['onboarding.content.question_creation.15']
                },
                {
                    element: document.querySelector('.ob-question-creation-16'),
                    intro: i18n['onboarding.content.question_creation.16']
                },
                {
                    element: document.querySelector('.ob-question-creation-17'),
                    intro: i18n['onboarding.content.question_creation.17']
                },
                {
                    element: document.querySelector('.ob-question-creation-18'),
                    intro: i18n['onboarding.content.question_creation.18']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-1'),
                    intro: i18n['onboarding.content.assignment_creation.1']
                },
                {
                    intro: i18n['onboarding.content.assignment_creation.2']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-7'),
                    intro: i18n['onboarding.content.assignment_creation.3']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-13'),
                    intro: i18n['onboarding.content.assignment_creation.9']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-14'),
                    intro: i18n['onboarding.content.assignment_creation.10']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-15'),
                    intro: i18n['onboarding.content.assignment_creation.11']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-16'),
                    intro: i18n['onboarding.content.assignment_creation.12']
                }
            ].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/subject_edition_page')
        }).onbeforechange(function (targetElement) {
            if (targetElement) {
                if (targetElement.classList.contains('ob-assignment-creation-1')) {
                    document.getElementsByClassName('ob-assignment-creation-1')[0].click()
                }
                if (targetElement.classList.contains('ob-subject-creation-7')
                    || targetElement.classList.contains('ob-question-creation-2')
                    || targetElement.classList.contains('ob-question-creation-18')) {
                    document.getElementsByClassName('ob-subject-creation-7')[0].click()
                }
            }

        }).start()
    }

    let obQuestionCreationPage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    element: document.querySelector('.ob-question-creation-3'),
                    intro: i18n['onboarding.content.question_creation.3']
                },
                {
                    element: document.querySelector('.ob-question-creation-4'),
                    intro: i18n['onboarding.content.question_creation.4']
                },
                {
                    element: document.querySelector('.ob-question-creation-5'),
                    intro: i18n['onboarding.content.question_creation.5']
                },
                {
                    element: document.querySelector('.ob-question-creation-6'),
                    intro: i18n['onboarding.content.question_creation.6']
                },
                {
                    element: document.querySelector('.ob-question-creation-7'),
                    intro: i18n['onboarding.content.question_creation.7']
                },
                {
                    element: document.querySelector('.ob-question-creation-8'),
                    intro: i18n['onboarding.content.question_creation.8']
                },
                {
                    element: document.querySelector('.ob-question-creation-9'),
                    intro: i18n['onboarding.content.question_creation.9']
                },
                {
                    element: document.querySelector('.ob-question-creation-10'),
                    intro: i18n['onboarding.content.question_creation.10']
                },
                {
                    element: document.querySelector('.ob-question-creation-14'),
                    intro: i18n['questionCreationPage14']
                }
            ].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/question_creation_page')
        }).start()
    }

    let obAssignmentCreationPage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    element: document.querySelector('.ob-assignment-creation-8'),
                    intro: i18n['onboarding.content.assignment_creation.4']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-9'),
                    intro: i18n['onboarding.content.assignment_creation.5']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-10'),
                    intro: i18n['onboarding.content.assignment_creation.6']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-11'),
                    intro: i18n['onboarding.content.assignment_creation.7']
                },
                {
                    element: document.querySelector('.ob-assignment-creation-12'),
                    intro: i18n['onboarding.content.assignment_creation.8']

                },
            ].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/assignment_creation_page')
        }).start();
    }

    let obPlayerPage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    intro: i18n['onboarding.content.prepare_sequence.1']
                },
                {
                    element: document.querySelector('.ob-start-sequence-2'),
                    intro: i18n['onboarding.content.prepare_sequence.2']
                },
                {
                    element: document.querySelector('.ob-start-sequence-3'),
                    intro: i18n['onboarding.content.prepare_sequence.3']
                },
                {
                    element: document.querySelector('.ob-start-sequence-4'),
                    intro: i18n['onboarding.content.prepare_sequence.4']
                },
                {
                    element: document.querySelector('.ob-play-sequence-1'),
                    intro: i18n['onboarding.content.play_sequence.1']
                },
                {
                    element: document.querySelector('.ob-play-sequence-2'),
                    intro: i18n['onboarding.content.play_sequence.2']
                },
                {
                    element: document.querySelector('.ob-play-sequence-3'),
                    intro: i18n['onboarding.content.play_sequence.3']
                },
                {
                    element: document.querySelector('.ob-play-sequence-4'),
                    intro: i18n['onboarding.content.play_sequence.4']
                }].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/player_page')
        }).start();
    }

    let obSharedSubjectsPage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    element: document.querySelector('.ob-shared-subjects-1'),
                    intro: i18n['onboarding.content.shared_subjects.1']
                },
                {
                    element: document.querySelector('.ob-shared-subjects-2'),
                    intro: i18n['onboarding.content.shared_subjects.2']
                },
                {
                    element: document.querySelector('.ob-shared-subjects-3'),
                    intro: i18n['onboarding.content.shared_subjects.3']
                }].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/shared_subjects_page')
        }).start();
    }

    let obOneSharedSubjectPage = function (i18n) {
        introJs().setOptions({
            steps: [
                {
                    element: document.querySelector('.ob-one-shared-subjects-1'),
                    intro: i18n['onboarding.content.one_shared_subject.1']
                },
                {
                    element: document.querySelector('.ob-one-shared-subjects-2'),
                    intro: i18n['onboarding.content.one_shared_subject.2']
                },
                {
                    element: document.querySelector('.ob-one-shared-subjects-3'),
                    intro: i18n['onboarding.content.one_shared_subject.3']
                }].filter(function (obj) {
                return !('element' in obj) || $(obj.element).length;
            })
        }).onexit(function () {
            fetch(elaasticQuestionsUrl + 'userAccount/updateOnboardingChapter/one_shared_subject_page')
        }).start();
    }

    let startContextualOb = function (manuallyActivated, onboardingChaptersAlreadySeen) {
        let pageUrl = window.location.href
        
        let contextualConfigs = [];
        let addContextualConfig = function(pageMatchers, elementClassNameList, matchingChapter, chapterFunction) {
          contextualConfigs.push({pageMatchers : pageMatchers, elementClassNameList : elementClassNameList, matchingChapter : matchingChapter, chapterFunction : chapterFunction})
        }
        
        addContextualConfig(["/course/#?$", "/home$"], ['ob-course-creation-1', 'ob-course-creation-2', 'ob-course-creation-3'], "COURSE_PAGE", obCoursePage)
        addContextualConfig(["/course/create#?$"], ['ob-course-creation-4', 'ob-course-creation-5'], "COURSE_CREATION_PAGE", obCourseCreationPage)
        addContextualConfig(["/course/[0-9]*#?$", "/subject/#?$", "/course/[0-9]*/show#?$"], ['ob-subject-creation-1', 'ob-subject-creation-2', 'ob-subject-creation-3'], "SUBJECT_PAGE", obSubjectPage)
        addContextualConfig(["/subject/create#?$", "addSubject"], ['ob-subject-creation-4', 'ob-subject-creation-5', 'ob-subject-creation-6'], "SUBJECT_CREATION_PAGE", obSubjectCreationPage)
        addContextualConfig(["/subject/[0-9]*\\?activeTab="], ['ob-subject-creation-7', 'ob-subject-creation-8'], "SUBJECT_EDITION_PAGE", obSubjectEditionPage)
        addContextualConfig(["addStatement"], ['ob-question-creation-3'],"QUESTION_CREATION_PAGE", obQuestionCreationPage)
        addContextualConfig(["addAssignment"], ['ob-assignment-creation-8'], "ASSIGNMENT_CREATION_PAGE", obAssignmentCreationPage)
        addContextualConfig(["play"], ['ob-start-sequence-2'], "PLAYER_PAGE", obPlayerPage)
        addContextualConfig(["shared_index"], ['ob-shared-subjects-1'], "SHARED_SUBJECTS_PAGE", obSharedSubjectsPage)
        addContextualConfig(["/subject/[0-9]*(/show)?\\?activeTab="], ['ob-one-shared-subjects-3'], "ONE_SHARED_SUBJECT_PAGE", obOneSharedSubjectPage) // TODO : Why there is no pageUrl constraint here ? the elementClassName already does the job

        contextualConfigs.forEach(config => {
            if ((manuallyActivated || !onboardingChaptersAlreadySeen.includes(config.matchingChapter)) &&
                (config.pageMatchers.some(pageMatcher => { return pageUrl.match(pageMatcher) !== null })) &&
                config.elementClassNameList.every(elementClassName => { return  document.getElementsByClassName(elementClassName).length !== 0})) {
                if(i18n !== undefined){
                    config.chapterFunction(i18n)
                } else {
                    fetch(elaasticQuestionsUrl + 'api/properties/onboarding/chapter/' + config.matchingChapter)
                        .then(response => response.json()
                        .then(data => {i18n = data;config.chapterFunction(i18n)}))
                    }
                }
              })
    }

    elaastic.startContextualOb = startContextualOb;
    elaastic.elaasticQuestionsUrl = elaasticQuestionsUrl;
};