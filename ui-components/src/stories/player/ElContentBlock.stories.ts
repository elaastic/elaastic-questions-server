import type { Meta, StoryObj } from '@storybook/vue3'
import ElContentBlock from '@/components/player/ElContentBlock.vue'

const meta = {
  title: 'player/ElContentBlock',
  component: ElContentBlock,
  tags: ['autodocs']
} satisfies Meta<typeof ElContentBlock>

export default meta
type Story = StoryObj<typeof meta>

export const Default: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
    setup() {
      return {
        args
      }
    },
    template: `
      <ContentBlock v-bind="args" v-model:open="args.open">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eu nunc nisl. In tincidunt, ante et venenatis aliquam, leo nulla interdum mauris, at volutpat magna dui nec felis. Nam ac vestibulum nibh. Vivamus vitae risus neque. Duis ultrices dui ut.</p>
      </ContentBlock>
    `
  }),
  args: {
    title: 'Mastering the Art of Productivity: Tips and Tricks for a More Efficient Life',
    open: true
  }
}

export const NotCollapsible: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
    setup() {
      return {
        args
      }
    },
    template: `
      <ContentBlock v-bind="args">
        <p>I'm a block of content that can't be folded.</p>
      </ContentBlock>
    `
  }),
  args: {
    title: 'Not collapsible',
    collapsible: false
  }
}
export const Closed: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
    setup() {
      return {
        args
      }
    },
    template: `
      <ContentBlock v-bind="args" v-model:open="args.open">
        <p>I'm a closed content block.</p>
      </ContentBlock>
    `
  }),
  args: {
    title: 'Closed content block',
    open: false
  }
}

export const WithSubtitle: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
    setup() {
      return {
        args
      }
    },
    template: `
      <ContentBlock
              v-bind="args"
              v-model:open="args.open"
      >
        <p>This content block illustrates a title with a subtitle.</p>
      </ContentBlock>
    `
  }),
  args: {
    title: 'I have a title',
    collapsible: true,
    open: true,
    subtitle: 'and a subtitle'
  }
}
export const Results: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
    setup() {
      return { args }
    },
    template: `
      <ContentBlock v-bind="args" v-model:open="args.open">
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
    open: true
  }
}


