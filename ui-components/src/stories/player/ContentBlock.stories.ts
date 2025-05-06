import type { Meta, StoryObj } from '@storybook/vue3'
import ContentBlock from '@/components/player/ContentBlock.vue'
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
    modelValue: 0,
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
    modelValue: 0
  }
}


