var elaastic = elaastic || {}

elaastic.saveAction = function(sequenceId, subject, action, object){
    if(sequenceId != null){
        fetch('/action/' + sequenceId + '/saveAction/' + subject + '/' + action + '/' + object)
    }
}