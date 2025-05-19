import type { Meta, StoryObj } from '@storybook/vue3'
import Block from '@/components/block/Block.vue'
import ResponseForm from "@/components/response/ResponseForm.vue";
import type {AnyResponse} from "@/models/Response";
import {ref} from "vue";

const meta = {
    title: 'Block/Block',
    component: Block,
    tags: ['autodocs'],
} satisfies Meta<typeof Block>

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
function sendAnswer(){
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
        components: { Block },
        setup() {
            return { args };
        },
        template: `
      <Block :title="args.title">
        <h1>Rendu</h1>
      </Block>
    `,
    }),
    args: {
        title: "Title",
    },
}
export const RespForm: Story = {
    render: (args) => ({
        components: { Block, ResponseForm },
        setup() {
            return { args, validate, answer, handleAnswer, sendAnswer };
        },
        template: `
      <Block :title="args.title">
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
      </Block>
    `,
    }),
    args: {
        title: "Réponse",
    },
}
