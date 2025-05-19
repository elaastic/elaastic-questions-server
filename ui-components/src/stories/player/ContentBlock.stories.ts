import type { Meta, StoryObj } from '@storybook/vue3'
import ContentBlock from '@/components/player/ContentBlock.vue'
import ResponseForm from "@/components/response/ResponseForm.vue";
import {ref} from "vue";
import type {AnyResponse} from "@/models/Response";
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
  trust: "Confiant(e)"
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
      return { args }
    },
    template: `
      <ContentBlock v-bind="args">
        <p>Contenu</p>
      </ContentBlock>
    `,
  }),
  args: {
    title: 'Enoncé',
    readonly: false,
    state: 0,
    side: "[Question ouverte]"
  },
}
export const RespForm: Story = {
  render: (args) => ({
    components: { ContentBlock, ResponseForm },
    setup() {
      return { args, answer, validate, handleAnswer, sendAnswer }
    },
    template: `
      <ContentBlock v-bind="args">
        <div v-if="!validate">
          <ResponseForm
                  :providedAnswers="[1,2,3,4,5,6,7,8,9]"
                  :trust-selections=" [
              { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
              { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
              { label: 'Confiant(e)', value: 'Confiant(e)' },
              { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
              ]"
                  :answer="answer"
                  @update:answer="handleAnswer"
          ></ResponseForm>
          <v-btn class="bouton" color="secondary" @click="sendAnswer()" style="margin-top: 5%; margin-bottom: 5%; margin-left: 4%;">Enregistrer</v-btn>
        </div>
        <div v-if="validate">
          <v-alert text="Réponse Envoyée" type="success" class="mb-4"></v-alert>
          <ul class="ml-9">
            <li>
              Votre Réponse : {{answer.choices}}
            </li>
            <li>
              Votre explication : {{answer.explanation}}
            </li>
            <li>
              Votre degré de confiance : {{answer.trust}}
            </li>
          </ul>
        </div>
      </ContentBlock>
    `,
  }),
  args: {
    title: 'Enoncé',
    readonly: false,
    state: 0,
    side: "[Question à choix multiple]"
  },
}
export const Results: Story = {
  render: (args) => ({
    components: { ContentBlock },
    setup() {
      return { args };
    },
    template: `
      <ContentBlock v-bind="args">
        <div>
          <div class="small" style="margin-bottom: 5px;">Choix</div>
          <div class="ui left labeled button small" tabindex="0" style="margin-right: 10px;">
            <a class="ui label green">1</a>
            <div class="ui button compact">
              <i class="icon"></i>
            </div>
            <a class="ui label green">2</a>
            <div class="ui button compact">
              <i class="icon"></i>
            </div>
          </div>
          <div style="margin-top: 15px;">
            <div class="small" style="margin-bottom: 5px">Score</div>
            <div class="ui medium label red">0%</div>
          </div>
        </div>
      </ContentBlock>
    `
  }),
  args: {
    title: 'Results',
    readonly: true,
    state: 0,
    side: ""
  }
}


