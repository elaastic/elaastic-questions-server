import type { Meta, StoryObj } from '@storybook/vue3'
import ContentBlock from '@/components/player/ContentBlock.vue'
import ResponseForm from "@/components/response/ResponseForm.vue";
const meta = {
  title: 'player/ContentBlock',
  component: ContentBlock,
  tags: ['autodocs'],
} satisfies Meta<typeof ContentBlock>

export default meta
type Story = StoryObj<typeof meta>

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
      return { args }
    },
    template: `
      <ContentBlock v-bind="args">
        <ResponseForm
          :provided-answers="['1', '2', '3', '4', '5', '6', '7', '8']"
          :selections-confiance="[
          { label: 'Pas du tout confiant(e)', value: 'Pas du tout confiant(e)' },
          { label: 'Pas vraiment confiant(e)', value: 'Pas vraiment confiant(e)' },
          { label: 'Confiant(e)', value: 'Confiant(e)' },
          { label: 'Tout à fait confiant(e)', value: 'Tout à fait confiant(e)' },
          ]"
          selected-confiance="Confiant(e)"
          default-text="Votre réponse">
        </ResponseForm>
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


