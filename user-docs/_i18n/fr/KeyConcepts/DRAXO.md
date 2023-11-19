## 2.6. DRAXO

DRAXO est une grille d'évaluation pour évaluer des réponses données à des questions de compréhension. 
Ces réponses exposent engénéral, des explications, justifications ou argumentation.
DRAXO a été mise au point dans le cadre des recherches menées à l'IRIT sur les systèmes technologiques 
venant en soutien à l'évaluation formative. DRAXO a pour objectif l'amélioration de la production de feedback textuel.
DRAXO peut être utilisée durant la deuxième phase d'une séquence Elaastic pour guider les apprenants dans
leur activité d'évaluation par les pairs.

### 2.6.1. Les critères DRAXO

La grille est structurée autour de plusieurs critères identifiés par les lettres D, R, A, X, et O. Voici une description détaillée de chaque critère :

**Critère « D » (Déchiffrable)**
- **Libellé proposé à l'évaluateur** : Je comprends ce qui est écrit dans la réponse.
- **Options de réponse** : Non, En partie, Oui.
- **Poursuite de l'évaluation** :
  - Si l'option choisie est _Oui_, l'évaluateur est invité à évaluer selon le critère suivant (R).
  - Si l'option de réponse est _Non_ ou _En partie_, l'évaluateur est invité à fournir un feedback textuel.
  - **Feedback textuel demandé à l'évaluateur** : Qu'est ce que vous ne comprenez pas dans la réponse ?
- **Fin de l'évaluation** :
  - Si fourniture du feedback textuel, l'évaluation est considérée comme achevée. 

**Critère « R » (Répond à la question)**
- **Libellé proposé à l'évaluateur** : Je trouve que la réponse correspond à la question posée.
- **Options de réponse** : Non, En partie, Oui, Je ne sais pas.
- **Poursuite de l'évaluation** :
  - Si l'option choisie est _Oui_, l'évaluateur est invité à évaluer selon le critère suivant (A).
  - Si l'option de réponse est _Non_ ou _En partie_, l'évaluateur est invité à fournir un feedback textuel.
  - **Feedback demandé à l'évaluateur** : Pourquoi la réponse, selon vous, ne correspond pas à la question posée ?
- **Fin de l'évaluation** : 
  - Si fourniture du feedback textuel, l'évaluation est considérée comme achevée.
  - Si l'option _Je ne sais pas_ est choisie, l'évaluation est interrompue sans demande de 
feedback textuel. Dans ce cas, il est considéré que l'évaluateur n'est pas en mesure d'évaluer la réponse.

**Critère « A » (Acceptable)**
- **Libellé proposé à l'évaluateur** : Je suis d’accord avec la réponse proposée.
- **Options de réponse** : Non, En partie, Oui, Je ne me prononce pas.
- **Poursuite de l'évaluation** :
  - si l'option choisie est _Oui_, l'évaluateur est invité à évaluer selon le critère suivant (X).
  - si l'option de réponse est _Non_ ou _En partie_, l'évaluateur est invité à fournir un feedback textuel.
  - **Feedback demandé à l'évaluateur** : En quoi n’êtes-vous pas d’accord avec la réponse proposée ?
- **Fin de l'évaluation** : 
  - Si fourniture du feedback textuel, l'évaluation est considérée comme achevée.
  - Si l'option _Je ne me prononce pas_ est choisie, l'évaluation est interrompue sans demande de
    feedback textuel. Dans ce cas, il est considéré que l'évaluateur n'est pas en mesure d'évaluer la réponse.


**Critère « X » (eXhaustive)**
- **Libellé proposé à l'évaluateur** : Je pense que la réponse est complète.
- **Options de réponse** : Non, Oui, Je ne sais pas.
- **Poursuite de l'évaluation** :
  - si l'option choisie est _Oui_, l'évaluateur est invité à évaluer selon le critère suivant (O).
  - si l'option de réponse est _Non_, l'évaluateur est invité à fournir un feedback textuel.
  - **Feedback demandé à l'évaluateur** : Que faudrait-il ajouter pour que la réponse soit complète ?
- **Fin de l'évaluation** : 
  - Si fourniture du feedback textuel, l'évaluation est considérée comme achevée.
  - Si l'option _Je ne sais pas_ est choisie, l'évaluation s'achève sans demande de
    feedback textuel.

**Critère « O » (Optimale)**
- **Libellé proposé à l'évaluateur** : Je pense que la réponse peut être améliorée.
- **Options de réponse** : Non, Oui, Je ne sais pas.
- **Poursuite de l'évaluation** :
  - si l'option de réponse est _Oui_, l'évaluateur est invité à fournir un feedback textuel.
  - **Feedback demandé** : Comment pouvez-vous aider l’auteur à améliorer sa réponse ?
- **Fin de l'évaluation** : 
  - Si fourniture du feedback textuel, l'évaluation est considérée comme achevée. 
  - Si une des options _Non_ ou _Je ne sais pas_ est choisie, l'évaluation se termine sans demande de
    feedback textuel.

### 2.6.2. Calcul du score d’une réponse

Le score, s'il peut être caculé, prend une valeur comprise entre 1 et 5.

**En fonction du caractère "Répond à la question" (critère R) :**
- Réponse « Non » : score = 1
- Réponse « En partie » : score = 1,5

**En fonction du degré d’accord (critère A) :**
- Réponse « Non » : score = 2
- Réponse « En partie » : score = 3
- Réponse « Oui » : score = 4

**Bonus si réponse complète (Critère X) :**
- Réponse « Oui » : score = 4,5

**Bonus si réponse optimale (Critère O) :**
- Réponse « Oui » : score = 5

**Le score n'est pas calculé dans les cas suivants :**
- Seul le critère D est évalué.
- Le critère R est évalué à « Je ne sais pas ».
- Le critère A est évalué à « je ne me prononce pas ».

