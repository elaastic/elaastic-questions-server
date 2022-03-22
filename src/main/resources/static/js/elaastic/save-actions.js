var elaastic = elaastic || {}

elaastic.saveAction = function(elaasticQuestionsUrl, sequenceId, subject, action, object){
    if(sequenceId != null){
        fetch(elaasticQuestionsUrl + 'action/' + sequenceId + '/saveAction/' + subject + '/' + action + '/' + object)
    }
}