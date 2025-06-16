import type { Meta, StoryObj } from '@storybook/vue3'
import ElContentBlock from '@/components/player/ElContentBlock.vue'
import {ref} from "vue";
const meta = {
  title: 'player/ElContentBlock',
  component: ElContentBlock,
  tags: ['autodocs'],
} satisfies Meta<typeof ElContentBlock>

export default meta
type Story = StoryObj<typeof meta>


export const Default: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
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
export const NotCollapsible: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
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
    collapsible: false,
    open: true,
    isSubtitleHidden: true
  },
}
export const Closed: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
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
    open: false,
    isSubtitleHidden: true
  },
}
export const SubtitleNotHidden: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
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
    isSubtitleHidden: false
  },
}
export const WithSubtitle: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
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
    isSubtitleHidden: false,
    subtitle: "Subtitle"
  },
}
export const Results: Story = {
  render: (args) => ({
    components: { ContentBlock: ElContentBlock },
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


