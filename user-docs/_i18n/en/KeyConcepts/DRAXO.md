## 2.7. DRAXO

DRAXO is an assessment grid for evaluating answers given to comprehension questions. 
These responses generally present explanations, justifications, or arguments.
DRAXO was developed as part of research conducted at IRIT on technological systems 
supporting formative assessment. DRAXO's goal is to improve the production of textual feedback.
DRAXO can be used during the second phase of an Elaastic sequence to guide learners in
their peer assessment activity.

### 2.7.1. The DRAXO criteria

The grid is structured around several criteria identified by the letters D, R, A, X, and O. Here is a detailed description of each criterion:

**Criterion "D" (unDerstandable)**
- **Prompt proposed to the evaluator**: I understand what the answer says.
- **Response options**: No, Partially, Yes.
- **Continuation of the assessment**:
  - If the chosen option is _Yes_, the evaluator is invited to assess according to the next criterion (R).
  - If the response option is _No_ or _Partially_, the evaluator is asked to provide textual feedback.
  - **Textual feedback requested from the evaluator**: What do you not understand about the answer?
- **End of the assessment**:
  - If textual feedback is provided, the assessment is considered completed.

**Criterion "R" (Relevant)**
- **Prompt proposed to the evaluator**: I think that the answer corresponds to the asked question.
- **Response options**: No, Partially, Yes, I don't know.
- **Continuation of the assessment**:
  - If the chosen option is _Yes_, the evaluator is invited to assess according to the next criterion (A).
  - If the response option is _No_ or _Partially_, the evaluator is asked to provide textual feedback.
  - **Feedback requested from the evaluator**: Why do you think the answer does not correspond to the asked question?
- **End of the assessment**:
  - If textual feedback is provided, the assessment is considered completed.
  - If the option _I don't know_ is chosen, the assessment is interrupted without a request for
textual feedback. In this case, it is considered that the evaluator is not in a position to assess the answer.

**Criterion "A" (Agreed)**
- **Prompt proposed to the evaluator**: I agree with the proposed answer.
- **Response options**: No, Partially, Yes, I do not pronounce myself.
- **Continuation of the assessment**:
  - If the chosen option is _Yes_, the evaluator is invited to assess according to the next criterion (X).
  - If the response option is _No_ or _Partially_, the evaluator is asked to provide textual feedback.
  - **Feedback requested from the evaluator**: In what way do you disagree with the proposed answer?
- **End of the assessment**:
  - If textual feedback is provided, the assessment is considered completed.
  - If the option _I do not pronounce myself_ is chosen, the assessment is interrupted without a request for
textual feedback. In this case, it is considered that the evaluator is not in a position to assess the answer.

**Criterion "X" (Exhaustive)**
- **Prompt proposed to the evaluator**: I think that the answer is complete.
- **Response options**: No, Yes, I don't know.
- **Continuation of the assessment**:
  - If the chosen option is _Yes_, the evaluator is invited to assess according to the next criterion (O).
  - If the response option is _No_, the evaluator is asked to provide textual feedback.
  - **Feedback requested from the evaluator**: What would you need to add for the answer to be complete?
- **End of the assessment**:
  - If textual feedback is provided, the assessment is considered completed.
  - If the option _I don't know_ is chosen, the assessment ends without a request for
textual feedback.

**Criterion "O" (Optimal)**
- **Prompt proposed to the evaluator**: I think that the answer can be improved.
- **Response options**: No, Yes, I don't know.
- **Continuation of the assessment**:
  - If the response option is _Yes_, the evaluator is asked to provide textual feedback.
  - **Feedback requested**: How can you help the author improve their answer?
- **End of the assessment**:
  - If textual feedback is provided, the assessment is considered completed.
  - If the options _No_ or _I don't know_ are chosen, the assessment ends without a request for
textual feedback.

### 2.7.2 Scoring of an answer

The score, if it can be calculated, ranges from 1 to 5.

**Based on the "Answers the question" feature (Criterion R):**
- Answer "No": score = 1
- Answer "Partially": score = 1.5

**Based on the degree of agreement (Criterion A):**
- Answer "No": score = 2
- Answer "Partially": score = 3
- Answer "Yes": score = 4

**Bonus if the answer is complete (Criterion X):**
- Answer "Yes": score = 4.5

**Bonus if the answer is optimal (Criterion O):**
- Answer "Yes": score = 5

**The score is not calculated in the following cases:**
- Only criterion D is evaluated.
- Criterion R is evaluated as "I don't know".
- Criterion A is evaluated as "I do not pronounce myself".
