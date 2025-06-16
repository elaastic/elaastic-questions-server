import type {Meta, StoryObj} from '@storybook/vue3'
import ContentBlock from '@/components/player/ContentBlock.vue'
import ResponseForm from "@/components/response/ResponseForm.vue";
import {ref} from "vue";
import {type AnyResponse, ConfidenceDegree} from "@/models/Response";
import ChoiceChip from '@/components/response/ChoiceChip.vue';

const meta = {
    title: 'player/ContentBlock',
    component: ContentBlock,
    tags: ['autodocs'],
} satisfies Meta<typeof ContentBlock>

export default meta
type Story = StoryObj<typeof meta>


const answer = ref<AnyResponse>({
    id: 1,
    questionType: "MultipleChoice",
    explanation: "",
    choices: [],
    confidence: ConfidenceDegree.CONFIDENT
});

const validate = ref(false);

function handleAnswer(newAnswer: AnyResponse) {
    answer.value = newAnswer;
}
function sendAnswer() {
    if (answer.value.questionType === "MultipleChoice") {
        if (answer.value.choices.length !== 0) {
            validate.value = true;
        }
    } else {
        validate.value = true;
    }
}

export const Default: Story = {
    render: (args) => ({
        components: { ContentBlock },
        setup() {
            const isOpen = ref(args.open);

            return {
                args,
                isOpen,
                updateOpen: (val: boolean) => isOpen.value = val
            }
        },
        template: `
      <ContentBlock
              v-bind="args"
              :open="isOpen"
              @update:open="updateOpen"
      >
        <p>Contenu</p>
      </ContentBlock>
    `,
    }),
    args: {
        title: 'Enoncé',
        collapsible: true,
        open: true,
        isSubtitleHidden: true
    },
}
export const responseForm: Story = {
    render: (args) => ({
        components: { ContentBlock, ResponseForm, ChoiceChip },
        setup() {
            const isOpen = ref(args.open);
            return { args, answer, validate, handleAnswer, sendAnswer, isOpen,
                updateOpen: (val: boolean) => isOpen.value = val }
        },
        template: `
      <ContentBlock v-bind="args" :open="isOpen"
                    @update:open="updateOpen">
        <div v-if="!validate">
          <ResponseForm
                  :providedAnswers="9"
                  :answer="answer"
                  @update:answer="handleAnswer"
          ></ResponseForm>
          <v-btn class="bouton" color="secondary" @click="sendAnswer()" style="margin-top: 5%; margin-bottom: 5%; margin-left: 4%;">Enregistrer</v-btn>
        </div>
        <div v-if="validate">
          <v-alert text="Réponse Envoyée" type="success" class="mb-4"></v-alert>
          <ul class="ml-9">
            <li>
              {{ answer.choices.length === 1 ? 'Votre Réponse :' : 'Vos Réponses' }} <ChoiceChip v-for="a in answer.choices" :value="a" color="gray"/>
            </li>
            <li>
              Votre explication : {{answer.explanation}}
            </li>
            <li>
              Votre degré de confiance : {{answer.confidence}}
            </li>
          </ul>
        </div>
      </ContentBlock>
    `,
    }),
    args: {
        title: 'Enoncé',
        collapsible: true,
        open: true,
        subtitle: "Question à choix multiple",
        isSubtitleHidden: false
    },
}
export const Results: Story = {
    render: (args) => ({
        components: { ContentBlock },
        setup() {
            const isOpen = ref(args.open);
            return { args, isOpen,
                updateOpen: (val: boolean) => isOpen.value = val };
        },
        template: `
      <ContentBlock v-bind="args" :open="isOpen"
                    @update:open="updateOpen">
        <div>
          <h3>Choix</h3>
          <div style="display: flex">
              <v-card style="width: 50px; background-color: green">
                <v-card-text style="color: white">1</v-card-text>
              </v-card>
              <v-card style="width: 50px; background-color: red">
                <v-card-text style="color: white">2</v-card-text>
              </v-card>
          </div>
            <p>Score</p>
          <v-card style="width: 60px; background-color: blue">
            <v-card-text style="color: white">50%</v-card-text>
          </v-card>
        </div>
      </ContentBlock>
    `
    }),
    args: {
        title: 'Results',
        collapsible: false,
        open: true,
        isSubtitleHidden: true
    }
}


