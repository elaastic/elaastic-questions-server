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
let validate = false;
function handleAnswer(newAnswer: AnyResponse) {
    answer.value = newAnswer;
    validate = true;
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
            return { args, validate, answer, handleAnswer };
        },
        template: `
      <Block :title="args.title">
        <ResponseForm v-if="!validate"
          :providedAnswers="[1,2,3,4,5,6,7,8,9]"
          :trust-selections=" [
            { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
            { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
            { label: 'Confiant(e)', value: 'Confiant(e)' },
            { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
            ]"
          :selectedConfiance="'Confiant(e)'"
          :isSend="false"
          :answer="answer"
          @update:answer="handleAnswer"
        ></ResponseForm>
        <div v-if="validate">
          <h1 class="main-title" style="
          text-align: center;
          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
          font-size: 50px;
          color: #333;
          margin-top: 50px;
          margin-bottom: 30px;
          font-weight: 600;
          letter-spacing: 1px;
          position: relative;">
            Réponse Envoyée
          </h1>
          <p style="
          display: block;
          font-size: 50px;
          color: green;
          margin: 10px auto 0 auto;
          text-align: center">
            ✔️
          </p>
        </div>
      </Block>
    `,
    }),
    args: {
        title: "Réponse",
    },
}
