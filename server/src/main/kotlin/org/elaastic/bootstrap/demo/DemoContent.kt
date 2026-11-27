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

package org.elaastic.bootstrap.demo

import org.elaastic.material.instructional.question.ChoiceItem
import org.elaastic.material.instructional.question.ChoiceSpecification
import org.elaastic.material.instructional.question.ExclusiveChoiceSpecification
import org.elaastic.material.instructional.question.MultipleChoiceSpecification
import org.elaastic.material.instructional.question.QuestionType

internal data class DemoCourse(
    val title: String,
    val ownerLogin: String,
    val subjects: List<DemoSubject> = emptyList()
)

internal data class DemoSubject(val title: String, val questions: List<DemoQuestion> = emptyList())

internal data class DemoQuestion(
    val title: String,
    val questionType: QuestionType,
    val content: String,
    val choiceSpecification: ChoiceSpecification? = null,
    val expectedExplanation: String? = null,
    val attachment: String? = null
)

internal const val DEMO_OWNER_LOGIN = "aein"

private val accords = listOf(
    DemoQuestion(
        questionType = QuestionType.ExclusiveChoice,
        choiceSpecification = ExclusiveChoiceSpecification(
            nbCandidateItem = 4,
            expectedChoice = ChoiceItem(2, 1f)
        ),
        content = "Complétez la phrase en choisissant la bonne orthographe du participe passé. Puis justifiez votre choix de manière rédigée.\n" +
                "\n" +
                "Le texte … en classe était très long et très difficile.\n" +
                "\n" +
                "    appri\n" +
                "    appris\n" +
                "    apprit\n" +
                "    apprie\n",
        title = "Question 1"
    ),
    DemoQuestion(
        questionType = QuestionType.OpenEnded,
        content = "À quoi sert le participe passé en français ? Dans quels cas l’utilise-t-on ?",
        title = "Question 2",
        expectedExplanation = "Le participe passé sert à former les temps composés d’un verbe (\"J’ai ouvert la porte\"). On peut aussi l'utiliser comme adjectif (\"le portail ouvert\")."
    )
)

private val fractions = listOf(
    DemoQuestion(
        questionType = QuestionType.MultipleChoice,
        choiceSpecification = MultipleChoiceSpecification(
            nbCandidateItem = 3,
            expectedChoiceList = listOf(ChoiceItem(1, 1f), ChoiceItem(3, 1f))
        ),
        content = "<p>Quel(s) symbole(s) repr&eacute;sente(nt) l&#39;inconnue dans l&#39;&eacute;quation suivante ? Expliquez ce que signifie r&eacute;soudre l&#39;&eacute;quation.</p>\n" +
                "\n" +
                "<p><span class=\"math-tex\">\\(7x + 4 = 25\\)</span></p>\n" +
                "\n" +
                "<ol>\n" +
                "\t<li><span class=\"math-tex\">\\(x\\)</span></li>\n" +
                "\t<li><span class=\"math-tex\">\\(+\\)</span></li>\n" +
                "\t<li><span class=\"math-tex\">\\(=\\)</span></li>\n" +
                "</ol>\n" +
                "<p>&nbsp;</p>\n",
        title = "Équations simples",
        expectedExplanation = "L'inconnue est représentée par le symbole x. Résoudre l'équation signifie trouver la valeur de x qui vérifie l'égalité."
    ),
    DemoQuestion(
        questionType = QuestionType.OpenEnded,
        content = "<p>Comment tracer l&#39;&eacute;quation lin&eacute;aire &eacute;crite sur le graphique ?</p>\n",
        title = "Tracer une équation linéaire",
        expectedExplanation = "<p>Cette &eacute;quation est lin&eacute;aire, ce qui signifie qu&#39;elle sera repr&eacute;sent&eacute;e par une ligne droite sur le graphique. Il vous suffit de trouver 2 points par lesquels la droite est cens&eacute;e passer, puis vous pourrez la tracer. La r&eacute;solution de l&#39;&eacute;quation pour 2 valeurs diff&eacute;rentes de y permet de trouver 2 points diff&eacute;rents.</p>\n" +
                "\n" +
                "<p>Plus de d&eacute;tails :<br />\n" +
                "Un premier point est d&eacute;j&agrave; &eacute;crit, il s&#39;agit de (0, -1). Un point est sur la droite si on peut &eacute;crire (x, y) comme un couple qui r&eacute;sout l&#39;&eacute;quation.<br />\n" +
                "Avec le couple (x = 0, y = -1), l&#39;&eacute;quation y = 2x -1 devient 2*0 - 1 = -1, ce qui est vrai.<br />\n" +
                "Un autre point que nous pouvons trouver est le point o&ugrave; y = 0 en r&eacute;solvant l&#39;&eacute;quation de cette fa&ccedil;on :<br />\n" +
                "2*x - 1 = y = 0<br />\n" +
                "2x = 1<br />\n" +
                "x = 1/2</p>\n" +
                "\n" +
                "<p>Avec les deux points (0, -1) et (1/2, 0), vous pouvez tracer la ligne repr&eacute;sentant toutes les solutions possibles de l&#39;&eacute;quation.</p>\n",
        attachment = "static/images/demonstrationContent/CreateQuestion.png"
    )
)

internal val demoCourses = listOf(
    DemoCourse("Demo", DEMO_OWNER_LOGIN),
    DemoCourse(
        "Maths 6e", DEMO_OWNER_LOGIN,
        listOf(
            DemoSubject("Nombres décimaux"),
            DemoSubject("Figures planes"),
            DemoSubject("Fractions", fractions)
        )
    ),
    DemoCourse(
        "Français 5e", "fsil",
        listOf(
            DemoSubject("Le champ lexical"),
            DemoSubject("Les compléments d'objets"),
            DemoSubject("Accords du participe passé", accords)
        )
    ),
    DemoCourse("Histoire 4e", "rand", listOf(DemoSubject("La révolution française")))
)
