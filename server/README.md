# server

## Spring profiles
- `dev`: Enables configuration dedicated to the development environment.
- `testing`: Enables the functional testing facilities (see [Testing facilities](#testing-facilities))
- `chatgpt`: Enables the use of ChatGPT for evaluating student responses.
- `oidc`: Enables the use of OpenID Connect for authentication.

## Testing facilities
> [!NOTE]
> Those facilities are enabled by the `testing` profile.

### Generating test suject
For teachers, on the "My subjects" page, you can generate a test subject by clicking on the "Generate test subject".
The generated subject is composed of:
- one question per type (open, single choice, multiple choice)
- three diffusions, allowing to easily test the 3 setup face to face, distant and hybrid.

### Script simulated learners behavior
A console "Functional Test Script" allows to simulate the behavior of learners, which is very convenient for testing.

The script should respect this grammar:
```
start      <context>
response   <phase> <username> <correctness> <confidence-degree> [<explanation>]
eval       <username> <evaluation-strategy>
publish
next
stop
---       
<context>               ::= FaceToFace | Distance | Blended                 
<phase>                 ::= 1 | 2
<correctness>           ::= correct | *
<confidence-degree>     ::= NOT_CONFIDENT_AT_ALL | NOT_REALLY_CONFIDENT | CONFIDENT | TOTALLY_CONFIDENT
<evaluation-strategy>   ::= RANDOM | ALWAYS_MAX | ALWAYS_MIN | RELEVANT | IRRELEVANT
```

Example:
```
start FaceToFace
response 1 jtra correct TOTALLY_CONFIDENT
response 1 tsil correct CONFIDENT
next
eval jtra RANDOM 
eval tsil RELEVANT
next
publish 
stop
```