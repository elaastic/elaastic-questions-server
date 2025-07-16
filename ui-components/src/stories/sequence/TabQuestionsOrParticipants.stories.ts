import type { Meta, StoryObj } from '@storybook/vue3-vite'

import TabQuestionsOrParticipants from '@/components/sequence/TabQuestionsOrParticipants.vue'

const meta = {
  title: 'sequence/TabQuestionsOrParticipants',
  component: TabQuestionsOrParticipants,
  tags: ['autodocs', 'atomic'],
} satisfies Meta<typeof TabQuestionsOrParticipants>

export default meta
type Story = StoryObj<typeof meta>

export const NotStarted: Story = {
  args: {
    idAssignment: 23,
    course: { title: "Français", id: 8 },
    subject: { title: "L'accord du participe passé", id: 56 },
    audience: "4e5",
    scholarYear: "2023-2024",
    title: "L'accord du participe passé",
    questions: [
      {
        state: 'CLOSED',
        question: {
          title: 'Question 1',
          statement: `<p>À quoi sert le participe passé en français ? Dans quels cas l’utilise-t-on ? Répondez de manière rédigée en soignant votre expression.</p>`,
          questionNumber: 1,
        }
      },
      {
        state: 'RESPONSE_PHASE',
        question: {
          title: 'Question 2',
          statement: `
            <p>Complétez la phrase en choisissant la bonne orthographe du participe passé. Puis justifiez votre choix de manière rédigée.</p>

            <p>Le texte … en classe était très long et très difficile.</p>

            <p>1) appri</p>
            <p>2) appris</p>
            <p>3) apprit</p>
            <p>4) apprie</p>
            `,
          questionNumber: 2,
        }
      },
      {
        state: 'BLENDED',
        question: {
          title: 'Question 3',
          statement: `
            <p>Selon vous, les deux participes passés soulignés sont-ils bien orthographiés ? Justifiez votre choix de manière rédigée. </p>

            <p>Les serveuses ont salué les clients puis elles les ont invités à s’asseoir à la table près de la fenêtre. </p>

            <p>1) « salué » est mal orthographié, « invités » est mal orthographié ;</p>
            <p>2) « salué » est mal orthographié, « invités » est bien orthographié ;</p>
            <p>3) « salué » est bien orthographié, « invités » est mal orthographié ;</p>
            <p>4) « salué » est bien orthographié, « invités » est bien orthographié ;</p>
        `,
          questionNumber: 3,
        }
      },
      {
        state: 'CONFRONTING_VIEWPOINT',
        question: {
          title: 'Question 4',
          statement: `
            <p>Complétez la phrase en choisissant la bonne orthographe du participe passé. Puis justifiez votre choix de manière rédigée. </p>

            <p>Hier soir, Garry et Eugénie se sont … pour une simple histoire de stylo quatre couleurs. </p>

            <p>1)  chamaillé</p>
            <p>2) chamailler</p>
            <p>3) chamaillée</p>
            <p>4) chamaillés</p>
          `,
          questionNumber: 4,
        }
      },
      {
        state: 'RESULTS_PHASE',
        question: {
          title: 'Question 5',
          statement: `
            <p>Complétez la phrase en choisissant la bonne orthographe du participe passé. Puis justifiez votre choix de manière rédigée.</p>

            <p>Je n’aime pas vraiment les chaussures que mes parents ont … pour moi. </p>

            <p>1) choisi</p>
            <p>2) choisis</p>
            <p>3) choisie</p>
            <p>4) choisies</p>
          `,
          questionNumber: 5,
        }
      },
      {
        state: 'DISTANT',
        question: {
          title: 'Question 6',
          statement: `
            <p>Complétez la phrase en choisissant la bonne orthographe du participe passé. Puis justifiez votre choix de manière rédigée.</p>

            <p>Des mauvaises décisions, Valériane en a … énormément, ces derniers temps ! </p>

            <p>1) pris</p>
            <p>2) prise</p>
            <p>3) prises</p>
          `,
          questionNumber: 6,
        }
      },
      {
        state: 'NOT_STARTED',
        question: {
          title: 'Question 7',
          statement: `
            <p>Le participe passé souligné est-il bien orthographié, selon vous ? Justifiez votre choix de manière rédigée. </p>

            <p>Valentin et Sofiane se sont donné rendez-vous à l’arrêt de bus. </p>

            <p>1) Oui, il est bien orthographié.</p>
            <p>2) Non, il est mal orthographié.</p>
          `,
          questionNumber: 7,
        }
      },
    ],
    numberOfParticipants: 5
  },
}
