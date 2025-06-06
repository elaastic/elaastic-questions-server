import type { Meta, StoryObj } from '@storybook/vue3'
import ContentBlock from '@/components/player/ContentBlock.vue'
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
  confidence: "Confiant(e)"
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


